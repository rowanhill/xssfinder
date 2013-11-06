<?php

require __DIR__ . '/../../../vendor/autoload.php';
require_once __DIR__ . '/Page/HomePage.php';
require_once __DIR__ . '/Page/LoginPage.php';
require_once __DIR__ . '/Page/UnreachedPage.php';

$server = new \XssFinder\Remote\ExecutorServer(
    '0.0.0.0',
    9090,
    array(
        '\XssFinder\TestSite\Page\HomePage',
        '\XssFinder\TestSite\Page\LoginPage',
        '\XssFinder\TestSite\Page\UnreachedPage'
    )
);
$server->serve();