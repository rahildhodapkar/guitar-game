package com.rahildhodapkar.guitar_game.theory;

public record Voicing(String chord, int[] frets, int[] fingers, int baseFret) {
  @Override
  public String toString() {
    return "Voicing:\n"
        + "\tChord name: "
        + chord
        + "\n"
        + createStringFromIntArray("\tFrets", frets)
        + createStringFromIntArray("\tFingers", fingers)
        + "\tBase fret: "
        + String.valueOf(baseFret)
        + "\n";
  }

  private String createStringFromIntArray(String label, int[] arr) {
    StringBuilder sb = new StringBuilder(label + ": [");
    for (int i = 0; i < arr.length; i++) {
      sb.append(arr[i]);
      sb.append(i < arr.length - 1 ? ", " : "");
    }
    sb.append("]\n");
    return sb.toString();
  }
}
