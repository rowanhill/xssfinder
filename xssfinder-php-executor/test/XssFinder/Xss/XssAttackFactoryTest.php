<?php

namespace XssFinder\Xss;

class XssAttackFactoryTest extends \PHPUnit_Framework_TestCase
{
    public function testCreatesXssAttack() {
        // given
        $factory = new XssAttackFactory();

        // when
        $attack = $factory->createXssAttack("1");

        // then
        assertThat($attack, is(not(nullValue())));
    }
}