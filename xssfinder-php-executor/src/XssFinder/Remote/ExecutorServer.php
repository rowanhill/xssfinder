<?php

namespace XssFinder\Remote;

use Thrift\Factory\TBinaryProtocolFactory;
use Thrift\Factory\TTransportFactory;
use Thrift\Server\TServerSocket;
use Thrift\Server\TSimpleServer;
use XssFinder\ExecutorProcessor;
use XssFinder\Runner\ExecutorContext;
use XssFinder\Runner\HtmlUnitDriverWrapper;
use XssFinder\Scanner\MethodDefinitionFactory;
use XssFinder\Scanner\PageDefinitionFactory;
use XssFinder\Scanner\PageFinderFactory;
use XssFinder\Scanner\ReflectionHelper;
use XssFinder\Scanner\ThriftToReflectionLookupFactory;
use XssFinder\Xss\XssAttackFactory;
use XssFinder\Xss\XssGenerator;

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

        $pageFinderFactory = new PageFinderFactory();
        $reflectionHelper = new ReflectionHelper();
        $methodDefinitionFactory = new MethodDefinitionFactory($reflectionHelper);
        $pageDefinitionFactory = new PageDefinitionFactory($methodDefinitionFactory, $reflectionHelper, $this->_pageClassNames);
        $lookupFactory = new ThriftToReflectionLookupFactory();
        $executorContext = new ExecutorContext(
            new HtmlUnitDriverWrapper(),
            new XssGenerator(new XssAttackFactory())
        );
        $handler = new ExecutorHandler(
            $pageFinderFactory,
            $pageDefinitionFactory,
            $lookupFactory,
            $executorContext,
            $this->_pageClassNames
        );

        $processor = new ExecutorProcessor($handler);
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