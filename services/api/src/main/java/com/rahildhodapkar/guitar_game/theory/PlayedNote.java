package com.rahildhodapkar.guitar_game.theory;

public record PlayedNote(PitchClass pitchClass, String displayName, int midiNumber) {
  final PlayedNote copy() {
    return new PlayedNote(pitchClass, displayName, midiNumber);
  }
}
