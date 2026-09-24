package com.aydindemir;

import org.junit.jupiter.api.Test;

class AuthServiceApplicationTests {

    @Test
    void applicationClassExists() {
        // Day 1 smoke test intentionally avoids requiring a running PostgreSQL instance.
        new AuthServiceApplication();
    }
}
