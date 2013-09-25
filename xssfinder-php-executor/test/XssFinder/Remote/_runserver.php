<?php
/*
 * This file starts a PHP executor server, and continues to listen until killed. It is used by tests that want
 * to test against a running server, as a hacky work-around for PHP builds that don't have threading support.
 */

require __DIR__ . '/../../../vendor/autoload.php';

/**
 * @page
 */
abstract class ExecutorServerTest_BasePage
{
    protected $_driver;

    public function __construct(RemoteWebDriver $driver)
    {
        $this->_driver = $driver;
    }
}

/**
 * @crawlStartPoint('url'=>'http://www.google.com')
 */
class ExecutorServerTest_SomePage extends ExecutorServerTest_BasePage
{
    /**
     * @return ExecutorServerTest_SomeOtherPage
     */
    public function goToSomeOtherPage() { return new ExecutorServerTest_SomeOtherPage($this->_driver); }
}

class ExecutorServerTest_SomeOtherPage extends ExecutorServerTest_BasePage
{
}

$server = new \XssFinder\Remote\ExecutorServer('localhost', 9090, array('\ExecutorServerTest_SomePage', '\ExecutorServerTest_SomeOtherPage'));
$server->serve();