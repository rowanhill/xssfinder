package org.xssfinder.testhelper;

import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;
import org.xssfinder.routing.PageDescriptor;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockPageDescriptorBuilder {
    public static PageDescriptor mockPageDescriptor(PageDefinition pageDefinition) {
        PageDescriptor mockPageDescriptor = mock(PageDescriptor.class);

        when(mockPageDescriptor.getPageDefinition()).thenReturn(pageDefinition);

        Set<MethodDefinition> methods = pageDefinition.getMethods();
        when(mockPageDescriptor.getTraversalMethods()).thenReturn(methods);

        boolean isRoot = pageDefinition.isCrawlStartPoint();
        when(mockPageDescriptor.isRoot()).thenReturn(isRoot);

        return mockPageDescriptor;

    }
}
