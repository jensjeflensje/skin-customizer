package dev.jensderuiter.skincustomizer.customizer.ui;


import dev.jensderuiter.skincustomizer.CustomizerPlugin;
import dev.jensderuiter.skincustomizer.Util;
import dev.jensderuiter.skincustomizer.customizer.option.ColorOption;
import dev.jensderuiter.skincustomizer.customizer.ui.base.InteractableButton;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.type.ChestMenu;

import java.util.List;
import java.util.function.Consumer;

public class ColoredScrollingButtons<T> extends ScrollingButtons<T> {


    private Consumer<List<String>> colorCallback;
    private List<ColorOption> options;

    public ColoredScrollingButtons(
            Location location,
            double y,
            List<T> values,
            Consumer<T> callback,
            Consumer<List<String>> colorCallback,
            List<ColorOption> options
    ) {
        super(location, y, values, callback);
        this.colorCallback = colorCallback;
        this.options = options;

        this.buttons.add(
            new InteractableButton(
                    getOffsetLocation(1.1),
                    Util.getSkull(CustomizerPlugin.getTextureConfig().getString("color")),
                    this::openColorMenu
            )
        );
    }

    private void openColorMenu(Player player) {
        Menu menu = ChestMenu.builder(1)
                .title("Color options")
                .redraw(true)
                .build();
        int i = 0;
        for (ColorOption option : options) {
            menu.getSlot(i).setItem(new ItemStack(option.getIcon()));
            menu.getSlot(i).setClickHandler(((p, c) -> colorCallback.accept(option.getColors())));
            i++;
        }
        menu.open(player);
    }
}
