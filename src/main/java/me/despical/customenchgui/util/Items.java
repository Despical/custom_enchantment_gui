package me.despical.customenchgui.util;

import me.despical.commons.compat.XMaterial;
import me.despical.commons.item.ItemBuilder;
import me.despical.customenchgui.Main;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Despical
 * <p>
 * Created at 9.04.2021
 */
public class Items {

    private final static Main plugin = JavaPlugin.getPlugin(Main.class);

    public static ItemStack
        BARRIER = new ItemBuilder(XMaterial.BARRIER.parseItem())
            .name(plugin.getConfig().getString("Exit-Item.Name"))
            .lore(plugin.getConfig().getStringList("Exit-Item.Lore")).build(),
        ENCHANTING_TABLE = new ItemBuilder(XMaterial.ENCHANTING_TABLE.parseItem())
                .name(plugin.getConfig().getString("Put-Item-To-Enchant.Name"))
                .lore(plugin.getConfig().getStringList("Put-Item-To-Enchant.Lore")).build(),
        AIR = XMaterial.AIR.parseItem(),
        GUNPOWDER = new ItemBuilder(XMaterial.GUNPOWDER.parseItem())
                .name(plugin.getConfig().getString("Cant-Enchant-This-Item.Name"))
                .lore(plugin.getConfig().getStringList("Cant-Enchant-This-Item.Lore")).build(),
        BLACK_STAINED_GLASS_PANE = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()).build();

}
