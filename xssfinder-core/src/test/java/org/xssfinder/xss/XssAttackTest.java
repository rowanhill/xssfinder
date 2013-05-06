package org.xssfinder.xss;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class XssAttackTest {
    @Test
    public void identifierIsRequiredAndAvailable() {
        // given
        XssAttack xssAttack = new XssAttack("1");

        // when
        String identifier = xssAttack.getIdentifier();

        // then
        assertThat(identifier, is("1"));
    }

    @Test
    public void constructsAttackString() {
        // given
        XssAttack xssAttack = new XssAttack("1");

        // when
        String attackString = xssAttack.getAttackString();

        // then
        assertThat(attackString, is("<script type=\"text/javascript\">if (typeof(window.xssfinder) === \"undefined\"){window.xssfinder = [];}window.xssfinder.push('1');</script>"));
    }
}
