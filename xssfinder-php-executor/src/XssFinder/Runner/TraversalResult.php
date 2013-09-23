<?php

namespace XssFinder\Runner;

class TraversalResult
{
    private $_inputIdsToAttackIds;

    public function __construct(array $inputIdsToAttackIds)
    {
        $this->_inputIdsToAttackIds = $inputIdsToAttackIds;
    }

    public function getInputIdsToAttackIds()
    {
        return $this->_inputIdsToAttackIds;
    }
}