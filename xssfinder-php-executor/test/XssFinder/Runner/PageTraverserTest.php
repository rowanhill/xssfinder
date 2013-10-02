<?php

namespace XssFinder\Runner;

use XssFinder\TraversalMode;
use XssFinder\TUntraversableException;

class PageTraverserTest extends \PHPUnit_Framework_TestCase
{
    /** @var CustomNormalTraversalStrategy */
    private $_mockNormalStrategy;
    /** @var CustomSubmitTraversalStrategy */
    private $_mockSubmitStrategy;
    /** @var SimpleMethodTraversalStrategy */
    private $_mockMethodStrategy;
    private $_traversalMode;
    private $_rootPage;
    private $_method;

    /** @var PageTraverser */
    private $_traverser;

    function setUp()
    {
        $this->_mockNormalStrategy = mock('XssFinder\Runner\CustomNormalTraversalStrategy');
        $this->_mockSubmitStrategy = mock('XssFinder\Runner\CustomSubmitTraversalStrategy');
        $this->_mockMethodStrategy = mock('XssFinder\Runner\SimpleMethodTraversalStrategy');
        $this->_traversalMode = TraversalMode::NORMAL;

        $this->_rootPage = new \PageTraverser_TestPages\RootPage();
        $rootPageClass = new \ReflectionClass('\PageTraverser_TestPages\RootPage');
        $this->_method = $rootPageClass->getMethod('goToSomePage');

        $this->_traverser = new PageTraverser(
            $this->_mockNormalStrategy,
            $this->_mockSubmitStrategy,
            $this->_mockMethodStrategy
        );
    }

    function testTraversalSatisfiedByNormalStrategyIsDelegatedToNormalStrategy()
    {
        // given
        $mockResult = $this->_mockCanSatisfyAndTraverseForStrategy($this->_mockNormalStrategy, $this->_traversalMode);

        // when
        $result = $this->_traverser->traverse($this->_rootPage, $this->_method, $this->_traversalMode);

        // then
        assertThat($result, is($mockResult));
    }

    function testTraversalSatisfiedBySubmitStrategyIsDelegatedToCustomSubmitStrategy()
    {
        // given
        $mockResult = $this->_mockCanSatisfyAndTraverseForStrategy($this->_mockSubmitStrategy, $this->_traversalMode);

        // when
        $result = $this->_traverser->traverse($this->_rootPage, $this->_method, $this->_traversalMode);

        // then
        assertThat($result, is($mockResult));
    }

    function testTraversalSatisfiedBySimpleMethodStrategyIsDelegatedToSimpleMethodStrategy()
    {
        // given
        $mockResult = $this->_mockCanSatisfyAndTraverseForStrategy($this->_mockMethodStrategy, $this->_traversalMode);

        // when
        $result = $this->_traverser->traverse($this->_rootPage, $this->_method, $this->_traversalMode);

        // then
        assertThat($result, is($mockResult));
    }

    /**
     * @expectedException \XssFinder\TUntraversableException
     */
    function testExceptionThrownIfNoStrategySatisfiesTraversal()
    {
        // when
        $this->_traverser->traverse($this->_rootPage, $this->_method, $this->_traversalMode);
    }

    /**
     * @param TraversalStrategy $mockStrategy
     * @param int $traversalMode
     * @return TraversalResult
     */
    private function _mockCanSatisfyAndTraverseForStrategy(
        TraversalStrategy $mockStrategy, $traversalMode
    ) {
        $mockResult = mock('XssFinder\Runner\TraversalResult');
        when($mockStrategy->canSatisfyMethod($this->_method, $traversalMode))->return(true);
        when($mockStrategy->traverse($this->_rootPage, $this->_method))->return($mockResult);
        return $mockResult;
    }
}

namespace PageTraverser_TestPages;

class RootPage {
    function goToSomePage() {}
}