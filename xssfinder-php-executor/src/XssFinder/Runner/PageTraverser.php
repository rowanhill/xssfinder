<?php

namespace XssFinder\Runner;

use ReflectionMethod;

class PageTraverser
{
    /** @var SimpleMethodTraversalStrategy */
    private $_simpleMethodStrategy;

    function __construct(SimpleMethodTraversalStrategy $simpleMethodStrategy)
    {
        $this->_simpleMethodStrategy = $simpleMethodStrategy;
    }

    /**
     * @param $page
     * @param ReflectionMethod $method
     * @param int $traversalMode
     * @return TraversalResult
     */
    function traverse($page, ReflectionMethod $method, $traversalMode)
    {
        if ($this->_simpleMethodStrategy->canSatisfyMethod($method, $traversalMode)) {
            return $this->_simpleMethodStrategy->traverse($page, $method);
        }
        return null;
    }

}