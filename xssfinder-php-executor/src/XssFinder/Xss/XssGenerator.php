<?php

namespace XssFinder\Xss;

class XssGenerator
{
    /** @var XssAttackFactory */
    private $_attackFactory;
    /** @var int */
    private $_nextXssId;

    public function __construct(XssAttackFactory $attackFactory)
    {
        $this->_attackFactory = $attackFactory;
        $this->_nextXssId = 1;
    }

    public function createXssAttack()
    {
        $id = (string)$this->_nextXssId;
        $this->_nextXssId++;
        return $this->_attackFactory->createXssAttack($id);
    }
}