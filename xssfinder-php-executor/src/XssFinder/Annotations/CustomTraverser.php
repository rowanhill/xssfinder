<?php

namespace XssFinder\Annotations;

interface CustomTraverser {
    /**
     * @param mixed $page The page object to traverse
     * @return mixed Resulting page object
     */
    public function traverse($page);
}