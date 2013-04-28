package org.dummytest.simple;

import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;

@Page
@CrawlStartPoint
public class HomePage {

    public SecondPage goToSecondPage() {
        return null;
    }
}
