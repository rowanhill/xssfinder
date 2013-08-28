<?php
namespace XssFinder\Remote;

use XssFinder\ExecutorIf;
use XssFinder\MethodDefinition;
use XssFinder\Scanner\PageDefinitionFactory;
use XssFinder\Scanner\PageFinderFactory;

class ExecutorHandler implements ExecutorIf
{
    /** @var \XssFinder\Scanner\PageFinderFactory */
    private $_pageFinderFactory;
    /** @var \XssFinder\Scanner\PageDefinitionFactory */
    private $_pageDefinitionFactory;
    /** @var array */
    private $_classNames;

    public function __construct(
        PageFinderFactory $pageFinderFactory,
        PageDefinitionFactory $pageDefinitionFactory,
        array $classNames
    ) {
        $this->_pageFinderFactory = $pageFinderFactory;
        $this->_pageDefinitionFactory = $pageDefinitionFactory;
        $this->_classNames = $classNames;
    }

    public function getPageDefinitions($namespaceIdentifier)
    {
        $pageFinder = $this->_pageFinderFactory->createPageFinder($namespaceIdentifier);
        $pageNames = $pageFinder->findPages($this->_classNames);
        $pageDefinitions = array();
        foreach ($pageNames as $pageName) {
            $pageDefinitions[] = $this->_pageDefinitionFactory->createPageDefinition($pageName);
        }
        return $pageDefinitions;
    }

    public function startRoute($pageIdentifier)
    {
        // TODO: Implement startRoute() method.
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