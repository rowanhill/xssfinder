<?php

namespace XssFinder\Scanner;

class PageFinderFactory {
    /**
     * @param string|null $namespace
     * @return PageFinder
     */
    public function createPageFinder($namespace = null)
    {
        return new PageFinder($namespace);
    }
}