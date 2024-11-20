package dev.jensderuiter.skinCustomizer.customizer.ui;

import dev.jensderuiter.skinCustomizer.CustomizerPlugin;
import dev.jensderuiter.skinCustomizer.Util;
import dev.jensderuiter.skinCustomizer.customizer.Destroyable;
import dev.jensderuiter.skinCustomizer.customizer.ui.base.InteractableButton;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScrollingButtons<T> implements Destroyable {

    private final List<T> values;
    private int index;

    private final Consumer<T> callback;
    private Location baseLocation;
    private double y;

    protected List<InteractableButton> buttons;

    public ScrollingButtons(Location location, double y, List<T> values, Consumer<T> callback) {
        this.values = values;
        this.index = 0;
        this.callback = callback;
        this.callback.accept(this.values.get(index));
        this.baseLocation = location;
        this.y = y;
        this.buttons = new ArrayList<>();

        this.buttons.add(
                new InteractableButton(
                        getOffsetLocation(0.8),
                        Util.getSkull(CustomizerPlugin.getTextureConfig().getString("next")),
                        this::next
                )
        );
        this.buttons.add(
                new InteractableButton(
                        getOffsetLocation(-0.8),
                        Util.getSkull(CustomizerPlugin.getTextureConfig().getString("previous")),
                        this::previous
                )
        );
    }

    private void applyCallback() {
        this.callback.accept(this.values.get(index));
    }

    private void next(Player player) {
        this.index++;
        if (this.index >= this.values.size()) {
            this.index = 0;
        }
        this.applyCallback();
    }

    private void previous(Player player) {
        this.index--;
        if (this.index < 0) {
            this.index = this.values.size() - 1;
        }
        this.applyCallback();
    }

    protected Location getOffsetLocation(double offset) {
        Location offsetLocation = this.baseLocation.clone();
        double sinus = Math.sin(baseLocation.getYaw() / 180 * Math.PI);
        double cosinus = Math.cos(baseLocation.getYaw() / 180 * Math.PI);
        double newX = offset * cosinus - 0 * sinus;
        double newZ = 0 * cosinus + offset * sinus;
        offsetLocation.setYaw(baseLocation.getYaw() - 180);
        return offsetLocation.add(newX, y, newZ);
    }

    public void destroy() {
        this.buttons.forEach(InteractableButton::destroy);
    }

}
