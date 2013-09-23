<?php

namespace XssFinder\Runner;

use PHPUnit_Framework_TestCase;

class TraversalResultTest extends PHPUnit_Framework_TestCase
{
    public function testHoldsInputIdsToAttackIds()
    {
        // given
        $inputIdsToAttackIds = array('//some/thing' => '123');
        $traversalResult = new TraversalResult($inputIdsToAttackIds);

        // when
        $ids = $traversalResult->getInputIdsToAttackIds();

        // then
        assertThat($ids, is($inputIdsToAttackIds));
    }
}