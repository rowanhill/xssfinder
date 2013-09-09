<?php

namespace Wiremock;

class Wiremock
{
    /** @var int */
    private $_port;

    public function __construct($port)
    {
        $this->_port = $port;
    }

    public function stubFor()
    {
        return new StubBuilder($this->_port);
    }

    public function verify()
    {
        return new VerifyBuilder($this->_port);
    }
}

/*------------------------------------------------------------------------------
 * JSON data holders
 *------------------------------------------------------------------------------*/

abstract class JsonApiCallFragment
{
    abstract function toArray();
}

abstract class JsonApiCall
{
    abstract function toJson();
}

class Verify extends JsonApiCall
{
    public $method = 'GET';
    /** @var UrlMatcher */
    public $urlMatcher = null;

    function toJson()
    {
        if ($this->urlMatcher == null)
        {
            throw new \Exception("Url matcher must be specified");
        }
        $call = array_merge(array('method' => $this->method), $this->urlMatcher->toArray());
        return json_encode($call);
    }
}

class Stub extends JsonApiCall
{
    /** @var Request */
    public $request;
    /** @var Response */
    public $response;

    function __construct()
    {
        $this->request = new Request();
        $this->response = new Response();
    }

    function toJson()
    {
        $call = array(
            'request' => $this->request->toArray(),
            'response' => $this->response->toArray()
        );

        return json_encode($call);
    }
}

class Request extends JsonApiCallFragment
{
    public $method = 'GET';
    /** @var UrlMatcher */
    public $urlMatcher = null;

    public function toArray()
    {
        if ($this->urlMatcher == null) {
            throw new \Exception('Request URL must be specified');
        }
        return array_merge(array('method' => $this->method), $this->urlMatcher->toArray());
    }
}

class UrlMatcher extends JsonApiCallFragment
{
    public $matcherScheme;
    public $urlValue;

    public function toArray()
    {
        if ($this->matcherScheme == null || $this->urlValue == null) {
            throw new \Exception('Matching scheme and URL value must be specified');
        }
        return array($this->matcherScheme => $this->urlValue);
    }
}

class Response extends JsonApiCallFragment
{
    public $status = 200;
    /** @var BodySpecifier */
    public $body;
    public $headers = array();

    function __construct()
    {
        $this->body = new BodyLiteralSpecifier('');
    }

    public function toArray()
    {
        $response = array();
        $response['status'] = $this->status;
        if (!empty($this->headers)) {
            $response['headers'] = $this->headers;
        }
        return array_merge($response, $this->body->toArray());
    }
}

abstract class BodySpecifier extends JsonApiCallFragment {}

class BodyLiteralSpecifier extends BodySpecifier
{
    private $_body;
    function __construct($body) { $this->_body = $body; }
    public function toArray()
    {
        return array('body' => $this->_body);
    }
}

/*------------------------------------------------------------------------------
 * Builders
 *------------------------------------------------------------------------------*/

abstract class JsonApiCallBuilder
{
    /** @var JsonApiCall */
    protected $_jsonApiCall;
    private $_port;

    function __construct(JsonApiCall $jsonApiCall, $port)
    {
        $this->_jsonApiCall = $jsonApiCall;
        $this->_port = $port;
    }

    public function _makeCall($path)
    {
        $json = $this->_jsonApiCall->toJson();

        $port = $this->_port;
        $ch = curl_init("http://localhost:$port/$path");
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST");
        curl_setopt($ch, CURLOPT_POSTFIELDS, $json);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                'Content-Type: application/json',
                'Content-Length: ' . strlen($json))
        );

        $result = curl_exec($ch);

        curl_close($ch);

        return $result;
    }
}


abstract class JsonApiCallFragmentBuilder
{
    /** @var JsonApiCallBuilder */
    protected $_callBuilder;

    function __construct(JsonApiCallBuilder $callBuilder)
    {
        $this->_callBuilder = $callBuilder;
    }
}

abstract class StubApiCallFragmentBuilder extends JsonApiCallFragmentBuilder
{
    public function setUp()
    {
        /** @var StubBuilder $callBuilder */
        $callBuilder = $this->_callBuilder;
        $callBuilder->setUp();
    }
}

abstract class VerifyApiCallFragmentBuilder extends JsonApiCallFragmentBuilder
{
    public function check()
    {
        /** @var VerifyBuilder $callBuilder */
        $callBuilder = $this->_callBuilder;
        $callBuilder->check();
    }
}

class VerifyBuilder extends JsonApiCallBuilder
{
    /** @var Verify */
    private $_verify;

    /**
     * @param int $port
     */
    function __construct($port)
    {
        $this->_verify = new Verify();
        parent::__construct($this->_verify, $port);
    }

    public function get()
    {
        return new VerifyUrlMatcherBuilder($this, $this->_verify);
    }

    public function check()
    {
        $json = $this->_makeCall('__admin/requests/count');
        $result = json_decode($json, true);
        $count = $result['count'];
        if ($count != 1) {
            throw new \Exception("Expected call to have been made once, but was made $count times");
        }
        assertThat($count, is(1));
    }
}

class StubBuilder extends JsonApiCallBuilder
{
    /** @var Stub */
    private $_stub;

    /**
     * @param int $port
     */
    function __construct($port)
    {
        $this->_stub = new Stub();
        parent::__construct($this->_stub, $port);
    }

    public function get()
    {
        return new StubUrlMatcherBuilder($this, $this->_stub);
    }

    public function setUp()
    {
        $this->_makeCall('__admin/mappings/new');
    }
}

class StubUrlMatcherBuilder extends JsonApiCallFragmentBuilder
{
    /** @var Stub */
    private $_stub;

    function __construct(JsonApiCallBuilder $callBuilder, Stub $stub)
    {
        parent::__construct($callBuilder);
        $this->_stub = $stub;
    }

    public function url($url)
    {
        $this->_stub->request->urlMatcher = new UrlMatcher();
        $this->_stub->request->urlMatcher->matcherScheme = 'url';
        $this->_stub->request->urlMatcher->urlValue = $url;
        return new RequestSpecifiedBuilder($this->_callBuilder, $this->_stub);
    }
}

class VerifyUrlMatcherBuilder extends VerifyApiCallFragmentBuilder
{
    /** @var Verify */
    private $_verify;

    function __construct(JsonApiCallBuilder $callBuilder, Verify $verify)
    {
        parent::__construct($callBuilder);
        $this->_verify = $verify;
    }

    public function url($url)
    {
        $this->_verify->urlMatcher = new UrlMatcher();
        $this->_verify->urlMatcher->matcherScheme = 'url';
        $this->_verify->urlMatcher->urlValue = $url;
        return $this;
    }
}

class RequestSpecifiedBuilder extends JsonApiCallFragmentBuilder
{
    private $_stub;

    function __construct(JsonApiCallBuilder $callBuilder, Stub $stub)
    {
        parent::__construct($callBuilder);
        $this->_stub = $stub;
    }

    public function willReturnResponse()
    {
        return new ResponseBuilder($this->_callBuilder, $this->_stub);
    }
}

class ResponseBuilder extends StubApiCallFragmentBuilder
{
    private $_stub;

    function __construct(JsonApiCallBuilder $callBuilder, Stub $stub)
    {
        parent::__construct($callBuilder);
        $this->_stub = $stub;
    }

    /**
     * @param int $status
     * @return $this
     */
    public function withStatus($status)
    {
        $this->_stub->response->status = $status;
        return $this;
    }

    /**
     * @param string $body
     * @return $this
     */
    public function withBody($body)
    {
        $this->_stub->response->body = new BodyLiteralSpecifier($body);
        return $this;
    }

    /**
     * @param string $header
     * @param string $value
     * @return $this
     */
    public function withHeader($header, $value)
    {
        $this->_stub->response->headers[$header] = $value;
        return $this;
    }
}