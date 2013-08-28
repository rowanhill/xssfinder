<?php
namespace XssFinder\Scanner;


use XssFinder\Annotations\Annotations;

class PageFinder
{
    private $_namespace;

    public function __construct($namespace = null)
    {
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
        $annotationsManager = Annotations::getConfiguredManager();
        $pageClasses = array();
        foreach ($classNames as $className) {
            $class = new \ReflectionClass($className);
            if ($this->_namespace != null && $class->getNamespaceName() !== $this->_namespace) {
                continue;
            }
            $annotations = $annotationsManager->getClassAnnotations($class, '@page');
            if (!empty($annotations)) {
                $pageClasses[] = $className;
            }
        }
        return $pageClasses;
    }
}