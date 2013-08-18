<?php
namespace XssFinder\Annotations;

class Annotations
{
    /**
     * Annotations cannot be in a namespace, so we commonise all their loading here.
     */
    public static function load()
    {
        require_once dirname(__FILE__) . '/PageAnnotation.php';
    }
}