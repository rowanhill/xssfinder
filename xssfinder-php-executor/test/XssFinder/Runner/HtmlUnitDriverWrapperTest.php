<?php

namespace XssFinder\Runner;

require_once(__DIR__ . '/../../Wiremock.php');

use Wiremock\Wiremock;
use XssFinder\TestHelper\Selenium;
use XssFinder\Xss\XssAttack;
use XssFinder\Xss\XssGenerator;

class HtmlUnitDriverWrapperTest extends \PHPUnit_Framework_TestCase
{
    const INDEX_PAGE = "HtmlUnitDriverWrapperTest_index.html";
    const TWO_FORM_PAGE = "HtmlUnitDriverWrapperTest_two_forms.html";
    const NO_XSS_PAGE = "HtmlUnitDriverWrapperTest_no_xss.html";

    /** @var Wiremock */
    private static $_wiremock;
    
    public static function setUpBeforeClass()
    {
        assertThat(\XssFinder\TestHelper\Wiremock::startWiremockServer(), is(true));
        self::$_wiremock = new Wiremock(8080);
    }

    public static function tearDownAfterClass()
    {
        assertThat(\XssFinder\TestHelper\Wiremock::stopWiremockServer(), is(true));
    }

    public function setUp()
    {
        self::$_wiremock->reset();
        assertThat(Selenium::startSeleniumServer(), is(true));
    }

    public function tearDown()
    {
        assertThat(Selenium::stopSeleniumServer(), is(true));
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
        self::$_wiremock->stubFor()->get()->url('/some-url')->willReturnResponse()->withBody('Here is a body')->setUp();
        $driver = new HtmlUnitDriverWrapper();

        // when
        $driver->visit('http://localhost:8080/some-url');

        // then
        self::$_wiremock->verify()->get()->url('/some-url')->check();
    }
    
    public function testXssAttackingPutsXssFromGeneratorInAllTextInputAndTextArea()
    {
        // given
        self::$_wiremock->stubFor()->get()->url('/')->willReturnResponse()->withBodyFile(self::INDEX_PAGE)->setUp();
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
        $requests = self::$_wiremock->findAll()->post()->url('/submit')->query();
        assertThat(count($requests['requests']), is(1));
        $request = $requests['requests'][0];
        $body = $request['body'];
        $params = $this->_parseParams($body);
        assertThat(count($params), is(5));
        assertThat($params, hasEntry('text1', 'xss'));
        assertThat($params, hasEntry('text2', 'xss'));
        assertThat($params, hasEntry('search', 'xss'));
        assertThat($params, hasEntry('password', 'xss'));
        assertThat($params, hasEntry('textarea', 'xss'));
    }

    public function testCurrentXssIdsAreGotFromJsArrayVar()
    {
        // given
        self::$_wiremock->stubFor()->get()->url('/')->willReturnResponse()->withBodyFile(self::INDEX_PAGE)->setUp();
        $driver = new HtmlUnitDriverWrapper();
        $driver->visit('http://localhost:8080/');

        // when
        $currentXssIds = $driver->getCurrentXssIds();

        // then
        assertThat($currentXssIds, is(array('123', '456')));
    }

    public function testCurrentXssIdsReturnsEmptyArrayIfJsVarNotDefined()
    {
        // given
        self::$_wiremock->stubFor()->get()->url('/')->willReturnResponse()->withBodyFile(self::NO_XSS_PAGE)->setUp();
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
        self::$_wiremock->stubFor()->get()->url('/')->willReturnResponse()->withBodyFile(self::INDEX_PAGE)->setUp();
        $driver = new HtmlUnitDriverWrapper();
        $driver->visit('http://localhost:8080/');

        // when
        $formCount = $driver->getFormCount();

        // then
        assertThat($formCount, is(1));
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