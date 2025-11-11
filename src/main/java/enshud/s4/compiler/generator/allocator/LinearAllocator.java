package enshud.s4.compiler.generator.allocator;

import enshud.s4.compiler.generator.location.MemoryLocation;
import enshud.s4.compiler.generator.location.RegisterLocation;
import enshud.s4.compiler.ir.Ir;
import enshud.s4.compiler.ir.IrFunction;
import enshud.s4.compiler.ir.IrProgram;
import enshud.s4.compiler.ir.operand.Symbol;

import java.util.*;

public class LinearAllocator implements IAllocator {
    enum Register {
        GR0,
        GR1,
        GR2,
        GR3,
        GR4,
        GR5,
    }

    @Override
    public Allocation allocateVariables(IrProgram program) {
        var mainAlloc = computeFunction(program.main);
        var functionAllocs = program.functions.values().stream().map(this::computeFunction).toList();
        var allAllocs = new ArrayList<Allocation>();
        allAllocs.add(mainAlloc);
        allAllocs.addAll(functionAllocs);
        return Allocation.merge(allAllocs);
    }

    private Allocation computeFunction(IrFunction function) {
        Allocation allocation = new Allocation();
        var instructions = function.getInstructions();

        var in = getFirstUse(instructions);
        var out = getFirstUse(instructions.reversed());

        System.out.println("---- Liveness Analysis ----");
        System.out.println(in);
        System.out.println(out);

        var registerAllocations = new HashMap<Symbol, Register>();
        String memoryBase = "VAR_AREA_START";
        var memoryOffset = 0;
        for (Ir instruction : instructions) {
            for (Symbol symbol : in.getOrDefault(instruction, Collections.emptySet())) {
                if (registerAllocations.size() == Register.values().length) {
                    allocation.put(symbol.name, new MemoryLocation(memoryBase, memoryOffset++));
                    continue;
                }
                var availableRegisters = new LinkedHashSet<>(Arrays.asList(Register.values()));
                availableRegisters.removeAll(registerAllocations.values());
                var assignedRegister = availableRegisters.getFirst();
                registerAllocations.put(symbol, assignedRegister);
                allocation.put(symbol.name, new RegisterLocation(assignedRegister.name()));
            }
            for (Symbol symbol : out.getOrDefault(instruction, Collections.emptySet())) {
                registerAllocations.remove(symbol);
            }
        }
        return allocation;
    }

    private Map<Ir, Set<Symbol>> getFirstUse(List<Ir> instructions) {
        Map<Ir, Set<Symbol>> in = new HashMap<>();
        var alreadyIn = new HashSet<Symbol>();
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
}
