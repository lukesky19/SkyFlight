# SkyFlight
## Description
* SkyFlight allows players to fly for a limited amount of time or infinite time.

## Features
* Allows players to fly for a limited amount of time or infinite time.
* Supports restricting flight for entire worlds.
* Flight is limited to islands in BentoBox worlds for those without the bypass permission.
* Adds a WorldGuard flag to control flight.
* Ability to import from my fork of the [BentoBox IslandFly addon](https://github.com/lukesky19/IslandFly/tree/extra).

## Dependencies
* [SkyLib](https://github.com/lukesky19/SkyLib)

## Soft Dependencies
* BentoBox
* WorldGuard

## Commands
* /skyflight - The base command and the command to enable or disable flight. NOTE: Infinite flight takes priority over timed flight.
    * Alias: /fly
* /skyflight help - View the plugin's help message.
* /skyflight reload - Reload the plugin.
* /skyflight import - Import flight time from my fork of the [BentoBox IslandFly addon](https://github.com/lukesky19/IslandFly/tree/extra).
  * NOTE: This should only be run once as it will overwrite any existing player data.
* /skyflight time - Get how much flight time you have.
* /skyflight time add <player> <time in seconds> - Add the time to the player's flight time. 
* /skyflight time remove <player> <time in seconds> - Remove the time from the player's flight time.
* /skyflight time set <player> <time in seconds> - Set the player's flight time.
* /skyflight time get <player> - Get the player's flight time.
* /skyflight info \[player] - View info on your player or a player.
  * This is intended to be a debug command only.

## Permissions
* `skyflight.commands.skyflight` - Base Command Permission
* `skyflight.commands.skyflight.help` - The permission to use the `/skyflight help` command.
* `skyflight.commands.skyflight.reload` - The permission to use the `/skyflight reload` command.
* `skyflight.commands.skyflight.import` - The permission to use the `/skyflight import` command.
* `skyflight.commands.skyflight.time` - The permission to use the `/skyflight time` command.
* `skyflight.commands.skyflight.time.add` - The permission to use the `/skyflight time add` command.
* `skyflight.commands.skyflight.time.remove` - The permission to use the `/skyflight time remove` command.
* `skyflight.commands.skyflight.time.set` - The permission to use the `/skyflight time set` command.
* `skyflight.commands.skyflight.time.get` - The permission to use the `/skyflight time get` command.
* `skyflight.commands.skyflight.info` - The permission to use the `/skyflight info [player]` command.
* `skyflight.fly.infinite` - The permission to use infinite flight.
* `skyflight.fly.timed` - The permission to use timed flight.

## FAQ
Q: What versions does this plugin support?

A: 1.21.4, 1.21.5, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10, 1.21.11, 26.1, 26.1.1, and 26.1.2.
Note: Java 25 is required even on versions older than 26.1.

Q: I get the following error: "SkyFlight has been compiled by a more recent version of the Java Runtime
(class file version 69.0), this version of the Java Runtime only recognizes class file versions up to 65.0"

A: SkyFlight is compiled using Java 25 as part of it's support of 26.1 and beyond. SkyFlight still works on older version as long as Java 25 is used.

Q: Are there any plans to support any other versions?

A: I will always do my best to support the latest versions of the game. I will sometimes support other versions until I no longer use them.

Q: Does this work on Spigot and Paper?

A: Only Paper is supported. There are no plans to support any other server software (i.e., Spigot, Folia).

## Issues, Bugs, or Suggestions
* Please create a new [GitHub Issue](https://github.com/lukesky19/SkyFlight/issues) with your issue, bug, or suggestion.
* If an issue or bug, please post any relevant logs containing errors related to SkyPrestige and your configuration files.
* I will attempt to solve any issues or implement features to the best of my ability.

## For Server Admins/Owners
* Download the plugin [SkyLib](https://github.com/lukesky19/SkyLib/releases).
* Download the plugin from the releases tab and add it to your server.

## Building
* Go to [SkyLib](https://github.com/lukesky19/SkyLib) and follow the "For Developers" instructions.
* Then run:
  ```./gradlew build```

### Why AGPL3?
I wanted a license that will keep my code open source. I believe in open source software and in-case this project goes unmaintained by me, I want it to live on through the work of others. And I want that work to remain open source to prevent a time when a fork can never be continued (i.e., closed-sourced and abandoned).