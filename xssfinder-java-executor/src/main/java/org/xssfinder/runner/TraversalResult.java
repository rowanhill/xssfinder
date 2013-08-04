package org.xssfinder.runner;

import java.util.Map;

public class TraversalResult {
    private Object page;
    private Map<String, String> inputIdsToAttackIds;

    public TraversalResult(Object page, Map<String, String> inputIdsToAttackIds) {
        this.page = page;
        this.inputIdsToAttackIds = inputIdsToAttackIds;
    }

    public Object getPage() {
        return page;
    }

    public Map<String, String> getInputIdsToAttackIds() {
        return inputIdsToAttackIds;
    }
}
