package com.leonid.model;

import java.util.List;
import java.util.Random;

public class RandomNameGenerator {

    private static final List<String> ADJECTIVES = List.of(
            "sneaky", "weird", "chunky", "sassy", "derpy", "clumsy", "grumpy", "noisy", "wobbly", "zesty"
    );

    private static final List<String> NOUNS = List.of(
            "potato", "llama", "nugget", "toaster", "banana", "sloth", "pickle", "duck", "yeti", "burrito"
    );

    private static final Random RANDOM = new Random();

    public static String generate() {
        String adjective = ADJECTIVES.get(RANDOM.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(RANDOM.nextInt(NOUNS.size()));
        return adjective + "-" + noun;
    }
}

