<?php

namespace XssFinder\Runner;

require_once(__DIR__ . '/../../Wiremock.php');

use Wiremock\Wiremock;
use XssFinder\TestHelper\Selenium;

class HtmlUnitDriverWrapperTest extends \PHPUnit_Framework_TestCase
{
    public function setUp()
    {
        assertThat(Selenium::startSeleniumServer(), is(true));
        assertThat(\XssFinder\TestHelper\Wiremock::startWiremockServer(), is(true));
    }

    public function tearDown()
    {
        assertThat(Selenium::stopSeleniumServer(), is(true));
        assertThat(\XssFinder\TestHelper\Wiremock::stopWiremockServer(), is(true));
    }

    public function testVisitingRequestsUrl()
    {
        // given
        $wiremock = new Wiremock(8080);
        $wiremock->stubFor()->get()->url('/some-url')->willReturnResponse()->withBody('Here is a body')->setUp();
        $driver = new HtmlUnitDriverWrapper();

        // when
        $driver->visit('http://localhost:8080/some-url');

        // then
        $wiremock->verify()->get()->url('/some-url')->check();
    }
}