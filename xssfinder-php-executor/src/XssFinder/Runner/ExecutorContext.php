<?php

namespace XssFinder\Runner;

use CrawlStartPointAnnotation;
use XssFinder\Annotations\Annotations;
use XssFinder\Scanner\ThriftToReflectionLookup;

class ExecutorContext
{
    /** @var DriverWrapper */
    private $_driverWrapper;
    /** @var ThriftToReflectionLookup */
    private $_lookup = null;

    public function __construct(
        DriverWrapper $driverWrapper
    ) {
        Annotations::load();
        $this->_driverWrapper = $driverWrapper;
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
    }
}