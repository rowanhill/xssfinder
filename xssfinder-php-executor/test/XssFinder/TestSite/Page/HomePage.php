<?php

namespace XssFinder\TestSite\Page;

/**
 * @page
 */
class HomePage
{
    /** @var \WebDriver */
    private $_driver;

    public function __construct(\WebDriver $driver)
    {
        $this->_driver = $driver;
    }

    /**
     * @submitAction
     * @return HomePage
     */
    public function unsafeSubmit()
    {
        $this->_driver->findElement(\WebDriverBy::id('unsafeSubmit'))->click();
        return $this;
    }

    /**
     * @submitAction
     * @return HomePage
     */
    public function safeSubmit()
    {
        $this->_driver->findElement(\WebDriverBy::id('safeSubmit'))->click();
        return $this;
    }

    /**
     * @return LoginPage
     */
    public function logout()
    {
        $this->_driver->findElement(\WebDriverBy::linkText('log out'))->click();
        return $this;
    }

    /**
     * @return UnreachedPage
     */
    public function throwException()
    {
        throw new \Exception('Intentional thrown exception');
    }
}