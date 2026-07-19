package com.rahildhodapkar.guitar_game.theory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class GuitarJsonLoaderTest {
  private static final GuitarTuning STANDARD_TUNING = GuitarTuning.standard();

  private final GuitarJsonLoader loader = new GuitarJsonLoader();

  @Test
  void loadsAUsableVoicingCatalog() throws IOException {
    Map<String, List<Voicing>> voicings = loader.loadVoicings();

    assertFalse(voicings.isEmpty());
    assertFalse(voicings.get("C").isEmpty());
    assertFalse(voicings.get("Cm").isEmpty());
    assertFalse(voicings.get("C7").isEmpty());
  }

  @Test
  void eachLoadedVoicingHasOneFretAndFingerValuePerGuitarString() throws IOException {
    Map<String, List<Voicing>> voicings = loader.loadVoicings();

    for (List<Voicing> chordVoicings : voicings.values()) {
      for (Voicing voicing : chordVoicings) {
        assertEquals(
            STANDARD_TUNING.numberOfStrings(),
            voicing.frets().length,
            () -> voicing.chord() + " must specify every string's fret");
        assertEquals(
            STANDARD_TUNING.numberOfStrings(),
            voicing.fingers().length,
            () -> voicing.chord() + " must specify every string's finger");
      }
    }
  }

  @Test
  void openPositionVoicingsMatchThePitchClassesOfTheirChords() throws IOException {
    Map<String, List<Voicing>> voicings = loader.loadVoicings();

    assertVoicingMatchesChord(firstOpenVoicing(voicings, "C"), "C", ChordQuality.MAJ);
    assertVoicingMatchesChord(firstOpenVoicing(voicings, "Cm"), "C", ChordQuality.MIN);
    assertVoicingMatchesChord(firstOpenVoicing(voicings, "C7"), "C", ChordQuality.DOM7);
    assertVoicingMatchesChord(firstOpenVoicing(voicings, "G"), "G", ChordQuality.MAJ);
    assertVoicingMatchesChord(firstOpenVoicing(voicings, "Am"), "A", ChordQuality.MIN);
    assertVoicingMatchesChord(firstOpenVoicing(voicings, "Em"), "E", ChordQuality.MIN);
    assertVoicingMatchesChord(firstOpenVoicing(voicings, "D"), "D", ChordQuality.MAJ);
  }

  private static Voicing firstOpenVoicing(Map<String, List<Voicing>> voicings, String chord) {
    return voicings.get(chord).stream()
        .filter(voicing -> voicing.baseFret() == 1)
        .findFirst()
        .orElseThrow(() -> new AssertionError("No open-position voicing for " + chord));
  }

  private static void assertVoicingMatchesChord(Voicing voicing, String root, ChordQuality quality) {
    List<PitchClass> expectedNotes = normalized(ChordBuilder.chordNotes(PitchClass.fromName(root), quality));
    List<PitchClass> actualNotes =
        normalized(
            GuitarVoicingCalculator.fretsToPitchClasses(STANDARD_TUNING, absoluteFrets(voicing)));

    assertFalse(actualNotes.isEmpty(), () -> voicing.chord() + " must sound at least one note");
    assertTrue(
        expectedNotes.containsAll(actualNotes),
        () ->
            voicing.chord()
                + " with frets "
                + Arrays.toString(voicing.frets())
                + " at base fret "
                + voicing.baseFret()
                + " sounded "
                + actualNotes
                + " which is not a subset of "
                + expectedNotes);
  }

  private static int[] absoluteFrets(Voicing voicing) {
    return Arrays.stream(voicing.frets())
        // -1 muted, 0 open string; positive frets are relative to baseFret (1 == baseFret)
        .map(fret -> fret <= 0 ? fret : fret + voicing.baseFret() - 1)
        .toArray();
  }

  private static List<PitchClass> normalized(List<PitchClass> pitchClasses) {
    return pitchClasses.stream()
        .distinct()
        .sorted(Comparator.comparing(PitchClass::value))
        .toList();
  }
}
