package de.voxellabs.resourcepack.resourcepack.utils;

import org.bukkit.ChatColor;

public class ColorUtils {

    /**
     * Konvertiert & Farb-Codes zu Minecraft-Farben
     * Unterstützt auch Hex-Farben im Format &#RRGGBB (1.16+)
     */
    public static String colorize(String text) {
        if (text == null) return "";

        // Hex-Farben konvertieren (&#RRGGBB -> §x§R§R§G§G§B§B)
        text = convertHexColors(text);

        // Standard & Farb-Codes konvertieren
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private static String convertHexColors(String text) {
        if (!text.contains("&#")) return text;

        StringBuilder result = new StringBuilder();
        char[] chars = text.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '&' && i + 7 < chars.length && chars[i + 1] == '#') {
                // Versuche Hex-Farbe zu parsen
                String hex = new String(chars, i + 2, 6);
                try {
                    // Validierung
                    Integer.parseInt(hex, 16);
                    // Konvertierung zu §x Format
                    result.append("§x");
                    for (char c : hex.toCharArray()) {
                        result.append('§').append(c);
                    }
                    i += 7; // &#RRGGBB überspringen
                } catch (NumberFormatException e) {
                    result.append(chars[i]);
                }
            } else {
                result.append(chars[i]);
            }
        }

        return result.toString();
    }
}
