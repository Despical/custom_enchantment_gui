package me.despical.customenchgui.gui.components;

import javafx.util.Pair;
import me.despical.commons.compat.XSound;
import me.despical.customenchgui.gui.EnchantmentGui;
import me.despical.customenchgui.util.Utils;
import me.despical.inventoryframework.GuiItem;
import me.despical.inventoryframework.pane.PaginatedPane;
import me.despical.inventoryframework.pane.StaticPane;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.List;

import static me.despical.customenchgui.util.Items.*;

/**
 * @author Despical
 * <p>
 * Created at 9.04.2021
 */
public class MainComponents implements GuiComponent {

    private final GuiItem closeItem = new GuiItem(BARRIER, e -> {
        if (e.getView().getCursor().getType() == Material.AIR) e.getWhoClicked().closeInventory();
    });

    @Override
    public void implementComponent(EnchantmentGui gui, PaginatedPane paginatedPane) {
        StaticPane pane = new StaticPane(9, 6);

        pane.fillWith(BLACK_STAINED_GLASS_PANE);
        pane.addItem(new GuiItem(gui.getItem() == null ? AIR : gui.getItem()), 1, 2);
        pane.addItem(new GuiItem(ENCHANTING_TABLE), 1, 3);
        pane.addItem(new GuiItem(GUNPOWDER), 5, 2);
        pane.addItem(closeItem, 4, 5);

        gui.getGui().setOnGlobalClick(e -> {
            // cancels if slot isn't 19 and less than 54 (top inventory)
            // or click type is number and action is hot bar swapping
            // or if player shift left/right clicked
            e.setCancelled((e.getRawSlot() != 19 && e.getRawSlot() < 54) || e.getClick() == ClickType.DROP ||
                    (e.getClick() == ClickType.NUMBER_KEY && e.getAction() == InventoryAction.HOTBAR_SWAP) ||
                    e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Player player = (Player) e.getWhoClicked();
                Inventory inventory = gui.getGui().getInventory(); // gui's inventory

                // 19 is the empty slot for item to be enchanted
                if (e.getRawSlot() == 19) {
                    ItemStack item = inventory.getItem(19); // item in 19th slot which is empty for enchanting item

                    // if this item is null then we don't know where player place it or even place it
                    // so set item to null; also updating gui cause player to drop item on cursor
                    if (item == null) {
                        gui.setItem(null);
                        return;
                    } else {
                        // if it is not null then we can set gui item to item in enchanting slot
                        gui.setItem(item);
                    }

                    if (Utils.canApplyEnchant(item)) {
                        List<Enchantment> enchantments = Utils.getApplicableEnchants(item);

                        // hide gun powder to avoid if enchantments size is less
                        pane.addItem(new GuiItem(BLACK_STAINED_GLASS_PANE), 5, 2);

                        for (int i = 0; i < enchantments.size(); i++) {
                            // location of enchanted book which is centered
                            Pair<Integer, Integer> location = getXY(i);

                            // enchantment of enchanted book which is final
                            Enchantment enchantment = enchantments.get(i);

                            // enchanted book with custom item meta which is configured in config
                            ItemStack enchantedBook = Utils.getEnchantedItem(enchantment, 1, false);
                            // add enchant to enchanted book then add HIDE_ENCHANTS flag to hide default name
                            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();
                            meta.addEnchant(enchantment, 1, true);
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            enchantedBook.setItemMeta(meta);

                            pane.addItem(new GuiItem(enchantedBook, event -> {
                                // clicked enchanted book in gui
                                ItemStack clickedBook = event.getCurrentItem();
                                // clicked enchanted book's meta with HIDE_ENCHANTS flag
                                EnchantmentStorageMeta clickedBooKMeta = (EnchantmentStorageMeta) clickedBook.getItemMeta();
                                clickedBooKMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                                // if event is left click then create a new gui for enchanted book levels
                                if (event.isLeftClick()) {
                                    StaticPane bookPane = new StaticPane(9, 6);
                                    bookPane.fillWith(BLACK_STAINED_GLASS_PANE);
                                    bookPane.addItem(closeItem, 4, 5);

                                    for (int level = enchantment.getStartLevel(); level <= enchantment.getMaxLevel(); level++) {
                                        clickedBooKMeta.addEnchant(enchantment, level, true);
                                        clickedBook.setItemMeta(clickedBooKMeta);

                                        int[] centeredX = getCenteredX(enchantment);
                                        // we need make level effectively final to use in lambda expressions
                                        int finalLevel = level;

                                        // add books to new gui pane
                                        bookPane.addItem(new GuiItem(Utils.getEnchantedItem(enchantment, level, true), enchEvent -> {
                                            // if item is enchanted then we shouldn't enchant again
                                            // item which is in 19th slot in main menu
                                            if (item.getEnchantments().isEmpty()) {
                                                double levelAmount = Utils.getLevelAmount(finalLevel);

                                                if (player.getLevel() > levelAmount) {
                                                    item.addUnsafeEnchantment(enchantment, finalLevel); // add selected enchantment to item

                                                    player.getInventory().addItem(item); // then give it to player
                                                    player.setLevel((int) (player.getLevel() - levelAmount)); // finally we can decrease player's level

                                                    // set item to null to avoid duplicating items when gui close
                                                    gui.setItem(null);

                                                    player.closeInventory();

                                                    XSound.BLOCK_ANVIL_USE.play(player);
                                                } else {
                                                    XSound.BLOCK_ANVIL_LAND.play(player);
                                                }
                                            }
                                        }), centeredX[level - 1], 2);

                                        // add pane which contains enchanted books for levels
                                        // then set page to 1 and update gui to make player's
                                        // view to new page
                                        paginatedPane.addPane(1, bookPane);
                                        paginatedPane.setPage(1);
                                        gui.getGui().update();
                                    }
                                }
                            }), location.getKey(), location.getValue());
                        }

                        // add item to updated gui
                        pane.addItem(new GuiItem(gui.getItem()), 1, 2);

                        // update gui to make player view applicable enchants
                    } else {
                        // if item can't have enchantments
                        // checks for is there any books from previous item
                        // also checked by centered
                        boolean shouldRestorePanes = inventory.getItem(19) != null && inventory.getItem(30) != null &&
                                inventory.getItem(30).getType() == Material.ENCHANTED_BOOK;

                        if (!shouldRestorePanes) {
                            return;
                        }

                        // fill books' places with black stained glass pane as old
                        for (int x = 3; x < 8; x++) {
                            for (int y = 1; y < 4; y++) {
                                pane.addItem(new GuiItem(BLACK_STAINED_GLASS_PANE), x, y);
                            }
                        }

                        // restore these items too and don't put them above the loop
                        // cuz #addItem method will override gun powder
                        pane.addItem(new GuiItem(inventory.getItem(19)), 1, 2);
                        pane.addItem(new GuiItem(GUNPOWDER), 5, 2);
                    }
                } else {
                    // checks for is there any books from previous item
                    // also checked by centered
                    boolean shouldRestorePanes = inventory.getItem(19) == null && inventory.getItem(30) != null &&
                            inventory.getItem(30).getType() == Material.ENCHANTED_BOOK;

                    if (!shouldRestorePanes) {
                        return;
                    }

                    // fill books' places with black stained glass pane as old
                    for (int x = 3; x < 8; x++) {
                        for (int y = 1; y < 4; y++) {
                            pane.addItem(new GuiItem(BLACK_STAINED_GLASS_PANE), x, y);
                        }
                    }

                    // restore these items too and don't put them above the loop
                    // cuz #addItem method will override gun powder
                    pane.addItem(new GuiItem(AIR), 1, 2);
                    pane.addItem(new GuiItem(GUNPOWDER), 5, 2);
                }

                // update gui to make player view applicable enchants or to make
                // new items visible (this part of code is extract from common)
                gui.getGui().update();
            }, 1L);
        });

        gui.getGui().setOnClose(e -> {
            Player player = (Player) e.getPlayer();

            if (paginatedPane.getPage() == 0) {
                // to avoid syncing problems between previous and current tasks
                if (gui.getItem() == null && gui.getGui().getInventory().getItem(19) != null) {
                    player.getInventory().addItem(gui.getGui().getInventory().getItem(19));
                    return;
                }

                if (gui.getItem() != null && e.getView().getCursor() == null) {
                    player.getInventory().addItem(gui.getItem());
                    return;
                }

                if (gui.getItem() == null && e.getView().getCursor() != null) {
                    player.getInventory().addItem(e.getView().getCursor());
                    return;
                }

                if (gui.getItem() != null && e.getView().getCursor() != null) {
                    if (e.getView().getCursor().equals(gui.getItem())) {
                        player.getInventory().addItem(gui.getItem());
                        return;
                    }
                    if (e.getView().getCursor().getType() == Material.AIR) {
                        player.getInventory().addItem(gui.getItem());
                    }
                }
            }

            if (paginatedPane.getPage() == 1 && gui.getItem() != null) {
                player.getInventory().addItem(gui.getItem());
            }
        });

        paginatedPane.addPane(0, pane);
    }

    private Pair<Integer, Integer> getXY(int slot) {
        if (slot < 5) {
            return new Pair<>(slot + 3, 2);
        } else if (slot < 10) {
            return new Pair<>(slot - 2, 3);
        } else if (slot < 15) {
            return new Pair<>(slot - 7, 1);
        } else if (slot < 20) {
            return new Pair<>(slot - 12, 4);
        }

        return new Pair<>(0, 0);
    }

    private int[] getCenteredX(Enchantment enchantment) {
        int maxLevel = enchantment.getMaxLevel();

        if (maxLevel == 2) return new int[]{3, 5};
        if (maxLevel == 3) return new int[]{3, 4, 5};
        if (maxLevel == 4) return new int[]{2, 3, 4, 5};
        if (maxLevel == 5) return new int[]{2, 3, 4, 5, 6};
        return new int[]{4};
    }
}