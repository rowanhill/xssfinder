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

    /**
     * Gets definitions of all page objects in the given namespace
     *
     * @param string $namespaceIdentifier The namespace to filter pages by
     * @return array An array of PageDefinition objects
     */
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

    /**
     * Navigate the driver to the URL associated with the specified PageDefinition
     *
     * @param string $pageIdentifier The identifier of the crawlStartPoint PageDefinition to start the root at
     */
    public function startRoute($pageIdentifier)
    {
        $this->_executorContext->visitUrlOfRootPage($pageIdentifier);
    }

    /**
     * Put XSS attacks into all available inputs
     *
     * @return array A map of input identifiers -> attack identifiers
     */
    public function putXssAttackStringsInInputs()
    {
        // TODO: Implement putXssAttackStringsInInputs() method.
        return array();
    }

    /**
     * @return array The set of currently XSS attack identifiers observable on the current page
     */
    public function getCurrentXssIds()
    {
        // TODO: Implement getCurrentXssIds() method.
        return array();
    }

    /**
     * @return int The number of forms observable on the current page
     */
    public function getFormCount()
    {
        // TODO: Implement getFormCount() method.
        return 0;
    }

    /**
     * Traverse the given method on the current page object in the given mode
     *
     * @param MethodDefinition $method The method to invoke on the current page object
     * @param string $mode The mode in which to traverse the method
     * @return array A map of input identifiers -> attack identifiers
     */
    public function traverseMethod(MethodDefinition $method, $mode)
    {
        // TODO: Implement traverseMethod() method.
        return array();
    }

    /**
     * Invoke the 'after route' event handler for a route starting at the identified page
     *
     * @param string $rootPageIdentifier The root page of the route that has just finished
     */
    public function invokeAfterRouteHandler($rootPageIdentifier)
    {
        // TODO: Implement invokeAfterRouteHandler() method.
    }
}