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

    public function setUp()
    {
        $json = $this->_jsonApiCall->toJson();

        $port = $this->_port;
        $ch = curl_init("http://localhost:$port/__admin/mappings/new");
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST");
        curl_setopt($ch, CURLOPT_POSTFIELDS, $json);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                'Content-Type: application/json',
                'Content-Length: ' . strlen($json))
        );

        curl_exec($ch);
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

    public function setUp()
    {
        $this->_callBuilder->setUp();
    }

}

class StubBuilder extends JsonApiCallBuilder
{
    /** @var Stub */
    private $_stub;
    function __construct($port)
    {
        $this->_stub = new Stub();
        parent::__construct($this->_stub, $port);
    }

    public function get()
    {
        return new UrlMatcherBuilder($this, $this->_stub);
    }
}

class UrlMatcherBuilder extends JsonApiCallFragmentBuilder
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

class ResponseBuilder extends JsonApiCallFragmentBuilder
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