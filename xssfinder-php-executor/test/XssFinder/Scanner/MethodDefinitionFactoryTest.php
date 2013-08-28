<?php

namespace XssFinder\Scanner;

class MethodDefinitionFactoryTest extends \PHPUnit_Framework_TestCase
{
    const SOME_PAGE = '\MethodDefinitionFactory\TestPages\SomePage';

    /** @var ReflectionHelper */
    private $_mockReflectionHelper;

    /** @var MethodDefinitionFactory */
    private $_factory;

    public function setUp()
    {
        $this->_mockReflectionHelper = mock('XssFinder\Scanner\ReflectionHelper');

        $this->_factory = new MethodDefinitionFactory($this->_mockReflectionHelper);
    }

    public function testDefinitionIdentifierIsMethodName()
    {
        //given
        $method = $this->_getMethod(self::SOME_PAGE, 'goToSomePage');

        // when
        $methodDefinition = $this->_factory->createMethodDefinition($method);

        // then
        assertThat($methodDefinition->identifier, equalTo('goToSomePage'));
    }

    public function testReturnTypeIsMethodReturnType()
    {
        // given
        $method = $this->_getMethod(self::SOME_PAGE, 'goToSomePage');
        when($this->_mockReflectionHelper->getReturnType($method))->return(self::SOME_PAGE);

        // when
        $methodDefinition = $this->_factory->createMethodDefinition($method);

        // then
        assertThat($methodDefinition->returnTypeIdentifier, equalTo(self::SOME_PAGE));
    }

    /**
     * @param $className
     * @param $methodName
     * @return \ReflectionMethod
     */
    private function _getMethod($className, $methodName)
    {
        $reflectionClass = new \ReflectionClass($className);
        return $reflectionClass->getMethod($methodName);
    }
}

namespace MethodDefinitionFactory\TestPages;

class SomePage
{
    /**
     * @return SomePage
     */
    public function goToSomePage() { return new SomePage(); }
}