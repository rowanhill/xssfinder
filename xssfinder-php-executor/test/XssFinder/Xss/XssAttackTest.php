<?php

namespace XssFinder\Xss;

class XssAttackTest extends \PHPUnit_Framework_TestCase
{
    public function testIdentifierIsRequiredAndAvailable()
    {
        // given
        $xssAttack = new XssAttack("1");

        // when
        $identifier = $xssAttack->getIdentifier();

        // then
        assertThat($identifier, is("1"));
    }

    public function testConstructsAttackString()
    {
        // given
        $xssAttack = new XssAttack("1");

        // when
        $attackString = $xssAttack->getAttackString();

        // then
        assertThat($attackString, is("<script type=\"text/javascript\">\n  if (typeof(window.xssfinder) === \"undefined\") {\n    window.xssfinder = [];\n  }\n  window.xssfinder.push('1');\n</script>"));

    }
}