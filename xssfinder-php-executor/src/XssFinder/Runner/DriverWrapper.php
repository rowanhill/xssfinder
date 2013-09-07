<?php

namespace XssFinder\Runner;

interface DriverWrapper
{
    /**
     * @return PageInstantiator A PageInstantiator that can create pages driven by the wrapped driver
     */
    function getPageInstantiator();

    /**
     * Navigate the driver to given URL
     *
     * @param $url string The URL to visit
     */
    function visit($url);

    /**
     * Put XSS attacks from the given XssGenerator into all available inputs
     *
     * @param $xssGenerator XssGenerator XssGenerator to produce XSS attacks
     * @return array A map of input identifiers -> attack identifiers
     */
    function putXssAttackStringsInInputs($xssGenerator);

    /**
     * @return array The set of currently XSS attack identifiers observable on the current page
     */
    function getCurrentXssIds();

    /**
     * @return int The number of forms observable on the current page
     */
    function getFormCount();

    /**
     * Close the existing session and open a new one, ready for the next test
     */
    function renewSession();
}