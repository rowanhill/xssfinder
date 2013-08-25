<?php
/*
 * This file starts a PHP executor server, and continues to listen until killed. It is used by tests that want
 * to test against a running server, as a hacky work-around for PHP builds that don't have threading support.
 */

require __DIR__ . '/../../../vendor/autoload.php';

$server = new \XssFinder\Remote\ExecutorServer('localhost', 9090, array());
$server->serve();