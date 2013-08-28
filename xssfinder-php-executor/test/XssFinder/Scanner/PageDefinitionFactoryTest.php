<?php

namespace XssFinder\Scanner;

class PageDefinitionFactoryTest extends \PHPUnit_Framework_TestCase
{
    const SOME_PAGE = '\PageDefinitionFactory\TestPages\SomePage';
    const NO_PAGE_RETURNING_PAGE = '\PageDefinitionFactory\TestPages\NoPageReturningPage';
    const PAGE_RETURNING_PAGE = '\PageDefinitionFactory\TestPages\PageReturningPage';
    const HOME_PAGE = '\PageDefinitionFactory\TestPages\HomePage';

    /** @var PageDefinitionFactory */
    private $_factory;
    /** @var MethodDefinitionFactory */
    private $_mockMethodFactory;
    /** @var ReflectionHelper */
    private $_mockReflectionHelper;

    public function setUp()
    {
        $knownPageClassNames = array(
            self::SOME_PAGE, self::NO_PAGE_RETURNING_PAGE, self::PAGE_RETURNING_PAGE, self::HOME_PAGE
        );
        $this->_mockMethodFactory = mock('XssFinder\Scanner\MethodDefinitionFactory');
        $this->_mockReflectionHelper = mock('XssFinder\Scanner\ReflectionHelper');
        $this->_factory = new PageDefinitionFactory(
            $this->_mockMethodFactory,
            $this->_mockReflectionHelper,
            $knownPageClassNames
        );
    }

    public function testDefinitionIdentifierIsFullyQualifiedClassName()
    {
        // when
        $pageDefinition = $this->_factory->createPageDefinition(self::SOME_PAGE);

        // then
        assertThat($pageDefinition->identifier, equalTo(self::SOME_PAGE));
    }

    public function testDefinitionMethodsIsEmptySetIfClassHasNoMethods()
    {
        // when
        $pageDefinition = $this->_factory->createPageDefinition(self::SOME_PAGE);

        // then
        assertThat($pageDefinition->methods, equalTo(array()));
    }

    public function testDefinitionMethodsIsEmptySetIfClassHasNoMethodsReturningPageObjects()
    {
        // when
        $pageDefinition = $this->_factory->createPageDefinition(self::NO_PAGE_RETURNING_PAGE);

        // then
        assertThat($pageDefinition->methods, equalTo(array()));
    }

    public function testDefinitionMethodsHasMethodDefinitionForMethodsOnClassWhichReturnPageObjects()
    {
        // given
        $method = $this->_getReflectionMethod(self::PAGE_RETURNING_PAGE, 'getSomePage');
        $mockMethodDefinition = $this->_mockMethodDefinitionCreationForMethod($method);
        when($this->_mockReflectionHelper->getReturnType($method))->return(self::SOME_PAGE);

        // when
        $pageDefinition = $this->_factory->createPageDefinition(self::PAGE_RETURNING_PAGE);

        // then
        assertThat($pageDefinition->methods, equalTo(array($mockMethodDefinition)));
    }

    public function testDefinitionIsCrawlStartPointIfPageIsAnnotatedWithCrawlStartPoint()
    {
        // when
        $pageDefinition = $this->_factory->createPageDefinition(self::HOME_PAGE);

        // then
        assertThat($pageDefinition->crawlStartPoint, equalTo(true));
    }

    public function testDefinitionIsNotCrawlStartPointIfPageIsNotAnnotatedWithCrawlStartPoint()
    {
        // when
        $pageDefinition = $this->_factory->createPageDefinition(self::SOME_PAGE);

        // then
        assertThat($pageDefinition->crawlStartPoint, equalTo(false));
    }

    private function _getReflectionMethod($className, $methodName)
    {
        $reflectionClass = new \ReflectionClass($className);
        return $reflectionClass->getMethod($methodName);
    }

    private function _mockMethodDefinitionCreationForMethod(\ReflectionMethod $method)
    {
        $mockMethodDefinition = mock('XssFinder\MethodDefinition');
        when($this->_mockMethodFactory->createMethodDefinition($method))->return($mockMethodDefinition);
        return $mockMethodDefinition;
    }
}

namespace PageDefinitionFactory\TestPages;
use XssFinder\PageDefinition;

class SomePage {}

class NoPageReturningPage
{
    /**
     * This comment is for illustration only - the ReflectionHelper mock is stubbed to provide the return type
     * @return string
     */
    public function getFoo() { return 'Foo'; }
}

class PageReturningPage
{
    /**
     * This comment is for illustration only - the ReflectionHelper mock is stubbed to provide the return type
     * @return SomePage
     */
    public function getSomePage() { return new SomePage(); }
}

/**
 * @crawlStartPoint
 */
class HomePage {}