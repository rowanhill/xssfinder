<?php

namespace XssFinder\TestHelper;

class Wiremock
{
    public static function startWiremockServer()
    {
        exec('java -jar ../vendor/wiremock/wiremock-1.33-standalone.jar &> wiremock.log &');
        return HttpWait::waitForServerToGive200("http://localhost:8080/__admin/");
    }

    public static function stopWiremockServer()
    {
        $result = 0;
        $output = array();
        exec(
            "kill -9 `ps -e | grep \"java -jar ../vendor/wiremock/wiremock-1.33-standalone.jar\" | grep -v grep | awk '{print $1}'`",
            $output,
            $result
        );
        return $result == 0;
    }
}