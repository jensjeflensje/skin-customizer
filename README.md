# Skin Customizer
A Spigot plugin/toolkit to create interactable, realtime skin customization interfaces inside Minecraft itself.
It features a variety of different components (hair styles, eyes, shirts...) and colors to work with.

## Dependencies
This project depends on [Citizens](https://www.spigotmc.org/resources/citizens.13811/) to create the skin preview, and [SkinsRestorer](https://skinsrestorer.net/) to actually apply the skin to the player.
Under the hood, the project uses [MineSkin](https://mineskin.org/) to upload the generated skin textures to Mojang and [jens.skin](https://jens.skin) (my project) to render a collection of chosen options to an actual skin.

## Structure
The project is focused around the [`SkinCustomizer`](src/main/java/dev/jensderuiter/skinCustomizer/customizer/SkinCustomizer.java) class, which can be instantiated with a Bukkit location to summon the customizer.
The customizer consists of:
- The skin preview using a Citizens NPC ([`SkinPreview`](src/main/java/dev/jensderuiter/skinCustomizer/customizer/preview/SkinPreview.java))
- The UI to customize the skin
  - Scrolling buttons to scroll through components for a specific category (e.g. hair styles)
  - Color options for each category
- The button to apply the skin

### UI elements (buttons)
The UI is based on display entities containing skulls, with an interaction entity acting as a hitbox for the button.
Every other element (
[apply button](src/main/java/dev/jensderuiter/skinCustomizer/customizer/SkinCustomizer.java#L94),
[scrolling buttons](src/main/java/dev/jensderuiter/skinCustomizer/customizer/ui/ScrollingButtons.java),
[color button](src/main/java/dev/jensderuiter/skinCustomizer/customizer/ui/ColoredScrollingButtons.java)
)
is based on one or multiple instances of this base button, [`InteractableButton`](src/main/java/dev/jensderuiter/skinCustomizer/customizer/ui/base/InteractableButton.java).
