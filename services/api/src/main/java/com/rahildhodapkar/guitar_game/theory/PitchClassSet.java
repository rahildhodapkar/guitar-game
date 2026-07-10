package com.rahildhodapkar.guitar_game.theory;

import java.util.Collection;
import java.util.List;

final class PitchClassSet {
  static int maskFromIntervals(List<Integer> intervals) {
    int mask = 0;
    for (int interval : intervals) {
      int normalizedInterval = Math.floorMod(interval, PitchClass.PITCH_CLASS_COUNT);
      mask |= (1 << normalizedInterval);
    }
    return mask;
  }

  static int maskFromPitchClasses(Collection<PitchClass> notes, PitchClass root) {
    int mask = 0;
    for (PitchClass note : notes) {
      int interval = Math.floorMod(note.value() - root.value(), PitchClass.PITCH_CLASS_COUNT);
      mask |= (1 << interval);
    }
    return mask;
  }

  static boolean containsInterval(int mask, int interval) {
    return (mask & (1 << interval)) != 0;
  }
}
