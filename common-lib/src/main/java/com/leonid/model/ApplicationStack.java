package com.leonid.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public enum ApplicationStack {
    JAVA("stack-java", "☕ Java"),
    FRONTEND_FRAMEWORK("stack-frontend", "⚛️ Frontend"), // React, Vue, Angular, Vite, etc.
    STATIC("stack-static", "📄 Static"), // Only HTML/CSS/JS without npm
    NODE_JS("stack-nodejs", "🟢 Node.js"),
    DOCKER("stack-docker", "🐳 Docker");

    private final String value;
    private final String label;

    public static ApplicationStack fromString(String value) {
        for (ApplicationStack stack : ApplicationStack.values()) {
            if (stack.getValue().equals(value)) {
                return stack;
            }
        }
        throw new IllegalArgumentException("Unknown stack: " + value);
    }

    public static String getArchiveDescription(ApplicationStack stack) {

        String randomName = RandomNameGenerator.generate();
        randomName += ".zip";

        switch (stack) {
            case JAVA -> {
                return "Good. Now send me your src. It must be archive. Like:\n" +
                        "```" + JAVA + "\n" +
                        randomName + "\n" +
                        "├── pom.xml\n" +
                        "└── src/\n" +
                        "    └── main/\n" +
                        "        ├── java/\n" +
                        "        └── resources/\n" +
                        "   ...\n" +
                        "```";
            }
            case STATIC -> {
                return "Good. Now send me your src. It must be archive. Like:\n" +
                        "```" + STATIC + "\n" +
                        randomName + "\n" +
                        "└─ index.html\n" +
                        "   style.css\n" +
                        "   script.js\n" +
                        "   ...\n" +
                        "```";
            }
            case DOCKER -> {
                return "Good. Now send me your src. It must be archive. Like:\n" +
                        "```" + DOCKER + "\n" +
                        randomName + "\n" +
                        "├── Dockerfile\n" +
                        "└── app/\n" +
                        "    ├── src/\n" +
                        "    └── resources/\n" +
                        "   ...\n" +
                        "```";
            }
            case FRONTEND_FRAMEWORK -> {
                return "Good. Now send me your src. It must be archive. Like:\n" +
                        "```" + FRONTEND_FRAMEWORK + "\n" +
                        randomName + "\n" +
                        "├── package.json\n" +
                        "├── public/\n" +
                        "│   └── index.html\n" +
                        "├── src/\n" +
                        "│   ├── App.jsx or App.vue or app.component.ts\n" +
                        "│   └── main.jsx or main.ts\n" +
                        "...\n" +
                        "```";
            }
            case NODE_JS -> {
                return "Good. Now send me your src. It must be archive. Like:\n" +
                        "```" + NODE_JS + "\n" +
                        randomName + "\n" +
                        "├── package.json\n" +
                        "├── server.js or index.js\n" +
                        "├── routes/\n" +
                        "├── controllers/\n" +
                        "├── views/ or public/\n" +
                        "...\n" +
                        "```";
            }
            default -> throw new IllegalArgumentException("Unknown stack: " + stack);
        }
    }

    public static List<ApplicationStack> getEC2Hosted(){
        return List.of(JAVA, NODE_JS, DOCKER);
    }

}

