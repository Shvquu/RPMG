package de.voxellabs.resourcepack.resourcepack.utils;

import java.util.logging.Logger;

public class ConsoleBanner {

    // ANSI Farb-Codes für die Konsole
    private static final String RESET  = "\u001B[0m";
    private static final String BOLD   = "\u001B[1m";
    private static final String GOLD   = "\u001B[38;5;220m";
    private static final String CYAN   = "\u001B[36m";
    private static final String GREEN  = "\u001B[32m";
    private static final String RED    = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GRAY   = "\u001B[90m";
    private static final String WHITE  = "\u001B[97m";

    /**
     * Banner beim Plugin-Start
     */
    public static void printStartBanner(Logger logger, String version, String packUrl, boolean enforce) {
        logger.info(RESET + "");
        logger.info(GOLD + BOLD + "  ██████╗ ███████╗███████╗ ██████╗ ██╗   ██╗██████╗  ██████╗███████╗" + RESET);
        logger.info(GOLD + BOLD + "  ██╔══██╗██╔════╝██╔════╝██╔═══██╗██║   ██║██╔══██╗██╔════╝██╔════╝" + RESET);
        logger.info(GOLD + BOLD + "  ██████╔╝█████╗  ███████╗██║   ██║██║   ██║██████╔╝██║     █████╗  " + RESET);
        logger.info(GOLD + BOLD + "  ██╔══██╗██╔══╝  ╚════██║██║   ██║██║   ██║██╔══██╗██║     ██╔══╝  " + RESET);
        logger.info(GOLD + BOLD + "  ██║  ██║███████╗███████║╚██████╔╝╚██████╔╝██║  ██║╚██████╗███████╗" + RESET);
        logger.info(GOLD + BOLD + "  ╚═╝  ╚═╝╚══════╝╚══════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝ ╚═════╝╚══════╝" + RESET);
        logger.info(GRAY   + "  ┌─────────────────────────────────────────────────────────────────┐" + RESET);
        logger.info(GRAY   + "  │  " + CYAN + "ResourcePack" + WHITE + "Enforcer" + GRAY + "                      " + WHITE + "by " + CYAN + "DeinName" + GRAY + "          │" + RESET);
        logger.info(GRAY   + "  │                                                                  │" + RESET);
        logger.info(GRAY   + "  │  " + WHITE + "Version:  " + GREEN + BOLD + version + RESET + padRight("", 52 - version.length()) + GRAY + "│" + RESET);
        logger.info(GRAY   + "  │  " + WHITE + "Status:   " + GREEN + BOLD + "✔ AKTIV" + RESET + "                                              " + GRAY + "│" + RESET);
        logger.info(GRAY   + "  │  " + WHITE + "Enforce:  " + (enforce ? GREEN + BOLD + "✔ Ja" : RED + BOLD + "✘ Nein") + RESET + "                                            " + GRAY + "│" + RESET);
        logger.info(GRAY   + "  │  " + WHITE + "Pack-URL: " + CYAN  + truncate(packUrl, 50) + RESET + "  " + GRAY + "│" + RESET);
        logger.info(GRAY   + "  └─────────────────────────────────────────────────────────────────┘" + RESET);
        logger.info(GREEN + BOLD + "  ✔ Plugin erfolgreich geladen!" + RESET);
        logger.info(RESET + "");
    }

    /**
     * Banner beim Plugin-Stop
     */
    public static void printStopBanner(Logger logger, String version) {
        logger.info(RESET + "");
        logger.info(RED + BOLD + "  ██████╗ ███████╗███████╗ ██████╗ ██╗   ██╗██████╗  ██████╗███████╗" + RESET);
        logger.info(RED + BOLD + "  ██╔══██╗██╔════╝██╔════╝██╔═══██╗██║   ██║██╔══██╗██╔════╝██╔════╝" + RESET);
        logger.info(RED + BOLD + "  ██████╔╝█████╗  ███████╗██║   ██║██║   ██║██████╔╝██║     █████╗  " + RESET);
        logger.info(RED + BOLD + "  ██╔══██╗██╔══╝  ╚════██║██║   ██║██║   ██║██╔══██╗██║     ██╔══╝  " + RESET);
        logger.info(RED + BOLD + "  ██║  ██║███████╗███████║╚██████╔╝╚██████╔╝██║  ██║╚██████╗███████╗" + RESET);
        logger.info(RED + BOLD + "  ╚═╝  ╚═╝╚══════╝╚══════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝ ╚═════╝╚══════╝" + RESET);
        logger.info(GRAY   + "  ┌─────────────────────────────────────────────────────────────────┐" + RESET);
        logger.info(GRAY   + "  │  " + RED + BOLD + "✘ Plugin wird deaktiviert..." + RESET + "                                    " + GRAY + "│" + RESET);
        logger.info(GRAY   + "  │                                                                  │" + RESET);
        logger.info(GRAY   + "  │  " + WHITE + "Version:  " + YELLOW + BOLD + version + RESET + padRight("", 52 - version.length()) + GRAY + "│" + RESET);
        logger.info(GRAY   + "  │  " + WHITE + "Status:   " + RED + BOLD + "✘ GESTOPPT" + RESET + "                                           " + GRAY + "│" + RESET);
        logger.info(GRAY   + "  └─────────────────────────────────────────────────────────────────┘" + RESET);
        logger.info(RED + BOLD + "  ✘ ResourcePackEnforcer wurde gestoppt. Auf Wiedersehen!" + RESET);
        logger.info(RESET + "");
    }

    /**
     * Banner wenn ein Update verfügbar ist
     */
    public static void printUpdateBanner(Logger logger, String currentVersion, String latestVersion, String downloadUrl) {
        logger.info(RESET + "");
        logger.info(YELLOW + BOLD + "  ██╗   ██╗██████╗ ██████╗  █████╗ ████████╗███████╗" + RESET);
        logger.info(YELLOW + BOLD + "  ██║   ██║██╔══██╗██╔══██╗██╔══██╗╚══██╔══╝██╔════╝" + RESET);
        logger.info(YELLOW + BOLD + "  ██║   ██║██████╔╝██║  ██║███████║   ██║   █████╗  " + RESET);
        logger.info(YELLOW + BOLD + "  ██║   ██║██╔═══╝ ██║  ██║██╔══██║   ██║   ██╔══╝  " + RESET);
        logger.info(YELLOW + BOLD + "  ╚██████╔╝██║     ██████╔╝██║  ██║   ██║   ███████╗" + RESET);
        logger.info(YELLOW + BOLD + "   ╚═════╝ ╚═╝     ╚═════╝ ╚═╝  ╚═╝   ╚═╝   ╚══════╝" + RESET);
        logger.info(GRAY   + "  ┌─────────────────────────────────────────────────────────────────┐" + RESET);
        logger.info(GRAY   + "  │  " + YELLOW + BOLD + "⚡ Ein neues Update ist verfügbar!" + RESET + "                               " + GRAY + "│" + RESET);
        logger.info(GRAY   + "  │                                                                  │" + RESET);
        logger.info(GRAY   + "  │  " + WHITE + "Aktuell:  " + RED    + BOLD + currentVersion + RESET + padRight("", 52 - currentVersion.length()) + GRAY + "│" + RESET);
        logger.info(GRAY   + "  │  " + WHITE + "Neu:      " + GREEN  + BOLD + latestVersion  + RESET + padRight("", 52 - latestVersion.length())  + GRAY + "│" + RESET);
        logger.info(GRAY   + "  │                                                                  │" + RESET);
        logger.info(GRAY   + "  │  " + WHITE + "Download: " + CYAN   + truncate(downloadUrl, 50) + RESET + "  " + GRAY + "│" + RESET);
        logger.info(GRAY   + "  └─────────────────────────────────────────────────────────────────┘" + RESET);
        logger.info(YELLOW + BOLD + "  ⚡ Bitte aktualisiere das Plugin so bald wie möglich!" + RESET);
        logger.info(RESET + "");
    }

    // Hilfsmethoden
    private static String padRight(String s, int n) {
        if (n <= 0) return "";
        return String.format("%-" + n + "s", s);
    }

    private static String truncate(String s, int max) {
        if (s == null) return "Nicht gesetzt";
        return s.length() > max ? s.substring(0, max - 3) + "..." : s;
    }
}
