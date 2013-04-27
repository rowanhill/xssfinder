package org.xssfinder.scanner;

import com.google.common.collect.ImmutableSet;
import org.dummytest.simple.HomePage;
import org.dummytest.simple.SecondPage;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PageFinderTest {
    @Test(expected=NoPagesFoundException.class)
    public void pageFinderThrowsExceptionIfNoPagesAreFound() {
        // given
        PageFinder pageFinder = new PageFinder("org.xssfinder.scanner");

        // when
        pageFinder.findAllPages();
    }

    @Test
    public void pageFinderReturnsClassesAnnotatedWithPage() {
        // given
        PageFinder pageFinder = new PageFinder("org.dummytest.simple");

        // when
        Set<Class<?>> pages = pageFinder.findAllPages();

        // then
        Set<Class<?>> expectedPages = ImmutableSet.of(HomePage.class, SecondPage.class);
        assertThat(pages, is(expectedPages));
    }
}
