package me.flukky.morelevelmobs.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import me.flukky.morelevelmobs.MoreLevelMobs;

public class CommandMobs implements CommandExecutor {
    private MoreLevelMobs plugin;

    public CommandMobs(MoreLevelMobs plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /morelevelmobs <reload|kill all|spawnmob <level> <type_monster>>");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                // Reload configuration
                plugin.reloadConfig();
                plugin.loadSettingsConfig();
                plugin.loadDropsConfig();
                sender.sendMessage("Configuration reloaded.");
                return true;

            case "kill":
                if (args.length == 2 && args[1].equalsIgnoreCase("all")) {
                    return killAllLevelMobs(sender);
                } else {
                    sender.sendMessage("Usage: /morelevelmobs kill all");
                    return false;
                }

            case "spawnmob":
                if (args.length == 3) {
                    try {
                        int level = Integer.parseInt(args[1]);
                        String monsterType = args[2].toUpperCase();
                        return spawnMob(sender, level, monsterType);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Level must be a number.");
                        return false;
                    }
                } else {
                    sender.sendMessage("Usage: /morelevelmobs spawnmob <level> <type_monster>");
                    return false;
                }

            default:
                sender.sendMessage("Usage: /morelevelmobs <reload|kill all|spawnmob <level> <type_monster>>");
                return false;
        }
    }

    private boolean killAllLevelMobs(CommandSender sender) {
        int killedCount = 0; // นับจำนวนมอนสเตอร์ที่ถูกฆ่า
        for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
            if (entity instanceof Creature) {
                if (isLevelMob((Creature) entity)) {
                    entity.remove(); // ฆ่ามอนสเตอร์
                    killedCount++;
                }
            }
        }
        sender.sendMessage(killedCount + " level monsters have been killed.");
        return true;
    }

    private boolean spawnMob(CommandSender sender, int level, String monsterType) {
        // สร้างมอนสเตอร์ตามประเภทที่ระบุ
        Entity entity = Bukkit.getWorlds().get(0).spawnEntity(Bukkit.getWorlds().get(0).getSpawnLocation(), 
                org.bukkit.entity.EntityType.valueOf(monsterType));
        
        if (entity instanceof Creature) {
            Creature monster = (Creature) entity;
            // ตั้งค่าระดับของมอนสเตอร์
            List<String> presets = plugin.getConfig().getStringList("default-rule.use-preset");
            String presetName = presets.get(0); // เลือกพรีเซ็ตแรกที่พบ
            monster.getPersistentDataContainer().set(new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER, level);
            plugin.getPreset().applyPreset(monster, level, presetName); // ใช้ preset สำหรับมอนสเตอร์

            sender.sendMessage(monsterType + " of level " + level + " has been spawned.");
            return true;
        } else {
            sender.sendMessage("Failed to spawn the monster. Check the type.");
            return false;
        }
    }

    private boolean isLevelMob(Creature creature) {
        return creature.getPersistentDataContainer().has(new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER);
    }

}
