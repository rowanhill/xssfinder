<?php

namespace XssFinder\Runner;

use ReflectionMethod;
use SubmitActionAnnotation;
use XssFinder\Annotations\Annotations;
use XssFinder\Annotations\CustomSubmitter;
use XssFinder\Annotations\ExactlyOneAnnotationRequiredException;

class CustomSubmitterInstantiator
{
    /**
     * @param ReflectionMethod $method
     * @return CustomSubmitter
     * @throws InvalidCustomSubmitterException
     * @throws NonExistentCustomSubmitterException
     */
    public function instantiate(ReflectionMethod $method)
    {
        $annotation = SubmitActionAnnotation::getSubmitAction($method);
        $submitterClassName = $annotation->submitterClass;

        if (!class_exists($submitterClassName)) {
            throw new NonExistentCustomSubmitterException("Custom submitter class $submitterClassName cannot be found");
        }

        $submitter = new $submitterClassName();

        $customSubmitterInterface = 'XssFinder\Annotations\CustomSubmitter';
        if (!($submitter instanceof $customSubmitterInterface)) {
            throw new InvalidCustomSubmitterException("$submitterClassName is not an instance of $customSubmitterInterface");
        }

        return $submitter;
    }

    public function hasCustomSubmitter(ReflectionMethod $method)
    {
        try {
            $annotation = SubmitActionAnnotation::getSubmitAction($method);
            return $annotation->submitterClass !== null;
        } catch (ExactlyOneAnnotationRequiredException $e) {
            return false;
        }
    }
}

class InvalidCustomSubmitterException extends \Exception {
    function __construct($message) { parent::__construct($message); }
}

class NonExistentCustomSubmitterException extends \Exception {
    function __construct($message) { parent::__construct($message); }
}