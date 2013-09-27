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

    /**
     * @return EC_TestPages_SecondPage
     */
    function goToSecondPage() { return new EC_TestPages_SecondPage(); }
}

/**
 * @page
 */
class EC_TestPages_SecondPage {
    /**
     * @return EC_TestPages_ThirdPage
     */
    function goToThirdPage() { return new EC_TestPages_ThirdPage(); }
}

/**
 * @page
 */
class EC_TestPages_ThirdPage {
}