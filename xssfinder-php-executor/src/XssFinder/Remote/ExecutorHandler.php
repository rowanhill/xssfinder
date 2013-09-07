<?php
namespace XssFinder\Remote;

use XssFinder\ExecutorIf;
use XssFinder\MethodDefinition;
use XssFinder\Runner\ExecutorContext;
use XssFinder\Scanner\PageDefinitionFactory;
use XssFinder\Scanner\PageFinderFactory;
use XssFinder\Scanner\ThriftToReflectionLookupFactory;

class ExecutorHandler implements ExecutorIf
{
    /** @var \XssFinder\Scanner\PageFinderFactory */
    private $_pageFinderFactory;
    /** @var \XssFinder\Scanner\PageDefinitionFactory */
    private $_pageDefinitionFactory;
    /** @var  \XssFinder\Runner\ExecutorContext */
    private $_executorContext;
    /** @var ThriftToReflectionLookupFactory */
    private $_lookupFactory;
    /** @var array */
    private $_classNames;

    public function __construct(
        PageFinderFactory $pageFinderFactory,
        PageDefinitionFactory $pageDefinitionFactory,
        ThriftToReflectionLookupFactory $lookupFactory,
        ExecutorContext $executorContext,
        array $classNames
    ) {
        $this->_pageFinderFactory = $pageFinderFactory;
        $this->_pageDefinitionFactory = $pageDefinitionFactory;
        $this->_lookupFactory = $lookupFactory;
        $this->_executorContext = $executorContext;
        $this->_classNames = $classNames;
    }

    public function getPageDefinitions($namespaceIdentifier)
    {
        $pageFinder = $this->_pageFinderFactory->createPageFinder($namespaceIdentifier);
        $pageNames = $pageFinder->findPages($this->_classNames);
        $pageDefinitions = array();
        $lookup = $this->_lookupFactory->createLookup();
        foreach ($pageNames as $pageName) {
            $pageDefinitions[] = $this->_pageDefinitionFactory->createPageDefinition($pageName, $lookup);
        }
        $this->_executorContext->setThriftToReflectionLookup($lookup);
        return $pageDefinitions;
    }

    public function startRoute($pageIdentifier)
    {
        $this->_executorContext->visitUrlOfRootPage($pageIdentifier);
    }

    public function putXssAttackStringsInInputs()
    {
        // TODO: Implement putXssAttackStringsInInputs() method.
    }

    public function getCurrentXssIds()
    {
        // TODO: Implement getCurrentXssIds() method.
    }

    public function getFormCount()
    {
        // TODO: Implement getFormCount() method.
    }

    public function traverseMethod(MethodDefinition $method, $mode)
    {
        // TODO: Implement traverseMethod() method.
    }

    public function invokeAfterRouteHandler($rootPageIdentifier)
    {
        // TODO: Implement invokeAfterRouteHandler() method.
    }
}