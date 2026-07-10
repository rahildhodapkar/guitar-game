package com.rahildhodapkar.guitar_game.theory;

import java.util.ArrayList;
import java.util.List;

public record GuitarTuning(List<PlayedNote> openStrings) {
  private static final int[] STANDARD_TUNING_MIDI_VALUES = {40, 45, 50, 55, 59, 64};

  public GuitarTuning {
    if (openStrings == null || openStrings.isEmpty()) {
      throw new IllegalArgumentException("Tuning must have at least one string");
    }
    // Make immutable
    openStrings = List.copyOf(openStrings);
  }

  // E2 A2 D3 G3 B3 E4
  public static GuitarTuning standard() {
    List<PlayedNote> standardTuning = new ArrayList<>();
    for (int midiNumber : STANDARD_TUNING_MIDI_VALUES) {
      PitchClass pc = new PitchClass(midiNumber % PitchClass.PITCH_CLASS_COUNT);
      PlayedNote pn = new PlayedNote(pc, pc.name(false), midiNumber);
      standardTuning.add(pn);
    }
    return new GuitarTuning(standardTuning);
  }

  public int numberOfStrings() {
    return openStrings.size();
  }
}
