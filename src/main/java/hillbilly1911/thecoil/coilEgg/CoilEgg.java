package hillbilly1911.thecoil.coilEgg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class CoilEgg extends JavaPlugin implements Listener, TabExecutor {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        // Register the plugin with the event system
        Bukkit.getPluginManager().registerEvents(this, this);

        // Register the command and its executor
        PluginCommand eggCommand = this.getCommand("egg");
        if (eggCommand != null) {
            eggCommand.setExecutor(this);
        }

        getLogger().info("CoilEgg has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("CoilEgg has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("egg")) {
            if (sender instanceof Player player) {
                // Check for permission
                if (!player.hasPermission("coilegg.use")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }

                // Check cooldown
                UUID playerId = player.getUniqueId();
                long currentTime = System.currentTimeMillis();
                if (cooldowns.containsKey(playerId)) {
                    long lastUsed = cooldowns.get(playerId);
                    if (currentTime - lastUsed < 3000) { // 3 seconds
                        long timeLeft = (3000 - (currentTime - lastUsed)) / 1000;
                        player.sendMessage(ChatColor.RED + "You must wait " + timeLeft + " more seconds to use this command.");
                        return true;
                    }
                }

                // Update cooldown and throw an egg
                cooldowns.put(playerId, currentTime);
                Location loc = player.getLocation();
                Egg egg = player.launchProjectile(Egg.class, player.getLocation().getDirection());
                player.sendMessage(ChatColor.GREEN + "You have thrown an egg!");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onEggHit(ProjectileHitEvent event) {
        // Check if the projectile is an egg and hits a player
        if (event.getEntity() instanceof Egg egg && event.getHitEntity() instanceof Player hitPlayer) {
            hitPlayer.sendMessage(ChatColor.YELLOW + "You've been egged!");
        }
    }
}