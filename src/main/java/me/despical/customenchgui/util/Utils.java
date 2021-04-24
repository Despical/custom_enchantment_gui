package me.despical.customenchgui.util;

import me.despical.commons.compat.XMaterial;
import me.despical.commons.item.ItemBuilder;
import me.despical.customenchgui.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Despical
 * <p>
 * Created at 9.04.2021
 */
public class Utils {

    private static final Main plugin = JavaPlugin.getPlugin(Main.class);

    public static List<Enchantment> getApplicableEnchants(ItemStack item) {
        List<Enchantment> enchantments = Arrays.asList(Enchantment.values());
        Collections.shuffle(enchantments);
        return item.getAmount() == 1 && item.getType() == Material.BOOK ? enchantments.stream().filter(e -> !e.getName().contains("CURSE")).limit(15).collect(Collectors.toList())
                : Arrays.stream(Enchantment.values()).filter(e -> e.getItemTarget().includes(item) && !e.getName().contains("CURSE")).collect(Collectors.toList());
    }

    public static boolean canApplyEnchant(ItemStack item) {
        return !getApplicableEnchants(item).isEmpty() && item.getEnchantments().isEmpty() || item.getType() == Material.BOOK;
    }

    public static double getLevelAmount(int level) {
        return plugin.getConfig().getDouble("Level-" + level + "-Amount", level * 300) + getUpgradeAmount();
    }

    public static double getUpgradeAmount() {
        return plugin.getConfig().getDouble("Upgrade-Amount", 300);
    }

    public static String getGuiTitle() {
        return plugin.getConfig().getString("Gui-Title", "Main Menu");
    }

    @NotNull
    public static ItemStack getEnchantedItem(Enchantment enchantment, int i, boolean custom) {
        ItemBuilder builder = new ItemBuilder(XMaterial.ENCHANTED_BOOK.parseItem());
        builder.name(plugin.getConfig().getString(enchantment.getName() + (custom ? ".Custom-Menu-Name" : ".Main-Menu-Name")));
        List<String> lore = plugin.getConfig().getStringList(enchantment.getName() + (custom ? ".Custom-Menu-Lore" : ".Main-Menu-Lore")).stream().map(str -> str.replace("%level%", Integer.toString(i))).collect(Collectors.toList());

        return builder.lore(lore).build();
    }
}