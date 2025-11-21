package enshud.s4.compiler.generator.impls.casl;

import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.instructions.*;

public class Ir2TIr {
}

class Ir2TIrVisitor implements IIrVisitor<Void> {
    @Override
    public Void visit(Call ir) {
        return null;
    }

    @Override
    public Void visit(Decl ir) {
        return null;
    }

    @Override
    public Void visit(DeclArray ir) {
        return null;
    }

    @Override
    public Void visit(Jump ir) {
        return null;
    }

    @Override
    public Void visit(Label ir) {
        return null;
    }

    @Override
    public Void visit(LoadIndexed ir) {
        return null;
    }

    @Override
    public Void visit(Move ir) {
        return null;
    }

    @Override
    public Void visit(StoreIndexed ir) {
        return null;
    }

    @Override
    public Void visit(Writeln writeln) {
        return null;
    }

    @Override
    public Void visit(BinaryOp binaryOp) {
        return null;
    }

    @Override
    public Void visit(Load load) {
        return null;
    }

    @Override
    public Void visit(Store store) {
        return null;
    }
}
