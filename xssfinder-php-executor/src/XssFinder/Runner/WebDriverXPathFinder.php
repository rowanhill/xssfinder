<?php

namespace XssFinder\Runner;

use WebDriverElement;

class WebDriverXPathFinder
{
    /**
     * @param WebDriverElement $element
     * @return string
     */
    public function getXPath(WebDriverElement $element)
    {
        $elementTagName = strtolower($element->getTagName());
        if ($element->getAttribute('id')) {
            return sprintf('//%s[@id="%s"]', $elementTagName, $element->getAttribute('id'));
        } elseif ($elementTagName === 'body') {
            return 'body';
        }

        $parentElement = $element->findElement(\WebDriverBy::xpath('..'));
        $childElements = $parentElement->findElements(\WebDriverBy::xpath('./*'));
        $matchingChildNumber = 1;
        foreach ($childElements as $childElement) {
            /** @var $childElement WebDriverElement */
            if ($childElement == $element) {
                return $this->getXPath($parentElement) . sprintf('/%s[%d]', $elementTagName, $matchingChildNumber);
            } elseif (strtolower($childElement->getTagName()) === $elementTagName) {
                $matchingChildNumber++;
            }
        }
        throw new \Exception("Web driver element is not a child of its parent");
    }
}