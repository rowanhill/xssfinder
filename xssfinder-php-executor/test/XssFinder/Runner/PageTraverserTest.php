<?php

namespace XssFinder\Runner;

use XssFinder\TraversalMode;

class PageTraverserTest extends \PHPUnit_Framework_TestCase
{
    private $_mockMethodStrategy;
    private $_traversalMode;
    private $_rootPage;
    private $_method;

    /** @var PageTraverser */
    private $_traverser;

    function setUp()
    {
        $this->_mockMethodStrategy = mock('XssFinder\Runner\SimpleMethodTraversalStrategy');
        $this->_traversalMode = TraversalMode::NORMAL;

        $this->_rootPage = new \PageTraverser_TestPages\RootPage();
        $rootPageClass = new \ReflectionClass('\PageTraverser_TestPages\RootPage');
        $this->_method = $rootPageClass->getMethod('goToSomePage');

        $this->_traverser = new PageTraverser($this->_mockMethodStrategy);
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