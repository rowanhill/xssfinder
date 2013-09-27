<?php

namespace XssFinder\Runner;

require_once('_CustomTraverserInstantiator_TestObjects.php');

use PHPUnit_Framework_TestCase;
use ReflectionClass;
use XssFinder\Annotations\Annotations;

class CustomTraverserInstantiatorTest extends PHPUnit_Framework_TestCase
{
    /** @var ReflectionClass */
    private $_testPageClass;

    /** @var CustomTraverserInstantiator */
    private $_instantiator;

    function setUp()
    {
        Annotations::load();
        $this->_testPageClass = new ReflectionClass('\CTI_Test\CTI_TestPage');
        $this->_instantiator = new CustomTraverserInstantiator();
    }

    function testInstantiatesObjectOfTypeSpecifiedInTraverseWith()
    {
        // given
        $method = $this->_testPageClass->getMethod('methodWithCustomTraverser');

        // when
        $customTraverser = $this->_instantiator->instantiate($method);

        // then
        assertThat($customTraverser, is(anInstanceOf('CTI_Test\CTI_Test_CustomTraverser')));
    }

    /**
     * @expectedException \XssFinder\Runner\InvalidCustomTraverserException
     */
    function testExceptionThrownIfSpecifiedTraverserDoesNotImplementCustomTraverser()
    {
        // given
        $method = $this->_testPageClass->getMethod('methodWithInvalidCustomTraverser');

        // when
        $this->_instantiator->instantiate($method);
    }

    /**
     * @expectedException \XssFinder\Runner\NonExistentCustomTraverserException
     */
    function testExceptionThrownIfSpecifiedTraverserDoesNotExist()
    {
        // given
        $method = $this->_testPageClass->getMethod('methodWithNonExistentCustomTraverser');

        // when
        $this->_instantiator->instantiate($method);
    }

    function testAnnotatedMethodsHaveCustomTraversers()
    {
        // given
        $method = $this->_testPageClass->getMethod('methodWithCustomTraverser');

        // when
        $hasCustomTraverser = $this->_instantiator->hasCustomTraverser($method);

        // then
        assertThat($hasCustomTraverser, is(true));
    }

    function testUnannotatedMethodsDoNotHaveCustomTraversers()
    {
        // given
        $method = $this->_testPageClass->getMethod('methodWithNoAnnotation');

        // when
        $hasCustomTraverser = $this->_instantiator->hasCustomTraverser($method);

        // then
        assertThat($hasCustomTraverser, is(false));
    }
}