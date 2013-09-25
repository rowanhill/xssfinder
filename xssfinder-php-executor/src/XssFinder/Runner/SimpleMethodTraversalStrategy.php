<?php

namespace XssFinder\Runner;

use ReflectionMethod;
use XssFinder\TraversalMode;
use XssFinder\TUntraversableException;

class SimpleMethodTraversalStrategy implements TraversalStrategy
{
    /**
     * @param ReflectionMethod $method
     * @param int $traversalMode
     * @return boolean
     */
    function canSatisfyMethod(ReflectionMethod $method, $traversalMode)
    {
        return true;
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
        if ($method->getNumberOfRequiredParameters() > 0)
        {
            throw new TUntraversableException();
        }
        $newPage = $method->invoke($page);
        return new TraversalResult($newPage, array());
    }
}