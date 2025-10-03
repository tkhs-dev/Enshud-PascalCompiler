package enshud.pascal.ast;

import enshud.pascal.ast.nodes.*;
import enshud.pascal.ast.nodes.expr.*;
import enshud.pascal.ast.nodes.stmt.Assignment;
import enshud.pascal.ast.nodes.stmt.Call;
import enshud.pascal.ast.nodes.stmt.Read;
import enshud.pascal.ast.nodes.stmt.Write;

public interface AstVisitor<T> {
    // プログラム全体
    T visit(Program node);

    // ブロック
    T visit(Block node);

    // 変数宣言
    T visit(VarDecl node);

    // 副プログラム宣言
    T visit(SubProgDecl node);

    T visit(SubProgHeader node);

    T visit(FormalParam node);

    // 型
    T visit(ArrayType node);

    T visit(StandardType node);

    // 文
    T visit(CompoundStmt node);

    T visit(Assignment node);

    T visit(Call node);

    T visit(Write node);

    T visit(Read node);

    T visit(If node);

    T visit(While node);

    // 式
    T visit(BinaryOp node);
    T visit(Const node);
    T visit(UnaryOp node);

    T visit(StandardVar node);

    T visit(IndexedVar node);
}
