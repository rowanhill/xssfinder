package org.xssfinder.testhelper;

import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockPageDefinitionBuilder {
    private final PageDefinition mockPageDefinition;

    private Set<MethodDefinition> methods = new HashSet<MethodDefinition>();

    public MockPageDefinitionBuilder() {
        mockPageDefinition = mock(PageDefinition.class);
    }

    public MockPageDefinitionBuilder(String mockName) {
        mockPageDefinition = mock(PageDefinition.class, mockName);
    }

    public MockPageDefinitionBuilder withName(String name) {
        when(mockPageDefinition.getIdentifier()).thenReturn(name);
        return this;
    }

    public MockPageDefinitionBuilder markedAsCrawlStartPoint() {
        when(mockPageDefinition.isCrawlStartPoint()).thenReturn(true);
        return this;
    }

    public MockMethodDefinitionBuilder withMethod() {
        return new MockMethodDefinitionBuilder(this);
    }

    public PageDefinition build() {
        when(mockPageDefinition.getMethods()).thenReturn(methods);
        return mockPageDefinition;
    }

    void addMethod(MethodDefinition mockMethodDefinition) {
        methods.add(mockMethodDefinition);
    }

    public static MockPageDefinitionBuilder mockPageDefinition() {
        return new MockPageDefinitionBuilder();
    }

    public static MockPageDefinitionBuilder mockPageDefinition(String mockName) {
        return new MockPageDefinitionBuilder(mockName);
    }
}
