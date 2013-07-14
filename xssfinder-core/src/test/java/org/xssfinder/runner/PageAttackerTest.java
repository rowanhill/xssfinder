package org.xssfinder.runner;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.remote.PageDefinition;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.xss.XssDescriptor;
import org.xssfinder.xss.XssDescriptorFactory;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PageAttackerTest {
    @Mock
    private ExecutorWrapper mockExecutor;
    @Mock
    private XssDescriptorFactory mockXssDescriptorFactory;

    @Mock
    private PageContext mockPageContext;
    @Mock
    private PageTraversal mockPageTraversal;
    @Mock
    private PageDefinition mockPageDefinition;
    @Mock
    private XssDescriptor mockXssDescriptor;

    private PageAttacker pageAttacker;

    @Before
    public void setUp() {
        when(mockPageContext.getPageTraversal()).thenReturn(mockPageTraversal);
        when(mockPageContext.getPageDefinition()).thenReturn(mockPageDefinition);
        when(mockPageContext.hasNextContext()).thenReturn(true, false);

        pageAttacker = new PageAttacker(mockExecutor, mockXssDescriptorFactory);
    }

    @Test
    public void attackingDoesNothingIfNextTraversalIsNotSubmission() {
        // when
        pageAttacker.attackIfAboutToSubmit(mockPageContext);

        // then
        verifyZeroInteractions(mockExecutor);
    }

    @Test
    public void attackingDoesNothingIfNextTraversalIsNull() {
        // when
        pageAttacker.attackIfAboutToSubmit(mockPageContext);

        // then
        verifyZeroInteractions(mockExecutor);
    }

    @Test
    public void attackingPutsXssAttacksInInputsIfTraversalIsSubmission() {
        //given
        when(mockPageTraversal.isSubmit()).thenReturn(true);

        // when
        pageAttacker.attackIfAboutToSubmit(mockPageContext);

        // then
        verify(mockExecutor).putXssAttackStringsInInputs();
    }

    @Test
    public void descriptionsOfAttackedInputsAreReturned() {
        //given
        when(mockPageTraversal.isSubmit()).thenReturn(true);
        when(mockExecutor.putXssAttackStringsInInputs())
                .thenReturn(ImmutableMap.of("body/form[1]/input[1]", "1"));
        when(mockXssDescriptorFactory.createXssDescriptor(mockPageTraversal, "body/form[1]/input[1]"))
                .thenReturn(mockXssDescriptor);

        // when
        Map<String, XssDescriptor> xssIdsToDescription =
                pageAttacker.attackIfAboutToSubmit(mockPageContext);

        // then
        Map<String, XssDescriptor> expectedDescriptions = ImmutableMap.of("1", mockXssDescriptor);
        assertThat(xssIdsToDescription, is(expectedDescriptions));
    }
}
