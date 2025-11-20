package enshud.s4.compiler.generator.impls.casl;

import enshud.s4.compiler.generator.allocator.Allocation;
import enshud.s4.compiler.generator.location.RegisterLocation;
import enshud.s4.compiler.generator.location.StackLocation;
import enshud.s4.compiler.ir.operand.Const;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

abstract class Instruction {
    String label;
    String opcode;

    static final int FRAME_VARIABLE_AREA_START = 4;

    Instruction(String opcode) {
        this.label = "";
        this.opcode = opcode;
    }

    void setLabel(String label) {
        this.label = label;
    }

    protected String build(Allocation allocation){
        return String.format("%s\t%s", label, opcode);
    }

    Optional<String> isRegister(Operand op, Allocation allocation) {
        if (!(op instanceof Temp)) {
            return Optional.empty();
        }
        var alloc = allocation.get((Temp)op);
        if (!(alloc instanceof RegisterLocation)) {
            return Optional.empty();
        }
        return Optional.of(((RegisterLocation) alloc).registerName);
    }

    String requireRegister(Operand op, Allocation allocation) {
        var reg = isRegister(op, allocation);
        if (reg.isEmpty()) {
            throw new RuntimeException("Operand is not allocated to a register: " + op);
        }
        return reg.get();
    }

    int constToInt(Const cnst) {
        if(cnst.value instanceof Integer) {
            return (Integer) cnst.value;
        }else if(cnst.value instanceof Boolean) {
            return ((Boolean) cnst.value) ? 1 : 0;
        } else {
            throw new RuntimeException("Unsupported constant type: " + cnst.value);
        }
    }
}

abstract class SingleAddressInstruction extends Instruction {
    String address;

    SingleAddressInstruction(String opcode, String address) {
        super(opcode);
        this.address = address;
    }

    @Override
    protected String build(Allocation allocation) {
        return String.format("%s %s", super.build(allocation), address);
    }
}

abstract class TwoOperandInstruction extends Instruction {
    Operand op1;
    Operand op2;

    TwoOperandInstruction(String opcode, Operand op1, Operand op2) {
        super(opcode);
        this.op1 = op1;
        this.op2 = op2;
    }

    protected boolean isRegisterAllowed() {
        return true;
    }

    @Override
    protected String build(Allocation allocation) {
        String reg1 = requireRegister(op1, allocation);

        var reg2 = isRegister(op2, allocation);
        if (reg2.isPresent()) {
            if (!isRegisterAllowed()) {
                throw new RuntimeException("Register to register operation is not allowed for opcode: " + opcode);
            }
            return String.format("%s %s,%s", super.build(allocation), reg1, reg2.get());
        }

        Optional<Registers> indexReg = Optional.empty();
        String immediate = "";
        if (op2 instanceof Const cnst) {
            immediate = String.valueOf(constToInt(cnst));
        } else {
            var tempOp2 = (Temp) op2;
            var alloc2 = allocation.get(tempOp2);
            if (alloc2 instanceof StackLocation stackLoc) {
                if(stackLoc.offset > 0){
                    immediate = String.valueOf(stackLoc.offset + FRAME_VARIABLE_AREA_START);
                }else{
                    immediate = String.valueOf(stackLoc.offset);
                }
                indexReg = Optional.of(Registers.FP);
            } else {
                throw new RuntimeException("Unsupported operand type for op2: " + op2);
            }
        }
        if (indexReg.isPresent()) {
            return String.format("%s %s,%s %s", super.build(allocation), reg1, immediate, indexReg.get().name());
        } else {
            return String.format("%s %s,%s", super.build(allocation), reg1, immediate);
        }
    }
}

class LD extends TwoOperandInstruction {
    LD(Operand op1, Operand op2) {
        super("LD", op1, op2);
    }
}

class LAD extends TwoOperandInstruction {
    LAD(Operand op1, Operand op2) {
        super("LDA", op1, op2);
    }

    @Override
    protected boolean isRegisterAllowed() {
        return false;
    }
}

class ST extends TwoOperandInstruction {
    ST(Operand op1, Operand op2) {
        super("ST", op1, op2);
    }

    @Override
    protected boolean isRegisterAllowed() {
        return false;
    }
}

class ADDA extends TwoOperandInstruction {
    ADDA(Operand op1, Operand op2) {
        super("ADDA", op1, op2);
    }
}

class ADDL extends TwoOperandInstruction {
    ADDL(Operand op1, Operand op2) {
        super("ADDL", op1, op2);
    }
}

class SUBA extends TwoOperandInstruction {
    SUBA(Operand op1, Operand op2) {
        super("SUBA", op1, op2);
    }
}

class SUBL extends TwoOperandInstruction {
    SUBL(Operand op1, Operand op2) {
        super("SUBL", op1, op2);
    }
}

class AND extends TwoOperandInstruction {
    AND(Operand op1, Operand op2) {
        super("AND", op1, op2);
    }
}

class OR extends TwoOperandInstruction {
    OR(Operand op1, Operand op2) {
        super("OR", op1, op2);
    }
}

class XOR extends TwoOperandInstruction {
    XOR(Operand op1, Operand op2) {
        super("XOR", op1, op2);
    }
}

class CPA extends TwoOperandInstruction {
    CPA(Operand op1, Operand op2) {
        super("CPA", op1, op2);
    }
}

class CPL extends TwoOperandInstruction {
    CPL(Operand op1, Operand op2) {
        super("CPL", op1, op2);
    }
}

class SLA extends TwoOperandInstruction {
    SLA(Operand op1, Operand op2) {
        super("SLA", op1, op2);
    }

    @Override
    protected boolean isRegisterAllowed() {
        return false;
    }
}

class SLL extends TwoOperandInstruction {
    SLL(Operand op1, Operand op2) {
        super("SLL", op1, op2);
    }

    @Override
    protected boolean isRegisterAllowed() {
        return false;
    }
}

class SRA extends TwoOperandInstruction {
    SRA(Operand op1, Operand op2) {
        super("SRA", op1, op2);
    }

    @Override
    protected boolean isRegisterAllowed() {
        return false;
    }
}

class SRL extends TwoOperandInstruction {
    SRL(Operand op1, Operand op2) {
        super("SRL", op1, op2);
    }

    @Override
    protected boolean isRegisterAllowed() {
        return false;
    }
}

class JPL extends SingleAddressInstruction {
    JPL(String address) {
        super("JPL", address);
    }
}

class JMI extends SingleAddressInstruction {
    JMI(String address) {
        super("JMI", address);
    }
}

class JNZ extends SingleAddressInstruction {
    JNZ(String address) {
        super("JNZ", address);
    }
}

class JZE extends SingleAddressInstruction {
    JZE(String address) {
        super("JZE", address);
    }
}

class JOV extends SingleAddressInstruction {
    JOV(String address) {
        super("JOV", address);
    }
}

class JUMP extends SingleAddressInstruction {
    JUMP(String address) {
        super("JUMP", address);
    }
}

class PUSH extends Instruction {
    Operand op;

    PUSH(Operand op) {
        super("PUSH");
        this.op = op;
    }

    @Override
    protected String build(Allocation allocation) {
        var reg = isRegister(op, allocation);
        if(reg.isPresent()){
            return String.format("%s %s %s", super.build(allocation), 0, reg.get());
        }
        if (op instanceof Const cnst) {
            int value = constToInt(cnst);
            return String.format("%s %d", super.build(allocation), value);
        } else {
            throw new RuntimeException("Unsupported operand type for PUSH: " + op);
        }
    }
}

class POP extends Instruction {
    Operand op;

    POP(Operand op) {
        super("POP");
        this.op = op;
    }

    @Override
    protected String build(Allocation allocation) {
        var reg = isRegister(op, allocation);
        if(reg.isPresent()){
            return String.format("%s %s", super.build(allocation), reg.get());
        }
        throw new RuntimeException("Unsupported operand type for POP: " + op);
    }
}

class RPUSH extends Instruction {
    RPUSH() {
        super("RPUSH");
    }
}

class RPOP extends Instruction {
    RPOP() {
        super("RPOP");
    }
}

class CALL extends SingleAddressInstruction {
    CALL(String address) {
        super("CALL", address);
    }
}

class RET extends Instruction {
    RET() {
        super("RET");
    }
}

class NOP extends Instruction {
    NOP() {
        super("NOP");
    }
}

class InstructionBuilder {
    private final List<Instruction> instructions;
    private final Allocation allocation;

    public InstructionBuilder(Allocation allocation) {
        this.instructions = new ArrayList<>();
        this.allocation = allocation;
    }

    public void add(Instruction instruction) {
        instructions.add(instruction);
    }

    public String build() {
        List<String> lines = instructions.stream().map(instr -> instr.build(allocation)).toList();
        return String.join("\n", lines);
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }
}


