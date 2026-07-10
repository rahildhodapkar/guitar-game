package com.rahildhodapkar.guitar_game.theory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public class GuitarJsonLoader {
  /**
   * Maps chords-db JSON {@code suffix} values onto {@link ChordQuality#getSymbol()}. Slash-chord
   * suffixes (e.g. "/E", "m9/Bb") and "alt" (an unspecified, discretionary set of alterations - no
   * single fixed interval formula) are intentionally omitted.
   */
  private static final Map<String, String> JSON_SUFFIX_TO_QUALITY_SYMBOL =
      Map.ofEntries(
          Map.entry("major", ChordQuality.MAJ.getSymbol()),
          Map.entry("minor", ChordQuality.MIN.getSymbol()),
          Map.entry("dim", ChordQuality.DIM.getSymbol()),
          Map.entry("dim7", ChordQuality.DIM7.getSymbol()),
          Map.entry("sus", ChordQuality.SUS4.getSymbol()),
          Map.entry("sus2", ChordQuality.SUS2.getSymbol()),
          Map.entry("sus4", ChordQuality.SUS4.getSymbol()),
          Map.entry("sus2sus4", ChordQuality.SUS2SUS4.getSymbol()),
          Map.entry("7sus4", ChordQuality.DOM7SUS4.getSymbol()),
          Map.entry("aug", ChordQuality.AUG.getSymbol()),
          Map.entry("5", ChordQuality.POWER5.getSymbol()),
          Map.entry("6", ChordQuality.MAJ6.getSymbol()),
          Map.entry("69", ChordQuality.MAJ6ADD9.getSymbol()),
          Map.entry("7", ChordQuality.DOM7.getSymbol()),
          Map.entry("7b5", ChordQuality.DOM7_FLAT5.getSymbol()),
          Map.entry("aug7", ChordQuality.AUG7.getSymbol()),
          Map.entry("9", ChordQuality.DOM9.getSymbol()),
          Map.entry("9b5", ChordQuality.DOM9_FLAT5.getSymbol()),
          Map.entry("aug9", ChordQuality.AUG9.getSymbol()),
          Map.entry("7b9", ChordQuality.DOM7_FLAT9.getSymbol()),
          Map.entry("7#9", ChordQuality.DOM7_SHARP9.getSymbol()),
          Map.entry("11", ChordQuality.DOM11.getSymbol()),
          Map.entry("9#11", ChordQuality.DOM9_SHARP11.getSymbol()),
          Map.entry("13", ChordQuality.DOM13.getSymbol()),
          Map.entry("maj7", ChordQuality.MAJ7.getSymbol()),
          Map.entry("maj7b5", ChordQuality.MAJ7_FLAT5.getSymbol()),
          Map.entry("maj7#5", ChordQuality.MAJ7_SHARP5.getSymbol()),
          Map.entry("maj7sus2", ChordQuality.MAJ7SUS2.getSymbol()),
          Map.entry("maj9", ChordQuality.MAJ9.getSymbol()),
          Map.entry("maj11", ChordQuality.MAJ11.getSymbol()),
          Map.entry("maj13", ChordQuality.MAJ13.getSymbol()),
          Map.entry("m6", ChordQuality.MIN6.getSymbol()),
          Map.entry("m69", ChordQuality.MIN6ADD9.getSymbol()),
          Map.entry("m7", ChordQuality.MIN7.getSymbol()),
          Map.entry("m7b5", ChordQuality.HALF_DIM7.getSymbol()),
          Map.entry("m9", ChordQuality.MIN9.getSymbol()),
          Map.entry("m11", ChordQuality.MIN11.getSymbol()),
          Map.entry("mmaj7", ChordQuality.MIN_MAJ7.getSymbol()),
          Map.entry("mmaj7b5", ChordQuality.MIN_MAJ7_FLAT5.getSymbol()),
          Map.entry("mmaj9", ChordQuality.MIN_MAJ9.getSymbol()),
          Map.entry("mmaj11", ChordQuality.MIN_MAJ11.getSymbol()),
          Map.entry("add9", ChordQuality.ADD9.getSymbol()),
          Map.entry("madd9", ChordQuality.MIN_ADD9.getSymbol()),
          Map.entry("add11", ChordQuality.ADD11.getSymbol()));

  private final ObjectMapper mapper;

  public GuitarJsonLoader() {
    this.mapper = JsonMapper.builder().build();
  }

  public List<Voicing> loadVoicings() throws IOException {
    try (InputStream inputStream = getClass().getResourceAsStream("/guitar.json"); ) {
      if (inputStream == null) {
        throw new IllegalStateException("guitar.json not found");
      }

      JsonNode root = mapper.readTree(inputStream);

      String[] keys = mapper.treeToValue(root.get("keys"), String[].class);

      JsonNode allChords = root.get("chords");

      List<Voicing> voicings = new ArrayList<>();

      for (String key : keys) {
        key = key.replace("#", "sharp");
        JsonNode chord = allChords.get(key);
        for (JsonNode chordVariant : chord) {
          String chordKey = chordVariant.get("key").asString();
          String suffix =
              JSON_SUFFIX_TO_QUALITY_SYMBOL.getOrDefault(chordVariant.get("suffix").asString(), "");
          JsonNode positions = chordVariant.get("positions");
          for (JsonNode position : positions) {
            int[] frets = mapper.treeToValue(position.get("frets"), int[].class);
            int[] fingers = mapper.treeToValue(position.get("fingers"), int[].class);
            int baseFret = position.get("baseFret").asInt();

            voicings.add(new Voicing(chordKey + suffix, frets, fingers, baseFret));
          }
        }
      }

      return voicings;
    }
  }

  public static void main(String args[]) {
    GuitarJsonLoader loader = new GuitarJsonLoader();
    try {
      List<Voicing> voicings = loader.loadVoicings();
      for (Voicing voicing : voicings) {
        System.out.println(voicing.toString());
      }
    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
}
