package enshud.s4.compiler.generator.impls.casl;

import enshud.s4.compiler.generator.allocator.Allocation;
import enshud.s4.compiler.generator.allocator.IAllocator;
import enshud.s4.compiler.generator.location.MemoryLocation;
import enshud.s4.compiler.generator.location.RegisterLocation;
import enshud.s4.compiler.generator.location.StackLocation;
import enshud.s4.compiler.ir.instructions.Decl;
import enshud.s4.compiler.ir.instructions.DeclArray;
import enshud.s4.compiler.ir.instructions.Ir;
import enshud.s4.compiler.ir.operand.Temp;
import enshud.s4.compiler.ir.program.IrFunction;
import enshud.s4.compiler.ir.program.IrProgram;

import java.util.*;

public class LinearAllocator implements IAllocator {
    @Override
    public Allocation allocate(IrProgram program) {
        var allocation = new Allocation();
        var variables = new HashMap<String, VariableInfo>();
        constructFrame(variables, allocation, program.main);
        program.functions.values()
                .forEach(f -> constructFrame(variables, allocation, f));
        computeFunction(allocation, variables, program.main);
        program.functions.values()
                .forEach(func -> computeFunction(allocation, variables, func));
        return allocation;
    }

    private void computeFunction(Allocation allocation, Map<String, VariableInfo> variables, IrFunction function) {
        var instructions = function.getInstructions();

        var in = getFirstUse(instructions);
        var out = getFirstUse(instructions.reversed());

        String memoryBase = "VAR_AREA_START";
        var memoryOffset = 0;

        System.out.println("---- Liveness Analysis ----");
        System.out.println(in);
        System.out.println(out);

        var registerAllocations = new HashMap<Symbol, Registers>();
        for (Ir instruction : instructions) {
            for (Symbol symbol : in.getOrDefault(instruction, Collections.emptySet())) {
                if(symbol instanceof Temp){
                    if (registerAllocations.size() == Registers.GENERAL_PURPOSE_REGISTERS.length) {
                        allocation.put(symbol.name, new MemoryLocation(memoryBase, memoryOffset++));
                        continue;
                    }
                    var availableRegisters = new LinkedHashSet<>(Arrays.asList(Registers.GENERAL_PURPOSE_REGISTERS));
                    availableRegisters.removeAll(registerAllocations.values());
                    var assignedRegister = availableRegisters.getFirst();
                    registerAllocations.put(symbol, assignedRegister);
                    allocation.put(symbol.name, new RegisterLocation(assignedRegister.name()));
                }else{
                    if (!variables.containsKey(symbol.name)) {
                        System.out.println(instruction);
                        throw new RuntimeException("Variable used before declaration: " + symbol.name);
                    }
                    var varInfo = variables.get(symbol.name);
                    allocation.put(symbol.name, new StackLocation(varInfo.offset, function.level - varInfo.offset));
                }

            }
            for (Symbol symbol : out.getOrDefault(instruction, Collections.emptySet())) {
                registerAllocations.remove(symbol);
            }
        }
    }

    private Map<Ir, Set<Temp>> getFirstUse(List<Ir> instructions) {
        Map<Ir, Set<Temp>> in = new HashMap<>();
        var alreadyIn = new HashSet<Temp>();
        for (Ir instruction : instructions) {
            var usedTemps = instruction.getUsedTemps();
            for (Symbol symbol : usedTemps) {
                if (!alreadyIn.contains(symbol)) {
                    if (!in.containsKey(instruction)) {
                        in.put(instruction, new HashSet<>());
                    }
                    in.get(instruction).add(symbol);
                    alreadyIn.add(symbol);
                }
            }
        }
        return in;
    }

    private void constructFrame(Map<String, VariableInfo> variables, Allocation allocation, IrFunction function) {
        int frameOffset = 0;

        for (Ir instruction : function.getInstructions()) {
            if(instruction instanceof Decl decl) {
                allocation.setFrameSize(function.name, frameOffset);
                variables.put(decl.varName, new VariableInfo(frameOffset, function.level));
                frameOffset += 1;
            }
            if(instruction instanceof DeclArray declArray){
                allocation.setFrameSize(function.name, frameOffset);
                variables.put(declArray.varName, new VariableInfo(frameOffset, function.level));
                frameOffset += declArray.upperBound - declArray.lowerBound + 1;
            }
        }
    }
}

class VariableInfo {
    public final int offset;
    public final int level;

    public VariableInfo(int offset, int level) {
        this.offset = offset;
        this.level = level;
    }
}