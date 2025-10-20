package enshud.pascal.ast;

import enshud.pascal.ast.nodes.*;
import enshud.pascal.ast.nodes.expr.*;
import enshud.pascal.ast.nodes.stmt.*;

public interface IAstVisitor<T> {
    // プログラム全体
    T visit(ProgramNode node);

    // ブロック
    T visit(BlockNode node);

    // 変数宣言
    T visit(VarDeclNode node);

    // 副プログラム宣言
    T visit(SubProgDeclNode node);

    T visit(SubProgHeaderNode node);

    T visit(FormalParamNode node);

    // 型
    T visit(ArrayTypeNode node);

    T visit(StandardTypeNode node);

    // 文
    T visit(CompoundStmtNode node);

    T visit(AssignmentNode node);

    T visit(CallNode node);

    T visit(WriteNode node);

    T visit(ReadNode node);

    T visit(IfNode node);

    T visit(WhileNode node);

    // 式
    T visit(BinaryOpNode node);

    T visit(ConstNode node);

    T visit(UnaryOpNode node);

    T visit(StandardVarNode node);

    T visit(IndexedVarNode node);
}
