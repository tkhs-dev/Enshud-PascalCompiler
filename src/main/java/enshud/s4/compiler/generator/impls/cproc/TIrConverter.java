package enshud.s4.compiler.generator.impls.cproc;

import enshud.s4.compiler.generator.Register;
import enshud.s4.compiler.generator.allocate.Allocation;
import enshud.s4.compiler.generator.allocate.RegisterAllocator;
import enshud.s4.compiler.generator.impls.cproc.assembly.instructions.*;
import enshud.s4.compiler.generator.impls.cproc.assembly.operand.AssemblyOperand;
import enshud.s4.compiler.generator.impls.cproc.assembly.operand.RegisterOperand;
import enshud.s4.compiler.generator.impls.cproc.tir.instructions.*;
import enshud.s4.compiler.generator.impls.cproc.tir.operand.ImmediateOperand;
import enshud.s4.compiler.generator.impls.cproc.tir.operand.LabelOperand;
import enshud.s4.compiler.generator.impls.cproc.tir.operand.TIROperand;
import enshud.s4.compiler.generator.impls.cproc.tir.operand.VirtualRegister;
import enshud.s4.compiler.generator.impls.cproc.tir.program.TIrFunction;
import enshud.s4.compiler.instruction.InstructionSet;
import enshud.util.SerialManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TIrConverter implements ITIRVisitor<Void> {
    private Allocation<VirtualRegister> allocation;

    private List<AssemblyInstr<?>> instructions;

    private final SerialManager spillLabelManager;

    public TIrConverter() {
        this.instructions = new java.util.ArrayList<>();
        this.spillLabelManager = new SerialManager("SPILL");
    }

    public InstructionSet<AssemblyInstr<?>> convert(TIrFunction func) {
        var spillLabels = new HashSet<String>();
        System.out.println("TIR Function: " + func.getName());
        func.getInstructions().forEach(System.out::println);
        while(true){
            allocation = new RegisterAllocator<>(func, CProcDescription.getInstance()).allocate();
            if (allocation.getSpilled().isEmpty()) {
                break;
            }
            System.out.println("Spilled registers: " + allocation.getSpilled());
            var spills = processSpills(func, allocation.getSpilled());
            spillLabels.addAll(spills);
        }

        System.out.println("Final TIR Instructions after spill code insertion:");
        func.getInstructions().forEach(System.out::println);

        for (var instr : func.getInstructions()) {
            instr.accept(this);
        }

        for (var variable : func.getVariables()) {
            instructions.add(
                    new DS(1).withLabel(variable.getLabel())
            );
        }

        for (var label : spillLabels) {
            instructions.add(
                    new DS(1).withLabel(label)
            );
        }

        return InstructionSet.of(instructions);
    }

    private Set<String> processSpills(TIrFunction func, Set<VirtualRegister> spilled) {
        Set<String> insertedLabels = new HashSet<>();
        for (var vr : spilled) {
            var editor = func.getEditor();
            while(editor.hasNext()) {
                var instr = editor.next();
                var uses = instr.getUse();
                var defs = instr.getDef();

                if (uses.contains(vr) && defs.contains(vr)) {
                    var label = spillLabelManager.New();
                    var reg = new VirtualRegister(label);
                    reg.setSpillCost(2);

                    editor.insertBefore(
                            new Ldd(reg, new LabelOperand(label))
                    );
                    instr.replaceDef(vr, reg);
                    instr.replaceUse(vr, reg);
                    editor.insertAfter(
                            new Std(reg, new LabelOperand(label))
                    );
                    insertedLabels.add(label);
                } else if (uses.contains(vr)) {
                    var label = spillLabelManager.New();
                    var reg = new VirtualRegister(label);
                    reg.setSpillCost(2);

                    editor.insertBefore(
                            new Ldd(reg, new LabelOperand(label))
                    );
                    instr.replaceUse(vr, reg);
                    insertedLabels.add(label);
                } else if (defs.contains(vr)) {
                    var label = spillLabelManager.New();
                    var reg = new VirtualRegister(label);
                    reg.setSpillCost(2);

                    instr.replaceDef(vr, reg);
                    editor.insertAfter(
                            new Std(reg, new LabelOperand(label))
                    );
                    insertedLabels.add(label);
                }
            }
        }
        return insertedLabels;
    }

    @Override
    public Void visit(Add ir) {
        addInstructions(
                ir,
                new ADD(
                        register(convertOperand(ir.getLeft()))
                )
        );
        return null;
    }

    @Override
    public Void visit(And ir) {
        addInstructions(
                ir,
                new AND(
                        register(convertOperand(ir.getLeft()))
                )
        );
        return null;
    }

    @Override
    public Void visit(Cmp ir) {
        addInstructions(
                ir,
                new CMP()
        );
        return null;
    }

    @Override
    public Void visit(Dec ir) {

        addInstructions(
                ir,
                new DEC(
                        register(convertOperand(ir.getOperand()))
                )
        );
        return null;
    }

    @Override
    public Void visit(End ir) {
        return null;
    }

    @Override
    public Void visit(Inc ir) {
        addInstructions(
                ir,
                new INC(
                        register(convertOperand(ir.getOperand()))
                )
        );
        return null;
    }

    @Override
    public Void visit(Jmp ir) {
        var jmpInstr = switch(ir.getType()){
            case UNCONDITIONAL -> new JMP(
                    label(convertOperand(ir.getTarget()))
            );
            case ZERO -> new JPZ(
                    label(convertOperand(ir.getTarget()))
            );
            case CARRY -> new JPC(
                    label(convertOperand(ir.getTarget()))
            );
        };
        addInstructions(ir, jmpInstr);
        return null;
    }

    @Override
    public Void visit(Ldd ir) {
        addInstructions(
                ir,
                new SETIX(label(convertOperand(ir.getLabelOperand()))),
                new LDD(register(convertOperand(ir.getDest())))
        );
        return null;
    }

    @Override
    public Void visit(Ldi ir) {
        addInstructions(
                ir,
                new LDI(
                        register(convertOperand(ir.getDest())),
                        immediate(convertOperand(ir.getValue()))
                )
        );
        return null;
    }

    @Override
    public Void visit(Mov ir) {
        // 割り当てレジスタが同じ場合は何もしない
        var destOp = register(convertOperand(ir.getDest()));
        var srcOp = register(convertOperand(ir.getSrc()));
        if (destOp.register.equals(srcOp.register)) {
            System.out.println("Skipping redundant MOV for register: " + destOp.register);
            return null;
        }

        addInstructions(
                ir,
                new MOV(
                        destOp
                )
        );
        return null;
    }

    @Override
    public Void visit(Mul ir) {
        addInstructions(
                ir,
                new MUL(
                        register(convertOperand(ir.getLeft()))
                )
        );
        return null;
    }

    @Override
    public Void visit(Nop ir) {
        addInstructions(
                ir,
                new NOP()
        );
        return null;
    }

    @Override
    public Void visit(Not ir) {
        addInstructions(
                ir,
                new NOT(
                        register(convertOperand(ir.getOperand()))
                )
        );
        return null;
    }

    @Override
    public Void visit(Or ir) {
        addInstructions(
                ir,
                new OR(
                        register(convertOperand(ir.getLeft()))
                )
        );
        return null;
    }

    @Override
    public Void visit(Std ir) {
        addInstructions(
                ir,
                new SETIX(label(convertOperand(ir.getDest()))),
                new STD(register(convertOperand(ir.getSrc())))
        );
        return null;
    }

    @Override
    public Void visit(Sti ir) {
        addInstructions(
                ir,
                new SETIX(
                        label(convertOperand(ir.getDest()))
                ),
                new STI(
                        immediate(convertOperand(ir.getValue()))
                )
        );
        return null;
    }

    @Override
    public Void visit(Sub ir) {
        addInstructions(
                ir,
                new SUB(
                        register(convertOperand(ir.getLeft()))
                )
        );
        return null;
    }

    private void addInstructions(TIRInstr<?> src, AssemblyInstr<?> ...instructions){
        var instrs = Arrays.stream(instructions).toList();
        src.getLabel().ifPresent(label -> {
            instrs.getFirst().setLabel(label);
        });
        this.instructions.addAll(instrs);
    }

    private RegisterOperand register(AssemblyOperand operand) {
        if (operand instanceof RegisterOperand regOp) {
            return regOp;
        } else {
            throw new IllegalArgumentException("Operand is not a RegisterOperand: " + operand);
        }
    }

    private enshud.s4.compiler.generator.impls.cproc.assembly.operand.ImmediateOperand immediate(AssemblyOperand operand) {
        if (operand instanceof enshud.s4.compiler.generator.impls.cproc.assembly.operand.ImmediateOperand immOp) {
            return immOp;
        } else {
            throw new IllegalArgumentException("Operand is not an ImmediateOperand: " + operand);
        }
    }

    private enshud.s4.compiler.generator.impls.cproc.assembly.operand.LabelOperand label(AssemblyOperand operand) {
        if (operand instanceof enshud.s4.compiler.generator.impls.cproc.assembly.operand.LabelOperand labelOp) {
            return labelOp;
        } else {
            throw new IllegalArgumentException("Operand is not a LabelOperand: " + operand);
        }
    }

    private AssemblyOperand convertOperand(TIROperand operand) {
        switch (operand) {
            case VirtualRegister vr -> {
                Register reg = allocation.getAllocationMap().get(vr);
                if (reg == null) {
                    throw new IllegalStateException("No register allocated for virtual register: " + vr);
                }
                return new RegisterOperand(reg);
            }
            case LabelOperand label -> {
                return new enshud.s4.compiler.generator.impls.cproc.assembly.operand.LabelOperand(label.getLabel());
            }
            case ImmediateOperand imm -> {
                return new enshud.s4.compiler.generator.impls.cproc.assembly.operand.ImmediateOperand(imm.getValue());
            }
            default -> throw new IllegalArgumentException("Unknown operand type: " + operand.getClass());
        }
    }
}
