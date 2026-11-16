# Drop Rig Tracker 1.2.0

A read-only Fabric client mod for Minecraft 1.21.11. While a container screen is open, it scans visible item display names for `1` through `9` (Dropper 1) and `1.2` through `9.2` (Dropper 2), then displays both detected values and which is higher.

It does not rewrite item names, change rendered items, hide drops, alter packets, or claim to change the server's result. The display is a local comparison only; the server's actual outcome remains authoritative.

Build with Java 21 and Gradle using `gradle build`.
