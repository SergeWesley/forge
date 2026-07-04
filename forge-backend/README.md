# Forge — Backend

Backend du projet **Forge**, un terminal interactif en temps réel accessible via WebSocket. Construit avec **Spring Boot 4** et **Java 21**, il expose un moteur de commandes extensible, un système de chat multi-utilisateurs et des intégrations avec des API externes.

---

## Table des matières

- [Fonctionnalités](#fonctionnalités)
- [Stack technique](#stack-technique)
- [Architecture](#architecture)
- [Commandes disponibles](#commandes-disponibles)
- [Démarrage rapide](#démarrage-rapide)
- [Docker](#docker)
- [Structure du projet](#structure-du-projet)
- [API WebSocket](#api-websocket)
- [Ajouter une commande](#ajouter-une-commande)
- [Contribuer](#contribuer)
- [Licence](#licence)

---

## Fonctionnalités

- **Terminal WebSocket** — Communication temps réel via STOMP over WebSocket
- **Moteur de commandes extensible** — Ajout de nouvelles commandes via une simple interface `Command`
- **Chat en temps réel** — Système de salons avec création/jointure par code, indicateur de frappe en direct
- **Intégrations API externes** — Météo (Open-Meteo), blagues (Chuck Norris API)
- **Sessions utilisateur** — Gestion de sessions via `Principal` WebSocket (UUID auto-généré)
- **Keep-Alive** — Service intégré pour maintenir les connexions actives

---

## Stack technique

| Technologie | Version | Rôle |
|---|---|---|
| **Java** | 21 | Langage |
| **Spring Boot** | 4.0.1 | Framework applicatif |
| **Spring WebSocket** | — | Communication temps réel (STOMP) |
| **Spring Validation** | — | Validation des données |
| **Jackson** | — | Sérialisation/désérialisation JSON |
| **Lombok** | — | Réduction du boilerplate |
| **Maven** | — | Gestion des dépendances & build |
| **Docker** | — | Conteneurisation |

---

## Architecture

```
┌──────────────────────────────────────────────────┐
│                   Client (Frontend)              │
│              WebSocket (STOMP over WS)           │
└──────────────┬────────────────────┬──────────────┘
               │ /app/command       │ /app/typing
               ▼                    ▼
┌──────────────────────────────────────────────────┐
│              TerminalController                  │
│         (MessageMapping WebSocket)               │
└──────────┬───────────────────────┬───────────────┘
           │                       │
           ▼                       ▼
┌─────────────────────┐  ┌─────────────────────────┐
│    CommandEngine     │  │      ChatEngine          │
│  (Routing & Exec)   │  │  (Rooms & Broadcast)     │
└──────┬──────────────┘  └─────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────┐
│              Command Implementations             │
│  echo │ weather │ joke │ status │ chat │ help    │
└──────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────┐
│            External Services                     │
│     OpenMeteoService  │  ChuckNorrisService      │
└──────────────────────────────────────────────────┘
```

Le `CommandEngine` utilise un pattern **Command** avec auto-discovery : toutes les implémentations de l'interface `Command` enregistrées comme beans Spring sont automatiquement détectées et routées.

---

## Commandes disponibles

| Commande | Description | Exemple |
|---|---|---|
| `echo` | Renvoie le texte passé en argument | `echo Hello World` |
| `weather` | Affiche la météo actuelle d'une ville | `weather Paris` |
| `joke` | Affiche une blague Chuck Norris | `joke` |
| `status` | Affiche le statut du serveur | `status` |
| `chat-start` | Crée un salon de chat | `chat-start` |
| `chat-join` | Rejoint un salon par code | `chat-join AB12` |
| `server-help` | Liste toutes les commandes disponibles | `server-help` |

---

## Démarrage rapide

### Prérequis

- **Java 21+** (JDK)
- **Maven 3.9+** (ou utiliser le wrapper inclus `./mvnw`)

### Lancer en local

```bash
# Cloner le repo
git clone <repo-url>
cd forge-backend

# Lancer l'application
./mvnw spring-boot:run
```

Le serveur démarre sur **`http://localhost:8080`**.

Le endpoint WebSocket est disponible sur **`ws://localhost:8080/ws`**.

### Lancer les tests

```bash
./mvnw test
```

---

## Docker

### Build & Run

```bash
# Build l'image
docker build -t forge-backend .

# Lancer le conteneur
docker run -p 8080:8080 forge-backend
```

Le `Dockerfile` utilise un **multi-stage build** :
1. **Build stage** — Maven + JDK 21 pour compiler le projet
2. **Run stage** — JRE 21 Alpine pour une image légère

---

## Structure du projet

```
forge-backend/
├── src/main/java/com/sergewesley/forge/
│   ├── ForgeApplication.java          # Point d'entrée Spring Boot
│   ├── config/
│   │   └── WebSocketConfig.java       # Configuration STOMP/WebSocket
│   ├── controller/
│   │   ├── HealthController.java      # Endpoint de healthcheck
│   │   └── TerminalController.java    # Handler WebSocket principal
│   ├── dto/
│   │   ├── CommandRequest.java        # DTO requête commande
│   │   ├── CommandResponse.java       # DTO réponse commande
│   │   ├── TypingEvent.java           # DTO événement de frappe
│   │   ├── chucknorris/               # DTOs API Chuck Norris
│   │   └── openmeteo/                 # DTOs API Open-Meteo
│   ├── engine/
│   │   ├── CommandEngine.java         # Routeur de commandes
│   │   ├── ChatEngine.java            # Gestion des salons de chat
│   │   └── ChatModeInterceptor.java   # Intercepteur mode chat
│   ├── external/
│   │   ├── chucknorris/               # Client API Chuck Norris
│   │   └── openmeteo/                 # Client API Open-Meteo
│   └── service/
│       ├── KeepAliveService.java       # Service keep-alive
│       └── command/
│           ├── api/
│           │   ├── Command.java        # Interface de commande
│           │   └── SessionAwareCommand.java  # Interface commande avec session
│           ├── base/
│           │   └── AbstractChatCommand.java  # Classe abstraite chat
│           ├── EchoCommand.java
│           ├── WeatherCommand.java
│           ├── StatusCommand.java
│           ├── JokeCommand.java
│           ├── HelpCommand.java
│           └── chat/
│               ├── ChatStartCommand.java
│               └── ChatJoinCommand.java
├── src/main/resources/
│   └── application.properties          # Configuration Spring
├── Dockerfile                          # Build Docker multi-stage
├── pom.xml                             # Dépendances Maven
└── README.md
```

---

## API WebSocket

### Connexion

```
Endpoint: ws://localhost:8080/ws
Protocole: STOMP
```

Chaque client reçoit un `Principal` avec un UUID unique à la connexion (pas d'authentification requise).

### Envoyer une commande

```
Destination:  /app/command
Payload:      { "command": "weather Paris" }
Réponse:      /user/queue/terminal
```

### Événement de frappe (chat)

```
Destination:  /app/typing
Payload:      { "currentText": "Hello..." }
Réponse:      /user/queue/typing  (broadcast aux autres participants)
```

### Format de réponse

```json
{
  "output": "Weather in Paris, France:\nTemperature: 18.5°C\nWind Speed: 12.3 km/h",
  "type": "default"
}
```

Les types de réponse possibles sont : `default`, `chat`, `system`.

---

## Ajouter une commande

Créer une nouvelle commande est simple — il suffit d'implémenter l'interface `Command` et d'annoter la classe avec `@Component` :

```java
@Component
public class PingCommand implements Command {

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String execute(String args) {
        return "Pong!";
    }

    @Override
    public String getDescription() {
        return "Répond pong. Usage: ping";
    }
}
```

La commande sera **automatiquement détectée** et ajoutée au moteur via l'injection Spring.

Pour les commandes nécessitant la session utilisateur, implémenter `SessionAwareCommand` à la place.

---

## Contribuer

1. Fork le projet
2. Crée une branche (`git checkout -b feature/ma-feature`)
3. Commit (`git commit -m 'feat: ajout de ma feature'`)
4. Push (`git push origin feature/ma-feature`)
5. Ouvre une Pull Request

---

## Licence

Ce projet est un projet personnel de **Serge Wesley**.
