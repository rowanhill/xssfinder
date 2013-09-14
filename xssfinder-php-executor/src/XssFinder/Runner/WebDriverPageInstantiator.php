<?php

namespace XssFinder\Runner;

use RemoteWebDriver;

class WebDriverPageInstantiator implements PageInstantiator
{
    private $_driver;

    function __construct(RemoteWebDriver $driver)
    {
        $this->_driver = $driver;
    }

    function instantiatePage(\ReflectionClass $class)
    {
        return $class->newInstance($this->_driver);
    }
}