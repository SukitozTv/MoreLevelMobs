package me.flukky.morelevelmobs.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

import me.flukky.morelevelmobs.MoreLevelMobs;

public class ListenerMobs implements Listener {
    private MoreLevelMobs plugin;

    public ListenerMobs(MoreLevelMobs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMonsterDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity monster = event.getEntity();
            Integer level = monster.getPersistentDataContainer().get(new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER);
            Location deathLocation = monster.getLocation();
            
            if (level != null) {
                plugin.getMobType().dropItems(monster, level, deathLocation);
            }
        }
    }

    @EventHandler
    public void onMonsterAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LivingEntity) {
            LivingEntity monster = (LivingEntity) event.getDamager();

            if (monster.getCustomName() != null && monster.getCustomName().contains("Lvl")) {
                Entity target = event.getEntity();
                
                double damage = monster.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue();
                
                event.setDamage(damage);

                if (plugin.getConfig().getBoolean("debug.show-message")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(ChatColor.RED + monster.getCustomName() + " ทำความเสียหาย: " + damage + " ไปยัง " + target.getType().name());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileDamage(EntityDamageByEntityEvent event) {
        // ตรวจสอบว่าเหตุการณ์เกิดจาก Projectile
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getCustomName() != null && projectile.getCustomName().contains("Lvl")) {
                Entity target = event.getEntity();

                // ตรวจสอบว่า Projectile ถูกยิงโดย LivingEntity เช่น Monster
                if (projectile.getShooter() instanceof LivingEntity) {
                    LivingEntity shooter = (LivingEntity) projectile.getShooter();

                    // ดึงค่าดาเมจพื้นฐานจาก LivingEntity ที่เป็นผู้ยิง
                    double baseDamage = shooter.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue(); // ค่าดาเมจพื้นฐาน (สามารถปรับเปลี่ยนได้ตามต้องการ)
                    /* if (shooter.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null) {
                        baseDamage = shooter.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue();
                        
                    } */
                    event.setDamage(baseDamage);

                    // แสดงข้อความใน Console สำหรับตรวจสอบ
                    if (plugin.getConfig().getBoolean("debug.show-message")) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(ChatColor.RED + shooter.getCustomName() + " ทำความเสียหาย: " + baseDamage + " ไปยัง " + target.getType().name());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMonsterDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity monster = (LivingEntity) event.getEntity();

            if (monster.getCustomName() != null && monster.getCustomName().contains("Lvl")) {
                double newHealth = monster.getHealth() - event.getFinalDamage(); 

                if (newHealth < 0) {
                    newHealth = 0;
                }

                String nameWithoutHp = monster.getCustomName().split("\\|")[0].trim(); 

                String updatedName = String.format("%s | %s❤: %.0f",
                        nameWithoutHp, 
                        ChatColor.RED, 
                        newHealth);

                monster.setCustomName(updatedName);
                monster.setCustomNameVisible(true); 
            }
        }
    }
    
}
