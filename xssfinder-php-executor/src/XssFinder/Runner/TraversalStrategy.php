<?php

namespace XssFinder\Runner;

use ReflectionMethod;
use XssFinder\TraversalMode;
use XssFinder\TUntraversableException;

interface TraversalStrategy
{
    /**
     * @param ReflectionMethod $method
     * @param TraversalMode $traversalMode
     * @return boolean
     */
    function canSatisfyMethod(ReflectionMethod $method, TraversalMode $traversalMode);


    /**
     * @param mixed $page
     * @param ReflectionMethod $method
     * @return TraversalResult
     *
     * @throws TUntraversableException
     */
    function traverse($page, ReflectionMethod $method);
}