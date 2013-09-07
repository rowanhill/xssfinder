<?php

namespace XssFinder\Runner;

use PHPUnit_Framework_TestCase;
use ReflectionClass;
use XssFinder\Scanner\ThriftToReflectionLookup;

require '_ExecutorContext_HomePage.php';

class ExecutorContextTest extends PHPUnit_Framework_TestCase
{
    const HOME_PAGE_URL = 'http://home';
    const HOME_PAGE_ID = "HomePage";

    /** @var DriverWrapper */
    private $_mockDriverWrapper;

    /** @var ExecutorContext */
    private $_executorContext;

    function setUp()
    {
        $this->_mockDriverWrapper = mock('\XssFinder\Runner\DriverWrapper');

        $this->_executorContext = new ExecutorContext($this->_mockDriverWrapper);
    }

    function testVisitingUrlOfRootPageIsDelegatedToDriverWrapperUsingUrlFromLookup()
    {
        // given
        /** @var ThriftToReflectionLookup $lookup */
        $lookup = mock('\XssFinder\Scanner\ThriftToReflectionLookup');
        $homePageClass = new ReflectionClass('\XssFinder\Runner\EC_TestPages_HomePage');
        when($lookup->getPageClass(self::HOME_PAGE_ID))->return($homePageClass);
        $this->_executorContext->setThriftToReflectionLookup($lookup);

        // when
        $this->_executorContext->visitUrlOfRootPage(self::HOME_PAGE_ID);

        // then
        verify($this->_mockDriverWrapper)->visit(self::HOME_PAGE_URL);
    }
}