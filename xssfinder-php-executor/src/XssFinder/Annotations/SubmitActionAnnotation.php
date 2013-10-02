<?php

use XssFinder\Annotations\Annotations;
use XssFinder\Annotations\ExactlyOneAnnotationRequiredException;

/**
 * @usage('method' => true)
 */
class SubmitActionAnnotation extends mindplay\annotations\Annotation
{
    /** @var string */
    public $submitterClass = null;

    /**
     * @param ReflectionMethod $method
     * @throws ExactlyOneAnnotationRequiredException
     * @return \SubmitActionAnnotation
     */
    static function getSubmitAction(ReflectionMethod $method)
    {
        $annotationsManager = Annotations::getConfiguredManager();
        $annotations = $annotationsManager->getMethodAnnotations($method, null, '@submitAction');
        if (count($annotations) != 1) {
            throw new ExactlyOneAnnotationRequiredException('Expected one @submitAction but found ' . count($annotations));
        }
        return current($annotations);
    }
}