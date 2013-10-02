<?php

namespace XssFinder\Runner;

use PHPUnit_Framework_TestCase;
use XssFinder\Annotations\LabelledXssGenerator;
use XssFinder\Xss\XssAttack;
use XssFinder\Xss\XssGenerator;

class LabelledXssGeneratorImplTest extends PHPUnit_Framework_TestCase
{
    const LABEL = 'label';
    const XSS_ATTACK_STRING = 'some attack';
    const XSS_ATTACK_ID = '123';

    /** @var XssGenerator */
    private $_mockXssGenerator;
    /** @var XssAttack */
    private $_mockXssAttack;

    /** @var LabelledXssGeneratorImpl */
    private $_labelledXssGenerator;

    function setUp()
    {
        $this->_mockXssGenerator = mock('XssFinder\Xss\XssGenerator');
        $this->_mockXssAttack = mock('XssFinder\Xss\XssAttack');
        when($this->_mockXssGenerator->createXssAttack())->return($this->_mockXssAttack);
        when($this->_mockXssAttack->getIdentifier())->return(self::XSS_ATTACK_ID);
        when($this->_mockXssAttack->getAttackString())->return(self::XSS_ATTACK_STRING);

        $this->_labelledXssGenerator = new LabelledXssGeneratorImpl($this->_mockXssGenerator);
    }

    function testXssIsGenerated()
    {
        // when
        $attackString = $this->_labelledXssGenerator->getXssAttackTextForLabel(self::LABEL);

        // then
        assertThat($attackString, is(self::XSS_ATTACK_STRING));
    }

    function testGeneratedXssIdIsAvailableKeyedByLabel()
    {
        // given
        $this->_labelledXssGenerator->getXssAttackTextForLabel(self::LABEL);

        // when
        $labelToXssId = $this->_labelledXssGenerator->getLabelsToAttackIds();

        // then
        $expectedLabelToXssId = array(self::LABEL => self::XSS_ATTACK_ID);
        assertThat($labelToXssId, is($expectedLabelToXssId));
    }
}