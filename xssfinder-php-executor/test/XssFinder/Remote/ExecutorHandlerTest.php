<?php

namespace XssFinder\Remote;

use PHPUnit_Framework_TestCase;
use XssFinder\Remote\ExecutorHandler;
use XssFinder\Scanner\PageDefinitionFactory;
use XssFinder\Scanner\PageFinder;
use XssFinder\Scanner\PageFinderFactory;

class ExecutorHandlerTest extends PHPUnit_Framework_TestCase
{
    const SOME_NAMESPACE = 'namespace';

    /** @var PageFinderFactory */
    private $_mockPageFinderFactory;
    /** @var PageFinder */
    private $_mockPageFinder;
    /** @var PageDefinitionFactory */
    private $_mockPageDefinitionFactory;

    /** @var ExecutorHandler */
    private $_handler;

    public function setUp()
    {
        $this->_mockPageFinderFactory = mock('\XssFinder\Scanner\PageFinderFactory');
        $this->_mockPageFinder = mock('\XssFinder\Scanner\PageFinder');
        $this->_mockPageDefinitionFactory = mock('\XssFinder\Scanner\PageDefinitionFactory');

        when($this->_mockPageFinderFactory->createPageFinder(self::SOME_NAMESPACE))->return($this->_mockPageFinder);

    }

    public function testGettingPageDefinitionsIsDelegatedToFinderAndFactory()
    {
        // given
        $classNames = array('SomePage', 'NotAPage');
        $pageNames = array('SomePage');
        when($this->_mockPageFinder->findPages($classNames))->return($pageNames);
        $mockPageDefinition = mock('XssFinder\PageDefinition');
        when($this->_mockPageDefinitionFactory->createPageDefinition('SomePage'))->return($mockPageDefinition);
        $this->_handler = new ExecutorHandler($this->_mockPageFinderFactory, $this->_mockPageDefinitionFactory, $classNames);

        // when
        $pageDefinitions = $this->_handler->getPageDefinitions(self::SOME_NAMESPACE);

        // then
        assertThat($pageDefinitions, equalTo(array($mockPageDefinition)));
    }
}