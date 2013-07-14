package org.xssfinder.runner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@Ignore("Need to work out how to replace LabelledXssGenerator")
@RunWith(MockitoJUnitRunner.class)
public class LabelledXssGeneratorImplTest {
    private static final String LABEL = "someLabel";
    private static final String XSS_TEXT = "some XSS";
    private static final String ATTACK_ID = "attackId";

    /*
    @Mock
    private XssGenerator mockXssGenerator;
    @Mock
    private XssJournal mockJournal;
    @Mock
    private XssDescriptorFactory mockDescriptorFactory;
    @Mock
    private PageTraversal mockTraversal;
    @Mock
    private XssDescriptor mockXssDescriptor;
    @Mock
    private XssAttack mockAttack;

    @InjectMocks
    private LabelledXssGeneratorImpl labelledXssGenerator;

    @Before
    public void setUp() {
        when(mockDescriptorFactory.createXssDescriptor(mockTraversal, LABEL)).thenReturn(mockXssDescriptor);

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
    public void generatingXssAddsDescriptorToJournal() {
        // when
        labelledXssGenerator.getXssAttackTextForLabel(LABEL);

        // then
        verify(mockJournal).addXssDescriptor(ATTACK_ID, mockXssDescriptor);
    }
    */
}
