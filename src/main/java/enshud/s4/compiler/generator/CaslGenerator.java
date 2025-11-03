package enshud.s4.compiler.generator;

import enshud.s4.compiler.generator.allocator.*;
import enshud.s4.compiler.ir.*;
import enshud.s4.compiler.ir.operand.Const;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.ArrayList;
import java.util.List;

public class CaslGenerator extends GeneratorBase<String> {
    private static final String REGISTER_OFFSET = "GR7";

    @Override
    protected void preprocess(IrProgram program) {
        // CASL専用の最適化

    }

    @Override
    protected IAllocator getAllocator() {
        return new LinearAllocator();
    }

    @Override
    protected IIrVisitor<String> getConverter(Allocation allocation) {
        System.out.println(allocation);
        // 無名クラスで実装
        return new IIrVisitor<String>() {
            @Override
            public String visit(Call ir) {
                List<String> ops = new ArrayList<>();
                ops.add("RPUSH");
                // 引数をスタックにプッシュ
                for (Operand arg : ir.args) {
                    ops.add(format("LPUSH", arg));
                }
                ops.add(format("CALL", ir.functionLabel));
                return String.join("\n", ops);
            }

            @Override
            public String visit(Decl ir) {
                return "";
            }

            @Override
            public String visit(DeclArray ir) {
                return "";
            }

            @Override
            public String visit(Jump ir) {
                switch (ir.type) {
                    case Jump.TYPE_UNCONDITIONAL:
                        return format("JUMP", ir.target);
                    case Jump.TYPE_TRUE:
                        return format("JNZ", ir.target);
                    case Jump.TYPE_FALSE:
                        return format("JZE", ir.target);
                    default:
                        throw new UnsupportedOperationException("Unknown jump type: " + ir.type);
                }
            }

            @Override
            public String visit(Label ir) {
                return "%s\tNOP";
            }

            @Override
            public String visit(Labeled labeled) {
                return labeled.label + "\t" + labeled.ir.accept(this);
            }

            @Override
            public String visit(GetArgument getArgument) {
                return "";
            }

            @Override
            public String visit(LoadIndexed ir) {
                return "";
            }

            @Override
            public String visit(Move ir) {
                return format("LD", ir.dest, ir.src);
            }

            @Override
            public String visit(StoreIndexed ir) {
                return "";
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
                    return formatC(instr, (Temp) op1, (Const) op2);
                } else {
                    return formatT(instr, (Temp) op1, (Temp) op2);
                }
            }

            private String format(String instr, Operand op) {
                if (op instanceof Const) {
                    return formatC(instr, (Const) op);
                } else {
                    return formatT(instr, (Temp) op);
                }
            }

            private String formatT(String instr, Temp temp1, Temp temp2) {
                Location alloc1 = allocation.get(temp1);
                Location alloc2 = allocation.get(temp2);
                if (alloc1 instanceof MemoryLocation){
                    throw new UnsupportedOperationException("Left operand cannot be in memory: " + temp1);
                }
                if (alloc2 instanceof MemoryLocation memLoc2){
                    if (memLoc2.offset.isEmpty()){
                        return String.format("%s %s %d", instr, ((RegisterLocation) alloc1).registerName, memLoc2.baseAddress);
                    }
                    return String.format("%s %s %d %s", instr, ((RegisterLocation) alloc1).registerName, memLoc2.baseAddress, memLoc2.offset);
                }else if (alloc2 instanceof RegisterLocation regLoc2){
                    return String.format("%s %s %s", instr, ((RegisterLocation) alloc1).registerName, regLoc2.registerName);
                }
                throw new UnsupportedOperationException("Unknown location type for temp: " + temp2);
            }

            private String formatT(String instr, Temp temp) {
                Location alloc = allocation.get(temp);
                if (alloc instanceof MemoryLocation memLoc){
                    if (memLoc.offset.isEmpty()){
                        return String.format("%s %d", instr, memLoc.baseAddress);
                    }
                    return String.format("%s %d %s", instr, memLoc.baseAddress, memLoc.offset);
                }else if (alloc instanceof RegisterLocation regLoc){
                    return String.format("%s %s", instr, regLoc.registerName);
                }
                throw new UnsupportedOperationException("Unknown location type for temp: " + temp);
            }

            private String formatC(String instr, Temp temp, Const constant){
                Location alloc = allocation.get(temp);
                if (alloc instanceof MemoryLocation memLoc){
                    if (memLoc.offset.isEmpty()){
                        return String.format("%s %d =%s", instr, memLoc.baseAddress, constant.value);
                    }
                    return String.format("%s %d %s =%s", instr, memLoc.baseAddress, memLoc.offset, constant.value);
                }else if (alloc instanceof RegisterLocation regLoc){
                    return String.format("%s %s =%s", instr, regLoc.registerName, constant.value);
                }
                throw new UnsupportedOperationException("Unknown location type for temp: " + temp);
            }

            private String formatC(String instr, Const constant) {
                return String.format("%s =%s", instr, constant.value);
            }

            private String format(String instr, String label) {
                return String.format("%s %s", instr, label);
            }
        };
    }

    @Override
    protected String concat(List<String> assemblies) {
        return String.join("\n", assemblies);
    }
}
