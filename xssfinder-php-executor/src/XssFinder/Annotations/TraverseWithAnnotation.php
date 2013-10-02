<?php
use XssFinder\Annotations\Annotations;
use XssFinder\Annotations\ExactlyOneAnnotationRequiredException;

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
            throw new ExactlyOneAnnotationRequiredException('No @traverseWith annotation found on ' . $method->getName());
        } elseif (count($annotations) > 1) {
            throw new ExactlyOneAnnotationRequiredException('Multiple @traverseWith annotation found on ' . $method->getName());
        }
        $annotation = current($annotations);
        return $annotation;
    }
}