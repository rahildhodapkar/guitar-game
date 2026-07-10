package com.rahildhodapkar.guitar_game.theory;

import java.util.ArrayList;
import java.util.List;

public final class GuitarVoicingCalculator {
  private GuitarVoicingCalculator() {}

  public static List<PitchClass> fretsToPitchClasses(GuitarTuning tuning, int[] frets) {
    if (frets.length != tuning.numberOfStrings()) {
      throw new IllegalArgumentException(
          "Number of frets does not match number of strings in tuning");
    }

    List<PitchClass> pitchClasses = new ArrayList<>();

    for (int string = 0; string < frets.length; string++) {
      int fret = frets[string];

      // -1 represents muted string
      if (fret == -1) {
        continue;
      }

      if (fret < -1) {
        throw new IllegalArgumentException("Fret cannot be less than -1");
      }

      int midiNumber = tuning.openStrings().get(string).midiNumber();

      PitchClass pc =
          tuning
              .openStrings()
              .get(string)
              .pitchClass()
              .transpose((midiNumber + fret) % PitchClass.PITCH_CLASS_COUNT);

      pitchClasses.add(pc);
    }

    return pitchClasses;
  }
}
