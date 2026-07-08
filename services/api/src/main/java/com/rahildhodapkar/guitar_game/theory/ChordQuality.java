package com.rahildhodapkar.guitar_game.theory;

import java.util.List;

public enum ChordQuality {
    // Major
    MAJ("", List.of(0, 4, 7), 1),
    MAJ6("6", List.of(0, 4, 7, 9), 2),
    MAJ7("maj7", List.of(0, 4, 7, 11), 2),
    MAJ9("maj9", List.of(0, 4, 7, 11, 14), 3),
    MAJ13("maj13", List.of(0, 4, 7, 11, 14, 21), 4),

    // Minor
    MIN("m", List.of(0, 3, 7), 1),
    MIN6("m6", List.of(0, 3, 7, 9), 2),
    MIN7("m7", List.of(0, 3, 7, 10), 2),
    MIN9("m9", List.of(0, 3, 7, 10, 14), 3),
    MIN11("m11", List.of(0, 3, 7, 10, 14, 17), 4),
    MIN13("m13", List.of(0, 3, 7, 10, 14, 21), 4),

    // Diminished
    DIM("dim", List.of(0, 3, 6), 2),
    DIM7("dim7", List.of(0, 3, 6, 9), 3),
    HALF_DIM7("m7b5", List.of(0, 3, 6, 10), 3),

    // Augmented
    AUG("aug", List.of(0, 4, 8), 2),
    AUG7("aug7", List.of(0, 4, 8, 10), 3),

    // Dominant
    DOM7("7", List.of(0, 4, 7, 10), 2),
    DOM9("9", List.of(0, 4, 7, 10, 14), 3),
    DOM11("11", List.of(0, 7, 10, 14, 17), 4),

    // Suspended
    SUS2("sus2", List.of(0, 2, 7), 1),
    SUS4("sus4", List.of(0, 5, 7), 1),

    // Add
    ADD9("add9", List.of(0, 4, 7, 14), 2);

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
