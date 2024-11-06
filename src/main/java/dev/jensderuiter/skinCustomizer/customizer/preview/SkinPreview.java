package dev.jensderuiter.skinCustomizer.customizer.preview;

import dev.jensderuiter.skinCustomizer.customizer.SkinCustomizer;
import dev.jensderuiter.skinCustomizer.customizer.TextureData;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class SkinPreview {

    private SkinCustomizer skinCustomizer;
    private NPC npc;
    private Location location;

    public SkinPreview(SkinCustomizer skinCustomizer) {
        this.skinCustomizer = skinCustomizer;
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "fdafafa3teageawfg");
        npc.data().setPersistent(NPC.Metadata.NAMEPLATE_VISIBLE, false);
    }

    public void summon(Location location) {
        this.location = location;
        Bukkit.getLogger().info("Spawn npc");
        npc.spawn(this.location);
    }

    public void setSkinData(TextureData skinData) {
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(
                skinData.getHash(),
                skinData.getSignature(),
                skinData.getValue()
        );
    }

}
