<?php

namespace CTI_Test;

use XssFinder\Annotations\CustomTraverser;

class CTI_TestPage
{
    /**
     * @traverseWith('traverserClass' => '\CTI_Test\CTI_Test_CustomTraverser')
     */
    function methodWithCustomTraverser() {}

    /**
     * @traverseWith('traverserClass' => '\CTI_Test\CTI_TestPage')
     */
    function methodWithInvalidCustomTraverser() {}

    /**
     * @traverseWith('traverserClass' => 'NonsenseClass')
     */
    function methodWithNonExistentCustomTraverser() {}

    function methodWithNoAnnotation() {}
}

class CTI_Test_CustomTraverser implements CustomTraverser
{
    public function traverse($page) {}
}