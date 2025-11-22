package org.mesmeralis.revelationPVP;


import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mesmeralis.revelationPVP.Utils.ColourUtils;

import java.util.*;

public enum Roles {
    CITIZEN(Material.AIR, ColourUtils.colour("&fCitizen"), ColourUtils.colour("&fC"), "This person has no powers.") {
        @Override
        public void applyJoinPerks(Player player) {
            // Nothing to do. Citizens suck.
        }

        @Override
        public double applyCombatPerks(Player attacker, Player victim, double baseDamage) {
            // ...
            return baseDamage;
        }

        @Override
        public double applyCombatPerks(Player attacker, Entity victim, double baseDamage) {
            // Still boring.
            return baseDamage;
        }

        @Override
        public double applyCombatProtections(Entity attacker, Player victim, double baseDamage) {
            return baseDamage;
        }
    },
    ASSASSIN(Material.BOW, ColourUtils.colour("#2A2A2A&lASSASSIN"),
            ColourUtils.colour("#2A2A2A&lA"),
            "&aIssues 5s of poison when fighting players. &7(30s cooldown)") {
        private final List<EntityType> protectedEntities = Arrays.asList(
                EntityType.ZOMBIE, EntityType.PHANTOM, EntityType.SPIDER,
                EntityType.PIGLIN, EntityType.PIGLIN_BRUTE,
                EntityType.RAVAGER, EntityType.PILLAGER, EntityType.SKELETON
        );

        @Override
        public void applyJoinPerks(Player player) {
        }

        @Override
        public double applyCombatPerks(Player attacker, Player victim, double baseDamage) {
            if (!victim.hasPotionEffect(PotionEffectType.LEVITATION)) {
                victim.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 1));
                victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1));
            }
            return baseDamage;
        }

        @Override
        public double applyCombatPerks(Player attacker, Entity victim, double baseDamage) {
            return baseDamage;
        }

        @Override
        public double applyCombatProtections(Entity attacker, Player victim, double baseDamage) {
            if (this.protectedEntities.contains(attacker.getType())) {
                return baseDamage / 2.0d;
            }
            return baseDamage;
        }
    },
    HYPNOTIST(Material.RECOVERY_COMPASS, ColourUtils.colour("#38C7B6&lHYPNOTIST"),
            ColourUtils.colour("#38C7B6&lH"),
            "&aIssues 10s of nausea when fighting players. &7(1m cooldown)") {
        private final Map<UUID, Long> abilityCoolDowns = new HashMap<>();

        @Override
        public void applyJoinPerks(Player player) {
            player.setWalkSpeed(0.3f);
        }

        @Override
        public double applyCombatPerks(Player attacker, Player victim, double baseDamage) {
            if (!attacker.hasPotionEffect(PotionEffectType.SPEED) && (System.currentTimeMillis() - this.abilityCoolDowns.getOrDefault(attacker.getUniqueId(), 0L)) > 60000L) {
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 2, false, false));
                this.abilityCoolDowns.put(attacker.getUniqueId(), System.currentTimeMillis());
            }
            return baseDamage;
        }

        @Override
        public double applyCombatPerks(Player attacker, Entity victim, double baseDamage) {
            return baseDamage;
        }

        @Override
        public double applyCombatProtections(Entity attacker, Player victim, double baseDamage) {
            return baseDamage;
        }
    },
    VIKING(Material.SHIELD, ColourUtils.colour("#B4602B&lVIKING"),
            ColourUtils.colour("#B4602B&lV"),
            "&aGets strength for 10 seconds. &7(60s cooldown)") {
        @Override
        public void applyJoinPerks(Player player) {
        }

        @Override
        public double applyCombatPerks(Player attacker, Player victim, double baseDamage) {
            if (!victim.hasPotionEffect(PotionEffectType.WITHER)) {
                victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 150, 1));
            }
            return baseDamage;
        }

        @Override
        public double applyCombatPerks(Player attacker, Entity victim, double baseDamage) {
            return baseDamage;
        }

        @Override
        public double applyCombatProtections(Entity attacker, Player victim, double baseDamage) {
            return baseDamage;
        }
    },
    WIZARD(Material.DIAMOND_AXE, ColourUtils.colour("#6A4DFF&lWIZARD"),
            ColourUtils.colour("#6A4DFF&lW"),
            "&aIssues 1.5x damage when using axes.",
            "&aDecreased hit delay when using axes.",
            "&c-0.3x walk speed.") {
        private final List<Material> axeMaterials = Arrays.asList(
                Material.WOODEN_AXE, Material.STONE_AXE,
                Material.GOLDEN_AXE, Material.IRON_AXE,
                Material.DIAMOND_AXE, Material.NETHERITE_AXE
        );

        @Override
        public void applyJoinPerks(Player player) {
        }

        @Override
        public double applyCombatPerks(Player attacker, Player victim, double baseDamage) {
            if (this.axeMaterials.contains(attacker.getInventory().getItemInMainHand().getType())) {
                return baseDamage * 1.5;
            }
            return baseDamage;
        }

        @Override
        public double applyCombatPerks(Player attacker, Entity victim, double baseDamage) {
            return baseDamage;
        }

        @Override
        public double applyCombatProtections(Entity attacker, Player victim, double baseDamage) {
            if (attacker instanceof Witch) {
                return -1.0d; // -1 cancels the damage event.
            }
            return baseDamage;
        }
    };

    private final String[] description;
    private final String shortPrefix;
    private final String prefix;
    private final Material icon;

    Roles(Material icon, String prefix, String shortPrefix, String... description) {
        this.description = description;
        this.shortPrefix = shortPrefix;
        this.prefix = prefix;
        this.icon = icon;
    }

    // Perks applied to a player when they join the server.
    public abstract void applyJoinPerks(Player player);

    // Called in PvP situations.
    public abstract double applyCombatPerks(Player attacker, Player victim, double baseDamage);

    // Called in PvE situations.
    public abstract double applyCombatPerks(Player attacker, Entity victim, double baseDamage);

    public abstract double applyCombatProtections(Entity attacker, Player victim, double baseDamage);

    public String[] getDescription() {
        return this.description;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getPrefixShort() {
        return this.shortPrefix;
    }

    public Material getIcon() {
        return this.icon;
    }

}
