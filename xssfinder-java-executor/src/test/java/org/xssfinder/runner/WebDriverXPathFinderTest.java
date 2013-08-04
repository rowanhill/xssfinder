package org.xssfinder.runner;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebDriverXPathFinderTest {
    @Test
    public void elementWithIdReturnsXPathSelector() {
        // given
        WebDriverXPathFinder finder = new WebDriverXPathFinder();
        WebElement element = mock(WebElement.class);
        when(element.getTagName()).thenReturn("DIV");
        when(element.getAttribute("id")).thenReturn("someId");

        // when
        String xpath = finder.getXPath(element);

        // then
        assertThat(xpath, is("//div[@id=\"someId\"]"));
    }

    @Test
    public void bodyReturnsXPathSelector() {
        // given
        WebDriverXPathFinder finder = new WebDriverXPathFinder();
        WebElement element = mock(WebElement.class);
        when(element.getTagName()).thenReturn("BODY");

        // when
        String xpath = finder.getXPath(element);

        // then
        assertThat(xpath, is("body"));
    }

    @Test
    public void nondescriptElementReturnsXPathRelativeToAncestors() {
        // given
        WebDriverXPathFinder finder = new WebDriverXPathFinder();
        WebElement element = mock(WebElement.class);
        when(element.getTagName()).thenReturn("DIV");
        WebElement parent = mock(WebElement.class);
        when(parent.getTagName()).thenReturn("DIV");
        when(parent.getAttribute("id")).thenReturn("parentId");
        when(element.findElement(By.xpath(".."))).thenReturn(parent);
        when(parent.findElements(By.xpath("./*"))).thenReturn(ImmutableList.of(element));

        // when
        String xpath = finder.getXPath(element);

        // then
        assertThat(xpath, is("//div[@id=\"parentId\"]/div[1]"));
    }

    @Test
    public void indexIsDerivedFromParentsChildrenOfSameTag() {
        // given
        WebDriverXPathFinder finder = new WebDriverXPathFinder();
        WebElement element = mock(WebElement.class);
        when(element.getTagName()).thenReturn("DIV");
        WebElement parent = mock(WebElement.class);
        when(parent.getTagName()).thenReturn("DIV");
        when(parent.getAttribute("id")).thenReturn("parentId");
        when(element.findElement(By.xpath(".."))).thenReturn(parent);
        WebElement priorSibling = mock(WebElement.class);
        when(priorSibling.getTagName()).thenReturn("DIV");
        WebElement priorIgnoredSibling = mock(WebElement.class);
        when(priorIgnoredSibling.getTagName()).thenReturn("P");
        WebElement postSibling = mock(WebElement.class);
        when(postSibling.getTagName()).thenReturn("DIV");
        when(parent.findElements(By.xpath("./*"))).thenReturn(ImmutableList.of(
                priorSibling, priorIgnoredSibling, element, postSibling));

        // when
        String xpath = finder.getXPath(element);

        // then
        assertThat(xpath, is("//div[@id=\"parentId\"]/div[2]"));

    }
}
