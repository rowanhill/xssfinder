<?php

namespace XssFinder\Runner;

use ReflectionClass;
use ReflectionMethod;
use XssFinder\Annotations\CustomSubmitter;
use XssFinder\TraversalMode;

class CustomSubmitTraversalStrategyTest extends \PHPUnit_Framework_TestCase
{
    /** @var CustomSubmitterInstantiator */
    private $_mockSubmitterInstantiator;

    /** @var ReflectionMethod */
    private $_method;

    /** @var CustomSubmitTraversalStrategy */
    private $_strategy;

    function setUp()
    {
        $this->_mockSubmitterInstantiator = mock('XssFinder\Runner\CustomSubmitterInstantiator');

        $rootPageClass = new ReflectionClass('CustomSubmitTraversalStrategy_TestPages\RootPage');
        $this->_method = $rootPageClass->getMethod('visitSomePage');

        $this->_strategy = new CustomSubmitTraversalStrategy($this->_mockSubmitterInstantiator);
    }

    function testCannotNormalSubmissionTraversalMode()
    {
        // when
        $canSatisfy = $this->_strategy->canSatisfyMethod($this->_method, TraversalMode::NORMAL);

        // then
        assertThat($canSatisfy, is(false));
    }

    function testCannotSatisfySubmitTraversalModeIfMethodHasNoCustomSubmitter()
    {
        // when
        $canSatisfy = $this->_strategy->canSatisfyMethod($this->_method, TraversalMode::SUBMIT);

        // then
        assertThat($canSatisfy, is(false));
    }

    function testCanSatisfySubmitTraversalModeIfMethodHasCustomSubmitter()
    {
        // given
        when($this->_mockSubmitterInstantiator->hasCustomSubmitter($this->_method))->return(true);

        // when
        $canSatisfy = $this->_strategy->canSatisfyMethod($this->_method, TraversalMode::SUBMIT);

        // then
        assertThat($canSatisfy, is(true));
    }

    function testTraversingIsDelegatedToCustomTraverser()
    {
        // given
        /** @var CustomSubmitter $mockSubmitter */
        $mockSubmitter = mock('XssFinder\Annotations\CustomSubmitter');
        when($this->_mockSubmitterInstantiator->instantiate($this->_method))->return($mockSubmitter);
        $mockPageOne = new \stdClass();
        $mockPageOne->page = 'one';
        $mockPageTwo = new \stdClass();
        $mockPageTwo->page = 'two';
        when($mockSubmitter->submit($mockPageOne, null))->return($mockPageTwo);

        // when
        $traversalResult = $this->_strategy->traverse($mockPageOne, $this->_method);

        // then
        assertThat($traversalResult->getPage(), is($mockPageTwo));
    }
}

namespace CustomSubmitTraversalStrategy_TestPages;

class RootPage
{
    public function visitSomePage() {}
}