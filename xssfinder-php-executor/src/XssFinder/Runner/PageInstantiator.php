<?php

namespace XssFinder\Runner;

interface PageInstantiator {

    function instantiatePage(\ReflectionClass $class);

}