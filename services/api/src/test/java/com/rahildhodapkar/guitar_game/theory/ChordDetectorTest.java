package com.rahildhodapkar.guitar_game.theory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class ChordDetectorTest {
    private static final ChordDetectOptions NO_ASSUMPTIONS = new ChordDetectOptions(false, false);
    private static final ChordDetectOptions ASSUME_PERFECT_FIFTH = new ChordDetectOptions(true, false);

    private final ChordDetector detector = new ChordDetector();

    private static PlayedNote note(String name, int midiNumber) {
        return new PlayedNote(PitchClass.fromName(name), name, midiNumber);
    }

    @Test
    void nullSourceReturnsNoChords() {
        assertEquals(List.of(), detector.detect(null, NO_ASSUMPTIONS));
    }

    @Test
    void emptySourceReturnsNoChords() {
        assertEquals(List.of(), detector.detect(List.of(), NO_ASSUMPTIONS));
    }

    @Test
    void singleNoteReturnsNoChords() {
        List<PlayedNote> source = List.of(note("C", 60));
        assertEquals(List.of(), detector.detect(source, NO_ASSUMPTIONS));
    }

    @Test
    void rootPositionMajorTriadIsDetected() {
        List<PlayedNote> source = List.of(note("C", 60), note("E", 64), note("G", 67));

        List<DetectedChord> detected = detector.detect(source, NO_ASSUMPTIONS);

        assertEquals(1, detected.size());
        DetectedChord chord = detected.get(0);
        assertEquals("C", chord.name());
        assertEquals(PitchClass.fromName("C"), chord.root());
        assertEquals(PitchClass.fromName("C"), chord.bass());
        assertEquals(ChordQuality.MAJ, chord.quality());
    }

    @Test
    void firstInversionMajorTriadIsDetectedWithSlashName() {
        List<PlayedNote> source = List.of(note("E", 52), note("G", 55), note("C", 60));

        List<DetectedChord> detected = detector.detect(source, NO_ASSUMPTIONS);

        assertEquals(1, detected.size());
        DetectedChord chord = detected.get(0);
        assertEquals("C/E", chord.name());
        assertEquals(PitchClass.fromName("C"), chord.root());
        assertEquals(PitchClass.fromName("E"), chord.bass());
        assertEquals(ChordQuality.MAJ, chord.quality());
    }

    @Test
    void dominantSeventhMissingFifthIsNotDetectedWithoutAssumption() {
        List<PlayedNote> source = List.of(note("C", 60), note("E", 64), note("Bb", 70));

        List<DetectedChord> detected = detector.detect(source, NO_ASSUMPTIONS);

        assertTrue(detected.isEmpty());
    }

    @Test
    void dominantSeventhMissingFifthIsDetectedWhenAssumed() {
        List<PlayedNote> source = List.of(note("C", 60), note("E", 64), note("Bb", 70));

        List<DetectedChord> detected = detector.detect(source, ASSUME_PERFECT_FIFTH);

        assertEquals(1, detected.size());
        DetectedChord chord = detected.get(0);
        assertEquals("C7", chord.name());
        assertEquals(PitchClass.fromName("C"), chord.root());
        assertEquals(PitchClass.fromName("C"), chord.bass());
        assertEquals(ChordQuality.DOM7, chord.quality());
    }

    @Test
    void rootPositionChordHasHigherWeightThanInversion() {
        List<PlayedNote> rootPosition = List.of(note("C", 60), note("E", 64), note("G", 67));
        List<PlayedNote> inversion = List.of(note("E", 52), note("G", 55), note("C", 60));

        double rootWeight = detector.detect(rootPosition, NO_ASSUMPTIONS).get(0).weight();
        double inversionWeight = detector.detect(inversion, NO_ASSUMPTIONS).get(0).weight();

        assertTrue(rootWeight > inversionWeight);
    }

    @Test
    void duplicatePitchClassesAcrossOctavesAreCollapsed() {
        List<PlayedNote> source = List.of(
                note("C", 48),
                note("C", 60),
                note("E", 64),
                note("G", 67));

        List<DetectedChord> detected = detector.detect(source, NO_ASSUMPTIONS);

        assertEquals(1, detected.size());
        assertEquals("C", detected.get(0).name());
    }

    @Test
    void twoNoteInputNeverMatchesAnyQuality() {
        List<PlayedNote> source = List.of(note("C", 60), note("G", 67));

        assertTrue(detector.detect(source, NO_ASSUMPTIONS).isEmpty());
        assertTrue(detector.detect(source, ASSUME_PERFECT_FIFTH).isEmpty());
    }

    @Test
    void denseChromaticClusterProducesNoMatchesWithoutCrashing() {
        List<PlayedNote> source = List.of(
                note("C", 60), note("C#", 61), note("D", 62), note("D#", 63),
                note("E", 64), note("F", 65), note("F#", 66), note("G", 67),
                note("G#", 68), note("A", 69), note("A#", 70), note("B", 71));

        assertTrue(detector.detect(source, NO_ASSUMPTIONS).isEmpty());
    }

    @Test
    void symmetricAugmentedTriadIsDetectedFromEveryNoteAsRoot() {
        List<PlayedNote> source = List.of(note("C", 60), note("E", 64), note("G#", 68));

        List<DetectedChord> detected = detector.detect(source, NO_ASSUMPTIONS);

        assertEquals(3, detected.size());
        Set<String> names = detected.stream().map(DetectedChord::name).collect(Collectors.toSet());
        assertEquals(Set.of("Caug", "Eaug/C", "G#aug/C"), names);
        for (DetectedChord chord : detected) {
            assertEquals(ChordQuality.AUG, chord.quality());
            assertEquals(PitchClass.fromName("C"), chord.bass());
        }
    }

    @Test
    void assumedPerfectFifthNeverCompensatesForAMissingThird() {
        List<PlayedNote> source = List.of(note("C", 60), note("G", 67), note("Bb", 70));

        assertTrue(detector.detect(source, ASSUME_PERFECT_FIFTH).isEmpty());
    }

    @Test
    void assumedPerfectFifthIsSuppressedWhenAnAlteredFifthIsAlreadyPresent() {
        List<PlayedNote> source = List.of(note("C", 60), note("E", 64), note("Gb", 66), note("Bb", 70));

        assertTrue(detector.detect(source, ASSUME_PERFECT_FIFTH).isEmpty());
    }

    @Test
    void doubledRootIncreasesWeightRelativeToUndoubled() {
        List<PlayedNote> undoubled = List.of(note("C", 60), note("E", 64), note("G", 67));
        List<PlayedNote> doubled = List.of(note("C", 48), note("C", 60), note("E", 64), note("G", 67));

        double undoubledWeight = detector.detect(undoubled, NO_ASSUMPTIONS).get(0).weight();
        double doubledWeight = detector.detect(doubled, NO_ASSUMPTIONS).get(0).weight();

        assertTrue(doubledWeight > undoubledWeight);
    }

    @Test
    void enharmonicSpellingsOfSameRootStillCountTowardFrequencyBonus() {
        List<PlayedNote> spelledConsistently = List.of(note("Db", 49), note("F", 53), note("Ab", 56));
        List<PlayedNote> spelledMixed = List.of(
                note("Db", 49), note("C#", 61), note("F", 53), note("Ab", 56));

        List<DetectedChord> consistent = detector.detect(spelledConsistently, NO_ASSUMPTIONS);
        List<DetectedChord> mixed = detector.detect(spelledMixed, NO_ASSUMPTIONS);

        assertEquals(1, consistent.size());
        assertEquals(1, mixed.size());
        assertEquals("Db", mixed.get(0).name());
        assertTrue(mixed.get(0).weight() > consistent.get(0).weight());
    }

    @Test
    void detectionIsDeterministicAcrossRepeatedCalls() {
        List<PlayedNote> source = List.of(note("E", 52), note("G", 55), note("C", 60));

        List<DetectedChord> first = detector.detect(source, NO_ASSUMPTIONS);
        List<DetectedChord> second = detector.detect(source, NO_ASSUMPTIONS);

        assertEquals(first, second);
    }

    @Test
    void addNinthChordIsDetected() {
        List<PlayedNote> source = List.of(
                note("C", 60), note("E", 64), note("G", 67), note("D", 62));

        List<DetectedChord> detected = detector.detect(source, NO_ASSUMPTIONS);

        assertEquals(1, detected.size());
        assertEquals(ChordQuality.ADD9, detected.get(0).quality());
    }

    @Test
    void majorSixthAndRelativeMinorSeventhShareAllFourNotes() {
        List<PlayedNote> source = List.of(note("C", 60), note("E", 64), note("G", 67), note("A", 69));

        List<DetectedChord> detected = detector.detect(source, NO_ASSUMPTIONS);

        assertEquals(2, detected.size());
        Set<String> names = detected.stream().map(DetectedChord::name).collect(Collectors.toSet());
        assertEquals(Set.of("C6", "Am7/C"), names);
    }

    @Test
    void bassNoteBreaksTheMajorSixthMinorSeventhTieInFavorOfTheLowerRoot() {
        List<PlayedNote> bassIsC = List.of(note("C", 60), note("E", 64), note("G", 67), note("A", 69));
        List<PlayedNote> bassIsA = List.of(note("A", 57), note("C", 60), note("E", 64), note("G", 67));

        List<DetectedChord> whenBassIsC = detector.detect(bassIsC, NO_ASSUMPTIONS);
        List<DetectedChord> whenBassIsA = detector.detect(bassIsA, NO_ASSUMPTIONS);

        DetectedChord cSixWhenBassIsC = whenBassIsC.stream()
                .filter(c -> c.quality() == ChordQuality.MAJ6).findFirst().orElseThrow();
        DetectedChord aMinSevenWhenBassIsC = whenBassIsC.stream()
                .filter(c -> c.quality() == ChordQuality.MIN7).findFirst().orElseThrow();
        assertTrue(cSixWhenBassIsC.weight() > aMinSevenWhenBassIsC.weight());

        DetectedChord aMinSevenWhenBassIsA = whenBassIsA.stream()
                .filter(c -> c.quality() == ChordQuality.MIN7).findFirst().orElseThrow();
        DetectedChord cSixWhenBassIsA = whenBassIsA.stream()
                .filter(c -> c.quality() == ChordQuality.MAJ6).findFirst().orElseThrow();
        assertTrue(aMinSevenWhenBassIsA.weight() > cSixWhenBassIsA.weight());

        assertEquals(cSixWhenBassIsC.weight(), aMinSevenWhenBassIsA.weight());
        assertEquals(aMinSevenWhenBassIsC.weight(), cSixWhenBassIsA.weight());
    }

    @Test
    void minorSixthAndRelativeHalfDiminishedSeventhShareAllFourNotes() {
        List<PlayedNote> source = List.of(note("A", 57), note("C", 60), note("E", 64), note("F#", 66));

        List<DetectedChord> detected = detector.detect(source, NO_ASSUMPTIONS);

        assertEquals(2, detected.size());
        Set<String> names = detected.stream().map(DetectedChord::name).collect(Collectors.toSet());
        assertEquals(Set.of("Am6", "F#m7b5/A"), names);

        DetectedChord am6 = detected.stream().filter(c -> c.quality() == ChordQuality.MIN6).findFirst().orElseThrow();
        DetectedChord fSharpHalfDim = detected.stream()
                .filter(c -> c.quality() == ChordQuality.HALF_DIM7).findFirst().orElseThrow();
        assertTrue(am6.weight() > fSharpHalfDim.weight());
    }

    @Test
    void fullyDiminishedSeventhIsAmbiguousFromAllFourOfItsNotes() {
        List<PlayedNote> source = List.of(
                note("C", 60), note("Eb", 63), note("Gb", 66), note("A", 69));

        List<DetectedChord> detected = detector.detect(source, NO_ASSUMPTIONS);

        assertEquals(4, detected.size());
        for (DetectedChord chord : detected) {
            assertEquals(ChordQuality.DIM7, chord.quality());
            assertEquals(PitchClass.fromName("C"), chord.bass());
        }

        DetectedChord rootedAtBass = detected.stream()
                .filter(c -> c.root().equals(PitchClass.fromName("C"))).findFirst().orElseThrow();
        List<DetectedChord> theOtherThree = detected.stream()
                .filter(c -> !c.root().equals(PitchClass.fromName("C"))).toList();

        assertEquals(3, theOtherThree.size());
        for (DetectedChord chord : theOtherThree) {
            assertTrue(rootedAtBass.weight() > chord.weight());
        }
        assertEquals(theOtherThree.get(0).weight(), theOtherThree.get(1).weight());
        assertEquals(theOtherThree.get(1).weight(), theOtherThree.get(2).weight());
    }

    @Test
    void onlyTheCandidateRootsOwnFrequencyAffectsItsWeight() {
        List<PlayedNote> baseline = List.of(note("C", 60), note("E", 64), note("G", 67), note("A", 69));
        List<PlayedNote> rootDoubled = List.of(
                note("C", 48), note("C", 60), note("E", 64), note("G", 67), note("A", 69));
        List<PlayedNote> nonRootDoubled = List.of(
                note("C", 60), note("E", 64), note("G", 67), note("G", 79), note("A", 69));

        double baselineCSixWeight = weightOf(detector.detect(baseline, NO_ASSUMPTIONS), ChordQuality.MAJ6);
        double baselineAmSevenWeight = weightOf(detector.detect(baseline, NO_ASSUMPTIONS), ChordQuality.MIN7);

        double rootDoubledCSixWeight = weightOf(detector.detect(rootDoubled, NO_ASSUMPTIONS), ChordQuality.MAJ6);
        double rootDoubledAmSevenWeight = weightOf(detector.detect(rootDoubled, NO_ASSUMPTIONS), ChordQuality.MIN7);

        assertTrue(rootDoubledCSixWeight > baselineCSixWeight);
        assertEquals(baselineAmSevenWeight, rootDoubledAmSevenWeight);

        double nonRootDoubledCSixWeight = weightOf(detector.detect(nonRootDoubled, NO_ASSUMPTIONS),
                ChordQuality.MAJ6);
        assertEquals(baselineCSixWeight, nonRootDoubledCSixWeight);
    }

    @Test
    void extendedChordWithNoThirdIsStillDetectedAndWeightedSensibly() {
        List<PlayedNote> source = List.of(
                note("C", 60), note("G", 67), note("Bb", 70), note("D", 74), note("F", 77));

        List<DetectedChord> detected = detector.detect(source, NO_ASSUMPTIONS);

        assertEquals(1, detected.size());
        assertEquals(ChordQuality.DOM11, detected.get(0).quality());
        assertEquals("C11", detected.get(0).name());
    }

    private static double weightOf(List<DetectedChord> detected, ChordQuality quality) {
        return detected.stream()
                .filter(c -> c.quality() == quality)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected a detection for " + quality))
                .weight();
    }
}
