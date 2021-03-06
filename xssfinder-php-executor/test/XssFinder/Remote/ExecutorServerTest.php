<?php

namespace XssFinder\Remote;

use Hamcrest_Description;
use RemoteWebDriver;
use Thrift\Protocol\TBinaryProtocol;
use Thrift\Protocol\TJSONProtocol;
use Thrift\Transport\TBufferedTransport;
use Thrift\Transport\TSocket;
use WebDriverCapabilityType;
use XssFinder\ExecutorClient;
use XssFinder\PageDefinition;
use XssFinder\TestHelper\Selenium;
use XssFinder\TraversalMode;

require_once(__DIR__ . '/../../../src/XssFinder/Executor.php');
require_once(__DIR__ . '/../../../src/XssFinder/Types.php');

class ExecutorServerTest extends \PHPUnit_Framework_TestCase
{
    public function setUp()
    {
        if (strtoupper(substr(PHP_OS, 0, 3)) === 'WIN') {
            $this->markTestSkipped('These tests rely on a *nix machine');
        }
    }

    public function tearDown()
    {
        if (strtoupper(substr(PHP_OS, 0, 3)) !== 'WIN') {
            // Kill the PHP process executing _runserver, if it is still around
            exec("kill -9 `ps -e | grep \"php XssFinder/Remote/_runserver.php\" | grep -v grep | awk '{print $1}'`");
        }
        $stoppedSelenium = Selenium::stopSeleniumServer();
        assertThat($stoppedSelenium, is(true));
    }

    /*
     * Launches a server in a separate process and connects to it.
     *
     * If this test fails with a message like "TSocket read 0 bytes" then the server died when the client to
     * communicate with it. You can debug this by commenting out the lines to start the server process and doing
     * so manually (connecting a debugger to it if you wish) then running this test.
     */
    public function testServerRespondsToClientRequestsAndFindsPagesAndStartsRoutes()
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

        // Create a client - in practice, it'll be a Java client, but that shouldn't matter
        $socket = new TSocket('localhost', 9090);
        $transport = new TBufferedTransport($socket, 1024, 1024);
        $protocol = new TBinaryProtocol($transport);
        $client = new ExecutorClient($protocol);
        $transport->open();

        // when
        $pages = $client->getPageDefinitions('');
        $client->startRoute('\ExecutorServerTest_SomePage');
        /** @var PageDefinition $page */
        $page = null;
        foreach ($pages as $candidatePage) {
            /** @var PageDefinition $candidatePage */
            if ($candidatePage->identifier == '\ExecutorServerTest_SomePage') {
                $page = $candidatePage;
                break;
            }
        }
        $goToSomeOtherPageMethod = current($page->methods);
        $attackIdsToInputIds = $client->traverseMethod($goToSomeOtherPageMethod, TraversalMode::NORMAL);

        // then
        assertThat(count($pages), equalTo(2));
        assertThat($pages, hasValue(new PageDefinitionMatcher('\ExecutorServerTest_SomePage')));
        assertThat($pages, hasValue(new PageDefinitionMatcher('\ExecutorServerTest_SomeOtherPage')));
        assertThat($attackIdsToInputIds, is(emptyArray()));
        $transport->close();
    }
}

class PageDefinitionMatcher implements \Hamcrest_Matcher
{
    private $_expectedIdentifier;

    public function __construct($expectedIdentifier)
    {
        $this->_expectedIdentifier = $expectedIdentifier;
    }

    /**
     * Evaluates the matcher for argument <var>$item</var>.
     *
     * @param mixed $item the object against which the matcher is evaluated.
     *
     * @return boolean <code>true</code> if <var>$item</var> matches,
     *   otherwise <code>false</code>.
     *
     * @see Hamcrest_BaseMatcher
     */
    public function matches($item)
    {
        if (!is_a($item, '\XssFinder\PageDefinition')) {
            return false;
        }
        /** @var PageDefinition $pageDefinition */
        $pageDefinition = $item;

        return $pageDefinition->identifier === $this->_expectedIdentifier;
    }

    /**
     * Generate a description of why the matcher has not accepted the item.
     * The description will be part of a larger description of why a matching
     * failed, so it should be concise.
     * This method assumes that <code>matches($item)</code> is false, but
     * will not check this.
     *
     * @param mixed $item The item that the Matcher has rejected.
     * @param Hamcrest_Description $description
     *   The description to be built or appended to.
     */
    public function describeMismatch($item, Hamcrest_Description $description)
    {
        $className = get_class($item);
        $id = $this->_expectedIdentifier;
        $description->appendText("$className is not a PageDescriptor with identifier $id");
    }

    /**
     * Generates a description of the object.  The description may be part
     * of a description of a larger object of which this is just a component,
     * so it should be worded appropriately.
     *
     * @param Hamcrest_Description $description
     *   The description to be built or appended to.
     */
    public function describeTo(Hamcrest_Description $description)
    {
        $id = $this->_expectedIdentifier;
        $description->appendText("<$id>");
    }
}
