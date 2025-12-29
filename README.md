# HubSystem

Ein zentrales Plugin-System für Minecraft Paper Server mit Echtzeit-Weltsynchronisation und Event-Features.

## Übersicht

HubSystem ist ein modulares Minecraft-Plugin, das als zentrale Verwaltungsplattform für Server-Features dient. Aktuell implementiert ist das RealTime-Modul, welches die Minecraft-Weltzeit mit der realen Zeit synchronisiert und automatische Silvester-Feuerwerke ermöglicht.

## Features

### RealTime-Modul

- **Echtzeit-Synchronisation**: Synchronisiert die Minecraft-Weltzeit mit der realen Uhrzeit
- **Zeitzone-Unterstützung**: Vollständige Unterstützung aller IANA-Zeitzonen
- **Sonnenaufgang/Sonnenuntergang**: Automatische Berechnung basierend auf Datum und Zeitzone
- **Silvester-Feuerwerk**: Automatisches Feuerwerk am Neujahrstag um Mitternacht
- **Konfigurierbar**: Umfangreiche Konfigurationsmöglichkeiten für alle Features

## Anforderungen

- **Minecraft**: Paper 1.21.11 oder höher
- **Java**: Version 21 oder höher
- **Build-Tool**: Maven 3.x

## Installation

### Vorkompiliert

1. Lade die neueste `HubSystem.jar` aus dem Releases-Bereich herunter
2. Platziere die JAR-Datei im `plugins`-Ordner deines Servers
3. Starte den Server neu
4. Konfiguriere das Plugin unter `plugins/HubSystem/RealTime.yml`

### Aus Quellcode

```bash
git clone <repository-url>
cd HubSystem
mvn clean package
```

Die kompilierte JAR-Datei befindet sich anschließend unter `target/HubSystem-1.0.jar`.

## Konfiguration

### RealTime.yml

Die Konfigurationsdatei wird beim ersten Start automatisch erstellt:

```yaml
# Zeitzone für die Weltzeit-Synchronisation
timezone: Europe/Berlin

# Synchronisations-Intervall in Sekunden
sync-interval-seconds: 1

# Silvester-Feuerwerk Konfiguration
newyear-firework:
  enabled: true
  center-x: 0.0
  center-y: 100.0
  center-z: 0.0
  radius: 50
  spawn-interval-seconds: 7
  world: world
```

#### Konfigurationsoptionen

| Option | Typ | Standard | Beschreibung |
|--------|-----|----------|--------------|
| `timezone` | String | `Europe/Berlin` | IANA-Zeitzone für die Zeitberechnung |
| `sync-interval-seconds` | Integer | `1` | Wie oft die Weltzeit aktualisiert wird |
| `newyear-firework.enabled` | Boolean | `true` | Aktiviert/deaktiviert das Silvester-Feuerwerk |
| `newyear-firework.center-x` | Double | `0.0` | X-Koordinate des Feuerwerk-Zentrums |
| `newyear-firework.center-y` | Double | `100.0` | Y-Koordinate des Feuerwerk-Zentrums |
| `newyear-firework.center-z` | Double | `0.0` | Z-Koordinate des Feuerwerk-Zentrums |
| `newyear-firework.radius` | Integer | `50` | Radius in dem Feuerwerke spawnen |
| `newyear-firework.spawn-interval-seconds` | Integer | `7` | Intervall zwischen Feuerwerk-Spawns |
| `newyear-firework.world` | String | `world` | Weltname für das Feuerwerk |

## Befehle

### /timezone

Zeigt die aktuelle Zeit an und verwaltet die Zeitzone.

**Aliase**: `/tz`

**Verwendung**:
- `/timezone` - Zeigt aktuelle Zeit, Zeitzone und Sonnenzeiten
- `/timezone reload` - Lädt die Konfiguration neu
- `/timezone <zeitzone>` - Setzt eine neue Zeitzone (z.B. `America/New_York`)

**Permission**: `hubsystem.timezone.admin` (Standard: OP)

### /fireworkdebug

Startet das Silvester-Feuerwerk für 1 Minute zu Testzwecken.

**Aliase**: `/fwdebug`, `/testfeuerwerk`

**Verwendung**:
- `/fireworkdebug` - Startet Debug-Feuerwerk für 60 Sekunden

**Permission**: `hubsystem.firework.debug` (Standard: OP)

## Permissions

| Permission | Beschreibung | Standard |
|------------|--------------|----------|
| `hubsystem.timezone.admin` | Erlaubt Admin-Befehle für RealTime | OP |
| `hubsystem.firework.debug` | Erlaubt das Starten des Test-Feuerwerks | OP |

## Technische Details

### Projekt-Struktur

```
HubSystem/
├── src/main/java/net/letstim/hubSystem/
│   ├── HubSystem.java              # Hauptklasse
│   ├── config/
│   │   └── RealTimeConfig.java     # Konfigurations-Manager
│   └── realtime/
│       ├── RealTime.java           # RealTime-Modul
│       ├── WorldTimeSync.java      # Zeit-Synchronisation
│       ├── SolarCalculator.java    # Sonnenstand-Berechnung
│       ├── NewYearFirework.java    # Silvester-Feuerwerk
│       ├── TimezoneCommand.java    # Zeitzone-Befehl
│       └── FireworkDebugCommand.java # Debug-Befehl
└── src/main/resources/
    └── plugin.yml                   # Plugin-Konfiguration
```

### Abhängigkeiten

- **Paper API**: 1.21.11-R0.1-SNAPSHOT
- **Java**: 21

### Funktionsweise

#### Echtzeit-Synchronisation

Das Plugin berechnet die Minecraft-Zeit basierend auf der realen Uhrzeit:

1. Ermittelt die aktuelle Zeit in der konfigurierten Zeitzone
2. Berechnet Sonnenaufgang und Sonnenuntergang für das aktuelle Datum
3. Mappt die reale Zeit auf die 24.000 Minecraft-Ticks:
   - Tag (Sonnenaufgang bis Sonnenuntergang): Tick 0-12.000
   - Nacht (Sonnenuntergang bis Sonnenaufgang): Tick 12.000-24.000
4. Deaktiviert den natürlichen Tag/Nacht-Zyklus (`doDaylightCycle = false`)

#### Silvester-Feuerwerk

Das Feuerwerk-System läuft automatisch:

1. Prüft jede Sekunde ob es Silvester/Neujahr um Mitternacht ist
2. Aktiviert sich von 0:00-1:00 Uhr am 31. Dezember oder 1. Januar
3. Spawnt in konfigurierten Intervallen zufällige Feuerwerke
4. Feuerwerke spawnen zufällig in einem konfigurierbaren Radius
5. Verschiedene Farben, Effekte und Flughöhen für Abwechslung

## Entwicklung

### Build

```bash
mvn clean package
```

### Neues Modul erstellen

1. Erstelle eine neue Klasse in `src/main/java/net/letstim/hubSystem/`
2. Initialisiere das Modul in `HubSystem.java`
3. Registriere Befehle in der `plugin.yml`
4. Füge Konfigurationsoptionen falls nötig hinzu

## Lizenz

Dieses Projekt ist für den privaten Gebrauch bestimmt.

## Support

Bei Fragen oder Problemen erstelle ein Issue im Repository.

## Changelog

### Version 1.0
- Initiales Release
- RealTime-Modul mit Echtzeit-Synchronisation
- Silvester-Feuerwerk-System
- Zeitzone-Verwaltung
- Debug-Befehle für Entwickler

