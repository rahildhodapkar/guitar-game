package com.rahildhodapkar.guitar_game.theory;

import java.util.List;

public record GuitarTuning(List<PlayedNote> openStrings) {
    public GuitarTuning {
        if (openStrings == null || openStrings.isEmpty()) {
            throw new IllegalArgumentException("Tuning must have at least one string");
        }
        // Make immutable
        openStrings = List.copyOf(openStrings);
    }

    public int numberOfStrings() {
        return openStrings.size();
    }
}
