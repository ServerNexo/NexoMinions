package me.nexo.minions.data;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum MinionType {

    COBBLESTONE("Cobblestone Minion", Material.COBBLESTONE, "COBBLESTONE", "minion_bee_cobble", Skills.MINING),
    DIAMOND("Diamond Minion", Material.DIAMOND, "DIAMOND", "minion_bee_diamond", Skills.MINING),
    WHEAT("Wheat Minion", Material.WHEAT, "WHEAT", "minion_bee_wheat", Skills.FARMING),
    OAK_WOOD("Oak Minion", Material.OAK_LOG, "OAK_LOG", "minion_bee_oak", Skills.FORAGING);

    private final String displayName;
    private final Material targetMaterial;
    private final String auroraCollectionID;
    private final String nexoModelID;
    private final Skill auraSkill;

    MinionType(String displayName, Material targetMaterial, String auroraCollectionID, String nexoModelID, Skill auraSkill) {
        this.displayName = displayName;
        this.targetMaterial = targetMaterial;
        this.auroraCollectionID = auroraCollectionID;
        this.nexoModelID = nexoModelID;
        this.auraSkill = auraSkill;
    }
}