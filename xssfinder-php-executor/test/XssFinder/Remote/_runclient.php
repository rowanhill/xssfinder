<?php

/*
 * This file is only used for manual integration testing. It assumes a selenium server is started, and an XSS Finder
 * server is running.
 */

require __DIR__ . '/../../../vendor/autoload.php';
require_once(__DIR__ . '/../../../src/XssFinder/Executor.php');
require_once(__DIR__ . '/../../../src/XssFinder/Types.php');

use Thrift\Protocol\TBinaryProtocol;
use Thrift\Transport\TBufferedTransport;
use Thrift\Transport\TSocket;
use XssFinder\ExecutorClient;

$socket = new TSocket('localhost', 9090);
$transport = new TBufferedTransport($socket, 1024, 1024);
$protocol = new TBinaryProtocol($transport);
$client = new ExecutorClient($protocol);
$transport->open();

$pages = $client->getPageDefinitions('');
$client->startRoute('\ExecutorServerTest_SomePage');

print_r($pages);

$transport->close();