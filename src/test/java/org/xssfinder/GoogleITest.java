package org.xssfinder;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.xssfinder.routing.GraphsFactory;
import org.xssfinder.routing.Route;
import org.xssfinder.routing.RouteGenerator;
import org.xssfinder.runner.*;

import java.util.List;
import java.util.Set;

/**
 * Quick integration test to check everything hangs together; should be removed in favour of something that
 * that runs against a local site.
 */
public class GoogleITest {
    @Test
    public void runThroughGoogleRoutes() {
        // Find all the pages
        // Note: If page classes were in own package, could do something like:
        //     PageFinder pageFinder = new PageFinder("org.xssfinder.dummytest.google");
        //     Set<Class<?>> pages = pageFinder.findAllPages();
        Set<Class<?>> pages = ImmutableSet.of(
                (Class<?>)HomePage.class,
                SearchResultsPage.class,
                ImageSearchResultsPage.class,
                GitHubPage.class
        );

        // Turn them into routes
        GraphsFactory graphsFactory = new GraphsFactory();
        RouteGenerator routeGenerator = new RouteGenerator(graphsFactory);
        List<Route> routes = routeGenerator.generateRoutes(pages);

        // Create the HtmlUnitDriver runner
        DriverWrapper driverWrapper = new DefaultHtmlUnitDriverWrapper();
        RouteRunner runner = new RouteRunner(
                driverWrapper,
                new PageTraverser(),
                routes
        );

        // Run!
        runner.run();
    }

    private static abstract class BasePage {
        protected final WebDriver driver;
        BasePage(WebDriver driver) {
            this.driver = driver;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @Page
    @CrawlStartPoint(url="http://www.google.co.uk")
    private static class HomePage extends BasePage {
        HomePage(WebDriver driver) {
            super(driver);
            System.out.println("Created Google home page");
        }

        @SubmitAction
        public SearchResultsPage searchForXssFinder() {
            driver.findElement(By.name("q")).sendKeys("XSS Finder");
            driver.findElement(By.name("btnG")).click();
            return new SearchResultsPage(driver);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @Page
    private static class SearchResultsPage extends BasePage {
        SearchResultsPage(WebDriver driver) {
            super(driver);
            System.out.println("Created Google search results page");
        }

        public ImageSearchResultsPage goToImageSearch() {
            driver.findElement(By.partialLinkText("Images")).click();
            return new ImageSearchResultsPage(driver);
        }

        public GitHubPage clickGitHubLink() {
            driver.findElement(By.partialLinkText("shadsidd")).click();
            return new GitHubPage(driver);
        }
    }

    @Page
    private static class ImageSearchResultsPage extends BasePage {
        ImageSearchResultsPage(WebDriver driver) {
            super(driver);
            System.out.println("Created Google image search results page");
        }
    }

    @Page
    private static class GitHubPage extends BasePage {
        GitHubPage(WebDriver driver) {
            super(driver);
            System.out.println("Created Github page");
        }

    }
}
