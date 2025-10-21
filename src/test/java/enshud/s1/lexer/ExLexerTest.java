package enshud.s1.lexer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lexerの追加単体テストクラス.
 *
 * 基本的にはlexer.run("data-ex/pas/XXXX.pas", "tmp/out.ts")を実行し，
 * 出力結果tmp/out.tsの中身がdata/ts/XXXX.tsと完全に一致するか，および 返り値の妥当性を確認している．
 *
 */
@Timeout(value = 10, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
@TestMethodOrder(MethodName.class)
public class ExLexerTest {

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
        final String out = new Lexer().run("data-ex/pas/normal01.pas", TMP_TS);
        final Path actual = Paths.get(TMP_TS);
        final Path expected = Paths.get("data-ex/ts/normal01.ts");
        assertThat(actual).hasSameTextualContentAs(expected);
        assertThat(out).isEqualTo("OK");
    }

    @Test
    public void testSemerr01() {
        final String out = new Lexer().run("data-ex/pas/semerr01.pas", TMP_TS);
        final Path actual = Paths.get(TMP_TS);
        final Path expected = Paths.get("data-ex/ts/semerr01.ts");
        assertThat(actual).hasSameTextualContentAs(expected);
        assertThat(out).isEqualTo("OK");
    }

    @Test
    public void testSemerr02() {
        final String out = new Lexer().run("data-ex/pas/semerr02.pas", TMP_TS);
        final Path actual = Paths.get(TMP_TS);
        final Path expected = Paths.get("data-ex/ts/semerr02.ts");
        assertThat(actual).hasSameTextualContentAs(expected);
        assertThat(out).isEqualTo("OK");
    }

    @Test
    public void testSemerr03() {
        final String out = new Lexer().run("data-ex/pas/semerr03.pas", TMP_TS);
        final Path actual = Paths.get(TMP_TS);
        final Path expected = Paths.get("data-ex/ts/semerr03.ts");
        assertThat(actual).hasSameTextualContentAs(expected);
        assertThat(out).isEqualTo("OK");
    }

    @Test
    public void testInvalidIdentifier() {
        final String nonExistFile = "data-ex/pas/lexerr01.pas";

        final String out = new Lexer().run(nonExistFile, TMP_TS);
        assertThat(out).isEqualTo("Invalid identifier: line 2");
    }
}