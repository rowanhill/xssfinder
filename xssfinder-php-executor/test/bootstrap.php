<?php
require_once('../vendor/autoload.php');
require_once('../vendor/hafriedlander/phockito/Phockito_Globals.php');
require_once('XssFinder/TestHelper/HttpWait.php');
require_once('XssFinder/TestHelper/Selenium.php');
require_once('XssFinder/TestHelper/Wiremock.php');
Phockito::include_hamcrest(true);