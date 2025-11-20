package enshud.s4.compiler.generator.impls.casl;

import enshud.s4.compiler.generator.GeneratorBase;
import enshud.s4.compiler.generator.allocator.Allocation;
import enshud.s4.compiler.generator.allocator.IAllocator;
import enshud.s4.compiler.generator.location.Location;
import enshud.s4.compiler.generator.location.MemoryLocation;
import enshud.s4.compiler.generator.location.RegisterLocation;
import enshud.s4.compiler.generator.location.StackLocation;
import enshud.s4.compiler.ir.*;
import enshud.s4.compiler.ir.instructions.*;
import enshud.s4.compiler.ir.operand.Const;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.program.IrProgram;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class CaslGenerator extends GeneratorBase<String> {

    @Override
    protected void preprocess(IrProgram program) {
        // CASL専用の最適化
        System.out.println("---- CASL Generator Preprocess ----");
        program.main.addAt(0, new Label("BEGIN"));
        // 出力
        program.main.getInstructions()
                .forEach(System.out::println);
    }

    @Override
    protected String postprocessOutput(String output) {
        return "CASL\tSTART BEGIN\n" +
                output +
                "\n\tEND";
    }

    @Override
    protected IAllocator getAllocator() {
        return new LinearAllocator();
    }

    @Override
    protected IIrVisitor<String> getConverter(Allocation allocation) {
        System.out.println(allocation);
        var nextLabel = new ArrayBlockingQueue<String>(1);
        // 無名クラスで実装
        return new IIrVisitor<String>() {

            @Override
            public String visit(Call ir) {
                InstructionBuilder ib = new InstructionBuilder(allocation);
                // 引数をスタックにプッシュ
                for (var arg : ir.args) {
                    ib.add(new PUSH(Const.ZERO));
                }
                ib.add(new CALL(ir.functionLabel));

                applyLabel(ib);
                return ib.build();
            }

            @Override
            public String visit(Decl ir) {
                return "NOP";
            }

            @Override
            public String visit(DeclArray ir) {
                return "NOP";
            }

            @Override
            public String visit(Jump ir) {
                var ib = new InstructionBuilder(allocation);
                switch (ir.type) {
                    case Jump.TYPE_UNCONDITIONAL ->
                        ib.add(new JUMP(ir.target));
                    case Jump.TYPE_TRUE ->
                        ib.add(new JNZ(ir.target));
                    case Jump.TYPE_FALSE ->
                        ib.add(new JZE(ir.target));
                    default ->
                        throw new UnsupportedOperationException("Unknown jump type: " + ir.type);
                }
                applyLabel(ib);
                return ib.build();
            }

            @Override
            public String visit(Label ir) {
                if (!nextLabel.offer(ir.name)) {
                    throw new IllegalStateException("Multiple labels in a row: " + ir.name);
                }
                return "";
            }

            @Override
            public String visit(Labeled labeled) {
                return labeled.label + "\t" + labeled.ir.accept(this);
            }

            @Override
            public String visit(GetArgument getArgument) {
                return "NOP";
            }

            @Override
            public String visit(Load load) {
                InstructionBuilder ib = new InstructionBuilder(allocation);
                ib.add(new LD(load.targetVar, load.var));
                applyLabel(ib);
                return ib.build();
            }

            @Override
            public String visit(Store store) {
                InstructionBuilder ib = new InstructionBuilder(allocation);
                StackLocation varAlloc = (StackLocation) allocation.get(store.target);
                Location srcAlloc;
                if (store.value instanceof Const cnst){
                    ib.add(new LAD(Registers.TEMP))
//                    ib.add(String.format("LAD %s, =%s", Registers.TEMP, cnst.value));
                    srcAlloc = Registers.TEMP.asLocation();
                } else {
                    srcAlloc = allocation.get((Symbol) store.value);
                }
                if (srcAlloc instanceof RegisterLocation regAlloc) {
//                    ib.add(String.format("ST %s, %s, %s", regAlloc.registerName, varAlloc.offset, Registers.FP));
                } else {
                    // TODO: スピルしている場合
                    throw new UnsupportedOperationException("Source of Store must be in register: " + srcAlloc);
                }

                return ib.build();
            }

            @Override
            public String visit(LoadIndexed ir) {
                return "NOP";
            }

            @Override
            public String visit(Move ir) {
                if (ir.src instanceof Const) {
                    return format("LAD", ir.dest, ir.src);
                } else {
                    return format("LD", ir.dest, ir.src);
                }
            }

            @Override
            public String visit(StoreIndexed ir) {
                return "NOP";
            }

            @Override
            public String visit(Return aReturn) {
                return "RET";
            }

            @Override
            public String visit(Writeln writeln) {
                return visit(new Call("WRITELN", List.of()));
            }

            @Override
            public String visit(BinaryOp binaryOp) {
                return switch (binaryOp.opType) {
                    case ADD -> format("ADDA", binaryOp.left, binaryOp.right);
                    case SUB -> format("SUBA", binaryOp.left, binaryOp.right);
                    case MUL -> format("MULA", binaryOp.left, binaryOp.right);
                    case DIV -> format("DIVA", binaryOp.left, binaryOp.right);
                    case MOD -> format("MODA", binaryOp.left, binaryOp.right);
                    case AND -> format("AND", binaryOp.left, binaryOp.right);
                    case OR -> format("OR", binaryOp.left, binaryOp.right);
                    case XOR -> format("XOR", binaryOp.left, binaryOp.right);
                    case EQ -> format("CPA", binaryOp.left, binaryOp.right);
                    case NEQ -> format("CPA", binaryOp.left, binaryOp.right);
                    case LT -> format("CPA", binaryOp.left, binaryOp.right);
                    case LTE -> format("CPA", binaryOp.left, binaryOp.right);
                    case GT -> format("CPA", binaryOp.left, binaryOp.right);
                    case GTE -> format("CPA", binaryOp.left, binaryOp.right);
                };
            }

            private String format(String instr, Operand op1, Operand op2) {
                if(op1 instanceof Const) {
                    throw new UnsupportedOperationException("Left operand cannot be a constant: " + op1);
                }
                if (op2 instanceof Const) {
                    return formatC(instr, (Symbol) op1, (Const) op2);
                } else {
                    return formatT(instr, (Symbol) op1, (Symbol) op2);
                }
            }

            private String format(String instr, Operand op) {
                if (op instanceof Const) {
                    return formatC(instr, (Const) op);
                } else {
                    return formatT(instr, (Symbol) op);
                }
            }

            private String formatT(String instr, Symbol symbol1, Symbol symbol2) {
                Location alloc1 = allocation.get(symbol1);
                Location alloc2 = allocation.get(symbol2);
                Location originalAlloc1 = alloc1;
                InstructionBuilder ib = new InstructionBuilder(allocation);
                if (alloc1 instanceof MemoryLocation memLoc1){
                    if (memLoc1.offset == 0){
//                        ib.add(String.format("LD %s, %s", Registers.TEMP, memLoc1.baseAddress));
                    }else{
//                        ib.add(String.format("LAD %s, =%d", Registers.OFFSET, memLoc1.offset));
//                        ib.add(String.format("LD %s, %s, %s", Registers.TEMP, memLoc1.baseAddress, Registers.OFFSET));
                    }
                    alloc1 = Registers.TEMP.asLocation();
                }
                if (alloc2 instanceof MemoryLocation memLoc2){
                    if (memLoc2.offset == 0){
//                        ib.add(String.format("%s %s, %s", instr, ((RegisterLocation) alloc1).registerName, memLoc2.baseAddress));
                    }else{
//                        ib.add(String.format("%s %s, %s, %d", instr, ((RegisterLocation) alloc1).registerName, memLoc2.baseAddress, memLoc2.offset));
                    }
                }else if (alloc2 instanceof RegisterLocation regLoc2){
//                    ib.add(String.format("%s %s, %s", instr, ((RegisterLocation) alloc1).registerName, regLoc2.registerName));
                }
                if (originalAlloc1 instanceof MemoryLocation memLoc1){
                    if (memLoc1.offset == 0){
//                        ib.add(String.format("ST %s, %s", Registers.TEMP, memLoc1.baseAddress));
                    }else{
//                        ib.add(String.format("ST %s, %s, %s", Registers.TEMP, memLoc1.baseAddress, Registers.OFFSET));
                    }
                }
                return ib.build();
            }

            private String formatT(String instr, Symbol symbol) {
                Location alloc = allocation.get(symbol);
                if (alloc instanceof MemoryLocation memLoc){
                    if (memLoc.offset == 0){
                        return String.format("%s %s", instr, memLoc.baseAddress);
                    }
                    return String.format("%s %s, %d", instr, memLoc.baseAddress, memLoc.offset);
                }else if (alloc instanceof RegisterLocation regLoc){
                    return String.format("%s %s", instr, regLoc.registerName);
                }
                throw new UnsupportedOperationException("Unknown location type for temp: " + symbol);
            }

            private String formatC(String instr, Symbol symbol, Const constant){
                Location alloc = allocation.get(symbol);
                if (alloc instanceof MemoryLocation memLoc){
                    if (memLoc.offset == 0){
                        return String.format("%s %s, =%s", instr, memLoc.baseAddress, constant.value);
                    }
                    return String.format("%s %s, %d, =%s", instr, memLoc.baseAddress, memLoc.offset, constant.value);
                }else if (alloc instanceof RegisterLocation regLoc){
                    return String.format("%s %s, =%s", instr, regLoc.registerName, constant.value);
                }
                throw new UnsupportedOperationException("Unknown location type for temp: " + symbol);
            }

            private String formatC(String instr, Const constant) {
                return String.format("%s =%s", instr, constant.value);
            }

            private String format(String instr, String label) {
                return String.format("%s %s", instr, label);
            }

            void applyLabel(InstructionBuilder ib) {
                // ラベルがあれば先頭に追加
                if(!nextLabel.isEmpty() && !ib.getInstructions().isEmpty()){
                    String label = nextLabel.poll();
                    ib.getInstructions().getFirst().setLabel(label);
                }
            }
        };
    }

    @Override
    protected String concat(List<String> assemblies) {
        return String.join("\n", assemblies);
    }
}