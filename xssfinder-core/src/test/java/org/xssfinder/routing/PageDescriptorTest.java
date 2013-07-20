package org.xssfinder.routing;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageDescriptorTest {
    @Mock
    private PageDefinition mockPageDefinition;

    @Test
    public void exposesPageClass() {
        // given
        PageDescriptor descriptor = new PageDescriptor(mockPageDefinition);

        // when
        PageDefinition pageDefinition = descriptor.getPageDefinition();

        // then
        assertThat(pageDefinition, is(mockPageDefinition));
    }

    @Test
    public void ordinaryPageIsNotARoot() {
        // given
        PageDescriptor descriptor = new PageDescriptor(mockPageDefinition);

        // when
        boolean isRoot = descriptor.isRoot();

        // then
        assertThat(isRoot, is(false));
    }

    @Test
    public void startPageIsARoot() {
        // given
        when(mockPageDefinition.isCrawlStartPoint()).thenReturn(true);
        PageDescriptor descriptor = new PageDescriptor(mockPageDefinition);

        // when
        boolean isRoot = descriptor.isRoot();

        // then
        assertThat(isRoot, is(true));
    }

    @Test
    public void traversalMethodsIsEmptyForLeafPage() {
        // given
        PageDescriptor descriptor = new PageDescriptor(mockPageDefinition);

        // when
        Set<MethodDefinition> traversalMethods = descriptor.getTraversalMethods();

        // then
        Set<MethodDefinition> emptySet = ImmutableSet.of();
        assertThat(traversalMethods, is(emptySet));
    }

    @Test
    public void traversalMethodsHasPagesReturnedByMethodsForNonLeafPage() throws Exception {
        // given
        MethodDefinition mockMethodDefinition = mock(MethodDefinition.class);
        when(mockMethodDefinition.getReturnTypeIdentifier()).thenReturn("Some Page");
        Set<MethodDefinition> methodDefinitions = ImmutableSet.of(mockMethodDefinition);
        when(mockPageDefinition.getMethods()).thenReturn(methodDefinitions);
        when(mockPageDefinition.getIdentifier()).thenReturn("Other Page");
        PageDescriptor descriptor = new PageDescriptor(mockPageDefinition);

        // when
        Set<MethodDefinition> traversalMethods = descriptor.getTraversalMethods();

        // then
        assertThat(traversalMethods, is(methodDefinitions));
    }

    @Test
    public void traversalMethodsIncludeMethodsThatReturnTheSamePage() throws Exception {
        // given
        MethodDefinition mockMethodDefinition = mock(MethodDefinition.class);
        when(mockMethodDefinition.getReturnTypeIdentifier()).thenReturn("Some Page");
        Set<MethodDefinition> methodDefinitions = ImmutableSet.of(mockMethodDefinition);
        when(mockPageDefinition.getMethods()).thenReturn(methodDefinitions);
        when(mockPageDefinition.getIdentifier()).thenReturn("Some Page");
        PageDescriptor descriptor = new PageDescriptor(mockPageDefinition);

        // when
        Set<MethodDefinition> traversalMethods = descriptor.getTraversalMethods();

        // then
        assertThat(traversalMethods, is(methodDefinitions));
    }

    @Test
    public void submitMethodsIsEmptyForLeafNode() throws Exception {
        // given
        PageDescriptor descriptor = new PageDescriptor(mockPageDefinition);

        // when
        Set<MethodDefinition> submitMethods = descriptor.getSubmitMethods();

        // then
        Set<MethodDefinition> emptySet = ImmutableSet.of();
        assertThat(submitMethods, is(emptySet));
    }

    @Test
    public void submitMethodsContainsSubmitMethodButNotOtherTraversals() throws Exception {
        // given
        MethodDefinition mockMethodDefinition = mock(MethodDefinition.class);
        when(mockMethodDefinition.getReturnTypeIdentifier()).thenReturn("Some Page");
        MethodDefinition mockSubmitMethodDefinition = mock(MethodDefinition.class);
        when(mockSubmitMethodDefinition.getReturnTypeIdentifier()).thenReturn("Some Page");
        when(mockSubmitMethodDefinition.isSubmitAnnotated()).thenReturn(true);
        when(mockPageDefinition.getIdentifier()).thenReturn("Other Page");
        Set<MethodDefinition> methodDefinitions = ImmutableSet.of(mockMethodDefinition, mockSubmitMethodDefinition);
        when(mockPageDefinition.getMethods()).thenReturn(methodDefinitions);
        PageDescriptor descriptor = new PageDescriptor(mockPageDefinition);

        // when
        Set<MethodDefinition> submitMethods = descriptor.getSubmitMethods();

        // then
        Set<MethodDefinition> expectedMethods = ImmutableSet.of(mockSubmitMethodDefinition);
        assertThat(submitMethods, is(expectedMethods));
    }
}
