package com.rahildhodapkar.guitar_game.theory;

import java.util.ArrayList;
import java.util.List;

public final class GuitarFretboard {
  private static final int MUTED_STRING = -1;
  private static final int OPEN_STRING = 0;

  private final GuitarTuning tuning;
  private final int maxFrets;

  public GuitarFretboard(GuitarTuning tuning, int maxFrets) {
    this.tuning = tuning;
    this.maxFrets = maxFrets;
  }

  public List<PlayedNote> notesFromFrets(List<Integer> frets) {
    if (tuning.numberOfStrings() != frets.size()) {
      throw new IllegalArgumentException(
          "Input frets size does not match the number of guitar strings");
    }

    for (int fret : frets) {
      if (fret < MUTED_STRING || fret > maxFrets) {
        throw new IllegalArgumentException(
            "Input fret value of " + String.valueOf(fret) + " is invalid");
      }
    }

    List<PlayedNote> openStrings = tuning.openStrings();
    List<PlayedNote> output = new ArrayList<>();

    for (int i = 0; i < tuning.numberOfStrings(); i++) {
      PlayedNote openString = openStrings.get(i);
      int fretPlayed = frets.get(i);
      PlayedNote note = getActualNotePlayed(openString, fretPlayed);
      if (note != null) {
        output.add(note);
      }
    }

    return output;
  }

  private PlayedNote getActualNotePlayed(PlayedNote openString, int fretPlayed) {
    if (fretPlayed < 0) {
      return null;
    }

    if (fretPlayed == OPEN_STRING) {
      return openString.copy();
    }

    int frettedMidiNumber = openString.midiNumber() + fretPlayed;

    PitchClass transposedPitchClass = openString.pitchClass().transpose(fretPlayed);
    return new PlayedNote(
        transposedPitchClass, transposedPitchClass.name(false), frettedMidiNumber);
  }
}
