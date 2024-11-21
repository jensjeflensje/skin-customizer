package dev.jensderuiter.skincustomizer.customizer.preview;

import dev.jensderuiter.skincustomizer.customizer.SkinCustomizer;
import dev.jensderuiter.skincustomizer.customizer.TextureData;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class SkinPreview {

    private SkinCustomizer skinCustomizer;
    private NPC npc;
    private Location location;

    public SkinPreview(SkinCustomizer skinCustomizer) {
        this.skinCustomizer = skinCustomizer;
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "skin_customizer");
        npc.data().setPersistent(NPC.Metadata.NAMEPLATE_VISIBLE, false);
    }

    public void summon(Location location) {
        this.location = location;
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
