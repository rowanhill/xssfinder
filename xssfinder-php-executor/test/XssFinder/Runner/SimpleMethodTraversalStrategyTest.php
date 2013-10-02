<?php

namespace XssFinder\Runner;

use PHPUnit_Framework_TestCase;
use ReflectionClass;
use ReflectionMethod;
use XssFinder\TraversalMode;

class SimpleMethodTraversalStrategyTest extends PHPUnit_Framework_TestCase
{
    /** @var SimpleMethodTraversalStrategy */
    private $_strategy;
    /** @var ReflectionMethod */
    private $_method;

    function setUp()
    {
        $rootPageClass = new ReflectionClass('SimpleMethodTraversalStrategy_TestPages\RootPage');
        $this->_method = $rootPageClass->getMethod('visitSomePage');

        $this->_strategy = new SimpleMethodTraversalStrategy();
    }

    function testCanSatisfyNormalTraversalMode() {
        // when
        $canSatisfy = $this->_strategy->canSatisfyMethod($this->_method, TraversalMode::NORMAL);

        // then
        assertThat($canSatisfy, is(true));
    }

    function testCanSatisfySubmitTraversalMode() {
        // when
        $canSatisfy = $this->_strategy->canSatisfyMethod($this->_method, TraversalMode::SUBMIT);

        // then
        assertThat($canSatisfy, is(true));
    }

    function testTraversingInvokesMethod()
    {
        // given
        $rootPage = new \SimpleMethodTraversalStrategy_TestPages\RootPage();

        // when
        $traversalResult = $this->_strategy->traverse($rootPage, $this->_method);

        // then
        assertThat($traversalResult->getPage(), is(anInstanceOf('SimpleMethodTraversalStrategy_TestPages\SecondPage')));
    }

    /**
     * @expectedException \Exception
     * @expectedExceptionMessage Test exception
     */
    function testTraversingMethodThatEncountersExceptionDoesNotCatchException()
    {
        // given
        $rootPage = new \SimpleMethodTraversalStrategy_TestPages\RootPage();
        $rootPageClass = new ReflectionClass('SimpleMethodTraversalStrategy_TestPages\RootPage');
        $this->_method = $rootPageClass->getMethod('raiseException');

        // when
        $this->_strategy->traverse($rootPage, $this->_method);
    }

    /**
     * @expectedException \XssFinder\TUntraversableException
     */
    function testTraversingMethodWithArgumentsThrowsTUntraversableException()
    {
        // given
        $rootPage = new \SimpleMethodTraversalStrategy_TestPages\RootPage();
        $rootPageClass = new ReflectionClass('SimpleMethodTraversalStrategy_TestPages\RootPage');
        $this->_method = $rootPageClass->getMethod('withParameter');

        // when
        $this->_strategy->traverse($rootPage, $this->_method);
    }
}

namespace SimpleMethodTraversalStrategy_TestPages;

class RootPage {
    public function visitSomePage() {
        return new SecondPage();
    }
    public function raiseException() {
        throw new \Exception('Test exception');
    }
    public function withParameter(\stdClass $param) {
        return new SecondPage();
    }
}

class SecondPage {}