package dev.jensderuiter.skinCustomizer.customizer.ui;


import dev.jensderuiter.skinCustomizer.Util;
import dev.jensderuiter.skinCustomizer.customizer.ui.base.InteractableButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.type.ChestMenu;

import java.util.List;
import java.util.function.Consumer;

public class ColoredScrollingButtons<T> extends ScrollingButtons<T> {

    @AllArgsConstructor
    @Getter
    public static class ColorOption {
        private Material icon;
        private String[] colors;
    }

    public static final List<ColorOption> EYE_OPTIONS = List.of(
            new ColorOption(Material.BROWN_WOOL, new String[] {"#7D3900"}),
            new ColorOption(Material.LIME_WOOL,  new String[] {"#86BC00"}),
            new ColorOption(Material.BLUE_WOOL,  new String[] {"#5C9DCE"})
    );

    public static final List<ColorOption> SHIRT_OPTIONS = List.of(
            new ColorOption(Material.WHITE_WOOL, new String[] {"#FFFFFF", "#DCE2E8", "#BAC1CB", "#9396A6"}),
            new ColorOption(Material.LIGHT_GRAY_WOOL,  new String[] {"#B1B6B7", "#9DA3A4", "#858B8E", "#676D73"}),
            new ColorOption(Material.BLUE_WOOL,  new String[] {"#5E7D9A", "#526B8C", "#46567A", "#3A436C"}),
            new ColorOption(Material.GREEN_WOOL,  new String[] {"#56B528", "#1E9A2B", "#147136", "#0C4F32"}),
            new ColorOption(Material.RED_WOOL,  new String[] {"#CA4738", "#B2292D", "#871A2A", "#651128"})
    );

    public static final List<ColorOption> HAIR_OPTIONS = List.of(
            new ColorOption(Material.RED_WOOL, new String[] {"#FF7A43", "#E25A41", "#BC3E30", "#9A2A2A"}), // ginger
            new ColorOption(Material.BLACK_WOOL, new String[] {"#2E2E3D", "#211F29", "#16141B", "#0D0C12"}),
            new ColorOption(Material.YELLOW_WOOL, new String[] {"#F5C168", "#E0A85B", "#C68848", "#A46938"}),
            new ColorOption(Material.BROWN_WOOL, new String[] {"#76482A", "#5E3621", "#4B2717", "#361A0F"}),
            new ColorOption(Material.GRAY_WOOL, new String[] {"#4D565F", "#3B4048", "#2A2D34", "#1E2127"}),
            new ColorOption(Material.LIGHT_GRAY_WOOL, new String[] {"#EBE2D9", "#D3C8BF", "#B8ACA3", "#988E85"}),
            new ColorOption(Material.LIGHT_BLUE_WOOL, new String[] {"#D9F0F1", "#BFDBE2", "#A4C2D1", "#8EAAC5"})
    );

    public static final List<ColorOption> PANT_OPTIONS = List.of(
            new ColorOption(Material.BROWN_WOOL, new String[] {"#6C4D39", "#523728", "#3C221B", "#341A15", "#291611"}),
            new ColorOption(Material.STRIPPED_SPRUCE_WOOD, new String[] {"#A3774F", "#81583B", "#633D2C", "#512F24", "#45271E"}),
            new ColorOption(Material.GRAY_WOOL, new String[] {"#2D2C36", "#27262D", "#1F1F25", "#1A1B1F", "#16161D"}),
            new ColorOption(Material.LIGHT_GRAY_WOOL, new String[] {"#887E91", "#6D6975", "#524E57", "#403F46", "#3B3741"}),
            new ColorOption(Material.PURPLE_WOOL, new String[] {"#5B355A", "#462948", "#331E37", "#2D1A31", "#26162B"})
    );

    public static final List<ColorOption> SHOE_OPTIONS = List.of(
            new ColorOption(Material.BROWN_WOOL, new String[] {"#5F3D2D", "#462E26", "#34231D", "#2C1C17"}),
            new ColorOption(Material.BLACK_WOOL, new String[] {"#201F26", "#15151A", "#0D0D10", "#08080B"}),
            new ColorOption(Material.GRAY_WOOL, new String[] {"#2F2E35", "#242429", "#1C1C1F", "#17171A"}),
            new ColorOption(Material.LIGHT_BLUE_WOOL, new String[] {"#88A4A8", "#799195", "#96B4B9", "#7EA8A8"})
    );

    private Consumer<String[]> colorCallback;
    private List<ColorOption> options;

    public ColoredScrollingButtons(
            Location location,
            double y,
            List<T> values,
            Consumer<T> callback,
            Consumer<String[]> colorCallback,
            List<ColorOption> options
    ) {
        super(location, y, values, callback);
        this.colorCallback = colorCallback;
        this.options = options;

        this.buttons.add(
            new InteractableButton(
                    getOffsetLocation(1.1),
                    Util.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTkxNGQyZDYzNDE4MGU3MTJhZDliYzI1MmI0MjU1YWM3MGJiOWI4ZDQ0OTY2Y2ZhZmY5NzNmZWExYWRjYzlmMCJ9fX0="),
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
            menu.getSlot(i).setItem(new ItemStack(option.icon));
            menu.getSlot(i).setClickHandler(((p, c) -> colorCallback.accept(option.colors)));
            i++;
        }
        menu.open(player);
    }
}
