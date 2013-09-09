<?php

namespace XssFinder\Xss;

class XssAttack
{
    const ATTACK_TEMPLATE = <<<'HTML'
<script type="text/javascript">
  if (typeof(window.xssfinder) === "undefined") {
    window.xssfinder = [];
  }
  window.xssfinder.push('%s');
</script>
HTML;

    private $_identifier;

    /**
     * @param string $identifier
     */
    public function __construct($identifier)
    {
        $this->_identifier = $identifier;
    }

    public function getIdentifier()
    {
        return $this->_identifier;
    }

    public function getAttackString()
    {
        return sprintf(self::ATTACK_TEMPLATE, $this->_identifier);
    }
}