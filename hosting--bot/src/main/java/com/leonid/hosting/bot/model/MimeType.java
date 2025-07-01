package com.leonid.hosting.bot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MimeType {
    JAR("application/java-archive"),
    ZIP("application/zip");
    private final String extension;
}
