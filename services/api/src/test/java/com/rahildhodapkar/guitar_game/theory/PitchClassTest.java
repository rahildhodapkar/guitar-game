package com.rahildhodapkar.guitar_game.theory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PitchClassTest {
    private static final String FLAT_SYMBOL = "b";

    @ParameterizedTest(name = "A PitchClass instance made from {0} should have a value of {1}")
    @CsvSource({
            "C, 0",
            "C#, 1",
            "Db, 1",
            "D, 2",
            "D#, 3",
            "Eb, 3",
            "E, 4",
            "F, 5",
            "F#, 6",
            "Gb, 6",
            "G, 7",
            "G#, 8",
            "Ab, 8",
            "A, 9",
            "A#, 10",
            "Bb, 10",
            "B, 11"
    })
    void testFromName(String name, int expected) {
        PitchClass pc = PitchClass.fromName(name);
        assertEquals(expected, pc.value());
    }

    @ParameterizedTest(name = "A PitchClass instance instantiated with value {0} should have name {1}")
    @CsvSource({
            "0, C",
            "1, C#",
            "1, Db",
            "2, D",
            "3, D#",
            "3, Eb",
            "4, E",
            "5, F",
            "6, F#",
            "6, Gb",
            "7, G",
            "8, G#",
            "8, Ab",
            "9, A",
            "10, A#",
            "10, Bb",
            "11, B"
    })
    void testName(int value, String expected) {
        boolean preferFlats = expected.endsWith(FLAT_SYMBOL);
        PitchClass pc = new PitchClass(value);
        assertEquals(expected, pc.name(preferFlats));
    }

    @ParameterizedTest(name = "A PitchClass instance instantianted with value {0} should throw an IllegalArgumentException")
    @ValueSource(ints = { -1, 12 })
    void invalidValueThrows(int value) {
        assertThrows(IllegalArgumentException.class, () -> new PitchClass(value));
    }

    @ParameterizedTest(name = "PitchClass's fromName should throw an IllegalArgumentException if invalid name {0} is passed in")
    @ValueSource(strings = { "Z", "H" })
    void invalidNameThrows(String name) {
        assertThrows(IllegalArgumentException.class, () -> PitchClass.fromName(name));
    }
}