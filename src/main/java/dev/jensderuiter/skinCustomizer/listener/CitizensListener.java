package dev.jensderuiter.skinCustomizer.listener;

import dev.jensderuiter.skinCustomizer.CustomizerPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class CitizensListener implements Listener {

    @EventHandler
    public void onCitizensLoad(CitizensEnableEvent event) {
        CustomizerPlugin.setCitizensEnabled(true);
        List<NPC> toRemove = new ArrayList<>();
        CitizensAPI.getNPCRegistry().forEach(npc -> {
            if (npc.getName().equals("skin_customizer")) {
                toRemove.add(npc);
            }
        });
        toRemove.forEach(NPC::destroy);

        // they can only be spawned once citizens is activated
        CustomizerPlugin.getCustomizers().forEach(customizer -> {
            if (customizer.getPreview() == null) {
                customizer.spawnPreview();
            }
        });
    }

}
