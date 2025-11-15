package enshud.s3.checker;

import enshud.pascal.ast.AstParseException;
import enshud.pascal.ast.AstParser;
import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.*;
import enshud.pascal.ast.nodes.expr.*;
import enshud.pascal.ast.nodes.stmt.*;
import enshud.pascal.type.Type;
import enshud.token.FileFormatException;
import enshud.token.Token;
import enshud.token.TokenFile;
import enshud.util.SerialManager;

import java.io.IOException;
import java.util.*;

public class Checker {

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// normalの確認
		System.out.println(new Checker().run("data/ts/normal01.ts"));
		System.out.println(new Checker().run("data/ts/normal02.ts"));

		// synerrの確認
		System.out.println(new Checker().run("data/ts/synerr01.ts"));
		System.out.println(new Checker().run("data/ts/synerr02.ts"));

		// semerrの確認
		System.out.println(new Checker().run("data/ts/semerr01.ts"));
		System.out.println(new Checker().run("data/ts/semerr02.ts"));
	}

	/**
	 * TODO
	 * 
	 * 開発対象となるChecker実行メソッド．
	 * 以下の仕様を満たすこと．
	 * 
	 * 仕様:
	 * 第一引数で指定されたtsファイルを読み込み，意味解析を行う．
	 * 意味的に正しい場合は"OK"を，正しくない場合は"Semantic error: line"という文字列とともに，
	 * 最初のエラーを見つけた行の番号を返すこと （例: "Semantic error: line 6"）．
	 * また，構文的なエラーが含まれる場合もエラーメッセージを返すこと （例： "Syntax error: line 1"）．
	 * 入力ファイル内に複数のエラーが含まれる場合は，最初に見つけたエラーのみを返すこと．
	 * 入力ファイルが見つからない場合は"File not found"を返すこと．
	 * 
	 * @param inputFileName 入力tsファイル名
	 */
	public String run(final String inputFileName) {
        try {
            TokenFile tf = new TokenFile(inputFileName);
            List<Token> tokens = tf.getTokens();
            AstParser parser = new AstParser(tokens);
            AstNode ast = parser.parse();
            SemanticCheckVisitor visitor = new SemanticCheckVisitor();
            ast.accept(visitor);
            // 構文解析が成功した場合
            return "OK";
        } catch (FileFormatException | IOException e) {
            return "File not found";
        } catch (AstParseException e) {
            return "Syntax error: line " + e.token.line;
        } catch (SemanticException e) {
            return "Semantic error: line " + e.line;
        }
    }
}


