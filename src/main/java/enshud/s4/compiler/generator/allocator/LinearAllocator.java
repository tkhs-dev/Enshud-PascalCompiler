package enshud.s4.compiler.generator.allocator;

import enshud.s4.compiler.ir.*;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.*;

public class LinearAllocator implements IAllocator {
    enum Register {
        GR0,
        GR1,
        GR2,
        GR3,
        GR4,
        GR5,
        GR6,
    }

    @Override
    public Allocation allocateVariables(IrProgram program) {
        Allocation allocation = new Allocation();
        var instructions = program.getInstructions();

        var in = computeFirstUse(instructions);
        var out = computeFirstUse(instructions.reversed());

        System.out.println(in);
        System.out.println(out);

        var registerAllocations = new HashMap<Temp, Register>();
        var memoryOffset = 0;
        for (Ir instruction : instructions) {
            for (Temp temp : in.getOrDefault(instruction, Collections.emptySet())) {
                if (registerAllocations.size() == Register.values().length) {
                    allocation.put(temp.name, new MemoryLocation(memoryOffset++));
                    continue;
                }
                var availableRegisters = new LinkedHashSet<>(Arrays.asList(Register.values()));
                availableRegisters.removeAll(registerAllocations.values());
                var assignedRegister = availableRegisters.getFirst();
                registerAllocations.put(temp, assignedRegister);
                allocation.put(temp.name, new RegisterLocation(assignedRegister.name()));
            }
            for (Temp temp : out.getOrDefault(instruction, Collections.emptySet())) {
                registerAllocations.remove(temp);
            }
        }
        return allocation;
    }

    private Map<Ir, Set<Temp>> computeFirstUse(List<Ir> instructions) {
        Map<Ir, Set<Temp>> in = new HashMap<>();
        var alreadyIn = new HashSet<Temp>();
        for (Ir instruction : instructions) {
            var usedTemps = instruction.getUsedTemps();
            for (Temp temp : usedTemps) {
                if (!alreadyIn.contains(temp)) {
                    if (!in.containsKey(instruction)) {
                        in.put(instruction, new HashSet<>());
                    }
                    in.get(instruction).add(temp);
                    alreadyIn.add(temp);
                }
            }
        }
        return in;
    }
}
