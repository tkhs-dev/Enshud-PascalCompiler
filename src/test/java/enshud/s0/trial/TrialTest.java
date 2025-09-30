package enshud.s0.trial;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;

/**
 * !!! このコードは編集禁止 !!!
 *
 * Trialの単体テストクラス． 
 * ここに記述された全ての単体テストが正しく動作するようにTrial.run()メソッドを開発すること．
 *
 */

// 各テストは10秒以内に実行が完了すること（無限ループ等の回避のため）．
@Timeout(value = 10, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)

// テストの実行順序は名前順
@TestMethodOrder(MethodName.class)
public class TrialTest {

	/**
	 * 単体テスト1 （正常系）．
	 * 入力ファイルがnormal01.pasの時に正しく5を返すかを確認．
	 */
	@Test
	public void testNormal01() {
		final String out = new Trial().run("data/pas/normal01.pas");
		assertThat(out).isEqualTo("5");
	}

	/**
	 * 単体テスト2 （正常系）．
	 * 入力ファイルがnormal02.pasの時に正しく7を返すかを確認．
	 */
	@Test
	public void testNormal02() {
		final String out = new Trial().run("data/pas/normal02.pas");
		assertThat(out).isEqualTo("7");
	}

	/**
	 * 単体テスト3 （正常系）．
	 * 入力ファイルがnormal03.pasの時に正しく7を返すかを確認．
	 */
	@Test
	public void testNormal03() {
		final String out = new Trial().run("data/pas/normal03.pas");
		assertThat(out).isEqualTo("7");
	}

	/**
	 * 単体テスト4 （異常系）．
	 * 入力ファイルが存在しない場合に"File not found"を返すかを確認．
	 */
	@Test
	public void testXInputFileNotFound() throws IOException {
		final String nonExistFile = "data/pas/xxxxxxxx.pas";
		Files.deleteIfExists(Path.of(nonExistFile));

		final String out = new Trial().run(nonExistFile);
		assertThat(out).isEqualTo("File not found");
	}

	/**
	 * 単体テスト5 （正常系）．
	 * ファイル名や特殊な識別子名をマスクした状態で正しく動作するかを確認．
	 * このテストが落ちる場合は要注意．
	 */
	@Test
	public void testZCheating() throws IOException {
		final Charset charset = StandardCharsets.UTF_8;
		final String rand = "s" + String.valueOf(new Random().nextInt() & Integer.MAX_VALUE);

		final String pasOrig = "data/pas/normal01.pas";
		final String pasCopy = "tmp/" + rand + ".ts";
		final String content = new String(Files.readAllBytes(Path.of(pasOrig)), charset);
		Files.write(Path.of(pasCopy), content.replaceAll("testBasic", rand).getBytes(charset));

		try {
			final String out = new Trial().run(pasCopy);
			assertThat(out).isEqualTo("5");
		} finally {
			Files.deleteIfExists(Path.of(pasCopy));
		}
	}
}