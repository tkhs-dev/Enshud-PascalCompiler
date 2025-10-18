package enshud.s4.compiler.ir;

public class LoadIndexed extends Ir {
    public final String targetVar;
    public final String arrayVar;
    public final String indexVar;

    public LoadIndexed(String targetVar, String arrayVar, String indexVar) {
        this.targetVar = targetVar;
        this.arrayVar = arrayVar;
        this.indexVar = indexVar;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
