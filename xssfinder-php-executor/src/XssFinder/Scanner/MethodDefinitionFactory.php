<?php

namespace XssFinder\Scanner;

use XssFinder\MethodDefinition;

class MethodDefinitionFactory
{
    /** @var \XssFinder\Scanner\ReflectionHelper */
    private $_reflectionHelper;

    public function __construct(ReflectionHelper $reflectionHelper)
    {
        $this->_reflectionHelper = $reflectionHelper;
    }

    /**
     * @param $method \ReflectionMethod
     * @return MethodDefinition
     */
    public function createMethodDefinition(\ReflectionMethod $method)
    {
        $methodDefinition = new MethodDefinition();
        $methodDefinition->identifier = $method->getName();
        $methodDefinition->returnTypeIdentifier = $this->_reflectionHelper->getReturnType($method);
        return $methodDefinition;
    }
}