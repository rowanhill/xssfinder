package org.xssfinder.xss;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class XssAttackFactoryTest {
    @Test
    public void createsXssAttack() {
        // given
        XssAttackFactory factory = new XssAttackFactory();

        // when
        XssAttack attack = factory.createXssAttack("1");

        // then
        assertThat(attack, is(not(nullValue())));
    }
}
