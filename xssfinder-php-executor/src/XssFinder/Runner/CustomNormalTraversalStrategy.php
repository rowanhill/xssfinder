<?php

namespace XssFinder\Runner;

use ReflectionMethod;
use XssFinder\TraversalMode;
use XssFinder\TUntraversableException;

class CustomNormalTraversalStrategy implements TraversalStrategy
{
    /** @var CustomTraverserInstantiator */
    private $_instantiator;

    function __construct(CustomTraverserInstantiator $instantiator)
    {
        $this->_instantiator = $instantiator;
    }

    /**
     * @param ReflectionMethod $method
     * @param int $traversalMode
     * @return boolean
     */
    function canSatisfyMethod(ReflectionMethod $method, $traversalMode)
    {
        return $traversalMode === TraversalMode::NORMAL && $this->_instantiator->hasCustomTraverser($method);
    }

    /**
     * @param mixed $page
     * @param ReflectionMethod $method
     * @return TraversalResult
     *
     * @throws TUntraversableException
     */
    function traverse($page, ReflectionMethod $method)
    {
        $customTraverser = $this->_instantiator->instantiate($method);
        $newPage = $customTraverser->traverse($page);
        return new TraversalResult($newPage, array());
    }
}