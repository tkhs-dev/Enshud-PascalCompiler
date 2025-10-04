package enshud.s2.parser;

import enshud.pascal.ast.AstParseException;
import enshud.pascal.ast.AstParser;
import enshud.pascal.ast.nodes.AstNode;
import enshud.token.FileFormatException;
import enshud.token.Token;
import enshud.token.TokenFile;

import java.io.IOException;
import java.util.List;

public class Parser {

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
    public static void main(final String[] args) throws FileFormatException, IOException {
		// normalの確認
		System.out.println(new Parser().run("data/ts/normal01.ts"));
		System.out.println(new Parser().run("data/ts/normal02.ts"));

		// synerrの確認
		System.out.println(new Parser().run("data/ts/synerr01.ts"));
		System.out.println(new Parser().run("data/ts/synerr02.ts"));

        TokenFile tf = new TokenFile("data/ts/normal20.ts");
        List<Token> tokens = tf.getTokens();
        AstParser parser = new AstParser(tokens);
        AstNode ast = parser.parse();
        System.out.println(ast);

	}

	/**
	 * TODO
	 * 
	 * 開発対象となるParser実行メソッド．
	 * 以下の仕様を満たすこと．
	 * 
	 * 仕様:
	 * 第一引数で指定されたtsファイルを読み込み，構文解析を行う．
	 * 構文が正しい場合は"OK"を，正しくない場合は"Syntax error: line"という文字列とともに，
	 * 最初のエラーを見つけた行の番号を返すこと （例: "Syntax error: line 1"）．
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
            // 構文解析が成功した場合
            return "OK";
        } catch (FileFormatException | IOException e) {
            return "File not found";
        } catch (AstParseException e) {
            return "Syntax error: line " + e.token.line;
        }
    }
}
