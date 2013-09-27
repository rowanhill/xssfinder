<?php

namespace XssFinder\Runner;

use ReflectionMethod;
use XssFinder\Annotations\CustomTraverser;

class CustomTraverserInstantiator
{
    /**
     * @param ReflectionMethod $method
     * @throws InvalidCustomTraverserException
     * @throws NonExistentCustomTraverserException
     * @return CustomTraverser
     */
    public function instantiate(ReflectionMethod $method)
    {
        $traverseWith = \TraverseWithAnnotation::getTraverseWith($method);
        $customTraverserClassName = $traverseWith->traverserClass;
        if (!class_exists($customTraverserClassName)) {
            throw new NonExistentCustomTraverserException("Custom traverser class $customTraverserClassName cannot be found");
        }
        $customTraverser = new $customTraverserClassName;
        $customTraverserInterface = 'XssFinder\Annotations\CustomTraverser';
        if (!($customTraverser instanceof $customTraverserInterface)) {
            throw new InvalidCustomTraverserException("$customTraverserClassName is not an instance of $customTraverserInterface");
        }
        return $customTraverser;
    }
}

class InvalidCustomTraverserException extends \Exception
{
    public function __construct($message = 'Custom traverser must implement CustomTraverser interface')
    {
        parent::__construct($message);
    }
}

class NonExistentCustomTraverserException extends \Exception
{
    public function __construct($message = 'Custom traverser class cannot be found')
    {
        parent::__construct($message);
    }
}