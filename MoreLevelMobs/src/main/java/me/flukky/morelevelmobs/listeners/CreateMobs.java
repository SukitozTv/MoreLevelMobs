package me.flukky.morelevelmobs.listeners;

import java.util.List;
import java.util.Random;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import me.flukky.morelevelmobs.MoreLevelMobs;

public class CreateMobs implements Listener {
    private MoreLevelMobs plugin;
    private final Random random = new Random();
    

    public CreateMobs(MoreLevelMobs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Monster) {
            double spawnChance = plugin.getConfig().getDouble("spawnChance");
            double chance = random.nextDouble() * 100;
            if (chance < spawnChance) {
                int minLevel = plugin.getConfig().getInt("minLevel", 1);
                int maxLevel = plugin.getConfig().getInt("maxLevel", 50);
                //int level = random.nextInt(maxLevel - minLevel + 1) + minLevel;
                int level = (int) (Math.pow(random.nextDouble(), plugin.getConfig().getInt("spawnBalance")) * (maxLevel - minLevel)) + minLevel;

                if (plugin.getConfig().getBoolean("debug.show-message")) {
                    plugin.getLogger().info("minLevel " + minLevel);
                    plugin.getLogger().info("maxLevel " + maxLevel);
                    plugin.getLogger().info("level " + level);
                }

                Creature monster = (Creature) event.getEntity();
                

                // ดึงพรีเซ็ตจาก default-rule
                List<String> presets = plugin.getConfig().getStringList("default-rule.use-preset");
                if (!presets.isEmpty()) {
                    if (plugin.getMobType().isMonsterType(monster.getType())) {
                        String presetName = presets.get(0); // เลือกพรีเซ็ตแรกที่พบ
                        plugin.getPreset().applyPreset(monster, level, presetName);
                    }
                } else {
                    if (plugin.getConfig().getBoolean("debug.show-message")) {
                        plugin.getLogger().warning("No preset found in default-rule.use-preset");
                    }
                }

                if (plugin.getConfig().getBoolean("debug.show-message")) {
                    String location = "X: " + monster.getLocation().getBlockX() +
                            ", Y: " + monster.getLocation().getBlockY() +
                            ", Z: " + monster.getLocation().getBlockZ();
                    plugin.getLogger().info("Spawned monster with level " + level + " at " + location + " with spawn chance: " + chance + "%");
                }
            }
        }
    }
}