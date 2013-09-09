<?php

namespace XssFinder\Xss;

class XssAttackFactory
{
    /**
     * @param string $identifier
     */
    public function createXssAttack($identifier)
    {
        return new XssAttack($identifier);
    }
}