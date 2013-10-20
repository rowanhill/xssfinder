<?php

namespace XssFinder\Runner;

use PHPUnit_Framework_TestCase;
use ReflectionClass;
use XssFinder\MethodDefinition;
use XssFinder\Scanner\ThriftToReflectionLookup;
use XssFinder\TraversalMode;
use XssFinder\Xss\XssGenerator;

require '_ExecutorContext_TestPages.php';

class ExecutorContextTest extends PHPUnit_Framework_TestCase
{
    const HOME_PAGE_URL = 'http://home';
    const HOME_PAGE_ID = "HomePage";
    const SECOND_PAGE_ID = "SecondPage";

    /** @var DriverWrapper */
    private $_mockDriverWrapper;
    /** @var XssGenerator */
    private $_mockXssGenerator;
    /** @var PageInstantiator */
    private $_mockPageInstantiator;

    /** @var ReflectionClass */
    private $_homePageClass;
    /** @var ReflectionClass */
    private $_secondPageClass;
    /** @var mixed */
    private $_mockHomePage;
    /** @var mixed */
    private $_mockSecondPage;
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
        $this->_secondPageClass = new ReflectionClass('\XssFinder\Runner\EC_TestPages_SecondPage');
        $this->_mockPageTraverser = mock('XssFinder\Runner\PageTraverser');
        $this->_mockHomePage = new \stdClass();
        $this->_mockHomePage->page = 'home page';
        $this->_mockSecondPage = new \stdClass();
        $this->_mockSecondPage->page = 'second page';
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
        $mockMethodDefinition = $this->_mockMethodDefinition('refreshHomePage');
        $lookup = $this->_setMockLookup();
        $method = $this->_getMethodAndAddToLookup($this->_homePageClass, 'refreshHomePage', $lookup);
        $mockTraversalResult = $this->_mockTraversalResultForPageAndMethod($this->_mockHomePage, $method);
        when($lookup->getPageClass(self::HOME_PAGE_ID))->return($this->_homePageClass);
        $this->_executorContext->visitUrlOfRootPage(self::HOME_PAGE_ID);

        // when
        $traversalResult = $this->_executorContext->traverseMethod($mockMethodDefinition, TraversalMode::NORMAL);

        // then
        assertThat($traversalResult, is($mockTraversalResult));
    }

    function testTraversingASecondTimeTraversesFromResultingPageObjectOfFirstTraversal()
    {
        // given
        $lookup = $this->_setMockLookup();
        when($lookup->getPageClass(self::HOME_PAGE_ID))->return($this->_homePageClass);
        when($lookup->getPageClass(self::SECOND_PAGE_ID))->return($this->_secondPageClass);
        $goToSecondPage = $this->_getMethodAndAddToLookup($this->_homePageClass, 'goToSecondPage', $lookup);
        $goToThirdPage = $this->_getMethodAndAddToLookup($this->_secondPageClass, 'goToThirdPage', $lookup);
        $mockTraversalResult1 = $this->_mockTraversalResultForPageAndMethod($this->_mockHomePage, $goToSecondPage);
        when($mockTraversalResult1->getPage())->return($this->_mockSecondPage);
        $mockTraversalResult2 = $this->_mockTraversalResultForPageAndMethod($this->_mockSecondPage, $goToThirdPage);
        $mockGoToSecondPageDefinition = $this->_mockMethodDefinition('goToSecondPage');
        $mockGoToThirdPageDefinition = $this->_mockMethodDefinition('goToThirdPage');
        $this->_executorContext->visitUrlOfRootPage(self::HOME_PAGE_ID);

        // when
        $this->_executorContext->traverseMethod($mockGoToSecondPageDefinition, TraversalMode::NORMAL);
        $traversalResult = $this->_executorContext->traverseMethod($mockGoToThirdPageDefinition, TraversalMode::NORMAL);

        // then
        assertThat($traversalResult, is($mockTraversalResult2));
    }

    function testGettingCurrentXssIdsIsDelegatedToDriverWrapper()
    {
        // given
        when($this->_mockDriverWrapper->getCurrentXssIds())->return(array('1','2'));

        // when
        $currentXssIds = $this->_executorContext->getCurrentXssIds();

        // then
        assertThat($currentXssIds, is(array('1','2')));
    }

    /**
     * @param $identifier
     * @return MethodDefinition
     */
    private function _mockMethodDefinition($identifier)
    {
        $mockMethodDefinition = mock('XssFinder\MethodDefinition');
        $mockMethodDefinition->identifier = $identifier;
        return $mockMethodDefinition;
    }

    /**
     * @return ThriftToReflectionLookup
     */
    private function _setMockLookup()
    {
        /** @var ThriftToReflectionLookup $lookup */
        $lookup = mock('\XssFinder\Scanner\ThriftToReflectionLookup');
        $this->_executorContext->setThriftToReflectionLookup($lookup);
        return $lookup;
    }

    /**
     * @param \ReflectionClass $pageClass
     * @param $methodName
     * @param \XssFinder\Scanner\ThriftToReflectionLookup $lookup
     * @return \ReflectionMethod
     */
    private function _getMethodAndAddToLookup(ReflectionClass $pageClass, $methodName, ThriftToReflectionLookup $lookup)
    {
        $method = $pageClass->getMethod($methodName);
        when($lookup->getMethod($methodName))->return($method);
        return $method;
    }

    /**
     * @param $page
     * @param $method
     * @return TraversalResult
     */
    private function _mockTraversalResultForPageAndMethod($page, $method)
    {
        $mockTraversalResult = mock('XssFinder\Runner\TraversalResult');
        when($this->_mockPageTraverser->traverse($page, $method, TraversalMode::NORMAL))
            ->return($mockTraversalResult);
        return $mockTraversalResult;
    }
}