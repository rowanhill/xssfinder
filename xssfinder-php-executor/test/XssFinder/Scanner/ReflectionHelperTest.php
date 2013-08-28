<?php

namespace XssFinder\Scanner;

use PHPUnit_Framework_TestCase;

class ReflectionHelperTest extends PHPUnit_Framework_TestCase
{
    public function testReturnTypeIsFoundForSimpleName()
    {
        // given
        $reflectionClass = new \ReflectionClass('\ReflectionHelperTest\TestObjects\SomeObject');
        $method = $reflectionClass->getMethod('getSomeObjectSimple');
        $reflectionHelper = new ReflectionHelper();

        // when
        $returnClassName = $reflectionHelper->getReturnType($method);

        //then
        assertThat($returnClassName, equalTo('\ReflectionHelperTest\TestObjects\SomeObject'));
    }

    public function testReturnTypeIsFoundForNamespacedName()
    {
        // given
        $reflectionClass = new \ReflectionClass('\ReflectionHelperTest\TestObjects\SomeObject');
        $method = $reflectionClass->getMethod('getSomeObjectNamespaced');
        $reflectionHelper = new ReflectionHelper();

        // when
        $returnClassName = $reflectionHelper->getReturnType($method);

        //then
        assertThat($returnClassName, equalTo('\ReflectionHelperTest\TestObjects\SomeObject'));
    }
}

namespace ReflectionHelperTest\TestObjects;

class SomeObject
{
    /**
     * @return SomeObject
     */
    public function getSomeObjectSimple() { return new SomeObject(); }

    /**
     * @return \ReflectionHelperTest\TestObjects\SomeObject
     */
    public function getSomeObjectNamespaced() { return new SomeObject(); }
}