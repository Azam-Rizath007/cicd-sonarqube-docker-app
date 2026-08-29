package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppTest {

    @Test
    void shouldReturnSuccessMessage() {

        String actualMessage = App.getMessage();

        assertNotNull(actualMessage);

        assertEquals(
                "CI/CD Pipeline is working successfully!",
                actualMessage
        );
    }
}