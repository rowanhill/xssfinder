<?php

namespace XssFinder\Annotations;

interface CustomSubmitter
{
    /**
     * @param mixed $page The page object to traverse
     * @param LabelledXssGenerator $labelledXssGenerator
     * @return mixed Resulting page object
     */
    public function submit($page, $labelledXssGenerator);
}