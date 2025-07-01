package com.leonid.workerbot;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {

        String java = getScriptEntry("JAVA");
        System.out.println(java);

    }

    private static String getScriptEntry(String name) {
        Path path = Path.of("init-scripts" + File.separator + name.toLowerCase() + "-instance-setup.sh");
        try {
            return String.join("\n", Files.readAllLines(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
