<?php

namespace XssFinder\Runner;

/**
 * The annotation library doesn't handle multiple classes in a single file well, so tests may need their pages in
 * dedicated files.
 */

/**
 * @page
 * @crawlStartPoint('url' => 'http://home')
 */
class EC_TestPages_HomePage {
    /**
     * @return EC_TestPages_HomePage $this
     */
    function refreshHomePage() { return $this; }
}