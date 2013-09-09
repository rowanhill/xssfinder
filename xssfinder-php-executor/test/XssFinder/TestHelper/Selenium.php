<?php

namespace XssFinder\TestHelper;

class Selenium
{
    public static function startSeleniumServer()
    {
        exec('java -jar ../vendor/selenium/selenium-server-standalone-2.35.0.jar &> selenium.log &');
        return HttpWait::waitForServerToGive200("http://localhost:4444/wd/hub/status");
    }

    public static function stopSeleniumServer()
    {
        try {
            get_headers('http://localhost:4444/selenium-server/driver?cmd=shutDownSeleniumServer', 1);
        } catch (\Exception $e) {
            return true;
        }
        return HttpWait::waitForServerToNotRespond("http://localhost:4444/wd/hub/status");
    }
}