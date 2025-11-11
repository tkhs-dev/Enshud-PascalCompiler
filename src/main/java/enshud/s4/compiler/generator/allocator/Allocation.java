package enshud.s4.compiler.generator.allocator;

import enshud.s4.compiler.generator.location.Location;
import enshud.s4.compiler.ir.operand.Symbol;

import java.util.Map;

public class Allocation {
    private Map<String, Location> varMap;
    public Allocation(){
        varMap = new java.util.HashMap<>();
    }
    public void put(String var, Location loc){
        varMap.put(var, loc);
    }
    public Location get(String var){
        if(varMap.containsKey(var)){
            return varMap.get(var);
        }else{
            throw new RuntimeException("Variable not allocated: " + var);
        }
    }

    public Location get(Symbol symbol){
        return get(symbol.toString());
    }

    public static Allocation merge(Allocation a1, Allocation a2){
        Allocation merged = new Allocation();
        for(var entry : a1.varMap.entrySet()){
            merged.put(entry.getKey(), entry.getValue());
        }
        for(var entry : a2.varMap.entrySet()){
            merged.put(entry.getKey(), entry.getValue());
        }
        return merged;
    }

    public static Allocation merge(java.util.List<Allocation> allocations){
        Allocation merged = new Allocation();
        for(var alloc : allocations){
            for(var entry : alloc.varMap.entrySet()){
                merged.put(entry.getKey(), entry.getValue());
            }
        }
        return merged;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(var entry : varMap.entrySet()){
            sb.append(entry.getKey()).append(" -> ").append(entry.getValue().toString()).append("\n");
        }
        return sb.toString();
    }
}
