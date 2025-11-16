package enshud.s4.compiler;

import enshud.casl.CaslSimulator;
import enshud.pascal.ast.AstParseException;
import enshud.pascal.ast.AstParser;
import enshud.pascal.ast.nodes.AstNode;
import enshud.s1.lexer.Lexer;
import enshud.s3.checker.SemanticCheckVisitor;
import enshud.s4.compiler.generator.CaslGenerator;
import enshud.s4.compiler.ir.IrConverter;
import enshud.token.FileFormatException;
import enshud.token.Token;
import enshud.token.TokenFile;

import java.io.IOException;
import java.util.List;

public class Compiler {

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
        // tsを生成
        new Lexer().run("data-ex/pas/test.pas", "tmp/out.ts");
		// Compilerを実行してcasを生成する
		System.out.println(new Compiler().run("tmp/out.ts", "tmp/out.cas"));

		// 上記casを，CASLアセンブラ & COMETシミュレータで実行する
		CaslSimulator.run("tmp/out.cas", "tmp/out.ans");
	}

	/**
	 * TODO
	 * 
	 * 開発対象となるCompiler実行メソッド．
	 * 以下の仕様を満たすこと．
	 * 
	 * 仕様:
	 * 第一引数で指定されたtsファイルを読み込み，CASL IIプログラムにコンパイルする．
	 * コンパイル結果のCASL IIプログラムは第二引数で指定されたcasファイルに書き出し"OK"という文字列を返すこと．
	 * 構文的もしくは意味的なエラーを発見した場合はエラーメッセージを返すこと．
	 * （エラーメッセージの内容はChecker.run()の出力に準ずる．）
	 * 入力ファイルが見つからない場合は"File not found"を返すこと．
	 * 
	 * @param inputFileName 入力tsファイル名
	 * @param outputFileName 出力casファイル名
	 */
	public String run(final String inputFileName, final String outputFileName) {
        try {
            TokenFile tf = new TokenFile(inputFileName);
            List<Token> tokens = tf.getTokens();
            AstParser parser = new AstParser(tokens);
            AstNode ast = parser.parse();
            SemanticCheckVisitor visitor = new SemanticCheckVisitor();
            ast.accept(visitor);
            var prog = IrConverter.convert(ast);
            System.out.println("---- IR Code ----");
            prog.getInstructions().forEach(System.out::println);
            System.out.println("---- CASL II Code ----");
			CaslGenerator generator = new CaslGenerator();
			String caslCode = generator.generate(prog);
			System.out.println(caslCode);
            // ファイルに書き出し
            java.nio.file.Files.writeString(java.nio.file.Path.of(outputFileName), caslCode);
            // 構文解析が成功した場合
            return "OK";
        } catch (FileFormatException | IOException e) {
            return "File not found";
        } catch (AstParseException e) {
            return "Syntax error: line " + e.token.line;
        }
	}
}
