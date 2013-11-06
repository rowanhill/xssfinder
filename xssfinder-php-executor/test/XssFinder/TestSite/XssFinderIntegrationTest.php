<?php

use Thrift\Protocol\TBinaryProtocol;
use Thrift\Transport\TBufferedTransport;
use Thrift\Transport\TSocket;
use XssFinder\ExecutorClient;
use XssFinder\TestHelper\Selenium;

class XssFinderIntegrationTest extends PHPUnit_Framework_TestCase
{
    public function testRunXssFinder()
    {
        // given
        $startedSelenium = Selenium::startSeleniumServer();
        assertThat($startedSelenium, is(true));

        // Connect to the selenium server - there's no good reason this should be necessary, but this test fails
        // without doing so. :\
        // Running this test manually (with selenium, _runserver and _runclient all running in different terminals)
        // passes without needing this.
        $host = 'http://localhost:4444/wd/hub';
        $capabilities = array(WebDriverCapabilityType::BROWSER_NAME => 'htmlunit', 'javascriptEnabled' => true);
        new RemoteWebDriver($host, $capabilities);

        // Start the server in another process
        exec('php XssFinder/Remote/_runserver.php &> runserver.log &');
        sleep(2);

        //
    }
}