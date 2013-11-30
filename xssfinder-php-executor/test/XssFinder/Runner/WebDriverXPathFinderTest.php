<?php

namespace XssFinder\Runner;

use WebDriverElement;

class WebDriverXPathFinderTest extends \PHPUnit_Framework_TestCase
{
    function testElementWithIdReturnsXPathSelector()
    {
        // given
        $finder = new WebDriverXPathFinder();
        $element = $this->_mockElement('DIV', 'someId');

        // when
        $xpath = $finder->getXPath($element);

        // then
        assertThat($xpath, is('//div[@id="someId"]'));
    }

    function testBodyReturnsXPathSelector()
    {
        // given
        $finder = new WebDriverXPathFinder();
        $element = $this->_mockElement('BODY');

        // when
        $xpath = $finder->getXPath($element);

        // then
        assertThat($xpath, is('body'));
    }

    function testNondescriptElementReturnsXPathRelativeToAncestors()
    {
        // given
        $finder = new WebDriverXPathFinder();
        $element = $this->_mockElement('DIV');
        $parent = $this->_mockElement('DIV', 'parentId');
        $this->_setChildren($parent, $element);

        // when
        $xpath = $finder->getXPath($element);

        // then
        assertThat($xpath, is('//div[@id="parentId"]/div[1]'));
    }

    function testElementIndexIsDerivedFromParentsChildOfSameTag()
    {
        // given
        $finder = new WebDriverXPathFinder();
        $parent = $this->_mockElement('DIV', 'parentId');
        $element = $this->_mockElement('DIV');
        $priorSibling = $this->_mockElement('DIV');
        $priorIgnoredSibling = $this->_mockElement('P');
        $laterSibling = $this->_mockElement('DIV');
        $this->_setChildren($parent, $priorSibling, $priorIgnoredSibling, $element, $laterSibling);

        // when
        $xpath = $finder->getXPath($element);

        // then
        assertThat($xpath, is('//div[@id="parentId"]/div[2]'));
    }

    /**
     * @param string $tag
     * @param string $id
     * @return WebDriverElement
     */
    private function _mockElement($tag, $id=null) {
        /** @var WebDriverElement $element */
        $element = mock('WebDriverElement');
        when($element->getTagName())->return($tag);
        if ($id) {
            when($element->getAttribute('id'))->return($id);
        }
        return $element;
    }

    /**
     * @param WebDriverElement $parent
     * @param WebDriverElement $child1
     * @param WebDriverElement $anotherChild,...
     */
    private function _setChildren($parent, $child1) {
        $children = array();
        if (func_num_args()>1) {
            for ($i = 1; $i < func_num_args(); $i++) {
                $child = func_get_arg($i);
                $children[] = $child;
                when($child->findElement(\WebDriverBy::xpath("..")))->return($parent);
            }
        }
        when($parent->findElements(\WebDriverBy::xpath("./*")))->return($children);
    }
}