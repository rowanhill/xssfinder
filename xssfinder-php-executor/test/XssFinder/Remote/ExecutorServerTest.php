<?php

namespace XssFinder\Remote;

use Hamcrest_Description;
use Thrift\Protocol\TBinaryProtocol;
use Thrift\Protocol\TJSONProtocol;
use Thrift\Transport\TBufferedTransport;
use Thrift\Transport\TSocket;
use XssFinder\ExecutorClient;
use XssFinder\PageDefinition;

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
    }

    public function testServerRespondsToClientRequestsAndFindsPages()
    {
        // given
        // Start the server in another process
        exec('php XssFinder/Remote/_runserver.php &> /dev/null &');
        sleep(1);
        // Create a client - in practice, it'll be a Java client, but that shouldn't matter
        $socket = new TSocket('localhost', 9090);
        $transport = new TBufferedTransport($socket, 1024, 1024);
        $protocol = new TBinaryProtocol($transport);
        $client = new ExecutorClient($protocol);
        $transport->open();

        // when
        $pages = $client->getPageDefinitions('');

        // then
        assertThat(count($pages), equalTo(2));
        assertThat($pages, hasValue(new PageDefinitionMatcher('\ExecutorServerTest_SomePage')));
        assertThat($pages, hasValue(new PageDefinitionMatcher('\ExecutorServerTest_SomeOtherPage')));
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
