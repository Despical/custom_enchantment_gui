package me.despical.customenchgui.gui;

import me.despical.customenchgui.Main;
import me.despical.customenchgui.gui.components.MainComponents;
import me.despical.customenchgui.util.Utils;
import me.despical.inventoryframework.Gui;
import me.despical.inventoryframework.pane.PaginatedPane;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Despical
 * <p>
 * Created at 9.04.2021
 */
public class EnchantmentGui {

    private final Gui gui;
    private ItemStack item = null;

    public EnchantmentGui(Main plugin) {
        this.gui = new Gui(plugin, 6, Utils.getGuiTitle());

        PaginatedPane pane = new PaginatedPane(9, 6);

        this.gui.addPane(pane);
        this.implementComponents(pane);
    }

    private void implementComponents(PaginatedPane pane) {
        MainComponents mainComponents = new MainComponents();
        mainComponents.implementComponent(this, pane);
    }

    public void openGui(Player player) {
        this.gui.show(player);
    }

    public Gui getGui() {
        return gui;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
}