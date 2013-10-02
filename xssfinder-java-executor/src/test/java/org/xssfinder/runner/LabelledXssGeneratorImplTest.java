package org.xssfinder.runner;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.xss.XssAttack;
import org.xssfinder.xss.XssGenerator;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LabelledXssGeneratorImplTest {
    private static final String LABEL = "someLabel";
    private static final String XSS_TEXT = "some XSS";
    private static final String ATTACK_ID = "attackId";

    @Mock
    private XssGenerator mockXssGenerator;
    @Mock
    private XssAttack mockAttack;

    @InjectMocks
    private LabelledXssGeneratorImpl labelledXssGenerator;

    @Before
    public void setUp() {
        when(mockAttack.getAttackString()).thenReturn(XSS_TEXT);
        when(mockAttack.getIdentifier()).thenReturn(ATTACK_ID);
        when(mockXssGenerator.createXssAttack()).thenReturn(mockAttack);
    }

    @Test
    public void xssIsGenerated() {
        // when
        String xss = labelledXssGenerator.getXssAttackTextForLabel(LABEL);

        // then
        assertThat(xss, is(XSS_TEXT));
    }

    @Test
    public void generatedXssIdIsAvailableKeyedByLabel() {
        // given
        labelledXssGenerator.getXssAttackTextForLabel(LABEL);

        // when
        Map<String,String> labelToXssId = labelledXssGenerator.getLabelsToAttackIds();

        // then
        Map<String, String> expectedLabelToXssId = ImmutableMap.of(LABEL, ATTACK_ID);
        assertThat(labelToXssId, is(expectedLabelToXssId));
    }
}
