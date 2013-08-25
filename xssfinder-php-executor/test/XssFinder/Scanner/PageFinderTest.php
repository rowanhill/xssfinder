<?php

namespace XssFinder\Scanner;

use PHPUnit_Framework_TestCase;

class PageFinderTest extends PHPUnit_Framework_TestCase
{
    public function testReturnsClassesFromAnyNamespaceAnnotatedAsPages()
    {
        // given
        $classNames = array('TestPages\SomePage');
        $pageFinder = new PageFinder();

        // when
        $pageClassNames = $pageFinder->findPages($classNames);

        // then
        $this->assertThat($pageClassNames, $this->equalTo($classNames));
    }

    public function testDoesNotReturnClassesFromAnyNamespaceNotAnnotatedAsPages()
    {
        // given
        $classNames = array('TestPages\NotAPage');
        $pageFinder = new PageFinder();

        // when
        $pageClassNames = $pageFinder->findPages($classNames);

        // then
        $this->assertThat($pageClassNames, $this->equalTo(array()));
    }

    public function testReturnsClassesFromSpecifiedNamespaceAnnotatedAsPages()
    {
        // given
        $classNames = array('TestPages\SomePage');
        $pageFinder = new PageFinder('TestPages');

        // when
        $pageClassNames = $pageFinder->findPages($classNames);

        // then
        $this->assertThat($pageClassNames, $this->equalTo($classNames));
    }

    public function testDoesNotReturnAnnotatedClassesFromWrongNamespace()
    {
        // given
        $classNames = array('NotTestPages\SomePage');
        $pageFinder = new PageFinder('TestPages');

        // when
        $pageClassNames = $pageFinder->findPages($classNames);

        // then
        $this->assertThat($pageClassNames, $this->equalTo(array()));
    }

    public function testLeadingBackslashOnNamespaceOfInputClassNamesIsAllowable()
    {
        // given
        $classNames = array('\TestPages\SomePage');
        $pageFinder = new PageFinder('TestPages');

        // when
        $pageClassNames = $pageFinder->findPages($classNames);

        // then
        $this->assertThat($pageClassNames, $this->equalTo($classNames));
    }

    public function testLeadingBackslashOnSpecifiedNamespaceIsAllowable()
    {
        // given
        $classNames = array('TestPages\SomePage');
        $pageFinder = new PageFinder('\TestPages');

        // when
        $pageClassNames = $pageFinder->findPages($classNames);

        // then
        $this->assertThat($pageClassNames, $this->equalTo($classNames));
    }
}

namespace TestPages;

/**
 * @page
 */
class SomePage {}

class NotAPage {}


namespace NotTestPages;

/**
 * @page
 */
class SomePage {}