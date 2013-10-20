<?php

namespace XssFinder\Remote;

use PHPUnit_Framework_TestCase;
use XssFinder\MethodDefinition;
use XssFinder\PageDefinition;
use XssFinder\Remote\ExecutorHandler;
use XssFinder\Runner\ExecutorContext;
use XssFinder\Runner\TraversalResult;
use XssFinder\Scanner\PageDefinitionFactory;
use XssFinder\Scanner\PageFinder;
use XssFinder\Scanner\PageFinderFactory;
use XssFinder\Scanner\ThriftToReflectionLookup;
use XssFinder\Scanner\ThriftToReflectionLookupFactory;
use XssFinder\TraversalMode;

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
        /** @var PageDefinition $mockPageDefinition */
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

    public function testPuttingXssAttackStringsInInputsIsDelegatedToExecutorContext()
    {
        // given
        $this->_handler = $this->_createExecutorHandler();
        $expectedResult = array('inputId' => 'attackId');
        when($this->_mockExecutorContext->putXssAttackStringsInInputs())->return($expectedResult);

        // when
        $inputIdsToAttackIds = $this->_handler->putXssAttackStringsInInputs();

        // then
        assertThat($inputIdsToAttackIds, is($expectedResult));
    }

    public function testTraversingMethodIsDelegatedToExecutorContext()
    {
        // given
        $this->_handler = $this->_createExecutorHandler();
        /** @var MethodDefinition $mockMethodDefinition */
        $mockMethodDefinition = mock('XssFinder\MethodDefinition');
        /** @var TraversalResult $mockTraversalResult */
        $mockTraversalResult = mock('XssFinder\Runner\TraversalResult');
        $expectedInputIdsToXssIds = array('//some/input' => '123');
        when($mockTraversalResult->getInputIdsToAttackIds())->return($expectedInputIdsToXssIds);
        when($this->_mockExecutorContext->traverseMethod($mockMethodDefinition, TraversalMode::NORMAL))
            ->return($mockTraversalResult);

        // when
        $inputIdsToXssIds = $this->_handler->traverseMethod($mockMethodDefinition, TraversalMode::NORMAL);

        // then
        assertThat($inputIdsToXssIds, is($expectedInputIdsToXssIds));
    }

    public function testGettingCurrentXssIdsIsDelegatedToExecutorContext()
    {
        // given
        $this->_handler = $this->_createExecutorHandler();
        when($this->_mockExecutorContext->getCurrentXssIds())->return(array('1','2'));

        // when
        $currentXssIds = $this->_handler->getCurrentXssIds();

        // then
        assertThat($currentXssIds, is(array('1','2')));
    }

    public function testGettingFormCountIsDelegatedToExecutorContext()
    {
        // given
        $this->_handler = $this->_createExecutorHandler();
        when($this->_mockExecutorContext->getFormCount())->return(3);

        // when
        $formCount = $this->_handler->getFormCount();

        // then
        assertThat($formCount, is(3));
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