# Skin Customizer
A Spigot plugin/toolkit to create interactable, realtime skin customization interfaces inside Minecraft itself.
It features a variety of different components (hair styles, eyes, shirts...) and colors to work with.

## Demo
[![Demo video](https://img.youtube.com/vi/3xdoilfft1E/0.jpg)](https://www.youtube.com/watch?v=3xdoilfft1E)

## Dependencies
This project depends on [Citizens](https://www.spigotmc.org/resources/citizens.13811/) to create the skin preview, and [SkinsRestorer](https://skinsrestorer.net/) to actually apply the skin to the player.
Under the hood, the project uses [MineSkin](https://mineskin.org/) to upload the generated skin textures to Mojang and [jens.skin](https://jens.skin) (my project) to render a collection of chosen options to an actual skin.

## Getting started
To test the plugin, make sure you install all dependencies and run the `/customizer` command as an operator.
The customizer will spawn.

### Developer guide
Start using this project as a dependency by including it in your pom.xml like this:
```xml
<!-- Repository (if not already present) -->
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<!-- Dependency -->
<dependency>
    <groupId>com.github.jensjeflensje</groupId>
    <artifactId>skincustomizer</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Please refer to this [readme](src/main/java/dev/jensderuiter/skincustomizer/README.md) for the developer guide on creating customizers using Java code.

## Structure
The project is focused around the [`SkinCustomizer`](src/main/java/dev/jensderuiter/skincustomizer/customizer/SkinCustomizer.java) class, which can be instantiated with a Bukkit location to summon the customizer.
The customizer consists of:
- The skin preview using a Citizens NPC ([`SkinPreview`](src/main/java/dev/jensderuiter/skincustomizer/customizer/preview/SkinPreview.java))
- The UI to customize the skin
  - Scrolling buttons to scroll through components for a specific category (e.g. hair styles)
  - Color options for each category
- The button to apply the skin

### UI elements (buttons)
The UI is based on display entities containing skulls, with an interaction entity acting as a hitbox for the button.
Every other element (
[apply button](src/main/java/dev/jensderuiter/skincustomizer/customizer/SkinCustomizer.java#L94),
[scrolling buttons](src/main/java/dev/jensderuiter/skincustomizer/customizer/ui/ScrollingButtons.java),
[color button](src/main/java/dev/jensderuiter/skincustomizer/customizer/ui/ColoredScrollingButtons.java)
)
is based on one or multiple instances of this base button, [`InteractableButton`](src/main/java/dev/jensderuiter/skincustomizer/customizer/ui/base/InteractableButton.java).

### Customization
Most element of the skin customizer are customizable in the [`config.yml`](src/main/resources/config.yml) file,
like the different categories,
options within that category, and color options.
These options come together to form the [`SkinCustomizerOptions`](src/main/java/dev/jensderuiter/skincustomizer/customizer/option/SkinCustomizerOptions.java)
object, which can also be replaced by a custom one and given to `SkinCustomizer`'s constructor.

## Caveats
This system of constantly uploading new skins to Mojang can be quite slow.
The plugin tries to relieve this by caching the skin using the combination of components as a cache key,
but this doesn't apply to newly made combinations.