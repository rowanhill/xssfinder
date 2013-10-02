<?php

namespace XssFinder\Annotations;

use Exception;

class ExactlyOneAnnotationRequiredException extends Exception
{
    /**
     * @param string $message
     */
    public function __construct($message)
    {
        parent::__construct($message);
    }
}