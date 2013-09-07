<?php

namespace XssFinder\Scanner;

use PHPUnit_Framework_TestCase;
use ReflectionClass;

class ThriftToReflectionLookupTest extends PHPUnit_Framework_TestCase
{
    const PAGE_ID = "SomePage";
    const METHOD_ID = "someMethod";

    function testPageMappingIsEmptyOnConstruction()
    {
        // given
        $lookup = new ThriftToReflectionLookup();

        // when
        $pageClass = $lookup->getPageClass(self::PAGE_ID);

        // then
        assertThat($pageClass, is(nullValue()));
    }

    function testMethodMappingIsEmptyOnConstruction()
    {
        // given
        $lookup = new ThriftToReflectionLookup();

        // when
        $method = $lookup->getMethod(self::METHOD_ID);

        // then
        assertThat($method, is(nullValue()));
    }

    function testSetPageClassIsAvailableFromLookup()
    {
        // given
        $lookup = new ThriftToReflectionLookup();
        $reflectionClass = new ReflectionClass('\XssFinder\Scanner\TTRL_TestPages_SomePage');
        $lookup->putPageClass(self::PAGE_ID, $reflectionClass);

        // when
        $pageClass = $lookup->getPageClass(self::PAGE_ID);

        // then
        assertThat($pageClass, is($reflectionClass));
    }

    function testSetMethodIsAvailableFromLookup()
    {
        // given
        $lookup = new ThriftToReflectionLookup();
        $reflectionClass = new ReflectionClass('\XssFinder\Scanner\TTRL_TestPages_SomePage');
        $someMethod = $reflectionClass->getMethod('someMethod');
        $lookup->putMethod(self::METHOD_ID, $someMethod);

        // when
        $method = $lookup->getMethod(self::METHOD_ID);

        // then
        assertThat($method, is($someMethod));
    }
}

class TTRL_TestPages_SomePage {
    function someMethod() {}
}