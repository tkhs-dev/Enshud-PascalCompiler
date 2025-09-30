package enshud.s4.compiler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;

import enshud.casl.CaslSimulator;

/**
 * !!! このコードは編集禁止 !!!
 * 
 * Compilerの単体テストクラス． ここに記述された全ての単体テストが正しく動作するようにCompiler.run()メソッドを開発すること．
 * 
 * テストクラスの読み方はTrialTest.javaのコメントを確認すること．
 * 
 * 基本的には以下2つを実行し， - Compiler.run("data/ts/XXXX.ts", "tmp/out.cas"); -
 * CaslSimulator.run("tmp/out.cas", "tmp/out.com");
 * 
 * 以下を確認する．
 * - XXXXがnormalXXの場合： 各ansファイルの結果と一致するか
 * - XXXXがsynerrXXの場合： "Syntax error: line X"と返すか
 * - XXXXがsemerrXXの場合： "Sematic error: line X"と返すか
 * 
 */
@Timeout(value = 10, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
@TestMethodOrder(MethodName.class)
public class CompilerTest {
	private static final String TMP_CAS = "tmp/out.cas";
	private static final String TMP_ANS = "tmp/out.ans";

	@BeforeEach
	public void before() throws IOException {
		final Path tmpOutCasPath = Paths.get(TMP_CAS);
		final Path tmpOutAnsPath = Paths.get(TMP_CAS);
		Files.createDirectories(tmpOutCasPath.getParent());
		Files.createDirectories(tmpOutAnsPath.getParent());
		Files.deleteIfExists(tmpOutCasPath);
		Files.deleteIfExists(tmpOutAnsPath);
	}

	@Test
	public void testNormal01() {
		final String out = new Compiler().run("data/ts/normal01.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal01.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal02() {
		final String out = new Compiler().run("data/ts/normal02.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal02.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal03() {
		final String out = new Compiler().run("data/ts/normal03.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal03.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal04() {
		final String out = new Compiler().run("data/ts/normal04.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal04.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal05() {
		final String out = new Compiler().run("data/ts/normal05.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal05.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal06() {
		final String out = new Compiler().run("data/ts/normal06.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal06.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal07() {
		final String out = new Compiler().run("data/ts/normal07.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal07.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal08() {
		final String out = new Compiler().run("data/ts/normal08.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal08.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal09() {
		final String out = new Compiler().run("data/ts/normal09.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal09.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal10() {
		final String out = new Compiler().run("data/ts/normal10.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal10.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal11() {
		final String out = new Compiler().run("data/ts/normal11.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal11.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal12() {
		final String out = new Compiler().run("data/ts/normal12.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal12.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal13() {
		final String out = new Compiler().run("data/ts/normal13.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal13.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal14() {
		final String out = new Compiler().run("data/ts/normal14.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal14.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal15() {
		final String out = new Compiler().run("data/ts/normal15.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal15.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal16() {
		final String out = new Compiler().run("data/ts/normal16.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal16.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal17() {
		final String out = new Compiler().run("data/ts/normal17.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal17.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal18() {
		final String out = new Compiler().run("data/ts/normal18.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal18.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal19() {
		final String out = new Compiler().run("data/ts/normal19.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal19.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal20() {
		final String out = new Compiler().run("data/ts/normal20.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal20.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testSynerr01() {
		final String out = new Compiler().run("data/ts/synerr01.ts", TMP_CAS);
		assertThat(out).isEqualTo("Syntax error: line 1");
	}

	@Test
	public void testSynerr02() {
		final String out = new Compiler().run("data/ts/synerr02.ts", TMP_CAS);
		assertThat(out).isEqualTo("Syntax error: line 3");
	}

	@Test
	public void testSynerr03() {
		final String out = new Compiler().run("data/ts/synerr03.ts", TMP_CAS);
		assertThat(out).isEqualTo("Syntax error: line 8");
	}

	@Test
	public void testSynerr04() {
		final String out = new Compiler().run("data/ts/synerr04.ts", TMP_CAS);
		assertThat(out).isEqualTo("Syntax error: line 10");
	}

	@Test
	public void testSynerr05() {
		final String out = new Compiler().run("data/ts/synerr05.ts", TMP_CAS);
		assertThat(out).isEqualTo("Syntax error: line 11");
	}

	@Test
	public void testSynerr06() {
		final String out = new Compiler().run("data/ts/synerr06.ts", TMP_CAS);
		assertThat(out).isEqualTo("Syntax error: line 13");
	}

	@Test
	public void testSynerr07() {
		final String out = new Compiler().run("data/ts/synerr07.ts", TMP_CAS);
		assertThat(out).isEqualTo("Syntax error: line 30");
	}

	@Test
	public void testSynerr08() {
		final String out = new Compiler().run("data/ts/synerr08.ts", TMP_CAS);
		assertThat(out).isEqualTo("Syntax error: line 31");
	}

	////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testSemerr01() {
		final String out = new Compiler().run("data/ts/semerr01.ts", TMP_CAS);
		assertThat(out).isEqualTo("Semantic error: line 6");
	}

	@Test
	public void testSemerr02() {
		final String out = new Compiler().run("data/ts/semerr02.ts", TMP_CAS);
		assertThat(out).isEqualTo("Semantic error: line 23");
	}

	@Test
	public void testSemerr03() {
		final String out = new Compiler().run("data/ts/semerr03.ts", TMP_CAS);
		assertThat(out).isEqualTo("Semantic error: line 29");
	}

	@Test
	public void testSemerr04() {
		final String out = new Compiler().run("data/ts/semerr04.ts", TMP_CAS);
		assertThat(out).isEqualTo("Semantic error: line 29");
	}

	@Test
	public void testSemerr05() {
		final String out = new Compiler().run("data/ts/semerr05.ts", TMP_CAS);
		assertThat(out).isEqualTo("Semantic error: line 30");
	}

	@Test
	public void testSemerr06() {
		final String out = new Compiler().run("data/ts/semerr06.ts", TMP_CAS);
		assertThat(out).isEqualTo("Semantic error: line 31");
	}

	@Test
	public void testSemerr07() {
		final String out = new Compiler().run("data/ts/semerr07.ts", TMP_CAS);
		assertThat(out).isEqualTo("Semantic error: line 31");
	}

	@Test
	public void testSemerr08() {
		final String out = new Compiler().run("data/ts/semerr08.ts", TMP_CAS);
		assertThat(out).isEqualTo("Semantic error: line 34");
	}

	////////////////////////////////////////////////////////////////////////////////

	/**
	 * 入力ファイルが存在しない場合に正しく動作するか
	 */
	@Test
	public void testXInputFileNotFound() throws IOException {
		final String nonExistFile = "data/ts/xxxxxxxx.ts";
		Files.deleteIfExists(Path.of(nonExistFile));

		final String out = new Compiler().run(nonExistFile, TMP_CAS);
		assertThat(out).isEqualTo("File not found");
	}

	/**
	 * 出力ファイルが既に存在する場合に正しく動作するか
	 */
	@Test
	public void testXOutputFileAlreadyExists() throws IOException {
		Files.writeString(Path.of(TMP_CAS), "xxxxx", StandardOpenOption.CREATE);

		final String out = new Compiler().run("data/ts/normal01.ts", TMP_CAS);
		CaslSimulator.run(TMP_CAS, TMP_ANS);
		final Path actual = Paths.get(TMP_ANS);
		final Path expected = Paths.get("data/ans/normal01.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	/**
	 * 指定したファイルに適切に出力しているか（tmp/out.cas, tmp/out.ans以外のファイルを指定しても適切に動作するか）
	 */
	@Test
	public void testXUseParameter() {
		final String zzzCas = "tmp/zzz.cas";
		final String zzzAns = "tmp/zzz.ans";

		final String out = new Compiler().run("data/ts/normal01.ts", zzzCas);
		CaslSimulator.run(zzzCas, zzzAns);
		final Path actual = Paths.get(zzzAns);
		final Path expected = Paths.get("data/ans/normal01.ans");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	/**
	 * ファイル名や特殊な識別子名をマスクした状態で正しく動作するかを確認．
	 * このテストが落ちる場合は要注意．
	 */
	@Test
	public void testZCheating() throws IOException {
		final Charset charset = StandardCharsets.UTF_8;
		final String rand = "s" + String.valueOf(new Random().nextInt() & Integer.MAX_VALUE);

		final String tsOrig = "data/ts/normal17.ts";
		final String tsCopy = "tmp/" + rand + ".ts";
		final String tsContent = new String(Files.readAllBytes(Path.of(tsOrig)), charset);
		Files.write(Path.of(tsCopy), tsContent.replaceAll("(?i)gcd", rand).getBytes(charset));

		final String ansOrig = "data/ans/normal17.ans";
		final String ansCopy = "tmp/" + rand + ".ans";
		final String ansContent = new String(Files.readAllBytes(Path.of(ansOrig)), charset);
		Files.write(Path.of(ansCopy), ansContent.replaceAll("(?i)gcd", rand).getBytes(charset));

		try {
			final String out = new Compiler().run(tsCopy, TMP_CAS);
			CaslSimulator.run(TMP_CAS, TMP_ANS);

			final Path actual = Paths.get(TMP_ANS);
			final Path expected = Paths.get(ansCopy);
			assertThat(actual).hasSameTextualContentAs(expected);
			assertThat(out).isEqualTo("OK");
		} finally {
			Files.deleteIfExists(Path.of(ansCopy));
			Files.deleteIfExists(Path.of(tsCopy));
		}
	}
}