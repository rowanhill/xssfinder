<?php

namespace XssFinder\Runner;

require_once('_CustomSubmitterInstantiator_TestObjects.php');

use ReflectionClass;
use XssFinder\Annotations\Annotations;

class CustomSubmitterInstantiatorTest extends \PHPUnit_Framework_TestCase
{
    /** @var ReflectionClass */
    private $_testPageClass;

    /** @var CustomSubmitterInstantiator */
    private $_instantiator;

    function setUp()
    {
        Annotations::load();
        $this->_testPageClass = new ReflectionClass('\CSI_Test\TestPage');
        $this->_instantiator = new CustomSubmitterInstantiator();
    }

    function testUnannotatedMethodsDoNotHaveCustomSubmitter()
    {
        // given
        $method = $this->_testPageClass->getMethod('methodWithNoAnnotation');

        // when
        $hasCustomTraverser = $this->_instantiator->hasCustomSubmitter($method);

        // then
        assertThat($hasCustomTraverser, is(false));
    }

    function testSubmitActionMethodsWithoutSpecifiedSubmitterDoNoHaveCustomSubmitter()
    {
        // given
        $method = $this->_testPageClass->getMethod('methodWithAnnotationButNoSubmitter');

        // when
        $hasCustomTraverser = $this->_instantiator->hasCustomSubmitter($method);

        // then
        assertThat($hasCustomTraverser, is(false));
    }

    function testSubmitActionMethodsWithSpecifiedSubmittersHaveCustomSubmitters()
    {
        // given
        $method = $this->_testPageClass->getMethod('methodWithAnnotationAndSubmitter');

        // when
        $hasCustomTraverser = $this->_instantiator->hasCustomSubmitter($method);

        // then
        assertThat($hasCustomTraverser, is(true));
    }

    function testInstantiatesObjectOfTypeSpecifiedInSubmitAction()
    {
        // given
        $method = $this->_testPageClass->getMethod('methodWithAnnotationAndSubmitter');

        // when
        $customTraverser = $this->_instantiator->instantiate($method);

        // then
        assertThat($customTraverser, is(anInstanceOf('CSI_Test\CustomSubmitter')));
    }

    /**
     * @expectedException \XssFinder\Runner\InvalidCustomSubmitterException
     */
    function testExceptionThrownIfSpecifiedSubmitterDoesNotImplementCustomSubmitter()
    {
        // given
        $method = $this->_testPageClass->getMethod('methodWithInvalidCustomSubmitter');

        // when
        $this->_instantiator->instantiate($method);
    }

    /**
     * @expectedException \XssFinder\Runner\NonExistentCustomSubmitterException
     */
    function testExceptionThrownIfSpecifiedSubmitterDoesNotExist()
    {
        // given
        $method = $this->_testPageClass->getMethod('methodWithNonExistentCustomSubmitter');

        // when
        $this->_instantiator->instantiate($method);
    }
}