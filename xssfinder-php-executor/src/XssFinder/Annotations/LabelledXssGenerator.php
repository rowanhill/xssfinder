<?php

namespace XssFinder\Annotations;

interface LabelledXssGenerator
{
    /**
     * @param string $label
     * @return string An XSS attack string
     */
    function getXssAttackTextForLabel($label);
}