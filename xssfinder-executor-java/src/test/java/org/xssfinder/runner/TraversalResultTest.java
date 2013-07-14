package org.xssfinder.runner;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TraversalResultTest {
    @Test
    public void resultingPageAndAttackedIdsAreAvailable() {
        // given
        Object givenPage = mock(Object.class);
        Map<String, String> givenInputIdsToAttackIds = ImmutableMap.of("foo", "bar");
        TraversalResult result = new TraversalResult(givenPage, givenInputIdsToAttackIds);

        // when
        Object page = result.getPage();
        Map<String, String> inputIdsToAttackIds = result.getInputIdsToAttackIds();

        // then
        assertThat(page, is(givenPage));
        assertThat(inputIdsToAttackIds, is(givenInputIdsToAttackIds));
    }
}
