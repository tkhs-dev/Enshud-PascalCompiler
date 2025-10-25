package enshud.s4.compiler.generator.allocator;

import enshud.s4.compiler.ir.operand.Temp;

import java.util.Map;

public class Allocation {
    private Map<String, String> varMap;
    public Allocation(){
        varMap = new java.util.HashMap<>();
    }
    public void put(String var, String loc){
        varMap.put(var, loc);
    }
    public String get(String var){
        if(varMap.containsKey(var)){
            return varMap.get(var);
        }else{
            throw new RuntimeException("Variable not allocated: " + var);
        }
    }

    public String get(Temp temp){
        return get(temp.toString());
    }
}
