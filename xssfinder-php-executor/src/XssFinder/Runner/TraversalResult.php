<?php

namespace XssFinder\Runner;

class TraversalResult
{
    private $_page;
    private $_inputIdsToAttackIds;

    public function __construct($page, array $inputIdsToAttackIds)
    {
        $this->_page = $page;
        $this->_inputIdsToAttackIds = $inputIdsToAttackIds;
    }

    public function getInputIdsToAttackIds()
    {
        return $this->_inputIdsToAttackIds;
    }

    public function getPage()
    {
        return $this->_page;
    }
}