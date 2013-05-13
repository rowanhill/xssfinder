package org.xssfinder.runner;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.xss.XssDescriptor;
import org.xssfinder.xss.XssDescriptorFactory;
import org.xssfinder.xss.XssGenerator;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PageAttackerTest {
    @Mock
    private XssGenerator mockXssGenerator;
    @Mock
    private XssDescriptorFactory mockXssDescriptorFactory;

    @Mock
    private PageContext mockPageContext;
    @Mock
    private DriverWrapper mockDriverWrapper;
    @Mock
    private PageTraversal mockPageTraversal;
    @Mock
    private Object mockPage;
    @Mock
    private XssDescriptor mockXssDescriptor;

    private PageAttacker pageAttacker;

    @Before
    public void setUp() {
        when(mockPageContext.getDriverWrapper()).thenReturn(mockDriverWrapper);
        when(mockPageContext.getPageTraversal()).thenReturn(mockPageTraversal);
        when(mockPageContext.getPage()).thenReturn(mockPage);
        when(mockPageContext.hasNextContext()).thenReturn(true, false);

        pageAttacker = new PageAttacker(mockXssGenerator, mockXssDescriptorFactory);
    }

    @Test
    public void attackingDoesNothingIfNextTraversalIsNotSubmission() {
        // when
        pageAttacker.attackIfAboutToSubmit(mockPageContext);

        // then
        verifyZeroInteractions(mockDriverWrapper);
    }

    @Test
    public void attackingDoesNothingIfNextTraversalIsNull() {
        // when
        pageAttacker.attackIfAboutToSubmit(mockPageContext);

        // then
        verifyZeroInteractions(mockDriverWrapper);
    }

    @Test
    public void attackingPutsXssAttacksInInputsIfTraversalIsSubmission() {
        //given
        when(mockPageTraversal.isSubmit()).thenReturn(true);

        // when
        pageAttacker.attackIfAboutToSubmit(mockPageContext);

        // then
        verify(mockDriverWrapper).putXssAttackStringsInInputs(mockXssGenerator);
    }

    @Test
    public void descriptionsOfAttackedInputsAreReturned() {
        //given
        when(mockPageTraversal.isSubmit()).thenReturn(true);
        when(mockDriverWrapper.putXssAttackStringsInInputs(mockXssGenerator))
                .thenReturn(ImmutableMap.of("body/form[1]/input[1]", "1"));
        when(mockXssDescriptorFactory.createXssDescriptor(mockPage, "body/form[1]/input[1]"))
                .thenReturn(mockXssDescriptor);

        // when
        Map<String, XssDescriptor> xssIdsToDescription =
                pageAttacker.attackIfAboutToSubmit(mockPageContext);

        // then
        Map<String, XssDescriptor> expectedDescriptions = ImmutableMap.of("1", mockXssDescriptor);
        assertThat(xssIdsToDescription, is(expectedDescriptions));
    }
}
