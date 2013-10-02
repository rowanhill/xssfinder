<?php

namespace XssFinder\Runner;

use ReflectionMethod;
use XssFinder\TUntraversableException;

class PageTraverser
{
    /** @var CustomNormalTraversalStrategy */
    private $_customNormalStrategy;
    /** @var CustomSubmitTraversalStrategy */
    private $_customSubmitStrategy;
    /** @var SimpleMethodTraversalStrategy */
    private $_simpleMethodStrategy;

    function __construct(
        CustomNormalTraversalStrategy $customNormalStrategy,
        CustomSubmitTraversalStrategy $customSubmitStrategy,
        SimpleMethodTraversalStrategy $simpleMethodStrategy
    ) {
        $this->_customNormalStrategy = $customNormalStrategy;
        $this->_customSubmitStrategy = $customSubmitStrategy;
        $this->_simpleMethodStrategy = $simpleMethodStrategy;
    }

    /**
     * @param $page
     * @param ReflectionMethod $method
     * @param int $traversalMode
     * @throws \XssFinder\TUntraversableException
     * @return TraversalResult
     */
    function traverse($page, ReflectionMethod $method, $traversalMode)
    {
        $orderedStrategies = array(
            $this->_customNormalStrategy,
            $this->_customSubmitStrategy,
            $this->_simpleMethodStrategy
        );
        foreach ($orderedStrategies as $strategy) {
            /** @var TraversalStrategy $strategy */
            if ($strategy->canSatisfyMethod($method, $traversalMode)) {
                return $strategy->traverse($page, $method);
            }
        }
        throw new TUntraversableException(array('message' => 'Cannot traverse method'));
    }

}