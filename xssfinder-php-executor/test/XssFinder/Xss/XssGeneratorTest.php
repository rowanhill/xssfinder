<?php

namespace XssFinder\Xss;

class XssGeneratorTest extends \PHPUnit_Framework_TestCase
{
    public function testCreatesAttackStartingWithOneAsIdentifier()
    {
        // given
        $mockFactory = mock('XssFinder\Xss\XssAttackFactory');
        $mockAttack = mock('XssFinder\Xss\XssAttack');
        when($mockFactory->createXssAttack("1"))->return($mockAttack);
        $generator = new XssGenerator($mockFactory);

        // when
        $attack = $generator->createXssAttack();

        // then
        assertThat($attack, is($mockAttack));
    }

    public function testIdentifiersIncrement()
    {
        // given
        $mockFactory = mock('XssFinder\Xss\XssAttackFactory');
        $mockAttack1 = mock('XssFinder\Xss\XssAttack');
        $mockAttack2 = mock('XssFinder\Xss\XssAttack');
        when($mockFactory->createXssAttack("1"))->return($mockAttack1);
        when($mockFactory->createXssAttack("2"))->return($mockAttack2);
        $generator = new XssGenerator($mockFactory);

        // when
        $attack1 = $generator->createXssAttack();
        $attack2 = $generator->createXssAttack();

        // then
        assertThat($attack1, is($mockAttack1));
        assertThat($attack2, is($mockAttack2));
    }
}