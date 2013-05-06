package org.xssfinder.xss;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class XssGeneratorTest {
    @Test
    public void createsAttackStartingWithOneAsIdentifier() {
        // given
        XssAttackFactory mockFactory = mock(XssAttackFactory.class);
        XssAttack mockAttack = mock(XssAttack.class);
        when(mockFactory.createXssAttack("1")).thenReturn(mockAttack);
        XssGenerator generator = new XssGenerator(mockFactory);

        // when
        XssAttack attack = generator.createXssAttack();

        // then
        assertThat(attack, is(mockAttack));
    }

    @Test
    public void identifiersIncrement() {
        // given
        XssAttackFactory mockFactory = mock(XssAttackFactory.class);
        XssAttack mockAttack1 = mock(XssAttack.class);
        when(mockFactory.createXssAttack("1")).thenReturn(mockAttack1);
        XssAttack mockAttack2 = mock(XssAttack.class);
        when(mockFactory.createXssAttack("2")).thenReturn(mockAttack2);
        XssGenerator generator = new XssGenerator(mockFactory);

        // when
        XssAttack attack1 = generator.createXssAttack();
        XssAttack attack2 = generator.createXssAttack();

        // then
        assertThat(attack1, is(mockAttack1));
        assertThat(attack2, is(mockAttack2));

    }
}
