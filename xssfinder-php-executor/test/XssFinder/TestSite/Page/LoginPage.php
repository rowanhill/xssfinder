<?php

namespace XssFinder\TestSite\Page;
use XssFinder\Annotations\CustomSubmitter;
use XssFinder\Annotations\CustomTraverser;
use XssFinder\Annotations\LabelledXssGenerator;

/**
 * @page
 * @crawlStartPoint('url'=>'http://localhost:8085/simple/')
 */
class LoginPage
{
    /** @var \WebDriver */
    private $_driver;

    public function __construct(\WebDriver $driver)
    {
        $this->_driver = $driver;
    }

    /**
     * @submitAction('submitterClass'=>'XssFinder\TestSite\Page\LoginPageSubmitter')
     * @traverseWith('traverserClass'=>'XssFinder\TestSite\Page\LoginPageTraverser')
     * @param string $username
     * @param string $password
     * @return HomePage
     */
    public function logInAs($username, $password)
    {
        $this->_driver->findElement(\WebDriverBy::name('j_username'))->clear();
        $this->_driver->findElement(\WebDriverBy::name('j_username'))->sendKeys($username);
        $this->_driver->findElement(\WebDriverBy::name('j_password'))->clear();
        $this->_driver->findElement(\WebDriverBy::name('j_password'))->sendKeys($password);
        $this->_driver->findElement(\WebDriverBy::name('submit'))->click();
        return new HomePage($this->_driver);
    }
}

class LoginPageSubmitter implements CustomSubmitter
{
    /**
     * @param mixed $page The page object to traverse
     * @param LabelledXssGenerator $labelledXssGenerator
     * @throws \Exception
     * @return mixed Resulting page object
     */
    public function submit($page, $labelledXssGenerator)
    {
        if (!($page instanceof LoginPage)) {
            throw new \Exception("$page was not an instance of LoginPage");
        }
        return $page->logInAs(
            $labelledXssGenerator->getXssAttackTextForLabel('username'),
            $labelledXssGenerator->getXssAttackTextForLabel('password')
        );
    }
}

class LoginPageTraverser implements CustomTraverser
{
    /**
     * @param mixed $page The page object to traverse
     * @throws \Exception
     * @return mixed Resulting page object
     */
    public function traverse($page)
    {
        if (!($page instanceof LoginPage)) {
            throw new \Exception("$page was not an instance of LoginPage");
        }
        return $page->logInAs('testuser', 'strongpassword');
    }
}