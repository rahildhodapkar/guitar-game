package com.rahildhodapkar.guitar_game.theory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ChordDetector {
    private static final int MINOR_THIRD_BIT = 1 << 3;
    private static final int MAJOR_THIRD_BIT = 1 << 4;
    private static final int DIMINISHED_FIFTH_BIT = 1 << 6;
    private static final int PERFECT_FIFTH_BIT = 1 << 7;
    private static final int AUGMENTED_FIFTH_BIT = 1 << 8;
    private static final int MINOR_SEVENTH_BIT = 1 << 10;
    private static final int MAJOR_SEVENTH_BIT = 1 << 11;

    private record Pair(ChordQuality quality, int bitmask) {
    };

    private static final List<Pair> CHORD_QUALITY_BITMASKS = createChordQualityBitmasks();

    public List<DetectedChord> detect(List<PlayedNote> source, ChordDetectOptions options) {
        if (source == null || source.isEmpty()) {
            return List.of();
        }

        Map<Integer, Integer> noteFrequency = new HashMap<>();

        for (PlayedNote note : source) {
            noteFrequency.merge(note.pitchClass().value(), 1, Integer::sum);
        }

        List<PlayedNote> cleanedSource = new ArrayList<>(
                source.stream()
                        .sorted(Comparator.comparing(PlayedNote::midiNumber))
                        .collect(Collectors.toMap(
                                pn -> pn.pitchClass().value(),
                                Function.identity(),
                                (first, second) -> first,
                                LinkedHashMap::new))
                        .values());

        if (cleanedSource.size() < 2) {
            return List.of();
        }

        Map<Integer, String> originalNoteNames = createOriginalNoteNameMap(cleanedSource);

        List<PitchClass> pitchClasses = cleanedSource.stream().map(pn -> pn.pitchClass()).toList();

        PitchClass bass = pitchClasses.get(0);

        List<DetectedChord> detectedChordBuffer = new ArrayList<>();
        for (PitchClass candidateRoot : pitchClasses) {
            int intervalMask = PitchClassSet.maskFromPitchClasses(pitchClasses, candidateRoot);
            for (Pair pair : CHORD_QUALITY_BITMASKS) {
                ChordQuality cq = pair.quality();
                int cqMask = pair.bitmask();

                // TODO: This only checks for 7th chords, look into expanding
                int effectiveMask = intervalMask;
                boolean assumedFifth = false;
                if (effectiveMask != cqMask && options.assumePerfectFifth()) {
                    if (!hasDimOrAugFifth(effectiveMask) && qualityHasThirdPerfectFifthAndSeventh(cqMask)) {
                        effectiveMask |= PERFECT_FIFTH_BIT;
                        assumedFifth = true;
                    }
                }

                if (effectiveMask == cqMask) {
                    String detectedChordName = createDetectedChordName(candidateRoot, bass, cq,
                            originalNoteNames);
                    double weight = calculateWeight(candidateRoot, bass, cq, hasMinOrMajThird(effectiveMask),
                            assumedFifth, noteFrequency);
                    detectedChordBuffer.add(new DetectedChord(detectedChordName, candidateRoot, cq, bass, weight));
                }
            }
        }

        return detectedChordBuffer;
    }

    private static String createDetectedChordName(PitchClass candidateRoot,
            PitchClass bass, ChordQuality chordQuality, Map<Integer, String> originalNoteNames) {
        String rootName = originalNoteNames.getOrDefault(candidateRoot.value(), candidateRoot.name(false));
        String bassName = originalNoteNames.getOrDefault(bass.value(), bass.name(false));
        String qualitySymbol = chordQuality.getSymbol();

        String detectedChordName = rootName.equals(bassName)
                ? rootName + qualitySymbol
                : rootName + qualitySymbol + "/" + bassName;

        return detectedChordName;
    }

    private static List<Pair> createChordQualityBitmasks() {
        List<Pair> result = new ArrayList<>();
        for (ChordQuality quality : ChordQuality.values()) {
            result.add(new Pair(quality, PitchClassSet.maskFromIntervals(quality.getIntervals())));
        }
        return result;
    }

    private static double calculateWeight(PitchClass root, PitchClass bass, ChordQuality quality, boolean hasThird,
            boolean assumedFifth, Map<Integer, Integer> noteFrequency) {
        double weight = 100.0;

        // TODO: tweak
        if (root.equals(bass)) {
            weight += 30.0;
        } else {
            weight -= 15.0;
        }

        if (assumedFifth) {
            weight -= 10.0;
        } else {
            weight += 10.0;
        }

        if (hasThird) {
            weight += 10.0;
        }

        int rootFreq = noteFrequency.get(root.value());
        if (rootFreq > 1) {
            weight += rootFreq * 5.0;
        }

        weight -= quality.getComplexity() * 5.0;

        return weight;
    }

    private static Map<Integer, String> createOriginalNoteNameMap(List<PlayedNote> originalNotes) {
        HashMap<Integer, String> m = new HashMap<>();
        for (PlayedNote note : originalNotes) {
            m.putIfAbsent(note.pitchClass().value(), note.displayName());
        }
        return m;
    }

    private boolean hasDimOrAugFifth(int mask) {
        return (mask & DIMINISHED_FIFTH_BIT) != 0 || (mask & AUGMENTED_FIFTH_BIT) != 0;
    }

    private boolean qualityHasThirdPerfectFifthAndSeventh(int mask) {
        return (hasMinOrMajThird(mask) && ((mask & PERFECT_FIFTH_BIT) != 0)
                && ((mask & MINOR_SEVENTH_BIT) != 0 || (mask & MAJOR_SEVENTH_BIT) != 0));
    }

    private boolean hasMinOrMajThird(int mask) {
        return (mask & MINOR_THIRD_BIT) != 0 || (mask & MAJOR_THIRD_BIT) != 0;
    }
}