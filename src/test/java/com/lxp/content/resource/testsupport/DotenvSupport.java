package com.lxp.content.resource.testsupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Minimal .env loader for tests only (no external dependency). */
public final class DotenvSupport {
    private DotenvSupport() {}

    public static Map<String, String> loadFromProjectRoot() {
        File f = new File(System.getProperty("user.dir"), ".env");
        if (!f.exists()) return Collections.emptyMap();
        Map<String, String> m = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq < 1) continue;
                String k = line.substring(0, eq).trim();
                String v = line.substring(eq + 1).trim();
                // strip optional quotes
                if ((v.startsWith("\"") && v.endsWith("\"")) || (v.startsWith("'") && v.endsWith("'"))) {
                    v = v.substring(1, v.length() - 1);
                }
                m.put(k, v);
            }
        } catch (IOException ignored) {}
        return m;
    }
}
