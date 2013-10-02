<?php
namespace XssFinder\Annotations;

use mindplay\annotations\AnnotationCache;

class Annotations
{
    private static $_hasInitialisedAnnotationsLibrary = false;

    /**
     * Annotations cannot be in a namespace, so we commonise all their loading here.
     */
    public static function load()
    {
        require_once dirname(__FILE__) . '/PageAnnotation.php';
        require_once dirname(__FILE__) . '/CrawlStartPointAnnotation.php';
        require_once dirname(__FILE__) . '/SubmitActionAnnotation.php';
        require_once dirname(__FILE__) . '/TraverseWithAnnotation.php';
    }

    /**
     * @return \mindplay\annotations\AnnotationManager
     */
    public static function getConfiguredManager()
    {
        if (!self::$_hasInitialisedAnnotationsLibrary) {
            self::load();
            \mindplay\annotations\Annotations::$config['cache'] = new AnnotationCache(sys_get_temp_dir());
            self::$_hasInitialisedAnnotationsLibrary = true;
        }
        return \mindplay\annotations\Annotations::getManager();
    }
}