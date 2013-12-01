<?php

namespace XssFinder\Runner;

use WireMock\Client\LoggedRequest;
use WireMock\Client\WireMock;
use XssFinder\TestHelper\Selenium;
use XssFinder\Xss\XssAttack;
use XssFinder\Xss\XssGenerator;

class HtmlUnitDriverWrapperTest extends \PHPUnit_Framework_TestCase
{
    const INDEX_PAGE = "HtmlUnitDriverWrapperTest_index.html";
    const TWO_FORM_PAGE = "HtmlUnitDriverWrapperTest_two_forms.html";
    const NO_XSS_PAGE = "HtmlUnitDriverWrapperTest_no_xss.html";

    /** @var WireMock */
    private static $_wiremock;
    
    public static function setUpBeforeClass()
    {
        assertThat(\XssFinder\TestHelper\Wiremock::startWiremockServer(), is(true));
        self::$_wiremock = WireMock::create();
        assertThat(Selenium::startSeleniumServer(), is(true));
    }

    public static function tearDownAfterClass()
    {
        assertThat(\XssFinder\TestHelper\Wiremock::stopWiremockServer(), is(true));
        assertThat(Selenium::stopSeleniumServer(), is(true));
    }

    public function setUp()
    {
        self::$_wiremock->reset();
    }

    public function testCreatesWebDriverPageInstantiator()
    {
        // given
        $driver = new HtmlUnitDriverWrapper();

        // when
        $instantiator = $driver->getPageInstantiator();

        // then
        assertThat($instantiator, is(anInstanceOf('XssFinder\Runner\WebDriverPageInstantiator')));
    }

    public function testVisitingRequestsUrl()
    {
        // given
        self::$_wiremock->stubFor(
            WireMock::get(WireMock::urlEqualTo('/some-url'))->willReturn(
                WireMock::aResponse()->withBody('Here is a body')
            )
        );
        $driver = new HtmlUnitDriverWrapper();

        // when
        $driver->visit('http://localhost:8080/some-url');

        // then
        self::$_wiremock->verify(WireMock::getRequestedFor(WireMock::urlEqualTo('/some-url')));
    }

    public function testXssAttackingPutsXssFromGeneratorInAllTextInputAndTextArea()
    {
        // given
        self::$_wiremock->stubFor(
            WireMock::get(WireMock::urlEqualTo('/'))->willReturn(
                WireMock::aResponse()->withBodyFile(self::INDEX_PAGE)
            )
        );
        $driver = new HtmlUnitDriverWrapper();
        $driver->visit('http://localhost:8080/');
        /** @var XssGenerator $mockXssGenerator */
        $mockXssGenerator = mock('XssFinder\Xss\XssGenerator');
        /** @var XssAttack $mockAttack */
        $mockAttack = mock('XssFinder\Xss\XssAttack');
        when($mockAttack->getAttackString())->return("xss");
        when($mockXssGenerator->createXssAttack())->return($mockAttack);

        // when
        $driver->putXssAttackStringsInInputs($mockXssGenerator);
        $this->_clickSubmit($driver);

        // then
        $requests = self::$_wiremock->findAll(WireMock::postRequestedFor(WireMock::urlEqualTo('/submit')));
        assertThat($requests, is(arrayWithSize(1)));
        /** @var LoggedRequest $request */
        $request = current($requests);
        $params = $this->_parseParams($request->getBody());
        assertThat(count($params), is(5));
        assertThat($params, hasEntry('text1', 'xss'));
        assertThat($params, hasEntry('text2', 'xss'));
        assertThat($params, hasEntry('search', 'xss'));
        assertThat($params, hasEntry('password', 'xss'));
        assertThat($params, hasEntry('textarea', 'xss'));
    }

    public function testCurrentXssIdsAreGotFromJsArrayVarAndReturnedAsArrayKeys()
    {
        // given
        self::$_wiremock->stubFor(
            WireMock::get(WireMock::urlEqualTo('/'))->willReturn(
                WireMock::aResponse()->withBodyFile(self::INDEX_PAGE)
            )
        );
        $driver = new HtmlUnitDriverWrapper();
        $driver->visit('http://localhost:8080/');

        // when
        $currentXssIds = $driver->getCurrentXssIds();

        // then
        assertThat(array_keys($currentXssIds), is(array('123', '456')));
    }

    public function testCurrentXssIdsReturnsEmptyArrayIfJsVarNotDefined()
    {
        // given
        self::$_wiremock->stubFor(
            WireMock::get(WireMock::urlEqualTo('/'))->willReturn(
                WireMock::aResponse()->withBodyFile(self::NO_XSS_PAGE)
            )
        );
        $driver = new HtmlUnitDriverWrapper();
        $driver->visit('http://localhost:8080/');

        // when
        $currentXssIds = $driver->getCurrentXssIds();

        // then
        assertThat($currentXssIds, is(array()));
    }

    public function testFormCountIsCountOfNumberOfFormElementsOnPage()
    {
        // given
        self::$_wiremock->stubFor(
            WireMock::get(WireMock::urlEqualTo('/'))->willReturn(
                WireMock::aResponse()->withBodyFile(self::INDEX_PAGE)
            )
        );
        $driver = new HtmlUnitDriverWrapper();
        $driver->visit('http://localhost:8080/');

        // when
        $formCount = $driver->getFormCount();

        // then
        assertThat($formCount, is(1));
    }

    public function testBrowserSessionIsMaintainedAcrossRequests()
    {
        // given
        self::$_wiremock->stubFor(
            WireMock::get(WireMock::urlEqualTo('/'))->willReturn(
                WireMock::aResponse()->withBodyFile(self::INDEX_PAGE)
                    ->withHeader('Set-Cookie', 'TestCookie=SomeValue;Path/\\n')
            )
        );
        $driver = new HtmlUnitDriverWrapper();
        $driver->visit('http://localhost:8080/');

        // when
        $driver->visit('http://localhost:8080/cookie');

        // then
        self::$_wiremock->verify(
            WireMock::getRequestedFor(WireMock::urlEqualTo('/cookie'))
                ->withHeader('Cookie', WireMock::equalTo('TestCookie=SomeValue'))
        );
    }

    public function testRenewingSessionClosesBrowserSessionAndStartsNewOne()
    {
        // given
        self::$_wiremock->stubFor(
            WireMock::get(WireMock::urlEqualTo('/'))->willReturn(
                WireMock::aResponse()->withBodyFile(self::INDEX_PAGE)
                    ->withHeader('Set-Cookie', 'TestCookie=SomeValue;Path/\\n')
            )
        );
        $driver = new HtmlUnitDriverWrapper();
        $driver->visit('http://localhost:8080/');

        // when
        $driver->renewSession();
        $driver->visit('http://localhost:8080/cookie');

        // then
        self::$_wiremock->verify(
            WireMock::getRequestedFor(WireMock::urlEqualTo('/cookie'))
                ->withoutHeader('Cookie')
        );
    }

    private function _clickSubmit($driver)
    {
        $reflectionProperty = new \ReflectionProperty('XssFinder\Runner\HtmlUnitDriverWrapper', '_webDriver');
        $reflectionProperty->setAccessible(true);
        /** @var \RemoteWebDriver $webDriver */
        $webDriver = $reflectionProperty->getValue($driver);
        $webDriver->findElement(\WebDriverBy::id('submit'))->click();
    }

    private function _parseParams($body)
    {
        $params = array();
        $pairs = explode('&', $body);
        foreach ($pairs as $pair) {
            list($key, $value) = explode('=', $pair);
            $key = urldecode($key);
            $value = urldecode($value);
            $params[$key] = $value;
        }
        return $params;
    }
}