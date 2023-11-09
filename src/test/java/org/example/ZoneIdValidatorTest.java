package org.example;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.example.ZoneIdValidator.validate;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZoneIdValidatorTest {

    @ParameterizedTest
    @MethodSource("validTimeZones")
    void testValidZoneIds(String timezone) {
        assertTrue(validate(timezone));
    }

    static Stream<String> validTimeZones() {
        return Stream.of("UTC", "UTC+2", "UTC-4", "UTC+02:00", "UTC-02:00", "UTC+10:00", "UTC 2",  "UTC 4");
    }

    @ParameterizedTest
    @MethodSource("notValidTimeZones")
    void testNotValidZoneIds(String timezone) {
        assertFalse(validate(timezone));
    }

    static Stream<String> notValidTimeZones() {
        return Stream.of("", " ", "UTC+19", "UTC-19", "+4", "-10");
    }
}