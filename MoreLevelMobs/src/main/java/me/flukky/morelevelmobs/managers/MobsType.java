package me.flukky.morelevelmobs.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.flukky.morelevelmobs.MoreLevelMobs;

public class MobsType {
    private MoreLevelMobs plugin;

    private String lowLevelColor;
    private String midLevelColor;
    private String highLevelColor;
    private String extremeLevelColor;
    private Random random = new Random();

    public MobsType(MoreLevelMobs plugin) {
        this.plugin = plugin;
    }

    public void loadColor() {
        FileConfiguration config = plugin.getConfig();
        lowLevelColor = config.getString("level-colors.low-level-color").toUpperCase();
        midLevelColor = config.getString("level-colors.mid-level-color").toUpperCase();
        highLevelColor = config.getString("level-colors.high-level-color").toUpperCase();
        extremeLevelColor = config.getString("level-colors.extreme-level-color").toUpperCase();
    }

    public boolean isMonsterType(EntityType type) {
        switch (type) {
            case ZOMBIE:
            case SKELETON:
            case CREEPER:
            case SPIDER:
            //case ENDERMAN:
            case WITCH:
            case GHAST:
            //case SLIME:
            case MAGMA_CUBE:
            case BLAZE:
            case WITHER_SKELETON:
            case STRAY:
            case HUSK:
            case PHANTOM:
            case DROWNED:
            case PILLAGER:
            case EVOKER:
            case VINDICATOR:
            case RAVAGER:
            case ILLUSIONER:
            case ZOMBIE_VILLAGER:
                return true;
            default:
                return false;
        }
    }

    public String determineLevelColor(int level) {
        if (level <= 10) {
            return lowLevelColor;
        } else if (level <= 20) {
            return midLevelColor;
        } else if (level <= 30) {
            return highLevelColor;
        } else {
            return extremeLevelColor;
        }
    }

    public void equipEntityWithArmorAndWeapon(LivingEntity entity, int level) {
        if (entity instanceof Monster) {
            // ตั้งค่าชุดเกราะ
            if (level >= 5) {
                entity.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
            }else if (level >= 10) {
                entity.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
            }else if (level >= 15) {
                entity.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
                entity.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            }else if (level >= 20) {
                entity.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                entity.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
            }
            
            if (level >= 5) {
                entity.getEquipment().setItemInHand(new ItemStack(Material.GOLDEN_AXE));
            }
        }
    }

    public void dropItems(LivingEntity monster, int level, Location location) {
        FileConfiguration dropConfig = plugin.getDrops();
        double baseChance = dropConfig.getDouble("drop-chance.base", 5.0);
        double increasePerLevel = dropConfig.getDouble("drop-chance.increase-per-level", 2.5);
        double dropChance = baseChance + (level * increasePerLevel);

        // ตรวจสอบโอกาสดรอปไอเทม
        if (random.nextDouble() * 100 < dropChance) {
            List<ItemStack> items = getDropItemsForLevel(level, location);
            for (ItemStack item : items) {
                monster.getWorld().dropItemNaturally(monster.getLocation(), item);
            }
        }
    }

    public List<ItemStack> getDropItemsForLevel(int level, Location location) {
        List<ItemStack> dropItems = new ArrayList<>();
        Random random = new Random();
        String levelKey;
    
        // เลือกหมวดของระดับตาม level
        if (level <= 10) {
            levelKey = "level-1-10";
        } else if (level <= 20) {
            levelKey = "level-11-20";
        } else if (level <= 30) {
            levelKey = "level-21-30";
        } else {
            levelKey = "level-31+";
        }
    
        ConfigurationSection levelSection = plugin.getDrops().getConfigurationSection("items." + levelKey);
        if (levelSection != null) {
            for (String itemKey : levelSection.getKeys(false)) {
                String materialName = levelSection.getString(itemKey + ".item");
                String materialNameDisplay = levelSection.getString(itemKey + ".name");
                int amount = levelSection.getInt(itemKey + ".amount", 1);
                int chance = levelSection.getInt(itemKey + ".chance", 100);
                int customModelData = levelSection.getInt(itemKey + ".custom_model_data", 0);
                int xp = levelSection.getInt(itemKey + ".xp", 0); // อ่านค่า xp จาก config
    
                if (materialName != null && Material.matchMaterial(materialName) != null) {
                    Material material = Material.matchMaterial(materialName);
    
                    // ตรวจสอบโอกาสดรอป
                    if (random.nextInt(100) < chance) {
                        ItemStack item = new ItemStack(material, amount);
                        ItemMeta meta = item.getItemMeta();
    
                        // ตั้งค่าชื่อและคำบรรยาย
                        if (itemKey.startsWith("gem+")) {
                            if (meta != null && customModelData > 0) {
                                String gemName = ChatColor.DARK_PURPLE + "✦ " + ChatColor.GOLD + materialNameDisplay + ChatColor.DARK_PURPLE + " ✦";
                                meta.setDisplayName(gemName);
                                List<String> lore = new ArrayList<>();
                                lore.add(ChatColor.GRAY + "─────────────"); // Decorative divider
                                lore.add(ChatColor.DARK_AQUA + "✦ Bestow power upon your weapons and armor ✦");
                                lore.add(ChatColor.DARK_AQUA + "The chance to upgrade shall rise,");
                                lore.add(ChatColor.RED + "but beware! Failure may lurk in the shadows...");
                                lore.add("");
                                lore.add(ChatColor.LIGHT_PURPLE + "Hidden powers will be unleashed");
                                lore.add(ChatColor.LIGHT_PURPLE + "when this gem is used for enhancement.");
                                lore.add(ChatColor.LIGHT_PURPLE + "Forge an equipment that stands the test of time!");
                                lore.add(ChatColor.GRAY + "─────────────");
                                meta.setLore(lore);
                                meta.setCustomModelData(customModelData);
                            }
                        }
    
                        item.setItemMeta(meta);
                        dropItems.add(item);
                    }
                }
            }
        }
    
        // ตั้งค่า EXP คงที่ตามระดับ
        int xpDrop = 0;
        switch (level) {
            case 1: case 2: case 3: case 4: case 5:
                xpDrop = 5; // ดรอป 5 EXP สำหรับระดับ 1-5
                break;
            case 6: case 7: case 8: case 9: case 10:
                xpDrop = 10; // ดรอป 10 EXP สำหรับระดับ 6-10
                break;
            case 11: case 12: case 13: case 14: case 15:
                xpDrop = 15; // ดรอป 15 EXP สำหรับระดับ 11-15
                break;
            default:
                xpDrop = 20; // ดรอป 20 EXP สำหรับระดับที่สูงกว่า 15
                break;
        }

        // ดรอป EXP ลงบนพื้น
        if (xpDrop > 0 && location != null) {
            ExperienceOrb orb = location.getWorld().spawn(location, ExperienceOrb.class);
            orb.setExperience(xpDrop);
            Bukkit.getLogger().info("ดรอป EXP " + xpDrop + " ลงบนพื้นที่ " + location.toString());
        }
    
        return dropItems;
    }
    
}
