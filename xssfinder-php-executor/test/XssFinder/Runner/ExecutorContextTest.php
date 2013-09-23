<?php

namespace XssFinder\Runner;

use PHPUnit_Framework_TestCase;
use ReflectionClass;
use XssFinder\MethodDefinition;
use XssFinder\Scanner\ThriftToReflectionLookup;
use XssFinder\TraversalMode;
use XssFinder\Xss\XssGenerator;

require '_ExecutorContext_HomePage.php';

class ExecutorContextTest extends PHPUnit_Framework_TestCase
{
    const HOME_PAGE_URL = 'http://home';
    const HOME_PAGE_ID = "HomePage";

    /** @var DriverWrapper */
    private $_mockDriverWrapper;
    /** @var XssGenerator */
    private $_mockXssGenerator;
    /** @var PageInstantiator */
    private $_mockPageInstantiator;

    /** @var ReflectionClass */
    private $_homePageClass;
    /** @var mixed */
    private $_mockHomePage;
    /** @var PageTraverser */
    private $_mockPageTraverser;

    /** @var ExecutorContext */
    private $_executorContext;

    function setUp()
    {
        $this->_mockDriverWrapper = mock('\XssFinder\Runner\DriverWrapper');
        $this->_mockXssGenerator = mock('\XssFinder\Xss\XssGenerator');
        $this->_mockPageInstantiator = mock('\XssFinder\Runner\PageInstantiator');
        when($this->_mockDriverWrapper->getPageInstantiator())->return($this->_mockPageInstantiator);

        $this->_homePageClass = new ReflectionClass('\XssFinder\Runner\EC_TestPages_HomePage');
        $this->_mockPageTraverser = mock('XssFinder\Runner\PageTraverser');
        $this->_mockHomePage = new \stdClass();
        when($this->_mockPageInstantiator->instantiatePage($this->_homePageClass))->return($this->_mockHomePage);

        $this->_executorContext = new ExecutorContext(
            $this->_mockDriverWrapper,
            $this->_mockXssGenerator,
            $this->_mockPageTraverser
        );
    }

    function testVisitingUrlOfRootPageIsDelegatedToDriverWrapperUsingUrlFromLookup()
    {
        // given
        /** @var ThriftToReflectionLookup $lookup */
        $lookup = mock('\XssFinder\Scanner\ThriftToReflectionLookup');
        when($lookup->getPageClass(self::HOME_PAGE_ID))->return($this->_homePageClass);
        $this->_executorContext->setThriftToReflectionLookup($lookup);

        // when
        $this->_executorContext->visitUrlOfRootPage(self::HOME_PAGE_ID);

        // then
        verify($this->_mockDriverWrapper)->visit(self::HOME_PAGE_URL);
    }

    function testVisitingRootPageInstantiatesPage()
    {
        // given
        /** @var ThriftToReflectionLookup $lookup */
        $lookup = mock('\XssFinder\Scanner\ThriftToReflectionLookup');
        when($lookup->getPageClass(self::HOME_PAGE_ID))->return($this->_homePageClass);
        $this->_executorContext->setThriftToReflectionLookup($lookup);

        // when
        $this->_executorContext->visitUrlOfRootPage(self::HOME_PAGE_ID);

        // then
        verify($this->_mockPageInstantiator)->instantiatePage($this->_homePageClass);
    }

    function testPuttingXssAttackStringsInInputsIsDelegatedToDriverWrapper()
    {
        // given
        when($this->_mockDriverWrapper->putXssAttackStringsInInputs($this->_mockXssGenerator))
            ->return(array('foo', 'bar'));

        // when
        $inputToAttackMapping = $this->_executorContext->putXssAttackStringsInInputs();

        // then
        assertThat($inputToAttackMapping, is(array('foo', 'bar')));
    }

    function testTraversingMethodDelegatesToPageTraverser()
    {
        // given
        /** @var MethodDefinition $mockMethodDefinition */
        $mockMethodDefinition = mock('XssFinder\MethodDefinition');
        $mockMethodDefinition->identifier = 'refreshHomePage';
        /** @var ThriftToReflectionLookup $lookup */
        $lookup = mock('\XssFinder\Scanner\ThriftToReflectionLookup');
        $method = $this->_homePageClass->getMethod('refreshHomePage');
        when($lookup->getMethod('refreshHomePage'))->return($method);
        $this->_executorContext->setThriftToReflectionLookup($lookup);
        /** @var TraversalResult $mockTraversalResult */
        $mockTraversalResult = mock('XssFinder\Runner\TraversalResult');
        when($this->_mockPageTraverser->traverse($this->_mockHomePage, $method, TraversalMode::NORMAL))
            ->return($mockTraversalResult);
        when($lookup->getPageClass(self::HOME_PAGE_ID))->return($this->_homePageClass);
        $this->_executorContext->visitUrlOfRootPage(self::HOME_PAGE_ID);


        // when
        $traversalResult = $this->_executorContext->traverseMethod($mockMethodDefinition, TraversalMode::NORMAL);

        // then
        assertThat($traversalResult, is($mockTraversalResult));
    }
}