package dev.jensderuiter.skincustomizer.customizer.ui.base;

import dev.jensderuiter.skincustomizer.customizer.Destroyable;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class InteractableButton implements Destroyable {

    private static final double SCALE = 0.3d;

    @Getter
    private static final ConcurrentHashMap<UUID, InteractableButton> instances = new ConcurrentHashMap<>();

    private final Consumer<Player> callback;

    private ItemDisplay display;
    private Interaction interaction;

    public InteractableButton(Location location, ItemStack itemStack, Consumer<Player> callback) {
        this.callback = callback;

        display = (ItemDisplay) location.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY);
        display.setItemStack(itemStack);
        Transformation transformation = display.getTransformation();
        transformation.getScale().set(SCALE);
        display.setTransformation(transformation);

        Location interactLocation = location.clone().add(0, -SCALE/2, 0);
        interaction = (Interaction) location
                .getWorld()
                .spawnEntity(interactLocation, EntityType.INTERACTION);
        interaction.setInteractionHeight((float) SCALE);
        interaction.setInteractionWidth((float) SCALE);

        instances.put(interaction.getUniqueId(), this);
    }

    public void click(Player player) {
        this.callback.accept(player);
    }

    public void destroy() {
        this.display.remove();
        this.interaction.remove();
    }

    public void setItemStack(ItemStack itemStack) {
        display.setItemStack(itemStack);
    }

}
