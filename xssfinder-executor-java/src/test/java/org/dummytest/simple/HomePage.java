package org.dummytest.simple;

import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;

@Page
@CrawlStartPoint(url=HomePage.URL)
public class HomePage {
    static final String URL = "http://localhost:8080/";

    public SecondPage goToSecondPage() {
        return null;
    }
}
