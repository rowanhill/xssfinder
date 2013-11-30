<?php

namespace XssFinder\TestHelper;

class HttpWait
{
    public static function waitForServerToGive200($url, $timeoutSecs = 10)
    {
        $startTime = microtime(true);
        $serverStarted = false;
        while (microtime(true) - $startTime < $timeoutSecs) {
            try {
                $headers = @get_headers($url, 1);
            } catch (\Exception $e) {
                continue;
            }
            if (isset($headers) && isset($headers[0]) && $headers[0] === 'HTTP/1.1 200 OK') {
                $serverStarted = true;
                break;
            }
        }
        return $serverStarted;
    }

    public static function waitForServerToNotRespond($url, $timeoutSecs = 10)
    {
        $startTime = microtime(true);
        $serverShutDown = false;
        while (microtime(true) - $startTime < $timeoutSecs) {
            try {
                if (!@get_headers($url, 1)) {
                    $serverShutDown = true;
                    break;
                }
            } catch (\Exception $e) {
                $serverShutDown = true;
                break;
            }
        }
        return $serverShutDown;
    }
}