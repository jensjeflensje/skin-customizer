package dev.jensderuiter.skincustomizer.listener;

import dev.jensderuiter.skincustomizer.customizer.ui.base.InteractableButton;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class InteractionClickListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if(!(event.getRightClicked() instanceof Interaction)) return;
        UUID uuid = event.getRightClicked().getUniqueId();
        InteractableButton button = InteractableButton.getInstances().get(uuid);
        if (button != null) button.click(event.getPlayer());
    }

}
