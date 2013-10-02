<?php

namespace XssFinder\Runner;

use PHPUnit_Framework_TestCase;
use ReflectionClass;
use ReflectionMethod;
use XssFinder\Annotations\CustomTraverser;
use XssFinder\TraversalMode;

class CustomNormalTraversalStrategyTest extends PHPUnit_Framework_TestCase
{
    /** @var CustomTraverserInstantiator */
    private $_mockInstantiator;

    /** @var CustomNormalTraversalStrategy */
    private $_strategy;
    /** @var ReflectionMethod */
    private $_method;

    function setUp()
    {
        $rootPageClass = new ReflectionClass('CustomNormalTraversalStrategy_TestPages\RootPage');
        $this->_method = $rootPageClass->getMethod('visitSomePage');

        $this->_mockInstantiator = mock('XssFinder\Runner\CustomTraverserInstantiator');
        $this->_strategy = new CustomNormalTraversalStrategy($this->_mockInstantiator);
    }

    function testCannotSatisfySubmissionTraversalMode()
    {
        // when
        $canSatisfy = $this->_strategy->canSatisfyMethod($this->_method, TraversalMode::SUBMIT);

        // then
        assertThat($canSatisfy, is(false));
    }

    function testCannotSatisfyNormalTraversalModeIfMethodHasNoCustomTraverser()
    {
        // when
        $canSatisfy = $this->_strategy->canSatisfyMethod($this->_method, TraversalMode::NORMAL);

        // then
        assertThat($canSatisfy, is(false));
    }

    function testCanSatisfyNormalTraversalModeIfMethodHasCustomTraverser()
    {
        // given
        when($this->_mockInstantiator->hasCustomTraverser($this->_method))->return(true);

        // when
        $canSatisfy = $this->_strategy->canSatisfyMethod($this->_method, TraversalMode::NORMAL);

        // then
        assertThat($canSatisfy, is(true));
    }

    function testTraversingIsDelegatedToCustomTraverser()
    {
        // given
        /** @var CustomTraverser $mockTraverser */
        $mockTraverser = mock('XssFinder\Annotations\CustomTraverser');
        when($this->_mockInstantiator->instantiate($this->_method))->return($mockTraverser);
        $mockPageOne = new \stdClass();
        $mockPageOne->page = 'one';
        $mockPageTwo = new \stdClass();
        $mockPageTwo->page = 'two';
        when($mockTraverser->traverse($mockPageOne))->return($mockPageTwo);

        // when
        $traversalResult = $this->_strategy->traverse($mockPageOne, $this->_method);

        // then
        assertThat($traversalResult->getPage(), is($mockPageTwo));
    }
}

namespace CustomNormalTraversalStrategy_TestPages;

class RootPage
{
    public function visitSomePage() {}
}