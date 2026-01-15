package enshud.s4.compiler.generator;

import java.util.Optional;

public interface AllocatableRegister {
    public Optional<Register> getPredefined();
    public double getSpillCost();
    // 制限のない完全な汎用レジスタのみが割り当て可能か？
    public boolean needsGeneralPurpose();
}
