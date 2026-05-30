# 🤝 Contributing zu ResourcePackEnforcer

Danke, dass du zu ResourcePackEnforcer beitragen möchtest!  
Jeder Beitrag — egal ob Bugfix, neue Funktion oder Dokumentation — ist willkommen.

---

## 📋 Inhaltsverzeichnis

- [Verhaltenskodex](#verhaltenskodex)
- [Wie kann ich beitragen?](#wie-kann-ich-beitragen)
- [Entwicklungsumgebung einrichten](#entwicklungsumgebung-einrichten)
- [Branches & Workflow](#branches--workflow)
- [Commit-Konventionen](#commit-konventionen)
- [Pull Request einreichen](#pull-request-einreichen)
- [Code-Style](#code-style)

---

## 🤝 Verhaltenskodex

- Sei respektvoll und konstruktiv im Umgang mit anderen
- Kritisiere Code, nicht Personen
- Hilf anderen, die neu im Projekt sind

---

## 💡 Wie kann ich beitragen?

### 🐛 Bug melden
1. Prüfe zuerst ob der Bug bereits unter [Issues](https://github.com/DeinGitHubName/ResourcePackEnforcer/issues) gemeldet wurde
2. Öffne ein neues Issue mit dem Label `bug`
3. Beschreibe den Bug so genau wie möglich:
    - Minecraft- & Server-Version
    - Plugin-Version
    - Schritte zur Reproduktion
    - Erwartetes vs. tatsächliches Verhalten
    - Fehlermeldung / Stacktrace (falls vorhanden)

### ✨ Feature vorschlagen
1. Öffne ein Issue mit dem Label `enhancement`
2. Beschreibe die gewünschte Funktion und warum sie sinnvoll wäre
3. Warte auf Feedback bevor du mit der Implementierung beginnst

### 📖 Dokumentation verbessern
Tippfehler, unklare Erklärungen oder fehlende Informationen?  
Einfach Fork → Änderung → Pull Request — kein Issue nötig.

---

## 🛠️ Entwicklungsumgebung einrichten

### Voraussetzungen
- **Java 21** (JDK)
- **Maven 3.8+**
- **Git**
- IDE deiner Wahl (IntelliJ IDEA empfohlen)

### Setup

```bash
# 1. Repository forken (über GitHub UI)

# 2. Fork klonen
git clone https://github.com/DeinGitHubName/ResourcePackEnforcer.git
cd ResourcePackEnforcer

# 3. Abhängigkeiten laden & bauen
mvn clean package -DskipTests

# 4. Upstream als Remote hinzufügen
git remote add upstream https://github.com/DeinGitHubName/ResourcePackEnforcer.git
```

### Lokal testen
Die fertige `.jar` liegt nach dem Build im `target/` Ordner und kann direkt in einen lokalen Testserver kopiert werden.

---

## 🌿 Branches & Workflow

| Branch | Beschreibung |
|---|---|
| `main` | Stabiler Release-Branch — nur via PR |
| `dev` | Entwicklungs-Branch — Basis für neue Features |
| `feature/xxx` | Neues Feature (von `dev` abzweigen) |
| `fix/xxx` | Bugfix (von `dev` oder `main` abzweigen) |

```bash
# Vor jedem neuen Branch: upstream synchronisieren
git fetch upstream
git checkout dev
git merge upstream/dev

# Neuen Feature-Branch erstellen
git checkout -b feature/mein-feature

# Nach der Arbeit pushen
git push origin feature/mein-feature
```

---

## 📝 Commit-Konventionen

Wir verwenden [Conventional Commits](https://www.conventionalcommits.org/):

```
<typ>(<bereich>): <kurze Beschreibung>
```

### Typen

| Typ | Wann verwenden |
|---|---|
| `feat` | Neue Funktion |
| `fix` | Bugfix |
| `docs` | Nur Dokumentation |
| `refactor` | Code-Umstrukturierung ohne neue Funktion |
| `chore` | Build, Dependencies, CI |
| `style` | Formatierung, kein Logik-Änderung |
| `perf` | Performance-Verbesserung |

### Beispiele

```bash
git commit -m "feat(update-checker): versionsnummer aus pom.xml lesen"
git commit -m "fix(listener): kick wird doppelt ausgelöst behoben"
git commit -m "docs(readme): installationsanleitung ergänzt"
git commit -m "chore(deps): spigot-api auf 1.20.6 aktualisiert"
```

---

## 🔀 Pull Request einreichen

1. Stelle sicher dass dein Branch auf dem neuesten Stand von `dev` ist
2. Führe `mvn clean package` aus und stelle sicher dass der Build erfolgreich ist
3. Öffne einen Pull Request gegen den `dev` Branch (nicht `main`)
4. Fülle die PR-Vorlage vollständig aus:
    - Was wurde geändert und warum?
    - Wie wurde es getestet?
    - Gibt es Breaking Changes?
5. Warte auf ein Review — wir melden uns so schnell wie möglich

> PRs direkt gegen `main` werden abgelehnt.

---

## 🎨 Code-Style

- **Sprache:** Englisch für Code & Kommentare, Deutsch für Nutzer-sichbare Texte (Nachrichten, Config)
- **Einrückung:** 4 Spaces (keine Tabs)
- **Klammern:** Öffnende Klammer auf derselben Zeile
- **Methoden:** camelCase, sprechende Namen
- **Klassen:** PascalCase
- **Konstanten:** UPPER_SNAKE_CASE
- Keine ungenutzten Imports
- Javadoc für alle `public` Methoden

```java
// ✔ Gut
public void sendResourcePack(Player player) {
    if (player == null) return;
    // ...
}

// ✘ Schlecht
public void Send(Player p) {
    if(p==null){return;}
    // ...
}
```

---

<div align="center">
  <sub>Danke für deinen Beitrag! 🎉</sub>
</div>