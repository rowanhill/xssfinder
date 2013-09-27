<?php
use XssFinder\Annotations\Annotations;

/**
 * @usage('method' => true)
 */
class TraverseWithAnnotation extends mindplay\annotations\Annotation
{
    /** @var string */
    public $traverserClass;

    /**
     * Get the TraverseWithAnnotation on the given class
     *
     * @param ReflectionMethod $method Method to get the annotation from
     * @return TraverseWithAnnotation The traverseWith annotation
     * @throws Exception If not exactly one such annotation is found
     */
    public static function getTraverseWith(ReflectionMethod $method)
    {
        $annotationsManager = Annotations::getConfiguredManager();
        $annotations = $annotationsManager->getMethodAnnotations($method, null, '@traverseWith');
        if (count($annotations) == 0) {
            throw new Exception('No @traverseWith annotation found on ' . $method->getName());
        } elseif (count($annotations) > 1) {
            throw new Exception('Multiple @traverseWith annotation found on ' . $method->getName());
        }
        $annotation = current($annotations);
        return $annotation;
    }

    public static function isAnnotated(ReflectionMethod $method)
    {
        $annotationsManager = Annotations::getConfiguredManager();
        $annotations = $annotationsManager->getMethodAnnotations($method, null, '@traverseWith');
        return count($annotations) > 0;
    }
}