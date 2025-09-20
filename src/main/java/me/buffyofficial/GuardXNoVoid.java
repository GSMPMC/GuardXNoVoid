package me.buffyofficial;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class GuardXNoVoid extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private List<String> enabledWorlds;
    private final String prefix = ChatColor.translateAlternateColorCodes('&', "&5&lGuardX &c&lNoVoid &7>> ");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadLocalConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("GuardX NoVoid enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("GuardX NoVoid disabled.");
    }

    private void reloadLocalConfig() {
        reloadConfig();
        config = getConfig();
        enabledWorlds = config.getStringList("enabled-worlds");
    }

    @EventHandler
    public void onPlayerFallIntoVoid(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if (!enabledWorlds.contains(world.getName())) {
            return;
        }

        if (player.getLocation().getY() < 0) {
            Location spawn = world.getSpawnLocation().add(0.5, 0, 0.5);
            player.teleport(spawn);
            player.setFallDistance(0f);
            player.sendMessage(prefix + ChatColor.GREEN + "You were saved from the void!");
        }
    }

    @EventHandler
    public void onVoidDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        World world = player.getWorld();

        if (!enabledWorlds.contains(world.getName())) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.setCancelled(true);
            Location spawn = world.getSpawnLocation().add(0.5, 0, 0.5);
            player.teleport(spawn);
            player.setFallDistance(0f);
            player.sendMessage(prefix + ChatColor.GREEN + "You were saved from the void!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("guardxnv")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                reloadLocalConfig();
                sender.sendMessage(prefix + ChatColor.YELLOW + "Configuration reloaded!");
                return true;
            } else {
                sender.sendMessage(prefix + ChatColor.RED + "Usage: /guardxnv reload");
                return true;
            }
        }
        return false;
    }
}
