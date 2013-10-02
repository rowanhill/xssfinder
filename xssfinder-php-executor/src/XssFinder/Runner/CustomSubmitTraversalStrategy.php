<?php

namespace XssFinder\Runner;

use ReflectionMethod;
use XssFinder\TraversalMode;
use XssFinder\TUntraversableException;

class CustomSubmitTraversalStrategy implements TraversalStrategy
{
    /** @var CustomSubmitterInstantiator */
    private $_customSubmitterInstantiator;
    /** @var LabelledXssGeneratorImpl */
    private $_labelledXssGenerator;

    public function __construct(
        CustomSubmitterInstantiator $customSubmitterInstantiator,
        LabelledXssGeneratorImpl $labelledXssGenerator
    ) {
        $this->_customSubmitterInstantiator = $customSubmitterInstantiator;
        $this->_labelledXssGenerator = $labelledXssGenerator;
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
        $newPage = $submitter->submit($page, $this->_labelledXssGenerator);
        $traversalResult = new TraversalResult($newPage, $this->_labelledXssGenerator->getLabelsToAttackIds());
        return $traversalResult;
    }
}