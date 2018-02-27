package org.cloudfoundry.identity.uaa.oauth.openid;

import org.cloudfoundry.identity.uaa.util.JsonUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

public class IdTokenSerializationTest {

    private IdToken idToken;

    @Before
    public void setup() {
        Set<String> amr = new HashSet<String>() {{
            add("amr1");
            add("amr2");
        }};

        Set<String> acr = new HashSet<String>() {{
            add("acr1");
            add("acr2");
        }};

        DateTimeUtils.setCurrentMillisFixed(1000L);

        idToken = new IdToken("sub", "aud", "iss", DateTime.now().toDate(), DateTime.now().toDate(), DateTime.now().toDate(), amr, acr, "azp", "givenname", "familyname", 1123l, "123", new HashSet<>(), new HashMap<>());
    }

    @After
    public void teardown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void testSerializingIdToken() {
        String idTokenJsonString = JsonUtils.writeValueAsString(idToken);
        assertThat(idTokenJsonString, hasJsonPath("acr.values", hasItems("acr1", "acr2")));
        assertThat(idTokenJsonString, hasJsonPath("amr", hasItems("amr1", "amr2")));
        assertThat(idTokenJsonString, hasJsonPath("sub"));
        assertThat(idTokenJsonString, hasJsonPath("given_name"));
        assertThat(idTokenJsonString, hasJsonPath("family_name"));
        assertThat(idTokenJsonString, hasJsonPath("phone_number"));
        assertThat(idTokenJsonString, hasJsonPath("user_attributes"));
        assertThat(idTokenJsonString, hasJsonPath("previous_logon_time", is(1123)));
        assertThat(idTokenJsonString, hasJsonPath("iat", is(1000)));
        assertThat(idTokenJsonString, hasJsonPath("exp", is(1000)));
        assertThat(idTokenJsonString, hasJsonPath("auth_time", is(1000)));
    }

    @Test
    public void testSerializingIdToken_omitNullValues() {
        idToken = new IdToken("sub", "aud", "iss", DateTime.now().toDate(), DateTime.now().toDate(), DateTime.now().toDate(), null, null, "azp", null, null, 1123l, null, new HashSet<>(), new HashMap<>());

        String idTokenJsonString = JsonUtils.writeValueAsString(idToken);

        assertThat(idTokenJsonString, hasNoJsonPath("given_name"));
        assertThat(idTokenJsonString, hasNoJsonPath("family_name"));
        assertThat(idTokenJsonString, hasNoJsonPath("phone_number"));
    }
}