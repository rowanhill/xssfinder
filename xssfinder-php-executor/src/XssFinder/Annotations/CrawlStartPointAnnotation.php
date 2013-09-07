<?php
use XssFinder\Annotations\Annotations;

/**
 * @usage('class' => true, 'inherited' => true)
 */
class CrawlStartPointAnnotation extends mindplay\annotations\Annotation
{
    /**
     * @var string The URL of the annotated page
     */
    public $url;

    /**
     * Get the CrawlStartPointAnnotation on the given class
     *
     * @param ReflectionClass $pageClass Page class to get the annotation from
     * @return CrawlStartPointAnnotation The crawlStartPoint annotation
     * @throws Exception If not exactly one such annotation is found
     */
    public static function getCrawlStartPoint($pageClass)
    {
        $annotationsManager = Annotations::getConfiguredManager();
        $annotations = $annotationsManager->getClassAnnotations($pageClass, '@crawlStartPoint');
        if (count($annotations) == 0) {
            throw new Exception('No @crawlStartPoint annotation found on ' . $pageClass->getName());
        } elseif (count($annotations) > 1) {
            throw new Exception('Multiple @crawlStartPoint annotation found on ' . $pageClass->getName());
        }
        $annotation = current($annotations);
        return $annotation;
    }
}