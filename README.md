# 🎮 LobbyPlus

![Minecraft](https://img.shields.io/badge/Minecraft-1.20.6%20%E2%86%92%201.21%2B-brightgreen?style=for-the-badge)
![Spigot/Paper](https://img.shields.io/badge/Spigot%2FPaper-Supported-blue?style=for-the-badge)
[![Build](https://github.com/OWNER/REPO/actions/workflows/maven.yml/badge.svg)](https://github.com/OWNER/REPO/actions/workflows/maven.yml)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

**LobbyPlus** es un plugin de lobby profesional para Minecraft Java: menús configurables, múltiples lobbies, lobby por defecto desde `config.yml`, broadcasts, anti-drop/anti-move del ítem de menú y restricción por mundos.

---

## ✨ Características

- Múltiples lobbies (`/setspawn`, `/spawn`, `/lobby`).
- **Lobby por defecto** configurable (`default-lobby`).
- Ítem de menú (con PDC) solo en mundos de lobby.
- GUI configurable: `WARP`, `COMMAND`, `MESSAGE`.
- Anti-drop/move del ítem en mundos de lobby.
- Broadcasts automáticos.
- Probado en **Spigot/Paper 1.20.6 (Java 17)** y **1.21+ (Java 21)**.

---

## 📦 Instalación

1. Copia el `.jar` en `plugins/`.
2. Inicia el servidor (Java 17 para 1.20.6; Java 21 para 1.21+).
3. Ajusta `plugins/LobbyPlus/config.yml` y `lobbies.yml`.

---

## ⚙️ Configuración básica

```yml
# Lobby por defecto (join, /spawn sin args)
default-lobby: mainlobby

lobby:
  worlds: ["world"]  # mundos donde se entrega & mantiene el ítem

join:
  giveItem: true
  itemSlot: 4
  teleportToDefault: true

menu:
  title: "&8Selecciona un destino"
  size: 27
  closeOnClick: true
  items:
    - slot: 10
      material: COMPASS
      name: "&aMain Lobby"
      lore: ["&7Ir al lobby principal"]
      action: WARP
      value: mainlobby
```

---

## ⌨️ Comandos

| Comando                 | Descripción                                   | Permiso              |
|-------------------------|-----------------------------------------------|----------------------|
| `/lobbyhelp`            | Muestra ayuda                                 | `lobbyplus.help`     |
| `/setspawn [lobby]`     | Guarda tu posición como spawn de un lobby     | `lobbyplus.setspawn` |
| `/spawn [lobby]`        | Teleporta a un lobby (o al default sin args)  | `lobbyplus.spawn`    |
| `/lobby set <nombre>`   | Crea/actualiza un lobby                       | `lobbyplus.admin`    |
| `/lobby delete <nombre>`| Elimina un lobby                              | `lobbyplus.admin`    |
| `/lobby list`           | Lista lobbies                                 | `lobbyplus.admin`    |
| `/lobby reload`         | Recarga configuración y lobbies               | `lobbyplus.admin`    |
| `/lobby openmenu`       | Abre el menú                                  | `lobbyplus.admin`    |

---

## 🔑 Permisos

- `lobbyplus.*` (todos)
- `lobbyplus.help`, `lobbyplus.spawn`, `lobbyplus.setspawn`, `lobbyplus.admin`  
> Incluye compat con `lobbycore.*` si tu setup antiguo lo usa.
---

## 📄 Licencia
MIT
