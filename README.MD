<div align="center">

# 📦 ResourcePackEnforcer

**Erzwingt ein Resourcepack auf deinem Minecraft-Server — mit Kick-Nachricht, Update-Checker und vollständiger Konfiguration.**

[![Release](https://img.shields.io/github/v/release/Shvquu/RPMG?style=for-the-badge&color=gold)](https://github.com/Shvquu/RPMG/releases/latest)
[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://www.oracle.com/java/)
[![Spigot](https://img.shields.io/badge/Spigot-1.20+-yellow?style=for-the-badge)](https://www.spigotmc.org/)
[![License](https://img.shields.io/github/license/Shvquu/RPMG?style=for-the-badge)](LICENSE)
[![Build](https://img.shields.io/github/actions/workflow/status/Shvquu/RPMG/release.yml?style=for-the-badge)](https://github.com/Shvquu/RPMG/actions)

</div>

---

## ✨ Features

- 📥 **Automatisches Senden** des Resourcepacks beim Joinen
- 🔒 **Erzwungenes Resourcepack** — Spieler werden bei Ablehnung gekickt
- 💬 **Vollständig anpassbare Kick-Nachricht** mit Farbcodes & Hex-Farben
- 🔔 **Konsolen-Banner** beim Start, Stop und bei verfügbaren Updates
- 🔄 **Automatischer Update-Checker** via GitHub Releases API
- ⚙️ **Config wird automatisch generiert** und fehlende Keys ergänzt
- 🛡️ **Bypass-Permission** für Admins und Moderatoren
- 🎨 **Hex-Farben** (`&#RRGGBB`) und `&`-Farbcodes überall unterstützt

---

## 📋 Voraussetzungen

| Anforderung | Version |
|---|---|
| Java | 21+ |
| Spigot / Paper | 1.20+ |

---

## 🚀 Installation

1. Lade die neueste `.jar` von den [Releases](https://github.com/DeinGitHubName/ResourcePackEnforcer/releases/latest) herunter
2. Lege die Datei in den `plugins/` Ordner deines Servers
3. Starte den Server — die `config.yml` wird automatisch erstellt
4. Trage deine Resourcepack-URL in der `config.yml` ein
5. Starte den Server neu oder führe `/rp reload` aus

---

## ⚙️ Konfiguration

```yaml
resourcepack:
  # Direkte Download-URL zur .zip Datei
  url: "https://example.com/resourcepack.zip"

  # SHA-1 Hash zur Validierung (oder "none")
  hash: "none"

  # Prompt-Nachricht beim Anfordern des Packs (1.17+)
  prompt: "&eBitte akzeptiere das Resourcepack!"

  # true = Kick bei Ablehnung | false = optional
  enforce: true

  # Verzögerung in Ticks (20 = 1 Sekunde)
  send-delay: 20

messages:
  kick-message: |-
    &c&lResourcepack abgelehnt!
    &7Du musst das Resourcepack akzeptieren,
    &7um diesen Server betreten zu können.

  pack-loaded:     "&a&lResourcepack erfolgreich geladen!"
  pack-failed:     "&c&lFehler beim Laden. Bitte neu verbinden!"
  prefix:          "&8[&6ResourcePack&8] &r"
  reload-success:  "&aKonfiguration erfolgreich neu geladen!"
  pack-sent:       "&aDas Resourcepack wurde an %player% gesendet!"
```

> **Tipp:** Hex-Farben werden mit `&#RRGGBB` unterstützt, z.B. `&#FFD700` für Gold.

---

## 💬 Befehle

| Befehl | Beschreibung | Permission |
|---|---|---|
| `/rp reload` | Config neu laden | `resourcepack.admin` |
| `/rp send [Spieler]` | Pack an einen Spieler senden | `resourcepack.admin` |
| `/rp sendall` | Pack an alle Spieler senden | `resourcepack.admin` |
| `/rp info` | Plugin-Status anzeigen | `resourcepack.admin` |

**Alias:** `/resourcepack`

---

## 🔑 Permissions

| Permission | Beschreibung | Standard |
|---|---|---|
| `resourcepack.admin` | Zugriff auf alle Befehle | OP |
| `resourcepack.bypass` | Resourcepack-Pflicht umgehen | `false` |

---

## 🔄 Update-Checker

Das Plugin prüft beim Start automatisch ob eine neue Version auf GitHub verfügbar ist.

- Neue Version gefunden → **Update-Banner in der Konsole**
- Admins mit `resourcepack.admin` erhalten beim **Joinen eine Chat-Nachricht**

```
[ResourcePack] ⚡ Update verfügbar! (1.0.0 → 1.1.0)
[ResourcePack] Download: https://github.com/.../releases/latest
```

---

## 🏗️ Selbst bauen

```bash
git clone https://github.com/DeinGitHubName/ResourcePackEnforcer.git
cd ResourcePackEnforcer
mvn clean package
```

Die fertige `.jar` liegt anschließend im `target/` Ordner.

> Releases werden automatisch via **GitHub Actions** gebaut und veröffentlicht, sobald ein Tag (`v1.0.0`) gepusht wird.

---

## 📁 Projektstruktur

```
ResourcePackPlugin/
├── .github/
│   ├── workflows/
│   │   └── release.yml       # Auto-Build bei neuem Tag
│   └── dependabot.yml        # Automatische Dependency-Updates
├── src/main/
│   ├── java/de/resourcepack/plugin/
│   │   ├── ResourcePackPlugin.java       # Hauptklasse
│   │   ├── commands/
│   │   │   └── ResourcePackCommand.java  # /rp Befehl
│   │   ├── listeners/
│   │   │   ├── PlayerJoinListener.java   # Pack beim Join senden
│   │   │   └── ResourcePackListener.java # Status-Events verarbeiten
│   │   ├── manager/
│   │   │   ├── ConfigManager.java        # Config-Verwaltung
│   │   │   └── ResourcePackManager.java  # Pack-Logik
│   │   └── utils/
│   │       ├── ColorUtils.java           # Farb-Codes & Hex
│   │       ├── ConsoleBanner.java        # ASCII-Banner
│   │       └── UpdateChecker.java        # GitHub Update-Check
│   └── resources/
│       ├── config.yml
│       └── plugin.yml
└── pom.xml
```

---

## 📜 Lizenz

Dieses Projekt steht unter der [MIT License](LICENSE).

---

<div align="center">
  <sub>Made with ❤️ — Pull Requests sind willkommen!</sub>
</div>