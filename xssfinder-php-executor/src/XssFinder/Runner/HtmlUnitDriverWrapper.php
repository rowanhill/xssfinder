<?php

namespace XssFinder\Runner;

use RemoteWebDriver;
use WebDriverCapabilityType;
use XssFinder\Xss\XssGenerator;

class HtmlUnitDriverWrapper implements DriverWrapper
{
    /** @var RemoteWebDriver */
    private $_webDriver;

    function __construct()
    {
        $this->_webDriver = $this->_createDriver();
    }

    /**
     * @return PageInstantiator A PageInstantiator that can create pages driven by the wrapped driver
     */
    function getPageInstantiator()
    {
        return new WebDriverPageInstantiator($this->_webDriver);
    }

    /**
     * Navigate the driver to given URL
     *
     * @param $url string The URL to visit
     */
    function visit($url)
    {
        $this->_webDriver->get($url);
    }

    /**
     * Put XSS attacks from the given XssGenerator into all available inputs
     *
     * @param $xssGenerator XssGenerator XssGenerator to produce XSS attacks
     * @return array A map of input identifiers -> attack identifiers
     */
    function putXssAttackStringsInInputs($xssGenerator)
    {
        $elements = $this->_webDriver->findElements(
            \WebDriverBy::cssSelector('input[type=text],input[type=search],input[type=password],textarea')
        );
        $inputsToAttacks = array();
        $xpathFinder = new WebDriverXPathFinder();
        foreach ($elements as $element) {
            /** @var \WebDriverElement $element */
            $xssAttack = $xssGenerator->createXssAttack();
            $element->sendKeys($xssAttack->getAttackString());
            $inputIdentifier = $xpathFinder->getXPath($element);
            $inputsToAttacks[$inputIdentifier] = $xssAttack->getIdentifier();
        }
        return $inputsToAttacks;
    }

    /**
     * @return array The set of currently XSS attack identifiers observable on the current page
     */
    function getCurrentXssIds()
    {
        // RemoteWebDriver doesn't (yet) implement returning arrays, so we have to get the values one by one - this
        // is significantly slower
        $numberOfXssIds = $this->_webDriver->executeScript('return (window.xssfinder || []).length');
        $xssIds = array();
        for ($i = 0; $i < $numberOfXssIds; $i++) {
            // Thrift takes the _keys_ of the array when serialising a 'set', so we use the xss attack ID as the key.
            $xssId = $this->_webDriver->executeScript("return window.xssfinder[$i]");
            $xssIds[$xssId] = true;
        }
        return $xssIds;
    }

    /**
     * @return int The number of forms observable on the current page
     */
    function getFormCount()
    {
        return count($this->_webDriver->findElements(\WebDriverBy::xpath('//form')));
    }

    /**
     * Close the existing session and open a new one, ready for the next test
     */
    function renewSession()
    {
        $this->_webDriver->close();
        $this->_webDriver = $this->_createDriver();
    }

    private function _createDriver()
    {
        $host = 'http://localhost:4444/wd/hub';
        $capabilities = array(WebDriverCapabilityType::BROWSER_NAME => 'htmlunit', 'javascriptEnabled' => true);
        return new RemoteWebDriver($host, $capabilities);
    }
}