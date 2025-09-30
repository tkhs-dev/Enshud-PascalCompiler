package enshud.s1.lexer;

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

/**
 * !!! このコードは編集禁止 !!!
 * 
 * Lexerの単体テストクラス． ここに記述された全ての単体テストが正しく動作するようにLexer.run()メソッドを開発すること．
 * 
 * テストクラスの読み方はTrialTest.javaのコメントを確認すること．
 * 
 * 基本的にはlexer.run("data/pas/XXXX.pas", "tmp/out.ts")を実行し，
 * 出力結果tmp/out.tsの中身がdata/ts/XXXX.tsと完全に一致するか，および 返り値の妥当性を確認している．
 * 
 */
@Timeout(value = 10, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
@TestMethodOrder(MethodName.class)
public class LexerTest {

	private static final String TMP_TS = "tmp/out.ts";

	/**
	 * 各テストの実行前に実行されるメソッド．
	 */
	@BeforeEach
	public void before() throws IOException {
		final Path tmpOutPath = Paths.get(TMP_TS);
		Files.createDirectories(tmpOutPath.getParent());
		Files.deleteIfExists(tmpOutPath);
	}

	@Test
	public void testNormal01() {
		final String out = new Lexer().run("data/pas/normal01.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal01.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal02() {
		final String out = new Lexer().run("data/pas/normal02.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal02.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal03() {
		final String out = new Lexer().run("data/pas/normal03.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal03.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal04() {
		final String out = new Lexer().run("data/pas/normal04.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal04.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal05() {
		final String out = new Lexer().run("data/pas/normal05.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal05.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal06() {
		final String out = new Lexer().run("data/pas/normal06.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal06.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal07() {
		final String out = new Lexer().run("data/pas/normal07.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal07.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal08() {
		final String out = new Lexer().run("data/pas/normal08.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal08.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal09() {
		final String out = new Lexer().run("data/pas/normal09.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal09.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal10() {
		final String out = new Lexer().run("data/pas/normal10.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal10.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal11() {
		final String out = new Lexer().run("data/pas/normal11.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal11.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal12() {
		final String out = new Lexer().run("data/pas/normal12.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal12.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal13() {
		final String out = new Lexer().run("data/pas/normal13.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal13.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal14() {
		final String out = new Lexer().run("data/pas/normal14.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal14.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal15() {
		final String out = new Lexer().run("data/pas/normal15.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal15.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal16() {
		final String out = new Lexer().run("data/pas/normal16.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal16.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal17() {
		final String out = new Lexer().run("data/pas/normal17.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal17.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal18() {
		final String out = new Lexer().run("data/pas/normal18.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal18.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal19() {
		final String out = new Lexer().run("data/pas/normal19.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal19.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal20() {
		final String out = new Lexer().run("data/pas/normal20.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal20.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testSynerr01() {
		final String out = new Lexer().run("data/pas/synerr01.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/synerr01.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testSynerr02() {
		final String out = new Lexer().run("data/pas/synerr02.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/synerr02.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testSynerr03() {
		final String out = new Lexer().run("data/pas/synerr03.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/synerr03.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testSynerr04() {
		final String out = new Lexer().run("data/pas/synerr04.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/synerr04.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testSynerr05() {
		final String out = new Lexer().run("data/pas/synerr05.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/synerr05.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testSynerr06() {
		final String out = new Lexer().run("data/pas/synerr06.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/synerr06.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testSynerr07() {
		final String out = new Lexer().run("data/pas/synerr07.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/synerr07.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testSynerr08() {
		final String out = new Lexer().run("data/pas/synerr08.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/synerr08.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testSemerr01() {
		final String out = new Lexer().run("data/pas/semerr01.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/semerr01.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testSemerr02() {
		final String out = new Lexer().run("data/pas/semerr02.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/semerr02.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testSemerr03() {
		final String out = new Lexer().run("data/pas/semerr03.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/semerr03.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testSemerr04() {
		final String out = new Lexer().run("data/pas/semerr04.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/semerr04.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testSemerr05() {
		final String out = new Lexer().run("data/pas/semerr05.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/semerr05.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testSemerr06() {
		final String out = new Lexer().run("data/pas/semerr06.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/semerr06.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testSemerr07() {
		final String out = new Lexer().run("data/pas/semerr07.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/semerr07.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testSemerr08() {
		final String out = new Lexer().run("data/pas/semerr08.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/semerr08.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	////////////////////////////////////////////////////////////////////////////////

	/**
	 * 入力ファイルが存在しない場合に正しく動作するか
	 */
	@Test
	public void testXInputFileNotFound() throws IOException {
		final String nonExistFile = "data/pas/xxxxxxxx.pas";
		Files.deleteIfExists(Path.of(nonExistFile));

		final String out = new Lexer().run(nonExistFile, TMP_TS);
		assertThat(out).isEqualTo("File not found");
	}

	/**
	 * 出力ファイルが既に存在する場合に正しく動作するか
	 */
	@Test
	public void testXOutputFileAlreadyExists() throws IOException {
		Files.writeString(Path.of(TMP_TS), "xxxxx", StandardOpenOption.CREATE);

		final String out = new Lexer().run("data/pas/normal01.pas", TMP_TS);
		final Path actual = Paths.get(TMP_TS);
		final Path expected = Paths.get("data/ts/normal01.ts");
		assertThat(actual).hasSameTextualContentAs(expected);
		assertThat(out).isEqualTo("OK");
	}

	/**
	 * 指定したファイルに適切に出力しているか（tmp/out.pas以外のファイルを指定しても適切に動作するか）
	 */
	@Test
	public void testXUseParameter() {
		final String zzzPas = "tmp/zzz.pas";
		final String out = new Lexer().run("data/pas/normal01.pas", zzzPas);
		final Path actual = Paths.get(zzzPas);
		final Path expected = Paths.get("data/ts/normal01.ts");
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

		final String pasOrig = "data/pas/normal17.pas";
		final String pasCopy = "tmp/" + rand + ".pas";
		final String pasContent = new String(Files.readAllBytes(Path.of(pasOrig)), charset);
		Files.write(Path.of(pasCopy), pasContent.replaceAll("(?i)gcd", rand).getBytes(charset));

		final String tsOrig = "data/ts/normal17.ts";
		final String tsCopy = "tmp/" + rand + ".ts";
		final String tsContent = new String(Files.readAllBytes(Path.of(tsOrig)), charset);
		Files.write(Path.of(tsCopy), tsContent.replaceAll("(?i)gcd", rand).getBytes(charset));

		try {
			final String out = new Lexer().run(pasCopy, TMP_TS);
			final Path actual = Paths.get(TMP_TS);
			final Path expected = Paths.get(tsCopy);
			assertThat(actual).hasSameTextualContentAs(expected);
			assertThat(out).isEqualTo("OK");
		} finally {
			Files.deleteIfExists(Path.of(pasCopy));
			Files.deleteIfExists(Path.of(tsCopy));
		}
	}
}