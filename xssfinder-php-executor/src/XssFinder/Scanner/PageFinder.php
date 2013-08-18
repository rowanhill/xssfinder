<?php
namespace XssFinder\Scanner;

use mindplay\annotations\AnnotationCache;
use mindplay\annotations\Annotations;

class PageFinder
{
    public function __construct()
    {
        Annotations::$config['cache'] = new AnnotationCache(sys_get_temp_dir());
        \XssFinder\Annotations\Annotations::load();
    }

    /**
     * Filters a list of classes, looking for those annotated as pages.
     *
     * @param $classNames string[] Array of fully qualified names of classes to test
     * @return string[] Array of fully qualified names of classes which are page objects
     */
    public function findPages($classNames)
    {
        $annotationsManager = Annotations::getManager();
        $pageClasses = array();
        foreach ($classNames as $className) {
            $annotations = $annotationsManager->getClassAnnotations($className);
            foreach ($annotations as $annotation) {
                if (is_a($annotation, 'PageAnnotation')) {
                    $pageClasses[] = $className;
                }
            }
        }
        return $pageClasses;
    }
}