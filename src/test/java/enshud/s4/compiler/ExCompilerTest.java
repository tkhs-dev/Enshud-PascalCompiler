package enshud.s4.compiler;

import enshud.casl.CaslSimulator;
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
 * Compilerの追加単体テストクラス.
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
public class ExCompilerTest {
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
        final String out = new Compiler().run("data-ex/ts/normal01.ts", TMP_CAS);
        CaslSimulator.run(TMP_CAS, TMP_ANS);
        final Path actual = Paths.get(TMP_ANS);
        final Path expected = Paths.get("data-ex/ans/normal01.ans");
        assertThat(actual).hasSameTextualContentAs(expected);
        assertThat(out).isEqualTo("OK");
    }

    @Test
    public void testSemerr01() {
        final String out = new Compiler().run("data-ex/ts/semerr01.ts", TMP_CAS);
        assertThat(out).isEqualTo("Semantic error: line 8");
    }
}