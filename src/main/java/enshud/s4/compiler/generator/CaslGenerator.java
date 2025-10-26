package enshud.s4.compiler.generator;

import enshud.s4.compiler.generator.allocator.Allocation;
import enshud.s4.compiler.generator.allocator.IAllocator;
import enshud.s4.compiler.ir.*;
import enshud.s4.compiler.ir.operand.Const;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.ArrayList;
import java.util.List;

public class CaslGenerator extends GeneratorBase<String> {
    @Override
    protected void preprocess(IrProgram program) {
        // CASL専用の最適化

    }

    @Override
    protected IAllocator getAllocator() {
        return null;
    }

    @Override
    protected IIrVisitor<String> getConverter(Allocation allocation) {
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
                if (ir.src instanceof Const) {
                    return format("LD", ir.dest, ir.src);
                } else {
                    return format("LD", ir.src, ir.dest);
                }
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
                return String.format("%s %s,%s", instr, op2Str(op1), op2Str(op2));
            }

            private String format(String instr, Operand op) {
                return String.format("%s %s", instr, op2Str(op));
            }

            private String format(String instr, String label) {
                return String.format("%s %s", instr, label);
            }

            private String op2Str(Operand op) {
                return op instanceof Temp s ? allocation.get(s) : op.toString();
            }
        };
    }

    @Override
    protected String concat(List<String> assemblies) {
        return String.join("\n", assemblies);
    }
}
