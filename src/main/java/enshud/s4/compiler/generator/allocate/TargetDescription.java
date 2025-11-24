package enshud.s4.compiler.generator.allocate;

import enshud.s4.compiler.generator.Register;

import java.util.List;

public interface TargetDescription {

    /** すべてのレジスタ */
    List<Register> getAllRegisters();

    /** レジスタ割当が使用してよい一般目的レジスタ */
    List<Register> getAllocatableRegisters();

    /** 呼び出し側が保存すべきレジスタ */
    List<Register> getCallerSaved();

    /** 呼び出される側が保存すべきレジスタ */
    List<Register> getCalleeSaved();

    /** allocator から除外すべきレジスタ */
    List<Register> getReservedRegisters();


    /** scratch register候補（spill 生成時に使える一時レジスタ） */
    List<Register> getScratchCandidates();
}