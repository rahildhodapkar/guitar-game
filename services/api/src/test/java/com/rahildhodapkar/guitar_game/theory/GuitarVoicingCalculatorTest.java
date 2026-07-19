package com.rahildhodapkar.guitar_game.theory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class GuitarVoicingCalculatorTest {

  @Test
  void standardOpenCShapeProducesExpectedPitchClasses() {
    int[] cMajorFrets = {-1, 3, 2, 0, 1, 0};

    List<PitchClass> pitchClasses =
        GuitarVoicingCalculator.fretsToPitchClasses(GuitarTuning.standard(), cMajorFrets);

    assertEquals(
        List.of(
            PitchClass.fromName("C"),
            PitchClass.fromName("E"),
            PitchClass.fromName("G"),
            PitchClass.fromName("C"),
            PitchClass.fromName("E")),
        pitchClasses);
  }

  @Test
  void mutedStringsAreExcluded() {
    int[] onlyLowEAndHighE = {0, -1, -1, -1, -1, 0};

    List<PitchClass> pitchClasses =
        GuitarVoicingCalculator.fretsToPitchClasses(GuitarTuning.standard(), onlyLowEAndHighE);

    assertEquals(List.of(PitchClass.fromName("E"), PitchClass.fromName("E")), pitchClasses);
  }

  @Test
  void fretsTransposePitchClassesAndWrapAtTheOctave() {
    int[] allTwelfthFret = {12, 12, 12, 12, 12, 12};

    List<PitchClass> pitchClasses =
        GuitarVoicingCalculator.fretsToPitchClasses(GuitarTuning.standard(), allTwelfthFret);

    assertEquals(
        List.of(
            PitchClass.fromName("E"),
            PitchClass.fromName("A"),
            PitchClass.fromName("D"),
            PitchClass.fromName("G"),
            PitchClass.fromName("B"),
            PitchClass.fromName("E")),
        pitchClasses);
  }

  @Test
  void wrongNumberOfFretsIsRejected() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            GuitarVoicingCalculator.fretsToPitchClasses(
                GuitarTuning.standard(), new int[] {0, 0, 0, 0, 0}));
  }

  @Test
  void fretLessThanMutedIsRejected() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            GuitarVoicingCalculator.fretsToPitchClasses(
                GuitarTuning.standard(), new int[] {-2, 0, 0, 0, 0, 0}));
  }
}
