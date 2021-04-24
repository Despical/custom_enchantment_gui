package me.despical.customenchgui.gui.components;

import me.despical.customenchgui.Main;
import me.despical.customenchgui.gui.EnchantmentGui;
import me.despical.inventoryframework.pane.PaginatedPane;
import org.bukkit.plugin.java.JavaPlugin;

public interface GuiComponent {

    Main plugin = JavaPlugin.getPlugin(Main.class);

    void implementComponent(EnchantmentGui gui, PaginatedPane paginatedPane);
}
