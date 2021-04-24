package me.despical.customenchgui.event;

import me.despical.customenchgui.Main;
import me.despical.customenchgui.gui.EnchantmentGui;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Despical
 * <p>
 * Created at 9.04.2021
 */
public class InteractEvent implements Listener {

    private final Main plugin;

    public InteractEvent(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block == null) return;
        if (block.getType() != Material.ENCHANTING_TABLE) return;

        event.setCancelled(true);
        new EnchantmentGui(plugin).openGui(event.getPlayer());
    }
}
