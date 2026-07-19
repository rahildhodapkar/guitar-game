package com.rahildhodapkar.guitar_game.api;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

// TODO: Expand testing
@WebMvcTest(TheoryController.class)
class TheoryControllerTest {
  @Autowired private MockMvc mockMvc;

  @Test
  void identifyReturnsCMajorForC_E_G() throws Exception {
    mockMvc
        .perform(get("/api/identify").param("notes", "C", "E", "G"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("name=C,")));
  }

  @Test
  void identifyReturnsCMinorForC_Eb_G() throws Exception {
    mockMvc
        .perform(get("/api/identify").param("notes", "C", "Eb", "G"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("name=Cm,")));
  }

  @Test
  void identifyReturnsDominantSeventhForC_E_G_Bb() throws Exception {
    mockMvc
        .perform(get("/api/identify").param("notes", "C", "E", "G", "Bb"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("name=C7,")));
  }

  @Test
  void identifyIsCaseInsensitiveToNoteNames() throws Exception {
    mockMvc
        .perform(get("/api/identify").param("notes", "c", "e", "g"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("name=C,")));
  }

  @Test
  void identifyWithSingleNoteReturnsNoChords() throws Exception {
    mockMvc
        .perform(get("/api/identify").param("notes", "C"))
        .andExpect(status().isOk())
        .andExpect(content().string("[]"));
  }

  @Test
  void identifyWithoutNotesParamIsBadRequest() throws Exception {
    mockMvc.perform(get("/api/identify")).andExpect(status().isBadRequest());
  }

  @Test
  void identifyWithUnknownNoteNameIsBadRequest() throws Exception {
    mockMvc
        .perform(get("/api/identify").param("notes", "H", "A", "B", "C"))
        .andExpect(status().isBadRequest());
  }
}
