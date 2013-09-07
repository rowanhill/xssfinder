<?php

namespace XssFinder\Scanner;

use XssFinder\Annotations\Annotations;
use XssFinder\PageDefinition;

class PageDefinitionFactory
{
    /** @var \XssFinder\Scanner\MethodDefinitionFactory */
    private $_methodDefinitionFactory;
    /** @var ReflectionHelper */
    private $_reflectionHelper;
    /** @var array */
    private $_knownPageClassNames;

    /**
     * @param MethodDefinitionFactory $methodDefinitionFactory
     * @param ReflectionHelper $reflectionHelper
     * @param array $knownPageClassNames
     */
    public function __construct(
        MethodDefinitionFactory $methodDefinitionFactory,
        ReflectionHelper $reflectionHelper,
        array $knownPageClassNames
    ) {
        $this->_methodDefinitionFactory = $methodDefinitionFactory;
        $this->_reflectionHelper = $reflectionHelper;
        $this->_knownPageClassNames = $knownPageClassNames;
    }

    /**
     * @param $className string Fully qualified name of the page object class
     * @param ThriftToReflectionLookup $lookup
     * @return \XssFinder\PageDefinition
     */
    public function createPageDefinition($className, ThriftToReflectionLookup $lookup)
    {
        $reflectionClass = new \ReflectionClass($className);

        $pageDefinition = new PageDefinition();
        $pageDefinition->identifier = '\\' . $reflectionClass->getName();
        $pageDefinition->methods = $this->_getMethodDefinitions($reflectionClass, $lookup);
        $pageDefinition->crawlStartPoint = $this->_isCrawlStartPoint($reflectionClass);

        $lookup->putPageClass($pageDefinition->identifier, $reflectionClass);

        return $pageDefinition;
    }

    /**
     * @param $reflectionClass \ReflectionClass
     * @param ThriftToReflectionLookup $lookup
     * @return array
     */
    private function _getMethodDefinitions(\ReflectionClass $reflectionClass, ThriftToReflectionLookup $lookup)
    {
        $methodDefinitions = array();
        foreach ($reflectionClass->getMethods() as $method) {
            $returnClassName = $this->_reflectionHelper->getReturnType($method);
            if ($returnClassName == null) {
                continue;
            }
            $methodDefinition =
                $this->_createMethodDefinitionIfReturnClassIsPage($returnClassName, $method);
            if ($methodDefinition !== null) {
                $methodDefinitions[] = $methodDefinition;
                $lookup->putMethod($methodDefinition->identifier, $method);
            }
        }
        return $methodDefinitions;
    }

    /**
     * @param $returnClassName string
     * @param $method \ReflectionMethod
     * @return null|\XssFinder\MethodDefinition
     */
    private function _createMethodDefinitionIfReturnClassIsPage($returnClassName, \ReflectionMethod $method)
    {
        if (in_array($returnClassName, $this->_knownPageClassNames)) {
            return $this->_methodDefinitionFactory->createMethodDefinition($method);
        }
        return null;
    }

    private function _isCrawlStartPoint(\ReflectionClass $reflectionClass)
    {
        $annotationsManager = Annotations::getConfiguredManager();
        $annotations = $annotationsManager->getClassAnnotations($reflectionClass, '@crawlStartPoint');
        return !empty($annotations);
    }
}