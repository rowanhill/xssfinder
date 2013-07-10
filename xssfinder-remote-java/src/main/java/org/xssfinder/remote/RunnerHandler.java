package org.xssfinder.remote;

import org.apache.thrift.TException;
import org.xssfinder.scanner.NoPagesFoundException;
import org.xssfinder.scanner.PageDefinitionFactory;
import org.xssfinder.scanner.PageFinder;

import java.util.HashSet;
import java.util.Set;

public class RunnerHandler implements Runner.Iface {
    private final PageFinder pageFinder;
    private final PageDefinitionFactory pageDefinitionFactory;

    public RunnerHandler(PageFinder pageFinder, PageDefinitionFactory pageDefinitionFactory) {
        this.pageFinder = pageFinder;
        this.pageDefinitionFactory = pageDefinitionFactory;
    }

    @Override
    public Set<PageDefinition> getPageDefinitions(String namespaceIdentifier) throws TException {
        try {
            Set<Class<?>> pageClasses = pageFinder.findAllPages(namespaceIdentifier);
            Set<PageDefinition> pageDefinitions = new HashSet<PageDefinition>();
            for (Class<?> pageClass : pageClasses) {
                pageDefinitions.add(pageDefinitionFactory.createPageDefinition(pageClass, pageClasses));
            }
            return pageDefinitions;
        } catch (NoPagesFoundException e) {
            throw new TException(e);
        }
    }
}
