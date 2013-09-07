<?php

namespace XssFinder\Remote;

use PHPUnit_Framework_TestCase;
use XssFinder\Remote\ExecutorHandler;
use XssFinder\Runner\ExecutorContext;
use XssFinder\Scanner\PageDefinitionFactory;
use XssFinder\Scanner\PageFinder;
use XssFinder\Scanner\PageFinderFactory;
use XssFinder\Scanner\ThriftToReflectionLookup;
use XssFinder\Scanner\ThriftToReflectionLookupFactory;

class ExecutorHandlerTest extends PHPUnit_Framework_TestCase
{
    const SOME_NAMESPACE = 'namespace';

    /** @var PageFinderFactory */
    private $_mockPageFinderFactory;
    /** @var PageFinder */
    private $_mockPageFinder;
    /** @var PageDefinitionFactory */
    private $_mockPageDefinitionFactory;
    /** @var ThriftToReflectionLookupFactory */
    private $_mockLookupFactory;
    /** @var ExecutorContext */
    private $_mockExecutorContext;

    /** @var ThriftToReflectionLookup */
    private $_mockLookup;

    /** @var ExecutorHandler */
    private $_handler;

    public function setUp()
    {
        $this->_mockPageFinderFactory = mock('\XssFinder\Scanner\PageFinderFactory');
        $this->_mockPageFinder = mock('\XssFinder\Scanner\PageFinder');
        $this->_mockPageDefinitionFactory = mock('\XssFinder\Scanner\PageDefinitionFactory');
        $this->_mockLookupFactory = mock('\XssFinder\Scanner\ThriftToReflectionLookupFactory');
        $this->_mockExecutorContext = mock('\XssFinder\Runner\ExecutorContext');

        $this->_mockLookup = mock('\XssFinder\Scanner\ThriftToReflectionLookup');
        when($this->_mockLookupFactory->createLookup())->return($this->_mockLookup);

        when($this->_mockPageFinderFactory->createPageFinder(self::SOME_NAMESPACE))->return($this->_mockPageFinder);

    }

    public function testGettingPageDefinitionsIsDelegatedToFinderAndFactoryAndLookupIsSetOnContext()
    {
        // given
        $classNames = array('SomePage', 'NotAPage');
        $pageNames = array('SomePage');
        when($this->_mockPageFinder->findPages($classNames))->return($pageNames);
        $mockPageDefinition = mock('XssFinder\PageDefinition');
        when($this->_mockPageDefinitionFactory->createPageDefinition(
            'SomePage', argOfTypeThat('XssFinder\Scanner\ThriftToReflectionLookup', anything()))
        )->return($mockPageDefinition);
        $this->_handler = $this->_createExecutorHandler($classNames);

        // when
        $pageDefinitions = $this->_handler->getPageDefinitions(self::SOME_NAMESPACE);

        // then
        assertThat($pageDefinitions, equalTo(array($mockPageDefinition)));
        verify($this->_mockExecutorContext)->setThriftToReflectionLookup($this->_mockLookup);
    }

    public function testStartingRouteIsDelegatedToExecutorContext()
    {
        // given
        $this->_handler = $this->_createExecutorHandler();

        // when
        $this->_handler->startRoute('SomePage');

        // then
        verify($this->_mockExecutorContext)->visitUrlOfRootPage('SomePage');
    }

    private function _createExecutorHandler($classNames = array())
    {
        return new ExecutorHandler(
            $this->_mockPageFinderFactory,
            $this->_mockPageDefinitionFactory,
            $this->_mockLookupFactory,
            $this->_mockExecutorContext,
            $classNames
        );
    }
}