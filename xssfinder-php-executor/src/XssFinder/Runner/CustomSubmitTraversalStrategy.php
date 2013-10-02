<?php

namespace XssFinder\Runner;

use ReflectionMethod;
use XssFinder\TraversalMode;
use XssFinder\TUntraversableException;

class CustomSubmitTraversalStrategy implements TraversalStrategy
{
    /** @var CustomSubmitterInstantiator */
    private $_customSubmitterInstantiator;

    public function __construct(CustomSubmitterInstantiator $customSubmitterInstantiator)
    {
        $this->_customSubmitterInstantiator = $customSubmitterInstantiator;
    }

    /**
     * @param ReflectionMethod $method
     * @param int $traversalMode
     * @return boolean
     */
    function canSatisfyMethod(ReflectionMethod $method, $traversalMode)
    {
        return $traversalMode === TraversalMode::SUBMIT && $this->_customSubmitterInstantiator->hasCustomSubmitter($method);
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
        $submitter = $this->_customSubmitterInstantiator->instantiate($method);
        $newPage = $submitter->submit($page, null); //TODO: Pass LabelledXssGenerator
        $traversalResult = new TraversalResult($newPage, array()); //TODO: Return LXG's recorded results
        return $traversalResult;
    }
}