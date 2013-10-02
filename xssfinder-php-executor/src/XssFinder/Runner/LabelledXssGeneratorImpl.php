<?php

namespace XssFinder\Runner;

use XssFinder\Annotations\LabelledXssGenerator;
use XssFinder\Xss\XssGenerator;

class LabelledXssGeneratorImpl implements LabelledXssGenerator
{
    /** @var XssGenerator */
    private $_xssGenerator;
    private $_labelsToXssIds;

    function __construct(XssGenerator $xssGenerator)
    {
        $this->_xssGenerator = $xssGenerator;
        $this->_labelsToXssIds = array();
    }

    /**
     * @param string $label
     * @return string An XSS attack string
     */
    function getXssAttackTextForLabel($label)
    {
        $attack = $this->_xssGenerator->createXssAttack();
        $this->_labelsToXssIds[$label] = $attack->getIdentifier();
        return $attack->getAttackString();
    }

    public function getLabelsToAttackIds()
    {
        return $this->_labelsToXssIds;
    }
}