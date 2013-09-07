<?php

namespace XssFinder\Scanner;

class ThriftToReflectionLookupFactory {
    public function createLookup()
    {
        return new ThriftToReflectionLookup();
    }
}