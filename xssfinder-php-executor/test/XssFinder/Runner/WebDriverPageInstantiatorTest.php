<?php

namespace XssFinder\Runner;

use WebDriverPageInstantiator_TestPages\SomePage;

class WebDriverPageInstantiatorTest extends \PHPUnit_Framework_TestCase
{
    /** @var \RemoteWebDriver */
    private $_mockDriver;
    /** @var WebDriverPageInstantiator */
    private $_instantiator;

    public function setUp()
    {
        $this->_mockDriver = mock('RemoteWebDriver');
        $this->_instantiator = new WebDriverPageInstantiator($this->_mockDriver);
    }

    public function testInstantiatesClassWithConstructorThatTakesWebDriver()
    {
        // given
        $somePageClass = new \ReflectionClass('\WebDriverPageInstantiator_TestPages\SomePage');

        // when
        /** @var SomePage $somePage */
        $somePage = $this->_instantiator->instantiatePage($somePageClass);

        // then
        assertThat($somePage, is(notNullValue()));
        assertThat($somePage, is(anInstanceOf('\WebDriverPageInstantiator_TestPages\SomePage')));
        assertThat($somePage->driver, is($this->_mockDriver));
    }
}

namespace WebDriverPageInstantiator_TestPages;

class SomePage {
    public $driver;
    public function __construct(\RemoteWebDriver $driver)
    {
        $this->driver = $driver;
    }
}