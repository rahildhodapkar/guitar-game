package com.rahildhodapkar.guitar_game.theory;

public record DetectedChord(
        String name,
        PitchClass root,
        ChordQuality quality,
        PitchClass bass,
        double weight) {
}
