<?php

namespace XssFinder\TestHelper;

class Wiremock
{
    public static function startWiremockServer()
    {
        exec('cd ../../wiremock && `java -jar wiremock-1.33-standalone.jar &> wiremock.log &`');
        return HttpWait::waitForServerToGive200("http://localhost:8080/__admin/");
    }

    public static function stopWiremockServer()
    {
        $result = 0;
        $output = array();
        exec(
            "kill -9 `ps -e | grep \"java -jar wiremock-1.33-standalone.jar\" | grep -v grep | awk '{print $1}'`",
            $output,
            $result
        );
        return $result == 0;
    }
}