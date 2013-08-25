<?php
namespace XssFinder\Scanner;

use mindplay\annotations\AnnotationCache;
use mindplay\annotations\Annotations;

class PageFinder
{
    private $_namespace;

    public function __construct($namespace = null)
    {
        Annotations::$config['cache'] = new AnnotationCache(sys_get_temp_dir());
        \XssFinder\Annotations\Annotations::load();

        // Strip of leading backslashes from the specified namespace
        $this->_namespace = preg_replace('/^\\\\/', '', $namespace);;
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
            $class = new \ReflectionClass($className);
            if ($this->_namespace != null && $class->getNamespaceName() !== $this->_namespace) {
                continue;
            }
            $annotations = $annotationsManager->getClassAnnotations($class);
            foreach ($annotations as $annotation) {
                if (is_a($annotation, 'PageAnnotation')) {
                    $pageClasses[] = $className;
                }
            }
        }
        return $pageClasses;
    }
}