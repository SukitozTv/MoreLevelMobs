package me.flukky.morelevelmobs.managers;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import me.flukky.morelevelmobs.MoreLevelMobs;

public class PresetMobs {
    private MoreLevelMobs plugin;
    private final Random random = new Random();

    public PresetMobs(MoreLevelMobs plugin) {
        this.plugin = plugin;
    }

    public void applyPreset(Creature monster, int level, String presetName) {
        double healthModifier = plugin.getSetting().getDouble("presets." + presetName + ".settings.attribute-modifier.max-health", 5.0);
        double attackModifier = plugin.getSetting().getDouble("presets." + presetName + ".settings.attribute-modifier.attack-damage", 2.25);
        double baseMovementSpeed = plugin.getSetting().getDouble("presets." + presetName + ".settings.attribute-modifier.movement-speed", 0.15);

        AttributeInstance healthAttr = monster.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        AttributeInstance speedAttr = monster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        AttributeInstance attackAttr = monster.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
    
        String levelColor = plugin.getMobType().determineLevelColor(level);

        if (healthAttr != null) {
            healthAttr.setBaseValue(healthAttr.getBaseValue() + healthModifier + level);
            ((LivingEntity) monster).setHealth(healthAttr.getBaseValue()); 
        }

        double newSpeed;
        if (level == 1) {
            newSpeed = speedAttr.getBaseValue() + 0.05;
        } else {
            // ใช้ค่าที่หักออกเพื่อลดการเพิ่มขึ้นเมื่อระดับสูง
            double increment = 0.01; // เพิ่มขึ้นทีละ 0.01
            double maxIncrement = baseMovementSpeed; // ค่ามากสุดที่เพิ่มได้
            newSpeed = speedAttr.getBaseValue() + Math.min((level - 1) * increment, maxIncrement);
        }

        //speedAttr.getBaseValue() + baseMovementSpeed
        double newDamage = attackAttr.getBaseValue() + attackModifier * level;

        if (plugin.getConfig().getBoolean("debug.show-message")) {
            plugin.getLogger().info("newSpeed " + newSpeed);
            plugin.getLogger().info("newDamage " + newDamage);
        }
    
        String monsterTypeName = monster.getType().name(); 
        double healthValue = healthAttr != null ? healthAttr.getBaseValue() : 0;
        String entityName;

        if (plugin.getConfig().getBoolean("debug.show-speed")) {
            entityName = String.format(" %sLvl %d %s%s | %s❤: %.0f | %s⚡: %.2f ",
            ChatColor.valueOf(levelColor), // สีของ Level
            level,
            ChatColor.WHITE, // สีของชื่อมอนสเตอร์ (สีขาว)
            monsterTypeName, // ใช้ชื่อประเภทของมอนสเตอร์
            ChatColor.RED, // สีของ HP (สีแดง)
            healthValue, // ใช้ค่าชีวิต
            ChatColor.BLUE, // สีของความเร็ว (สีน้ำเงิน)
            newSpeed);
        } else {
            entityName = String.format("%sLvl %d %s%s | %s❤: %.0f",
            ChatColor.valueOf(levelColor), // สีของ Level
            level,
            ChatColor.WHITE, // สีของชื่อมอนสเตอร์ (สีขาว)
            monsterTypeName, // ใช้ชื่อประเภทของมอนสเตอร์
            ChatColor.RED, // สีของ HP (สีแดง)
            healthValue);
        }
        //String entityName = String.format("%sLvl %d %s%s | %s❤: %.0f",
    
        monster.setCustomName(entityName);
        monster.setCustomNameVisible(true);

        plugin.getMobType().equipEntityWithArmorAndWeapon(monster, level);
    
        monster.getPersistentDataContainer().set(new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER, level);
        
        if (speedAttr != null) speedAttr.setBaseValue(newSpeed);
        if (attackAttr != null) attackAttr.setBaseValue(newDamage);
        
    }            
}
