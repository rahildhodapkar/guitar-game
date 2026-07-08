package com.rahildhodapkar.guitar_game.theory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ChordBuilderTest {
    private static final HashMap<String, ChordQuality> chordQualityMap = createChordQualityMap();

    @ParameterizedTest(name = "{0}{1} should have notes {2}")
    @CsvSource({
            "C, MAJ, C-E-G",
            "C, MIN, C-D#-G",
            "C, DIM, C-D#-F#",
            "C, AUG, C-E-G#",
            "C, MAJ7, C-E-G-B",
            "C, MIN7, C-D#-G-A#",
            "C, DOM7, C-E-G-A#",
            "C, SUS2, C-D-G",
            "C, SUS4, C-F-G",
            "A, MAJ, A-C#-E",
            "A, MIN, A-C-E",
            "D, MAJ, D-F#-A",
            "D, MIN, D-F-A",
            "E, MAJ, E-G#-B",
            "E, MIN, E-G-B",
            "F, MAJ, F-A-C",
            "F, MIN, F-G#-C",
            "G, MAJ, G-B-D",
            "G, MIN, G-A#-D",
            "B, MAJ, B-D#-F#",
            "B, MIN, B-D-F#",
            "B, DIM, B-D-F",
            "F#, MAJ, F#-A#-C#",
            "F#, MIN, F#-A-C#",
            "A, AUG, A-C#-F",
            "D, DOM7, D-F#-A-C",
            "G, DOM7, G-B-D-F",
            "A, MAJ7, A-C#-E-G#",
            "E, MIN7, E-G-B-D",
            "D, SUS2, D-E-A",
            "G, SUS4, G-C-D"
    })
    void testChordNotes(String rootNote, String chordQuality, String expected) {
        PitchClass pc = PitchClass.fromName(rootNote);
        ChordQuality cq = chordQualityMap.get(chordQuality);
        String chordNotes = String.join("-", ChordBuilder.chordNotes(pc, cq).stream().map(v -> v.name(false)).toList());
        assertEquals(expected, chordNotes);
    }

    private static HashMap<String, ChordQuality> createChordQualityMap() {
        HashMap<String, ChordQuality> m = new HashMap<>();
        m.put("MAJ", ChordQuality.MAJ);
        m.put("MAJ7", ChordQuality.MAJ7);
        m.put("MAJ9", ChordQuality.MAJ9);
        m.put("MAJ13", ChordQuality.MAJ13);
        m.put("MIN", ChordQuality.MIN);
        m.put("MIN7", ChordQuality.MIN7);
        m.put("MIN9", ChordQuality.MIN9);
        m.put("MIN13", ChordQuality.MIN13);
        m.put("DIM", ChordQuality.DIM);
        m.put("AUG", ChordQuality.AUG);
        m.put("DOM7", ChordQuality.DOM7);
        m.put("DOM9", ChordQuality.DOM9);
        m.put("DOM11", ChordQuality.DOM11);
        m.put("SUS2", ChordQuality.SUS2);
        m.put("SUS4", ChordQuality.SUS4);
        m.put("ADD9", ChordQuality.ADD9);
        return m;
    }
}
