package com.hileco.cortex.documentation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Documentation {

    private static final Path DOCS_PATH;
    private static final Map<String, Document> OPEN_DOCUMENTS;

    static {
        var userDir = System.getProperty("user.dir");
        DOCS_PATH = Paths.get(userDir, "build/generated-snippets");
        OPEN_DOCUMENTS = new HashMap<>();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Document of(String snippetPath) {
        return OPEN_DOCUMENTS.computeIfAbsent(snippetPath, ignored -> {
            try {
                var file = DOCS_PATH.resolve(String.format("%s.adoc", snippetPath)).toFile();
                if (!file.exists()) {
                    file.createNewFile();
                }
                var outputStream = new FileOutputStream(file, !file.exists());
                return new Document(snippetPath, outputStream);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
    }
}
