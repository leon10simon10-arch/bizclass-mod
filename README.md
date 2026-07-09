# Business Class Seats — NeoForge 1.21.1 (NeoForge 21.1.228)

Adds a modular 1-2-1 business class suite row:

```
[WINDOW_LEFT] [AISLE_LEFT]   [POD_LEFT | POD_RIGHT]   [AISLE_RIGHT] [WINDOW_RIGHT]
   door -> left aisle              shared pod door         door -> right aisle
```

## Items / Blocks
- **Business Class Suite** (`bizclass:business_seat`) — one block, six positions selected via
  the `seat_type` blockstate property (`window_left`, `aisle_left`, `pod_left`, `pod_right`,
  `aisle_right`, `window_right`). Every suite has a working sliding door, lie-flat recline, and
  seats one player.
- **IFE Screen** (`bizclass:ife_screen`) — decorative interactive block; right-click opens a
  simple in-flight entertainment menu screen.
- **Business Row Placer** (`bizclass:business_row_placer`) — right-click a floor block to lay
  down the *entire* six-block row in one go, correctly alternating and locked in (the two pod
  blocks are automatically linked so their door opens/closes together). This is the easiest way
  to get a perfectly aligned row; hand-placing individual suite blocks also auto-alternates
  against existing neighbours, but the row placer guarantees a perfect result every time.

## Controls
- **Right-click an empty suite** → sit down (door auto-opens if it was closed).
- **Shift + right-click an empty suite** → toggle the door open/closed without sitting.
- **Shift + right-click while seated** → toggle lie-flat recline.
- **Dismount (sneak / jump off)** → stands up and clears the seat automatically.
- **Right-click an IFE Screen** → opens the entertainment menu.

## Building
This project targets Minecraft 1.21.1 / NeoForge 21.1.228 / Java 21, using the standard
NeoForge MDK layout (Gradle + `net.neoforged.gradle.userdev`).

```
./gradlew build          # build the mod jar
./gradlew runClient      # test in a dev client
```

The Gradle wrapper isn't bundled in this archive (dev sandbox has no internet access to fetch
it) — run `gradle wrapper` once with a local Gradle install, or open the folder directly in
IntelliJ/VS Code with the NeoForge MDK plugin, which will bootstrap the wrapper for you.

## Notes / things worth polishing further
- Textures are simple placeholder cube textures generated for this scaffold (navy/cream/amber
  quilted look) — swap the PNGs in `src/main/resources/assets/bizclass/textures/block` for real
  art, and/or replace the `block/orientable`-based models with proper multi-element JSON models
  (or a custom model loader) for a real seat silhouette instead of a plain cube.
- The row placer currently assumes 6 clear blocks in a straight line starting from the clicked
  face; it will refuse to place (with a chat message) if anything is in the way.
- This code was written against the known 1.21.1 / NeoForge 21.1.228 API but has **not** been
  compiled in this sandbox (no network access to the NeoForge Maven here) — do a
  `./gradlew build` locally and fix any small API-signature drift before relying on it.
