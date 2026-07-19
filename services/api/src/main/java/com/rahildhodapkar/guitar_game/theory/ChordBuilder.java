package com.rahildhodapkar.guitar_game.theory;

import java.util.List;

public class ChordBuilder {
  public static List<PitchClass> chordNotes(PitchClass root, ChordQuality cq) {
    return cq.getIntervals().stream().map(root::transpose).toList();
  }
}
