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

    public function findAll()
    {
        return new FindBuilder($this->_port);
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

class BodyFileSpecifier extends BodySpecifier
{
    private $_file;
    function __construct($file) { $this->_file = $file; }
    public function toArray()
    {
        return array('bodyFileName' => $this->_file);
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


abstract class AbstractVerifyBuilder extends JsonApiCallBuilder
{
    /** @var Verify */
    protected $_verify;

    /**
     * @param int $port
     */
    function __construct($port)
    {
        $this->_verify = new Verify();
        parent::__construct($this->_verify, $port);
    }

    protected function getCallResult()
    {
        $json = $this->_makeCall($this->getCallUrl());
        return json_decode($json, true);
    }

    abstract protected function getCallUrl();
}

class FindBuilder extends AbstractVerifyBuilder
{
    protected function getCallUrl()
    {
        return '__admin/requests/find';
    }

    public function get()
    {
        return new FindUrlMatcherBuilder($this, $this->_verify);
    }

    public function post()
    {
        $this->_verify->method = 'POST';
        return new FindUrlMatcherBuilder($this, $this->_verify);
    }

    public function query()
    {
        return $this->getCallResult();
    }
}

class VerifyBuilder extends AbstractVerifyBuilder
{
    protected function getCallUrl()
    {
        return '__admin/requests/count';
    }

    public function get()
    {
        return new VerifyUrlMatcherBuilder($this, $this->_verify);
    }

    public function post()
    {
        $this->_verify->method = 'POST';
        return new VerifyUrlMatcherBuilder($this, $this->_verify);
    }

    public function check()
    {
        $result = $this->getCallResult();
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

abstract class AbstractVerifyUrlMatcherBuilder
{
    /** @var Verify */
    private $_verify;

    function __construct(Verify $verify)
    {
        $this->_verify = $verify;
    }

    protected function setUpUrlMatcher($url)
    {
        $this->_verify->urlMatcher = new UrlMatcher();
        $this->_verify->urlMatcher->matcherScheme = 'url';
        $this->_verify->urlMatcher->urlValue = $url;
    }
}

class VerifyUrlMatcherBuilder extends AbstractVerifyUrlMatcherBuilder
{
    /** @var VerifyBuilder */
    private $_verifyCallBuilder;

    function __construct(VerifyBuilder $verifyBuilder, Verify $verify)
    {
        parent::__construct($verify);
        $this->_verifyCallBuilder = $verifyBuilder;
    }

    public function url($url)
    {
        $this->setUpUrlMatcher($url);
        return $this->_verifyCallBuilder;
    }
}

class VerifyMaker
{
    /** @var VerifyBuilder */
    private $_verifyCallBuilder;

    function __construct(VerifyBuilder $verifyBuilder)
    {
        $this->_verifyCallBuilder = $verifyBuilder;
    }

    public function check()
    {
        $this->_verifyCallBuilder->check();
    }
}

class FindUrlMatcherBuilder extends AbstractVerifyUrlMatcherBuilder
{
    /** @var FindBuilder */
    private $_findCallBuilder;

    function __construct(FindBuilder $findCallBuilder, Verify $verify)
    {
        parent::__construct($verify);
        $this->_findCallBuilder = $findCallBuilder;
    }

    public function url($url)
    {
        $this->setUpUrlMatcher($url);
        return new QueryMaker($this->_findCallBuilder);
    }
}

class QueryMaker
{
    /** @var FindBuilder */
    private $_findCallBuilder;

    function __construct(FindBuilder $findCallBuilder)
    {
        $this->_findCallBuilder = $findCallBuilder;
    }

    public function query()
    {
        return $this->_findCallBuilder->query();
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
     * @param string $file
     * @return $this
     */
    public function withBodyFile($file)
    {
        $this->_stub->response->body = new BodyFileSpecifier($file);
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