package org.xssfinder.xss;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class XssGeneratorTest {
    @Test
    public void generatesScriptToAddInfoToWindow() {
        // given
        XssGenerator generator = new XssGenerator();

        // when
        String xss = generator.createXssString();

        // then
        assertThat(xss, is("<script type=\"text/javascript\">if (typeof(window.xssfinder) === \"undefined\"){window.xssfinder = [];}window.xssfinder.push(1);</script>"));
    }

    @Test
    public void xssIdIncrements() {
        // given
        XssGenerator generator = new XssGenerator();

        // when
        generator.createXssString();
        String xss = generator.createXssString();

        // then
        assertThat(xss, is("<script type=\"text/javascript\">if (typeof(window.xssfinder) === \"undefined\"){window.xssfinder = [];}window.xssfinder.push(2);</script>"));
    }
}
