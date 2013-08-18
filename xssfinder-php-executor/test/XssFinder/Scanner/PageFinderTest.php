<?php

namespace XssFinder\Scanner;

use PHPUnit_Framework_TestCase;

class PageFinderTest extends PHPUnit_Framework_TestCase
{
    public function testReturnsClassesAnnotatedAsPages()
    {
        // given
        $classNames = array('TestPages\SomePage');
        $pageFinder = new PageFinder();

        // when
        $pageClassNames = $pageFinder->findPages($classNames);

        // then
        $this->assertThat($pageClassNames, $this->equalTo($classNames));
    }

    public function testDoesNotReturnClassesNotAnnotatedAsPages()
    {
        // given
        $classNames = array('TestPages\NotAPage');
        $pageFinder = new PageFinder();

        // when
        $pageClassNames = $pageFinder->findPages($classNames);

        // then
        $this->assertThat($pageClassNames, $this->equalTo(array()));
    }
}

namespace TestPages;

/**
 * @page
 */
class SomePage {}

class NotAPage {}