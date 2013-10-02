<?php

namespace CSI_Test;

class TestPage
{
    /**
     * @submitAction('submitterClass' => '\CSI_Test\CustomSubmitter')
     */
    function methodWithAnnotationAndSubmitter() {}

    /**
     * @submitAction('submitterClass' => 'NonsenseClass')
     */
    function methodWithNonExistentCustomSubmitter() {}

    /**
     * @submitAction('submitterClass' => '\CSI_Test\TestPage')
     */
    function methodWithInvalidCustomSubmitter() {}

    /**
     * @submitAction
     */
    function methodWithAnnotationButNoSubmitter() {}

    function methodWithNoAnnotation() {}
}

class CustomSubmitter implements \XssFinder\Annotations\CustomSubmitter
{
    /**
     * @param mixed $page The page object to traverse
     * @param LabelledXssGenerator $labelledXssGenerator
     * @return mixed Resulting page object
     */
    public function submit($page, $labelledXssGenerator)
    {
        // TODO: Implement traverse() method.
    }
}
