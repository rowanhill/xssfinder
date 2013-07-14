package org.xssfinder.runner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.remote.PageDefinition;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorContextTest {
    private static final String HOME_PAGE_URL = "http://home";

    @Mock
    private DriverWrapper mockDriverWrapper;

    @InjectMocks
    private ExecutorContext context;

    @Test
    public void visitingUrlOfRootPageIsDelegatedToDriverWrapper() {
        // given
        String pageId = "HomePage";
        Class<?> pageClass = HomePage.class;
        context.addPageMapping(pageId, pageClass);

        // when
        context.visitUrlOfRootPage(pageId);

        // then
        verify(mockDriverWrapper).visit(HOME_PAGE_URL);
    }

    @CrawlStartPoint(url=HOME_PAGE_URL)
    private static class HomePage {}
}
