<?php

namespace XssFinder\Remote;

use Thrift\Factory\TBinaryProtocolFactory;
use Thrift\Factory\TTransportFactory;
use Thrift\Server\TServerSocket;
use Thrift\Server\TSimpleServer;

require_once(__DIR__ . '/../../../vendor/autoload.php');
require_once(__DIR__ . '/../Executor.php');
require_once(__DIR__ . '/../Types.php');

class ExecutorServer
{
    private $_server;
    private $_pageClassNames;

    /**
     * @param string $hostname The hostname to listen on
     * @param int $port The port to listen on
     * @param array $pageClassNames
     */
    public function __construct($hostname, $port, array $pageClassNames)
    {
        $this->_pageClassNames = $pageClassNames;

        $processor = new ExecutorHandler();
        $transport = new TServerSocket($hostname, $port);
        $transportFactory = new TTransportFactory();
        $protocolFactory = new TBinaryProtocolFactory();

        $this->_server = new TSimpleServer(
            $processor,
            $transport,
            $transportFactory,
            $transportFactory,
            $protocolFactory,
            $protocolFactory
        );
    }

    /**
     * Start the executor serving on the specified hostname & port. This is a blocking operation.
     */
    public function serve()
    {
        $this->_server->serve();
    }
}