<?php

class ReflectionHelperTest_SomeObject {
    /**
     * @return ReflectionHelperTest_SomeObject
     */
    public function getSomeObject() { return new self(); }
}