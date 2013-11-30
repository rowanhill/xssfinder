<?php

namespace XssFinder\Scanner;

use mindplay\annotations\standard\ReturnAnnotation;
use ReflectionMethod;
use SubmitActionAnnotation;
use XssFinder\Annotations\Annotations;

class ReflectionHelper
{
    /**
     * @param ReflectionMethod $method
     * @return null|string The fully qualified class name of the method's return type, or null if no such class exists
     *                     or no return attribute is given
     */
    public function getReturnType(ReflectionMethod $method)
    {
        $annotationsManager = Annotations::getConfiguredManager();

        $returnAnnotations = $annotationsManager->getMethodAnnotations($method, null, '@return');
        if (empty($returnAnnotations)) {
            return null;
        }
        /** @var ReturnAnnotation $returnAnnotation */
        $returnAnnotation = current($returnAnnotations);
        $returnClassName = $returnAnnotation->type;

        if (strpos($returnClassName, '\\') !== 0) {
            $returnClassName = '\\'.$returnClassName;
        }

        if (!class_exists($returnClassName)) {
            $returnClassName = '\\' . $method->getDeclaringClass()->getNamespaceName() . $returnClassName;
        }
        if (!class_exists($returnClassName)) {
            $returnClassName = null;
        }

        return $returnClassName;
    }

    /**
     * @param ReflectionMethod $method
     * @return boolean True if the method is submitAction annotated, false if not or the method does not exist
     */
    public function isSubmitAnnotated($method)
    {
        return $this->_hasAnnotation($method, '@submitAction');
    }

    public function isTraverseWithAnnotated($method)
    {
        return $this->_hasAnnotation($method, '@traverseWith');
    }

    private function _hasAnnotation($method, $annotationName)
    {
        $annotationsManager = Annotations::getConfiguredManager();

        $annotations = $annotationsManager->getMethodAnnotations($method, null, $annotationName);
        $annotation  = current($annotations);
        return !!$annotation;
    }
}