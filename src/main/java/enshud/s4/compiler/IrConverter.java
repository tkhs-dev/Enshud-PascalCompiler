package enshud.s4.compiler;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.*;
import enshud.pascal.ast.nodes.expr.*;
import enshud.pascal.ast.nodes.stmt.*;
import enshud.s4.compiler.ir.IrProgram;

public class IrConverter {
    public static IrProgram convert(AstNode astProgram) {
        IrConverterVisitor visitor = new IrConverterVisitor();
        return astProgram.accept(visitor);
    }
}

class IrConverterVisitor implements IAstVisitor<IrProgram> {
    private IrProgram visitAll(AstNode node) {
        node.getChildren().stream().map(child -> child.accept(this)).toList();
    }

    @Override
    public IrProgram visit(Program node) {
        var program1 = node.block.accept(this);
        var program2 = node.statement.accept(this);
        return IrProgram.merge(program1, program2);
    }

    @Override
    public IrProgram visit(Block node) {
        return node.accept(this);
    }

    @Override
    public IrProgram visit(VarDecl node) {
        var program = node.accept(this);

    }

    @Override
    public IrProgram visit(SubProgDecl node) {
        return null;
    }

    @Override
    public IrProgram visit(SubProgHeader node) {
        return null;
    }

    @Override
    public IrProgram visit(FormalParam node) {
        return null;
    }

    @Override
    public IrProgram visit(ArrayType node) {
        return null;
    }

    @Override
    public IrProgram visit(StandardType node) {
        return null;
    }

    @Override
    public IrProgram visit(CompoundStmt node) {
        return null;
    }

    @Override
    public IrProgram visit(Assignment node) {
        return null;
    }

    @Override
    public IrProgram visit(Call node) {
        return null;
    }

    @Override
    public IrProgram visit(Write node) {
        return null;
    }

    @Override
    public IrProgram visit(Read node) {
        return null;
    }

    @Override
    public IrProgram visit(If node) {
        return null;
    }

    @Override
    public IrProgram visit(While node) {
        return null;
    }

    @Override
    public IrProgram visit(BinaryOp node) {
        return null;
    }

    @Override
    public IrProgram visit(Const node) {
        return null;
    }

    @Override
    public IrProgram visit(UnaryOp node) {
        return null;
    }

    @Override
    public IrProgram visit(StandardVar node) {
        return null;
    }

    @Override
    public IrProgram visit(IndexedVar node) {
        return null;
    }
}
