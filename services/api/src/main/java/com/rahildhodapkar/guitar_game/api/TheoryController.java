package com.rahildhodapkar.guitar_game.api;

import com.rahildhodapkar.guitar_game.theory.ChordDetector;
import com.rahildhodapkar.guitar_game.theory.PitchClass;
import com.rahildhodapkar.guitar_game.theory.PlayedNote;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TheoryController {
  private final ChordDetector detector;

  public TheoryController() {
    this.detector = new ChordDetector();
  }

  @GetMapping("/identify")
  public String getChordIdentity(@RequestParam List<String> notes) {
    List<PlayedNote> playedNotes =
        notes.stream()
            .map(
                note -> {
                  PitchClass pc = PitchClass.fromName(note);
                  return new PlayedNote(pc, pc.name(false), pc.value());
                })
            .toList();
    return detector.detect(playedNotes, null).toString();
  }
}
