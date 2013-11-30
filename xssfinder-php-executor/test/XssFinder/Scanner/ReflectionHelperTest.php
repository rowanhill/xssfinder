<?php

namespace XssFinder\Scanner;

require_once 'ReflectionHelperTest_SomeObject.php';

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

    public function testReturnTypeHasLeadingSlashForSimpleNameInGlobalNamespace()
    {
        // given
        $reflectionClass = new \ReflectionClass('\ReflectionHelperTest_SomeObject');
        $method = $reflectionClass->getMethod('getSomeObject');
        $reflectionHelper = new ReflectionHelper();

        // when
        $returnClassName = $reflectionHelper->getReturnType($method);

        //then
        assertThat($returnClassName, equalTo('\ReflectionHelperTest_SomeObject'));
    }

    public function testMethodIsSubmitAnnotatedIfMethodHasSubmitActionAnnotation()
    {
        // given
        $reflectionClass = new \ReflectionClass('\ReflectionHelperTest\TestObjects\SomeObject');
        $method = $reflectionClass->getMethod('submit');
        $reflectionHelper = new ReflectionHelper();

        // when
        $isSubmit = $reflectionHelper->isSubmitAnnotated($method);

        // then
        assertThat($isSubmit, equalTo(true));
    }

    public function testMethodIsNotSubmitAnnotatedIfMethodDoesNotHaveSubmitActionAnnotation()
    {
        // given
        $reflectionClass = new \ReflectionClass('\ReflectionHelperTest\TestObjects\SomeObject');
        $method = $reflectionClass->getMethod('getSomeObjectSimple');
        $reflectionHelper = new ReflectionHelper();

        // when
        $isSubmit = $reflectionHelper->isSubmitAnnotated($method);

        // then
        assertThat($isSubmit, equalTo(false));
    }

    public function testMethodIsTraverserAnnotatedIfMethodHasTraverseWithAnnotation()
    {
        // given
        $reflectionClass = new \ReflectionClass('\ReflectionHelperTest\TestObjects\SomeObject');
        $method = $reflectionClass->getMethod('submit');
        $reflectionHelper = new ReflectionHelper();

        // when
        $isCustomTraversed = $reflectionHelper->isTraverseWithAnnotated($method);

        // then
        assertThat($isCustomTraversed, equalTo(true));
    }

    public function testMethodIsNotTraverserAnnotatedIfMethodDoesNotHaveTraverseWithAnnotation()
    {
        // given
        $reflectionClass = new \ReflectionClass('\ReflectionHelperTest\TestObjects\SomeObject');
        $method = $reflectionClass->getMethod('getSomeObjectSimple');
        $reflectionHelper = new ReflectionHelper();

        // when
        $isCustomTraversed = $reflectionHelper->isTraverseWithAnnotated($method);

        // then
        assertThat($isCustomTraversed, equalTo(false));
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

    /**
     * @return \stdClass
     */
    public function getStdClass() { return new \stdClass(); }

    /**
     * @submitAction
     * @traverseWith
     */
    public function submit() { }
}