package org.xssfinder.testhelper;

import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockMethodDefinitionBuilder {
    private final MockPageDefinitionBuilder mockPageDefinitionBuilder;
    private final MethodDefinition mockMethodDefinition = mock(MethodDefinition.class);

    public MockMethodDefinitionBuilder(MockPageDefinitionBuilder mockPageDefinitionBuilder) {
        this.mockPageDefinitionBuilder = mockPageDefinitionBuilder;
    }

    public MockMethodDefinitionBuilder withName(String name) {
        when(mockMethodDefinition.getIdentifier()).thenReturn(name);
        return this;
    }

    public MockMethodDefinitionBuilder toPage(PageDefinition mockPageDefinition) {
        String id = mockPageDefinition.getIdentifier();
        when(mockMethodDefinition.getReturnTypeIdentifier()).thenReturn(id);
        return this;
    }

    public MockPageDefinitionBuilder onPage() {
        mockPageDefinitionBuilder.addMethod(mockMethodDefinition);
        return mockPageDefinitionBuilder;
    }
}
