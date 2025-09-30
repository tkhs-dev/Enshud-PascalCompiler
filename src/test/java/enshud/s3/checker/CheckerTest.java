package enshud.s3.checker;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;

/**
 * !!! このコードは編集禁止 !!!
 * 
 * Checkerの単体テストクラス． ここに記述された全ての単体テストが正しく動作するようにChecker.run()メソッドを開発すること．
 * 
 * テストクラスの読み方はTrialTest.javaのコメントを確認すること．
 * 
 * 基本的にはchecker.run("data/ts/XXXX.ts")を実行し，以下を確認している．
 *  - XXXXがnormalXXの場合： "OK"と返すか
 *  - XXXXがsynerrXXの場合： "Syntax error: line X"と返すか
 *  - XXXXがsemerrXXの場合： "Sematic error: line X"と返すか
 * 
 */
@Timeout(value = 10, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
@TestMethodOrder(MethodName.class)
public class CheckerTest {

	@BeforeEach
	public void before() {
	}

	@Test
	public void testNormal01() {
		final String out = new Checker().run("data/ts/normal01.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal02() {
		final String out = new Checker().run("data/ts/normal02.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal03() {
		final String out = new Checker().run("data/ts/normal03.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal04() {
		final String out = new Checker().run("data/ts/normal04.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal05() {
		final String out = new Checker().run("data/ts/normal05.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal06() {
		final String out = new Checker().run("data/ts/normal06.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal07() {
		final String out = new Checker().run("data/ts/normal07.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal08() {
		final String out = new Checker().run("data/ts/normal08.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal09() {
		final String out = new Checker().run("data/ts/normal09.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal10() {
		final String out = new Checker().run("data/ts/normal10.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal11() {
		final String out = new Checker().run("data/ts/normal11.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal12() {
		final String out = new Checker().run("data/ts/normal12.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal13() {
		final String out = new Checker().run("data/ts/normal13.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal14() {
		final String out = new Checker().run("data/ts/normal14.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal15() {
		final String out = new Checker().run("data/ts/normal15.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal16() {
		final String out = new Checker().run("data/ts/normal16.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal17() {
		final String out = new Checker().run("data/ts/normal17.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal18() {
		final String out = new Checker().run("data/ts/normal18.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal19() {
		final String out = new Checker().run("data/ts/normal19.ts");
		assertThat(out).isEqualTo("OK");
	}

	@Test
	public void testNormal20() {
		final String out = new Checker().run("data/ts/normal20.ts");
		assertThat(out).isEqualTo("OK");
	}

	////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testSynerr01() {
		final String out = new Checker().run("data/ts/synerr01.ts");
		assertThat(out).isEqualTo("Syntax error: line 1");
	}

	@Test
	public void testSynerr02() {
		final String out = new Checker().run("data/ts/synerr02.ts");
		assertThat(out).isEqualTo("Syntax error: line 3");
	}

	@Test
	public void testSynerr03() {
		final String out = new Checker().run("data/ts/synerr03.ts");
		assertThat(out).isEqualTo("Syntax error: line 8");
	}

	@Test
	public void testSynerr04() {
		final String out = new Checker().run("data/ts/synerr04.ts");
		assertThat(out).isEqualTo("Syntax error: line 10");
	}

	@Test
	public void testSynerr05() {
		final String out = new Checker().run("data/ts/synerr05.ts");
		assertThat(out).isEqualTo("Syntax error: line 11");
	}

	@Test
	public void testSynerr06() {
		final String out = new Checker().run("data/ts/synerr06.ts");
		assertThat(out).isEqualTo("Syntax error: line 13");
	}

	@Test
	public void testSynerr07() {
		final String out = new Checker().run("data/ts/synerr07.ts");
		assertThat(out).isEqualTo("Syntax error: line 30");
	}

	@Test
	public void testSynerr08() {
		final String out = new Checker().run("data/ts/synerr08.ts");
		assertThat(out).isEqualTo("Syntax error: line 31");
	}

	////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testSemerr01() {
		final String out = new Checker().run("data/ts/semerr01.ts");
		assertThat(out).isEqualTo("Semantic error: line 6");
	}

	@Test
	public void testSemerr02() {
		final String out = new Checker().run("data/ts/semerr02.ts");
		assertThat(out).isEqualTo("Semantic error: line 23");
	}

	@Test
	public void testSemerr03() {
		final String out = new Checker().run("data/ts/semerr03.ts");
		assertThat(out).isEqualTo("Semantic error: line 29");
	}

	@Test
	public void testSemerr04() {
		final String out = new Checker().run("data/ts/semerr04.ts");
		assertThat(out).isEqualTo("Semantic error: line 29");
	}

	@Test
	public void testSemerr05() {
		final String out = new Checker().run("data/ts/semerr05.ts");
		assertThat(out).isEqualTo("Semantic error: line 30");
	}

	@Test
	public void testSemerr06() {
		final String out = new Checker().run("data/ts/semerr06.ts");
		assertThat(out).isEqualTo("Semantic error: line 31");
	}

	@Test
	public void testSemerr07() {
		final String out = new Checker().run("data/ts/semerr07.ts");
		assertThat(out).isEqualTo("Semantic error: line 31");
	}

	@Test
	public void testSemerr08() {
		final String out = new Checker().run("data/ts/semerr08.ts");
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

		final String out = new Checker().run(nonExistFile);
		assertThat(out).isEqualTo("File not found");
	}

	/**
	 * ファイル名や特殊な識別子名をマスクした状態で正しく動作するかを確認．
	 * このテストが落ちる場合は要注意．
	 */
	@Test
	public void testZCheating() throws IOException {
		final Charset charset = StandardCharsets.UTF_8;
		final String rand = "s" + String.valueOf(new Random().nextInt() & Integer.MAX_VALUE);

		final String tsOrig = "data/ts/semerr08.ts";
		final String tsCopy = "tmp/" + rand + ".ts";

		final String content = new String(Files.readAllBytes(Path.of(tsOrig)), charset);
		Files.write(Path.of(tsCopy), content.replaceAll("testAdv", rand).getBytes(charset));

		try {
			final String out = new Checker().run(tsCopy);
			assertThat(out).isEqualTo("Semantic error: line 34");
		} finally {
			Files.deleteIfExists(Path.of(tsCopy));
		}
	}
}