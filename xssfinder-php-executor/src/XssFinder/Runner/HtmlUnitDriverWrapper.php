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
        $host = 'http://localhost:4444/wd/hub';
        $capabilities = array(WebDriverCapabilityType::BROWSER_NAME => 'htmlunit', 'javascriptEnabled' => true);
        $this->_webDriver = new RemoteWebDriver($host, $capabilities);
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
        foreach ($elements as $element) {
            /** @var \WebDriverElement $element */
            $xssAttack = $xssGenerator->createXssAttack();
            $element->sendKeys($xssAttack->getAttackString());
            $inputIdentifier = $element->getAttribute('name'); //TODO: Derive XPath of $element to use as ID
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
            $xssIds[] = $this->_webDriver->executeScript("return window.xssfinder[$i]");
        }
        return $xssIds;
    }

    /**
     * @return int The number of forms observable on the current page
     */
    function getFormCount()
    {
        // TODO: Implement getFormCount() method.
    }

    /**
     * Close the existing session and open a new one, ready for the next test
     */
    function renewSession()
    {
        // TODO: Implement renewSession() method.
    }
}