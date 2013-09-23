<?php

namespace XssFinder\Runner;

use CrawlStartPointAnnotation;
use XssFinder\Annotations\Annotations;
use XssFinder\MethodDefinition;
use XssFinder\Scanner\ThriftToReflectionLookup;
use XssFinder\Xss\XssGenerator;

class ExecutorContext
{
    /** @var DriverWrapper */
    private $_driverWrapper;
    /** @var XssGenerator */
    private $_xssGenerator;
    /** @var ThriftToReflectionLookup */
    private $_lookup = null;
    /** @var PageInstantiator */
    private $_pageInstantiator;
    /** @var PageTraverser */
    private $_pageTraverser;

    /** @var mixed Current page-annotated object */
    private $_currentPage;

    public function __construct(
        DriverWrapper $driverWrapper,
        XssGenerator $xssGenerator,
        PageTraverser $pageTraverser
    ) {
        Annotations::load();
        $this->_driverWrapper = $driverWrapper;
        $this->_xssGenerator = $xssGenerator;
        $this->_pageInstantiator = $driverWrapper->getPageInstantiator();
        $this->_pageTraverser = $pageTraverser;
    }

    /**
     * @param ThriftToReflectionLookup $lookup
     */
    public function setThriftToReflectionLookup(ThriftToReflectionLookup $lookup)
    {
        $this->_lookup = $lookup;
    }

    /**
     * @param $pageIdentifier string The Thrift identifier of the root page to visit
     * @throws \Exception
     */
    public function visitUrlOfRootPage($pageIdentifier)
    {
        if ($this->_lookup === null) {
            throw new \Exception('ThriftToReflectionLookup has not yet been set');
        }
        $pageClass = $this->_lookup->getPageClass($pageIdentifier);
        $annotation = CrawlStartPointAnnotation::getCrawlStartPoint($pageClass);
        $this->_driverWrapper->visit($annotation->url);
        $this->_currentPage = $this->_pageInstantiator->instantiatePage($pageClass);
    }

    public function putXssAttackStringsInInputs()
    {
        return $this->_driverWrapper->putXssAttackStringsInInputs($this->_xssGenerator);
    }

    /**
     * @param MethodDefinition $methodDefinition
     * @param int $traversalMode
     * @return TraversalResult
     */
    public function traverseMethod(MethodDefinition $methodDefinition, $traversalMode)
    {
        $method = $this->_lookup->getMethod($methodDefinition->identifier);
        $traversalResult = $this->_pageTraverser->traverse($this->_currentPage, $method, $traversalMode);
        //TODO Update current page
        return $traversalResult;
    }
}