package enshud.s3.checker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checkerの追加単体テストクラス.
 * <p>
 * 基本的にはchecker.run("data/ts/XXXX.ts")を実行し，以下を確認している．
 * - XXXXがnormalXXの場合： "OK"と返すか
 * - XXXXがsynerrXXの場合： "Syntax error: line X"と返すか
 * - XXXXがsemerrXXの場合： "Sematic error: line X"と返すか
 */
@Timeout(value = 10, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
@TestMethodOrder(MethodName.class)
public class ExCheckerrTest {
    /**
     * 各テストの実行前に実行されるメソッド．
     */
    @BeforeEach
    public void before() throws IOException {
    }

    @Test
    public void testNormal01() {
        final String out = new Checker().run("data/ts/normal01.ts");
        assertThat(out).isEqualTo("OK");
    }

    @Test
    public void testSemerr01() {
        final String out = new Checker().run("data/ts/semerr01.ts");
        assertThat(out).isEqualTo("Semantic error: line 8");
    }
}