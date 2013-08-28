<?php

namespace XssFinder\Scanner;

use mindplay\annotations\standard\ReturnAnnotation;
use ReflectionMethod;
use XssFinder\Annotations\Annotations;

class ReflectionHelper
{
    /**
     * @param ReflectionMethod $method
     * @return null|string The fully qualified class name of the method's return type, or null if no such class exists
     */
    public function getReturnType(ReflectionMethod $method)
    {
        $annotationsManager = Annotations::getConfiguredManager();

        $returnAnnotations = $annotationsManager->getMethodAnnotations($method, null, '@return');
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
}