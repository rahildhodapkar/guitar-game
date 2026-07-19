package com.rahildhodapkar.guitar_game.theory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class GuitarFretboardTest {
  private final GuitarFretboard fretboard = new GuitarFretboard(GuitarTuning.standard(), 24);

  @Test
  void standardOpenCShapeProducesPlayedNotesWithCorrectMidiNumbers() {
    List<PlayedNote> notes = fretboard.notesFromFrets(List.of(-1, 3, 2, 0, 1, 0));

    assertEquals(
        List.of(
            new PlayedNote(PitchClass.fromName("C"), "C", 48),
            new PlayedNote(PitchClass.fromName("E"), "E", 52),
            new PlayedNote(PitchClass.fromName("G"), "G", 55),
            new PlayedNote(PitchClass.fromName("C"), "C", 60),
            new PlayedNote(PitchClass.fromName("E"), "E", 64)),
        notes);
  }

  @Test
  void mutedStringsAreNotReturned() {
    List<PlayedNote> notes = fretboard.notesFromFrets(List.of(0, -1, -1, -1, -1, 0));

    assertEquals(
        List.of(
            new PlayedNote(PitchClass.fromName("E"), "E", 40),
            new PlayedNote(PitchClass.fromName("E"), "E", 64)),
        notes);
  }

  @Test
  void twelfthFretRaisesMidiByAnOctaveWhileKeepingPitchClass() {
    List<PlayedNote> notes = fretboard.notesFromFrets(List.of(12, -1, -1, -1, -1, -1));

    assertEquals(List.of(new PlayedNote(PitchClass.fromName("E"), "E", 52)), notes);
  }

  @Test
  void wrongNumberOfStringsIsRejected() {
    assertThrows(
        IllegalArgumentException.class, () -> fretboard.notesFromFrets(List.of(0, 0, 0, 0, 0)));
  }

  @Test
  void fretOutsideConfiguredRangeIsRejected() {
    assertThrows(
        IllegalArgumentException.class, () -> fretboard.notesFromFrets(List.of(25, 0, 0, 0, 0, 0)));
  }
}
