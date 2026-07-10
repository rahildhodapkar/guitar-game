package com.rahildhodapkar.guitar_game.theory;

import java.util.List;

public enum ChordQuality {
  // Major
  MAJ("", List.of(0, 4, 7), 1),
  MAJ6("6", List.of(0, 4, 7, 9), 2),
  MAJ7("maj7", List.of(0, 4, 7, 11), 2),
  MAJ9("maj9", List.of(0, 4, 7, 11, 14), 3),
  MAJ11("maj11", List.of(0, 7, 11, 14, 17), 4), // 3rd omitted: clashes a semitone below the natural 11th
  MAJ13("maj13", List.of(0, 4, 7, 11, 14, 21), 4),
  MAJ6ADD9("69", List.of(0, 4, 7, 9, 14), 3),
  MAJ7_FLAT5("maj7b5", List.of(0, 4, 6, 11), 3),
  MAJ7_SHARP5("maj7#5", List.of(0, 4, 8, 11), 3),
  MAJ7SUS2("maj7sus2", List.of(0, 2, 7, 11), 3),
  ADD9("add9", List.of(0, 4, 7, 14), 2),
  ADD11("add11", List.of(0, 4, 7, 17), 2),

  // Minor
  MIN("m", List.of(0, 3, 7), 1),
  MIN6("m6", List.of(0, 3, 7, 9), 2),
  MIN7("m7", List.of(0, 3, 7, 10), 2),
  MIN9("m9", List.of(0, 3, 7, 10, 14), 3),
  MIN11("m11", List.of(0, 3, 7, 10, 14, 17), 4),
  MIN13("m13", List.of(0, 3, 7, 10, 14, 21), 4),
  MIN6ADD9("m69", List.of(0, 3, 7, 9, 14), 3),
  MIN_ADD9("madd9", List.of(0, 3, 7, 14), 2),
  MIN_MAJ7("mmaj7", List.of(0, 3, 7, 11), 2),
  MIN_MAJ7_FLAT5("mmaj7b5", List.of(0, 3, 6, 11), 3),
  MIN_MAJ9("mmaj9", List.of(0, 3, 7, 11, 14), 3),
  MIN_MAJ11("mmaj11", List.of(0, 3, 7, 11, 14, 17), 4),

  // Diminished
  DIM("dim", List.of(0, 3, 6), 2),
  DIM7("dim7", List.of(0, 3, 6, 9), 3),
  HALF_DIM7("m7b5", List.of(0, 3, 6, 10), 3),

  // Augmented
  AUG("aug", List.of(0, 4, 8), 2),
  AUG7("aug7", List.of(0, 4, 8, 10), 3),
  AUG9("aug9", List.of(0, 4, 8, 10, 14), 4),

  // Dominant
  DOM7("7", List.of(0, 4, 7, 10), 2),
  DOM9("9", List.of(0, 4, 7, 10, 14), 3),
  DOM11("11", List.of(0, 7, 10, 14, 17), 4), // 3rd omitted: clashes a semitone below the natural 11th
  DOM13("13", List.of(0, 4, 7, 10, 21), 4),
  DOM7_FLAT5("7b5", List.of(0, 4, 6, 10), 3),
  DOM9_FLAT5("9b5", List.of(0, 4, 6, 10, 14), 4),
  DOM7_FLAT9("7b9", List.of(0, 4, 7, 10, 13), 4),
  DOM7_SHARP9("7#9", List.of(0, 4, 7, 10, 15), 4),
  DOM9_SHARP11("9#11", List.of(0, 4, 7, 10, 14, 18), 4),
  DOM7SUS4("7sus4", List.of(0, 5, 7, 10), 2),

  // Suspended
  SUS2("sus2", List.of(0, 2, 7), 1),
  SUS4("sus4", List.of(0, 5, 7), 1),
  SUS2SUS4("sus2sus4", List.of(0, 2, 5, 7), 2),

  // Power chord (root + fifth only, no third)
  POWER5("5", List.of(0, 7), 1);

  private final String symbol;
  // Intervals comprising a chord (semitones between notes of a chord)
  private final List<Integer> intervals;
  private final int complexity;

  private ChordQuality(String symbol, List<Integer> intervals, int complexity) {
    this.symbol = symbol;
    this.intervals = intervals;
    this.complexity = complexity;
  }

  public List<Integer> getIntervals() {
    return this.intervals;
  }

  public String getSymbol() {
    return this.symbol;
  }

  public int getComplexity() {
    return this.complexity;
  }
}
