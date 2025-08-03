package org.acme.reservation;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(RunWithStaging.class)
public class ExampleStagingTest {

    @Test
    public void test() {
        // If the 'quarkus.test.profile.tags' property is set,
        // it has to contain the tag, 'staging', to enable this test.
        // Otherwise, this will be skipped.
    }
}
