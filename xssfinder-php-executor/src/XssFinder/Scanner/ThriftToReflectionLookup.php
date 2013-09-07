<?php

namespace XssFinder\Scanner;

use ReflectionClass;
use ReflectionMethod;

class ThriftToReflectionLookup
{
    private $_pageClassesById = array();
    private $_methodsById = array();

    /**
     * @param string $pageIdentifier
     * @return ReflectionClass|null
     */
    public function getPageClass($pageIdentifier)
    {
        return isset($this->_pageClassesById[$pageIdentifier]) ? $this->_pageClassesById[$pageIdentifier] : null;
    }

    /**
     * @param string $methodIdentifier
     * @return ReflectionMethod|null
     */
    public function getMethod($methodIdentifier)
    {
        return isset($this->_methodsById[$methodIdentifier]) ? $this->_methodsById[$methodIdentifier] : null;
    }

    /**
     * @param string $pageIdentifier
     * @param ReflectionClass $reflectionClass
     */
    public function putPageClass($pageIdentifier, ReflectionClass $reflectionClass)
    {
        $this->_pageClassesById[$pageIdentifier] = $reflectionClass;
    }

    /**
     * @param string $methodIdentifier
     * @param ReflectionMethod $method
     */
    public function putMethod($methodIdentifier, ReflectionMethod $method)
    {
        $this->_methodsById[$methodIdentifier] = $method;
    }
}