<?php

namespace XssFinder\Remote;

use Thrift\Protocol\TBinaryProtocol;
use Thrift\Protocol\TJSONProtocol;
use Thrift\Transport\TBufferedTransport;
use Thrift\Transport\TSocket;
use XssFinder\ExecutorClient;

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

    public function testServerRespondsToClientRequests()
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
        $this->assertThat($pages, $this->equalTo(array()));
        $transport->close();
    }
}
