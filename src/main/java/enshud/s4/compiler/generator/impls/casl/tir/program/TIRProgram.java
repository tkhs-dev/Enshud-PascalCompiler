package enshud.s4.compiler.generator.impls.casl.tir.program;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TIRProgram {
    public final TIRFunction main;

    public final Map<String, TIRFunction> functions;

    public TIRProgram(TIRFunction main, Map<String, TIRFunction> functions) {
        this.main = main;
        this.functions = functions;
    }

    public List<ConstData<?>> getConstDataList() {
        var constDataList = new ArrayList<ConstData<?>>();
        constDataList.addAll(main.constDataList);
        for (var func : functions.values()) {
            constDataList.addAll(func.constDataList);
        }
        return constDataList;
    }
}
