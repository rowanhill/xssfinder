<?php

namespace XssFinder\Runner;

use RemoteWebDriver;
use WebDriverCapabilityType;
use XssFinder\Xss\XssGenerator;

class HtmlUnitDriverWrapper implements DriverWrapper
{
    /**
     * @return PageInstantiator A PageInstantiator that can create pages driven by the wrapped driver
     */
    function getPageInstantiator()
    {
        // TODO: Implement getPageInstantiator() method.
    }

    /**
     * Navigate the driver to given URL
     *
     * @param $url string The URL to visit
     */
    function visit($url)
    {
        $host = 'http://localhost:4444/wd/hub';
        $capabilities = array(WebDriverCapabilityType::BROWSER_NAME => 'htmlunit', 'javascriptEnabled' => true);
        $webDriver = new RemoteWebDriver($host, $capabilities);
        $webDriver->get($url);
    }

    /**
     * Put XSS attacks from the given XssGenerator into all available inputs
     *
     * @param $xssGenerator XssGenerator XssGenerator to produce XSS attacks
     * @return array A map of input identifiers -> attack identifiers
     */
    function putXssAttackStringsInInputs($xssGenerator)
    {
        // TODO: Implement putXssAttackStringsInInputs() method.
    }

    /**
     * @return array The set of currently XSS attack identifiers observable on the current page
     */
    function getCurrentXssIds()
    {
        // TODO: Implement getCurrentXssIds() method.
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