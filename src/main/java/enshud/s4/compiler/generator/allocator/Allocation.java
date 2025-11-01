package enshud.s4.compiler.generator.allocator;

import enshud.s4.compiler.ir.operand.Temp;

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

    public Location get(Temp temp){
        return get(temp.toString());
    }
}
