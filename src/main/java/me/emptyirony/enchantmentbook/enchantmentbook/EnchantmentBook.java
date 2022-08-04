package me.emptyirony.enchantmentbook.enchantmentbook;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class EnchantmentBook extends JavaPlugin {
    private static final Random random = new Random();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void anvilUse(PrepareAnvilEvent event) {
                AnvilInventory anvil = event.getInventory();
                ItemStack itemStack = anvil.getContents()[0];
                if (itemStack == null) return;
                ItemStack book = anvil.getContents()[1];

                if (!isBook(book)) {
                    return;
                }

                net.minecraft.world.item.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
                if (!nmsCopy.canEnchant()) {
                    return;
                }

                ItemStack result = itemStack.clone();
                ItemMeta meta = result.getItemMeta();
                List<String> lore = meta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }

                lore.add("");
                lore.add(ChatColor.translateAlternateColorCodes('&', "&a30级附魔"));

                meta.setLore(lore);
                result.setItemMeta(meta);

                anvil.setRepairCost(0);
                anvil.setMaximumRepairCost(0);

                event.setResult(result);
            }

            @EventHandler
            public void onClick(InventoryClickEvent event) {
                if (!(event.getClickedInventory() instanceof AnvilInventory)) {
                    return;
                }

                AnvilInventory anvil = (AnvilInventory) event.getClickedInventory();
                ItemStack itemStack = anvil.getContents()[1];

                if (!isBook(itemStack)) {
                    return;
                }

                event.setCancelled(true);

                if (event.getClick() == ClickType.LEFT) {
                    ItemStack item = doEnchant(anvil.getContents()[0].clone());
                    anvil.setContents(new ItemStack[anvil.getSize()]);
                    event.getWhoClicked().setItemOnCursor(item);
                    Player player = (Player) event.getWhoClicked();
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1f);
                }
            }

            private ItemStack doEnchant(ItemStack item) {
                for (Enchantment enchantment : item.getEnchantments().keySet()) {
                    item.removeEnchantment(enchantment);
                }

                net.minecraft.world.item.ItemStack asNMSCopy = CraftItemStack.asNMSCopy(item);
                EnchantmentManager.a(random, asNMSCopy, 30, true);

                return CraftItemStack.asBukkitCopy(asNMSCopy);
            }

            private boolean isBook(ItemStack book) {
                if (book == null || book.getType() != Material.ENCHANTED_BOOK) return false;

                ItemMeta bookMeta = book.getItemMeta();
                if (bookMeta == null) return false;
                List<String> lore = bookMeta.getLore();
                if (lore == null) return false;

                boolean isBook = false;
                for (String s : lore) {
                    if (s.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&8Enhancement Book"))) {
                        isBook = true;
                        break;
                    }
                }

                return isBook;
            }


        }, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            Player player = (Player) sender;
            ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a强化书"));
            meta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', "&8Enhancement Book")));
            itemStack.setItemMeta(meta);

            player.getInventory().addItem(itemStack);
        }

        return true;
    }
}
