package com.rahildhodapkar.guitar_game.theory;

import java.util.HashMap;
import java.util.Map;

public record PitchClass(int value) {
  public static final int PITCH_CLASS_COUNT = 12;

  private static final int MIN = 0;
  private static final int MAX = 11;

  private static final String[] SHARP_NAMES = {
    "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
  };

  private static final String[] FLAT_NAMES = {
    "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"
  };

  private static final Map<String, Integer> NAME_TO_VALUE = buildNameMap();

  public PitchClass {
    if (value < MIN || value > MAX)
      throw new IllegalArgumentException(
          String.format("Value must be between %d and %d", MIN, MAX));
  }

  public static PitchClass fromName(String name) {
    name = sanitizeString(name);

    Integer val = NAME_TO_VALUE.get(name);

    if (val == null) {
      throw new IllegalArgumentException("Unknown note: " + name);
    }

    return new PitchClass(val);
  }

  public String name(boolean preferFlats) {
    String[] names = preferFlats ? FLAT_NAMES : SHARP_NAMES;
    return names[value];
  }

  public int intervalTo(PitchClass other) {
    return Math.floorMod(other.value - this.value, PITCH_CLASS_COUNT);
  }

  public PitchClass transpose(int semitones) {
    return new PitchClass(Math.floorMod(this.value + semitones, PITCH_CLASS_COUNT));
  }

  private static Map<String, Integer> buildNameMap() {
    Map<String, Integer> nameMap = new HashMap<>();

    for (int i = 0; i < SHARP_NAMES.length; i++) {
      nameMap.put(SHARP_NAMES[i], i);
      nameMap.put(FLAT_NAMES[i], i);
    }

    // Rare enharmonics
    nameMap.put("B#", 0);
    nameMap.put("Cb", 11);
    nameMap.put("E#", 5);
    nameMap.put("Fb", 4);

    return nameMap;
  }

  private static String sanitizeString(String str) {
    str = str.trim();
    str = str.substring(0, 1).toUpperCase() + str.substring(1);
    return str;
  }
}
