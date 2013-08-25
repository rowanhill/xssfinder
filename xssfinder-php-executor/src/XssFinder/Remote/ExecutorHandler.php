<?php
namespace XssFinder\Remote;

use XssFinder\ExecutorIf;
use XssFinder\MethodDefinition;

class ExecutorHandler implements ExecutorIf
{
    public function getPageDefinitions($namespaceIdentifier)
    {
        // TODO: Implement getPageDefinitions() method.
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