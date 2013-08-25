<?php

namespace XssFinder\Scanner;

use PHPUnit_Framework_TestCase;

class PageFinderTest extends PHPUnit_Framework_TestCase
{
    public function testReturnsClassesFromAnyNamespaceAnnotatedAsPages()
    {
        // given
        $classNames = array('PageFinder\TestPages\SomePage');
        $pageFinder = new PageFinder();

        // when
        $pageClassNames = $pageFinder->findPages($classNames);

        // then
        $this->assertThat($pageClassNames, $this->equalTo($classNames));
    }

    public function testDoesNotReturnClassesFromAnyNamespaceNotAnnotatedAsPages()
    {
        // given
        $classNames = array('PageFinder\TestPages\NotAPage');
        $pageFinder = new PageFinder();

        // when
        $pageClassNames = $pageFinder->findPages($classNames);

        // then
        $this->assertThat($pageClassNames, $this->equalTo(array()));
    }

    public function testReturnsClassesFromSpecifiedNamespaceAnnotatedAsPages()
    {
        // given
        $classNames = array('PageFinder\TestPages\SomePage');
        $pageFinder = new PageFinder('TestPages');

        // when
        $pageClassNames = $pageFinder->findPages($classNames);

        // then
        $this->assertThat($pageClassNames, $this->equalTo($classNames));
    }

    public function testDoesNotReturnAnnotatedClassesFromWrongNamespace()
    {
        // given
        $classNames = array('PageFinder\NotTestPages\SomePage');
        $pageFinder = new PageFinder('TestPages');

        // when
        $pageClassNames = $pageFinder->findPages($classNames);

        // then
        $this->assertThat($pageClassNames, $this->equalTo(array()));
    }

    public function testLeadingBackslashOnNamespaceOfInputClassNamesIsAllowable()
    {
        // given
        $classNames = array('\PageFinder\TestPages\SomePage');
        $pageFinder = new PageFinder('TPageFinder\estPages');

        // when
        $pageClassNames = $pageFinder->findPages($classNames);

        // then
        $this->assertThat($pageClassNames, $this->equalTo($classNames));
    }

    public function testLeadingBackslashOnSpecifiedNamespaceIsAllowable()
    {
        // given
        $classNames = array('PageFinder\TestPages\SomePage');
        $pageFinder = new PageFinder('\PageFinder\TestPages');

        // when
        $pageClassNames = $pageFinder->findPages($classNames);

        // then
        $this->assertThat($pageClassNames, $this->equalTo($classNames));
    }
}

namespace PageFinder\TestPages;

/**
 * @page
 */
class SomePage {}

class NotAPage {}


namespace PageFinder\NotTestPages;

/**
 * @page
 */
class SomePage {}