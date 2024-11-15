# Minecraft Auto-Ban Plugin

This Minecraft plugin automatically bans players for performing configurable actions. Each action can have a custom ban reason, and details of banned players are saved in a separate `banlist.yml` file.

Compatible versions are 1.16.5 to 1.21.
## Features
- Ban players for specific actions such as:
  - Dropping items
  - Entering the Nether or End
  - Placing TNT
  - Spamming chat (sending more than 5 messages within 5 seconds)
- Customize ban reasons for each action in the `config.yml` file.
- Records banned players' information (MCID, UUID, ban date, IP, and ban reason) in `banlist.yml`.

## Installation
1. Compile the Java code into a `.jar` file.
2. Place the `.jar` file into your server's `plugins` folder.
3. Restart or reload your server.
4. A default `config.yml` file will be generated in the plugin’s folder. Customize it as needed.

## Configuration

### config.yml
Customize which actions trigger a ban and specify ban reasons in the `config.yml` file. Here’s an example:

```yaml
ban-commands:
  - "/example" # Commands that will trigger a ban.
  - "/test"

ban-on:
  item-drop: true           # Ban for dropping items
  nether-entry: true        # Ban for entering the Nether
  end-entry: true           # Ban for entering the End
  tnt-place: true           # Ban for placing TNT
  spam-chat: true           # Ban for sending more than 5 messages in 5 seconds

reason:
  item-drop: "You have been banned for dropping items!"
  nether-entry: "You have been banned for entering the Nether!"
  end-entry: "You have been banned for entering the End!"
  tnt-place: "You have been banned for placing TNT!"
  spam-chat: "You have been banned for spamming chat!"
  command: "You have been banned for using prohibited commands!"
```

- **ban-commands**: List of commands that will trigger a ban if used.
- **ban-on**: Enables or disables bans for specific actions.
- **reason**: Customizable ban messages for each action.

### banlist.yml
The plugin automatically generates and updates `banlist.yml` when players are banned. Each banned player’s details are stored under their UUID, including:
- **MCID** (Minecraft ID)
- **UUID** (Unique User ID)
- **Date** (Ban date and time)
- **IP** (Player’s IP address)
- **Reason** (Reason for the ban)

Example entry in `banlist.yml`:
```yaml
banned-players:
  <UUID>:
    MCID: "<PlayerName>"
    UUID: "<PlayerUUID>"
    Date: "2024-11-15 13:45:22"
    IP: "123.45.67.89"
    Reason: "You have been banned for placing TNT!"
```

## Event Details

| Event           | Configuration Key | Default Ban Reason                           |
|-----------------|-------------------|----------------------------------------------|
| Item Drop       | item-drop         | "You have been banned for dropping items!"   |
| Nether Entry    | nether-entry      | "You have been banned for entering the Nether!" |
| End Entry       | end-entry         | "You have been banned for entering the End!" |
| TNT Placement   | tnt-place         | "You have been banned for placing TNT!"      |
| Spam Chat       | spam-chat         | "You have been banned for spamming chat!"    |
| Command Usage   | command           | "You have been banned for using prohibited commands!" |

## License
This plugin is open-source and distributed under the MIT License. 
