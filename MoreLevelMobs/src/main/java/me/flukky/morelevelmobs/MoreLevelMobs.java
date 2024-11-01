package me.flukky.morelevelmobs;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.flukky.morelevelmobs.commands.CommandMobs;
import me.flukky.morelevelmobs.listeners.CreateMobs;
import me.flukky.morelevelmobs.listeners.ListenerMobs;
import me.flukky.morelevelmobs.managers.MobsType;
import me.flukky.morelevelmobs.managers.PresetMobs;

public class MoreLevelMobs extends JavaPlugin implements Listener {
    private FileConfiguration settingsConfig;
    private FileConfiguration dropsConfig;
    private PresetMobs presetMobs;
    private MobsType mobsType;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig(); // สร้าง config.yml ถ้ายังไม่มี
        loadSettingsConfig(); // โหลด settings.yml
        loadDropsConfig();

        presetMobs = new PresetMobs(this);
        mobsType = new MobsType(this);
        mobsType.loadColor();

        getServer().getPluginManager().registerEvents(new CreateMobs(this), this);
        getServer().getPluginManager().registerEvents(new ListenerMobs(this), this);
        getCommand("morelevelmobs").setExecutor(new CommandMobs(this));
    }

    public void loadSettingsConfig() {
        File settingsFile = new File(getDataFolder(), "settings.yml");
        if (!settingsFile.exists()) {
            saveResource("settings.yml", false);
        }
        settingsConfig = YamlConfiguration.loadConfiguration(settingsFile);
        
    }

    public void loadDropsConfig() {
        File dropsFile = new File(getDataFolder(), "drops.yml");
        if (!dropsFile.exists()) {
            saveResource("drops.yml", false);
        }
        dropsConfig = YamlConfiguration.loadConfiguration(dropsFile);
        
    }

    public FileConfiguration getSetting() {
        return settingsConfig;
    }

    public FileConfiguration getDrops() {
        return dropsConfig;
    }

    public PresetMobs getPreset() {
        return presetMobs;
    }

    public MobsType getMobType() {
        return mobsType;
    }
}
