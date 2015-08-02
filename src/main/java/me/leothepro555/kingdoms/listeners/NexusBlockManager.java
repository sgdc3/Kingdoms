package me.leothepro555.kingdoms.listeners;

import me.confuser.bukkitutil.listeners.Listeners;
import me.leothepro555.kingdoms.Kingdoms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class NexusBlockManager extends Listeners<Kingdoms> {

  private Inventory nexusgui;
  private Inventory kchest;
  private Inventory dumpgui;
  private Inventory champions;
  private int rpi = 10;

  public NexusBlockManager() {
    if (plugin.getConfig().get("items-needed-for-one-resource-point") != null) {
      this.rpi = plugin.getConfig().getInt("items-needed-for-one-resource-point");
    }
  }

  @EventHandler
  public void onPlayerClick(PlayerInteractEvent event) {
    Player p = event.getPlayer();
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      if (event.getClickedBlock().getType() == Material.BEACON) {
        if (event.getClickedBlock().hasMetadata("nexusblock")) {
          event.setCancelled(true);
          if (!plugin.getChunkKingdom(event.getClickedBlock().getLocation().getChunk()).equals(plugin.getKingdom(p))) {
            if (!plugin.isAlly(plugin.getChunkKingdom(event.getClickedBlock().getLocation().getChunk()), p)) {
              p.sendMessage(ChatColor.RED + "You can't use a nexus that doesn't belong to your kingdom!");
            } else {
              dumpgui = Bukkit
                      .createInventory(null, 54, ChatColor.DARK_BLUE + "Donate to " + ChatColor.DARK_GREEN + plugin
                              .getChunkKingdom(event.getClickedBlock().getLocation().getChunk()));
              p.openInventory(dumpgui);
            }
            if (plugin.isKAlly(plugin.getKingdom(p), plugin
                    .getChunkKingdom(event.getClickedBlock().getLocation().getChunk()))) {
              p.sendMessage(ChatColor.RED + "You marked " + plugin.getChunkKingdom(event.getClickedBlock().getLocation()
                                                                                        .getChunk()) + " as an ally, but they did not mark your kingdom as their ally");
            }
          } else if (plugin.getChunkKingdom(event.getClickedBlock().getLocation().getChunk())
                           .equals(plugin.getKingdom(p))) {
            openNexusGui(p);
          }
        }
      }
    }
  }

  @EventHandler
  public void onInventoryMove(InventoryClickEvent event) {
    Player p = (Player) event.getWhoClicked();
    if (event.getInventory().getName().endsWith(ChatColor.AQUA + plugin.getKingdom(p) + "'s nexus")) {
      event.setCancelled(true);
      if (event.getCurrentItem() != null) {
        if (event.getCurrentItem().getItemMeta() != null) {
          if (event.getCurrentItem().getItemMeta().getDisplayName() != null) {
            if (event.getCurrentItem().getItemMeta().getDisplayName()
                     .equalsIgnoreCase(ChatColor.AQUA + "Kingdom Chest")) {
              openKingdomChest(p);
            }
            if (event.getCurrentItem().getItemMeta().getLore() != null) {
              if (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "Nexus Upgrade")) {
                int cost = 0;
                int max = 1;
                event.setCancelled(true);
                ItemStack item = event.getCurrentItem();
                if (plugin.isMod(plugin.getKingdom(p), p) || plugin.isKing(p)) {
                  if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "Damage Reduction")) {
                    cost = plugin.getConfig().getInt("cost.nexusupgrades.dmg-reduc");
                    max = plugin.getConfig().getInt("max.nexusupgrades.dmg-reduc");
                    if (plugin.rpm.hasAmtRp(plugin.getKingdom(p), cost)) {
                      if (plugin.powerups.getInt(plugin.getKingdom(p) + ".dmg-reduction") < max) {
                        plugin.rpm.minusRP(plugin.getKingdom(p), cost);
                        upgradePowerup(plugin.getKingdom(p), "dmg-reduction", 1);
                        p.sendMessage(ChatColor.GREEN + "Damage reduction upgraded! Total: " + plugin.powerups
                                .getInt(plugin.getKingdom(p) + ".dmg-reduction"));
                        p.closeInventory();
                        openNexusGui(p);
                      } else {
                        p.sendMessage(ChatColor.RED + "This upgrade is at its maximum level!");
                      }
                    } else {
                      p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
                    }
                  }
                  if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "Regeneration Boost")) {
                    cost = plugin.getConfig().getInt("cost.nexusupgrades.regen-boost");
                    max = plugin.getConfig().getInt("max.nexusupgrades.regen-boost");
                    if (plugin.rpm.hasAmtRp(plugin.getKingdom(p), cost)) {
                      if (plugin.powerups.getInt(plugin.getKingdom(p) + ".regen-boost") < max) {
                        plugin.rpm.minusRP(plugin.getKingdom(p), cost);
                        upgradePowerup(plugin.getKingdom(p), "regen-boost", 5);
                        p.sendMessage(ChatColor.GREEN + "Regeneration boost upgraded! Total: " + plugin.powerups
                                .getInt(plugin.getKingdom(p) + ".regen-boost"));
                        p.closeInventory();
                        openNexusGui(p);
                      } else {
                        p.sendMessage(ChatColor.RED + "This upgrade is at its maximum level!");
                      }
                    } else {
                      p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
                    }
                  }
                  if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "Damage Boost")) {
                    cost = plugin.getConfig().getInt("cost.nexusupgrades.dmg-boost");
                    max = plugin.getConfig().getInt("max.nexusupgrades.dmg-boost");
                    if (plugin.rpm.hasAmtRp(plugin.getKingdom(p), cost)) {
                      if (plugin.powerups.getInt(plugin.getKingdom(p) + ".dmg-boost") < max) {
                        plugin.rpm.minusRP(plugin.getKingdom(p), cost);
                        upgradePowerup(plugin.getKingdom(p), "dmg-boost", 1);
                        p.sendMessage(ChatColor.GREEN + "Damage boost upgraded! Total: " + plugin.powerups
                                .getInt(plugin.getKingdom(p) + ".dmg-boost"));
                        p.closeInventory();
                        openNexusGui(p);
                      } else {
                        p.sendMessage(ChatColor.RED + "This upgrade is at its maximum level!");
                      }
                    } else {
                      p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
                    }
                  }
                  if (item.getItemMeta().getDisplayName()
                          .equalsIgnoreCase(ChatColor.AQUA + "Increase Kingdom Chest Size")) {
                    if (plugin.rpm.hasAmtRp(plugin.getKingdom(p), 30)) {
                      if (plugin.kingdoms.getInt(plugin.getKingdom(p) + ".chestsize") < 27) {
                        plugin.rpm.minusRP(plugin.getKingdom(p), 30);
                        plugin.kingdoms.set(plugin.getKingdom(p) + ".chestsize", plugin.kingdoms
                                .getInt(plugin.getKingdom(p) + ".chestsize") + 9);
                        plugin.saveKingdoms();
                        p.sendMessage(ChatColor.GREEN + "Chest Size upgraded! Total: " + plugin.kingdoms
                                .getInt(plugin.getKingdom(p) + ".chestsize"));
                        p.closeInventory();
                        openNexusGui(p);
                      } else {
                        p.sendMessage(ChatColor.RED + "Maximum level reached.");
                      }
                    } else {
                      p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
                    }
                  }
                } else {
                  p.sendMessage(ChatColor.RED + "Only kingdom kings and mods can upgrade bonuses");
                }
              } else if (event.getCurrentItem().getItemMeta().getLore()
                              .contains(ChatColor.LIGHT_PURPLE + "Nexus Option")) {
                event.setCancelled(true);
                ItemStack item = event.getCurrentItem();

                if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Convert items to resource points")) {

                  dumpgui = Bukkit.createInventory(null, 54, ChatColor.DARK_BLUE + "Close inventory to confirm.");
                  p.openInventory(dumpgui);
                }
              } else if (event.getCurrentItem().getItemMeta().getLore()
                              .contains(ChatColor.LIGHT_PURPLE + "Click to open Champion upgrades")) {
                if (plugin.isMod(plugin.getKingdom(p), p) || plugin.isKing(p)) {
                  event.setCancelled(true);
                  openChampionMenu(p);
                } else {
                  p.sendMessage(ChatColor.RED + "Only kingdom kings and mods can upgrade bonuses");
                  p.closeInventory();
                }
              } else if (event.getCurrentItem().getItemMeta().getLore()
                              .contains(ChatColor.LIGHT_PURPLE + "Click to open Miscellaneous upgrades")) {
                if (plugin.isMod(plugin.getKingdom(p), p) || plugin.isKing(p)) {
                  event.setCancelled(true);
                  openMisMenu(p);
                } else {
                  p.sendMessage(ChatColor.RED + "Only kingdom kings and mods can upgrade bonuses");
                  p.closeInventory();
                }
              } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Turrets")) {
                if (plugin.isMod(plugin.getKingdom(p), p) || plugin.isKing(p)) {
                  event.setCancelled(true);
                  openTurretShop(p);
                } else {
                  p.sendMessage(ChatColor.RED + "Only kingdom kings and mods can buy turrets");
                  p.closeInventory();
                }
              }
            }
          }
        }
      }
    } else if (event.getInventory().getName().equals(ChatColor.AQUA + plugin.getKingdom(p) + "'s Champion")) {
      event.setCancelled(true);
      ItemStack item = event.getCurrentItem();

      if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Return to main menu")) {
        p.closeInventory();
        openNexusGui(p);
      }

      if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Champion Weapon")) {
        if (getChampionUpgrade(plugin.getKingdom(p), "weapon") < 4) {
          if (plugin.rpm.hasAmtRp(plugin.getKingdom(p), 10)) {
            plugin.rpm.minusRP(plugin.getKingdom(p), 10);
            upgradeChampion(plugin.getKingdom(p), "weapon", 1);
            p.sendMessage(ChatColor.GREEN + "Champion Weapon upgraded! Total: " + getChampionUpgrade(plugin
                    .getKingdom(p), "weapon"));
            p.closeInventory();
            openChampionMenu(p);
          } else {
            p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
          }
        } else {
          p.sendMessage(ChatColor.RED + "This upgrade is at its maximum level.");
        }
      }

      if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Champion Resistance")) {
        if (getChampionUpgrade(plugin.getKingdom(p), "resist") < 100) {
          if (plugin.rpm.hasAmtRp(plugin.getKingdom(p), 5)) {
            plugin.rpm.minusRP(plugin.getKingdom(p), 5);
            upgradeChampion(plugin.getKingdom(p), "resist", 20);
            p.sendMessage(ChatColor.GREEN + "Champion Resistance upgraded! Total: " + getChampionUpgrade(plugin
                    .getKingdom(p), "resist"));
            p.closeInventory();
            openChampionMenu(p);
          } else {
            p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
          }
        } else {
          p.sendMessage(ChatColor.RED + "This upgrade is at its maximum level.");
        }
      }

      if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Champion Speed")) {
        if (getChampionUpgrade(plugin.getKingdom(p), "speed") < 3) {
          if (plugin.rpm.hasAmtRp(plugin.getKingdom(p), 20)) {
            plugin.rpm.minusRP(plugin.getKingdom(p), 20);
            upgradeChampion(plugin.getKingdom(p), "speed", 1);
            p.sendMessage(ChatColor.GREEN + "Champion Speed upgraded! Total: " + getChampionUpgrade(plugin
                    .getKingdom(p), "speed"));
            p.closeInventory();
            openChampionMenu(p);
          } else {
            p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
          }
        } else {
          p.sendMessage(ChatColor.RED + "This upgrade is at its maximum level.");
        }
      }

      if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Champion Health")) {
        if (plugin.rpm.hasAmtRp(plugin.getKingdom(p), 2)) {
          plugin.rpm.minusRP(plugin.getKingdom(p), 2);
          upgradeChampion(plugin.getKingdom(p), "health", 2);
          p.sendMessage(ChatColor.GREEN + "Champion Health upgraded! Total: " + getChampionUpgrade(plugin
                  .getKingdom(p), "health"));
          p.closeInventory();
          openChampionMenu(p);
        } else {
          p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
        }

      }

      if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Drag")) {
        if (plugin.kingdoms.getInt(plugin.getKingdom(p) + ".champion.drag") == 0) {
          if (plugin.rpm.hasAmtRp(plugin.getKingdom(p), 30)) {
            plugin.rpm.minusRP(plugin.getKingdom(p), 30);
            upgradeChampion(plugin.getKingdom(p), "drag", 1);
            p.sendMessage(ChatColor.GREEN + "Drag Enabled!");
            p.closeInventory();
            openChampionMenu(p);
          } else {
            p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
          }
        } else {
          p.sendMessage(ChatColor.RED + "This upgrade is at its maximum level");
        }
      }

      if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Mock")) {
        if (plugin.rpm.hasAmtRp(plugin.getKingdom(p), 10)) {
          plugin.rpm.minusRP(plugin.getKingdom(p), 10);
          upgradeChampion(plugin.getKingdom(p), "mock", 1);
          p.sendMessage(ChatColor.GREEN + "Mock upgraded! Total: " + getChampionUpgrade(plugin.getKingdom(p), "mock"));
          p.closeInventory();
          openChampionMenu(p);
        } else {
          p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
        }

      }

      if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Death Duel")) {
        if (plugin.kingdoms.getInt(plugin.getKingdom(p) + ".champion.duel") == 0) {
          if (plugin.rpm.hasAmtRp(plugin.getKingdom(p), 100)) {
            plugin.rpm.minusRP(plugin.getKingdom(p), 100);
            upgradeChampion(plugin.getKingdom(p), "duel", 1);
            p.sendMessage(ChatColor.GREEN + "Death Duel enabled!");
            p.closeInventory();
            openChampionMenu(p);
          } else {
            p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
          }
        } else {
          p.sendMessage(ChatColor.RED + "You have reached the maximum level for this upgrade");
        }
      }

    } else if (event.getInventory().getName().equals(ChatColor.AQUA + "Extra Upgrades")) {
      event.setCancelled(true);

      if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Return to main menu")) {
        p.closeInventory();
        openNexusGui(p);
      }
      ItemStack item = event.getCurrentItem();

      if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Anti-Creeper")) {
        this.upgradeMis(p, "anticreeper");
      }

      if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Anti-Trample")) {
        this.upgradeMis(p, "antitrample");
      }

      if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Nexus Guard")) {
        this.upgradeMis(p, "nexusguard");
      }

      if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Glory")) {
        this.upgradeMis(p, "glory");
      }

      if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Bomb Expertise")) {
        this.upgradeMis(p, "bombshards");
      }
    } else if (event.getInventory().getName().equals(ChatColor.AQUA + "Turret Shop")) {
      event.setCancelled(true);
      ItemStack item = event.getCurrentItem();
      if (item.getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "Turret")) {
        if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Nexus Turret")) {
          if (plugin.rpm.hasAmtRp(plugin.getKingdom(p), 300)) {
            p.sendMessage(ChatColor.GREEN + "Right click while holding " + ChatColor.AQUA + "Nexus Turret " + ChatColor.GREEN + "to place it");
            plugin.rpm.minusRP(plugin.getKingdom(p), 300);

            ItemStack i2 = new ItemStack(Material.RECORD_10);
            ItemMeta i2m = i2.getItemMeta();
            i2m.setDisplayName(ChatColor.AQUA + "Nexus Tower");
            ArrayList<String> i2l = new ArrayList<String>();
            i2l.add(ChatColor.GREEN + "Zaps all enemies in range with the");
            i2l.add(ChatColor.GREEN + "power of lightning");
            i2l.add(ChatColor.RED + "Can only be placed in the nexus chunk");
            i2l.add(ChatColor.BLUE + "Range: 5 blocks");
            i2l.add(ChatColor.BLUE + "Damage: 3 hearts/shot");
            i2l.add(ChatColor.BLUE + "Attack Speed: 1/sec");
            i2l.add(ChatColor.BLUE + "Targets: All targets in range");
            i2l.add(ChatColor.LIGHT_PURPLE + "Turret");
            i2m.setLore(i2l);
            i2.setItemMeta(i2m);

            p.getInventory().addItem(i2);
            p.updateInventory();
          } else {
            p.sendMessage(ChatColor.RED + "You don't have enough resource points for this turret!");
          }

        } else if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Arrow Turret")) {


          if (plugin.rpm.hasAmtRp(plugin.getKingdom(p), 100)) {
            p.sendMessage(ChatColor.GREEN + "Right click while holding " + ChatColor.AQUA + "Arrow Turret " + ChatColor.GREEN + "to place it");
            plugin.rpm.minusRP(plugin.getKingdom(p), 100);

            ItemStack i1 = new ItemStack(Material.RECORD_9);
            ItemMeta i1m = i1.getItemMeta();
            i1m.setDisplayName(ChatColor.AQUA + "Arrow Turret");
            ArrayList<String> i1l = new ArrayList<String>();
            i1l.add(ChatColor.GREEN + "Rapidly fires at anything other than");
            i1l.add(ChatColor.GREEN + "kingdom members. One target at a time.");
            i1l.add(ChatColor.BLUE + "Range: 7 blocks");
            i1l.add(ChatColor.BLUE + "Damage: 4 hearts/shot");
            i1l.add(ChatColor.BLUE + "Attack Speed: 1/sec");
            i1l.add(ChatColor.BLUE + "Targets: One random target in range");
            i1l.add(ChatColor.LIGHT_PURPLE + "Turret");
            i1m.setLore(i1l);
            i1.setItemMeta(i1m);

            p.getInventory().addItem(i1);
            p.updateInventory();

          } else {
            p.sendMessage(ChatColor.RED + "You don't have enough resource points for this turret!");
          }


        }
      }
    }
  }

  public int getChampionUpgrade(String kingdom, String powerup) {
    return plugin.kingdoms.getInt(kingdom + ".champion." + powerup);
  }

  public void upgradeChampion(String kingdom, String powerup, int amount) {
    plugin.kingdoms
            .set(kingdom + ".champion." + powerup, plugin.kingdoms.getInt(kingdom + ".champion." + powerup) + amount);
    plugin.saveKingdoms();
  }

  public void upgradePowerup(String kingdom, String powerup, int amount) {
    plugin.powerups.set(kingdom + "." + powerup, plugin.powerups.getInt(kingdom + "." + powerup) + amount);
    plugin.savePowerups();
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    Player p = (Player) event.getPlayer();
    if (event.getInventory().getName().equals(ChatColor.DARK_BLUE + "Close inventory to confirm.")) {

      int donatedamt = donateItems(event.getInventory().getContents(), plugin.getKingdom(p), p);

      ((Player) event.getPlayer())
              .sendMessage(ChatColor.GREEN + "Your kingdom gained " + donatedamt + " resource points");
    } else if (event.getInventory().getName().startsWith(ChatColor.DARK_BLUE + "Donate to " + ChatColor.DARK_GREEN)) {
      String[] nsplit = event.getInventory().getName().split(" ");
      String kingdom = ChatColor.stripColor(nsplit[nsplit.length - 1]);
      if (event.getInventory().getContents().length > 0) {
        int donatedamt = donateItems(event.getInventory().getContents(), kingdom, p);

        ((Player) event.getPlayer())
                .sendMessage(ChatColor.GREEN + kingdom + " has gained " + donatedamt + " resource points. Thank you for your donation!");
        plugin.messageKingdomPlayers(kingdom, ChatColor.GREEN + event.getPlayer()
                                                                     .getName() + " has donated " + donatedamt + " resource points");
      } else {
        ((Player) event.getPlayer()).sendMessage(ChatColor.GREEN + kingdom + " has gained 0 resource points.");
      }


    } else if (event.getInventory().getName().equals(ChatColor.AQUA + "Kingdom Chest")) {
      ArrayList<ItemStack> kchestcontents = new ArrayList<ItemStack>();
      for (ItemStack item : event.getInventory().getContents()) {
        if (item != null) {
          kchestcontents.add(item);
        }
      }

      plugin.chest.set(plugin.getKingdom(p), kchestcontents);
      plugin.saveChests();
    }
  }

  public int donateItems(ItemStack[] items, String kingdom, Player p) {
    int rpdonated = 0;
    for (ItemStack item : items) {
      if (item != null) {
        if (!plugin.blacklistitems.contains(item.getType())) {
          if (!plugin.usewhitelist) {
            int amt = item.getAmount();

            if (!plugin.specialcaseitems.containsKey(item.getType())) {
              rpdonated = rpdonated + amt;
            } else {
              amt = amt * plugin.specialcaseitems.get(item.getType());
              rpdonated = rpdonated + amt;
            }
          } else {
            int amt = item.getAmount();
            if (!plugin.whitelistitems.containsKey(item.getType())) {

              p.sendMessage(ChatColor.RED + item.getType()
                                                .name() + " cannot be traded for resource points. Do /k tradable to see allowed trades");
              p.getWorld().dropItemNaturally(p.getLocation(), item);
            } else {
              amt = amt * plugin.whitelistitems.get(item.getType());
              rpdonated = rpdonated + amt;
            }
          }
        } else {
          if (p != null) {
            p.sendMessage(ChatColor.RED + item.getType()
                                              .name() + " cannot be traded for resource points. Do /k tradable to see allowed trades");
            p.getWorld().dropItemNaturally(p.getLocation(), item);
          }
        }
      }
    }
    float i = rpdonated / rpi;
    String newint = Float.toString(i);
    String[] split = newint.split("\\.");
    plugin.rpm.addRP(kingdom, Integer.parseInt(split[0]));
    plugin.saveKingdoms();
    return Integer.parseInt(split[0]);

  }

  public void openNexusGui(Player p) {
    nexusgui = Bukkit.createInventory(null, 27, ChatColor.AQUA + plugin.getKingdom(p) + "'s nexus");

    ItemStack i1 = new ItemStack(Material.WHEAT);
    ItemMeta i1m = i1.getItemMeta();
    i1m.setDisplayName(ChatColor.AQUA + "Convert items to resource points");
    ArrayList<String> i1l = new ArrayList<String>();
    i1l.add(ChatColor.GREEN + "1 resourcepoint per " + rpi + " items");
    i1l.add(ChatColor.LIGHT_PURPLE + "Nexus Option");
    i1m.setLore(i1l);
    i1.setItemMeta(i1m);

    ItemStack i2 = new ItemStack(Material.IRON_CHESTPLATE);
    ItemMeta i2m = i2.getItemMeta();
    i2m.setDisplayName(ChatColor.AQUA + "Damage Reduction");
    ArrayList<String> i2l = new ArrayList<String>();
    i2l.add(ChatColor.GREEN + "Each upgrade reduces damage taken by another 1%");
    i2l.add(ChatColor.RED + "Current Damage Reduction: " + plugin.powerups
            .getInt(plugin.getKingdom(p) + ".dmg-reduction") + "%");
    i2l.add(ChatColor.RED + "Cost: " + plugin.getConfig().getInt("cost.nexusupgrades.dmg-reduc") + " resource points");
    i2l.add(ChatColor.RED + "Max Level: " + plugin.getConfig().getInt("max.nexusupgrades.dmg-reduc"));
    i2l.add(ChatColor.LIGHT_PURPLE + "Nexus Upgrade");
    i2m.setLore(i2l);
    i2.setItemMeta(i2m);

    ItemStack i3 = new ItemStack(Material.RED_ROSE);
    ItemMeta i3m = i3.getItemMeta();
    i3m.setDisplayName(ChatColor.AQUA + "Regeneration Boost");
    ArrayList<String> i3l = new ArrayList<String>();
    i3l.add(ChatColor.GREEN + "While in your own land, boost your health regeneration");
    i3l.add(ChatColor.GREEN + "by 5% more.");
    i3l.add(ChatColor.RED + "Current Regeneration Boost: " + plugin.powerups
            .getInt(plugin.getKingdom(p) + ".regen-boost") + "%");
    i3l.add(ChatColor.RED + "Cost: " + plugin.getConfig()
                                             .getInt("cost.nexusupgrades.regen-boost") + " resource points");
    i3l.add(ChatColor.RED + "Max Level: " + plugin.getConfig().getInt("max.nexusupgrades.regen-boost"));
    i3l.add(ChatColor.LIGHT_PURPLE + "Nexus Upgrade");
    i3m.setLore(i3l);
    i3.setItemMeta(i3m);

    ItemStack i4 = new ItemStack(Material.IRON_SWORD);
    ItemMeta i4m = i4.getItemMeta();
    i4m.setDisplayName(ChatColor.AQUA + "Damage Boost");
    ArrayList<String> i4l = new ArrayList<String>();
    i4l.add(ChatColor.GREEN + "Increase your damage by 1% more");
    i4l.add(ChatColor.RED + "Current Damage Boost: " + plugin.powerups
            .getInt(plugin.getKingdom(p) + ".dmg-boost") + "%");
    i4l.add(ChatColor.RED + "Cost: " + plugin.getConfig().getInt("cost.nexusupgrades.dmg-boost") + " resource points");
    i4l.add(ChatColor.RED + "Max Level: " + plugin.getConfig().getInt("max.nexusupgrades.dmg-boost"));
    i4l.add(ChatColor.LIGHT_PURPLE + "Nexus Upgrade");
    i4m.setLore(i4l);
    i4.setItemMeta(i4m);

    ItemStack i5 = new ItemStack(Material.BLAZE_ROD);
    ItemMeta i5m = i5.getItemMeta();
    i5m.setDisplayName(ChatColor.AQUA + "Champion Upgrades");
    ArrayList<String> i5l = new ArrayList<String>();
    i5l.add(ChatColor.GREEN + "Improve your defenses by upgrading");
    i5l.add(ChatColor.GREEN + "your kingdom's champion!");
    i5l.add(ChatColor.LIGHT_PURPLE + "Click to open Champion upgrades");
    i5m.setLore(i5l);
    i5.setItemMeta(i5m);

    ItemStack i6 = new ItemStack(Material.MONSTER_EGG);
    ItemMeta i6m = i6.getItemMeta();
    i6m.setDisplayName(ChatColor.AQUA + "Extra Upgrades");
    ArrayList<String> i6l = new ArrayList<String>();
    i6l.add(ChatColor.GREEN + "Miscellaneous Upgrades.");
    i6l.add(ChatColor.LIGHT_PURPLE + "Click to open Miscellaneous upgrades");
    i6m.setLore(i6l);
    i6.setItemMeta(i6m);

    ItemStack i7 = new ItemStack(Material.ENDER_PORTAL_FRAME);
    ItemMeta i7m = i7.getItemMeta();
    i7m.setDisplayName(ChatColor.AQUA + "Increase Kingdom Chest Size");
    ArrayList<String> i7l = new ArrayList<String>();
    i7l.add(ChatColor.RED + "Current Chest Size: " + plugin.kingdoms
            .getInt(plugin.getKingdom(p) + ".chestsize") + " slots");
    i7l.add(ChatColor.GREEN + "Allows more loot for the champion to capture with");
    i7l.add(ChatColor.GREEN + "the loot champion upgrade, and increases the kingdom");
    i7l.add(ChatColor.GREEN + "chest size.");
    i7l.add(ChatColor.RED + "Cost: 30 resource points for 9 more slots");
    i7l.add(ChatColor.LIGHT_PURPLE + "Nexus Upgrade");
    i7m.setLore(i7l);
    i7.setItemMeta(i7m);

    ItemStack i8 = new ItemStack(Material.DISPENSER);
    ItemMeta i8m = i8.getItemMeta();
    i8m.setDisplayName(ChatColor.AQUA + "Turrets");
    ArrayList<String> i8l = new ArrayList<String>();
    i8l.add(ChatColor.LIGHT_PURPLE + "Click to open turrets shop. Build");
    i8l.add(ChatColor.LIGHT_PURPLE + "turrets with resource points");
    i8m.setLore(i8l);
    i8.setItemMeta(i8m);

    ItemStack chest = new ItemStack(Material.CHEST);
    ItemMeta chestm = chest.getItemMeta();
    chestm.setDisplayName(ChatColor.AQUA + "Kingdom Chest");
    ArrayList<String> chestl = new ArrayList<String>();
    chestl.add(ChatColor.GREEN + "A kingdom chest to store items! Space can be upgraded.");
    chestl.add(ChatColor.LIGHT_PURPLE + "Click to open Kingdom Chest");
    chestm.setLore(chestl);
    chest.setItemMeta(chestm);

    ItemStack r = new ItemStack(Material.HAY_BLOCK);
    ItemMeta rm = r.getItemMeta();
    rm.setDisplayName(ChatColor.AQUA + "Resource Points");
    ArrayList<String> rl = new ArrayList<String>();
    rl.add(ChatColor.GREEN + "Your kingdom currently has");
    rl.add(ChatColor.DARK_AQUA + "" + plugin.rpm.getRp(plugin.getKingdom(p)) + ChatColor.GREEN + " Resource Points");
    rm.setLore(rl);
    r.setItemMeta(rm);


    nexusgui.setItem(0, i1);
    nexusgui.setItem(9, i2);
    nexusgui.setItem(10, i3);
    nexusgui.setItem(11, i4);
    nexusgui.setItem(12, i7);
    nexusgui.setItem(20, i8);
    nexusgui.setItem(18, i5);
    nexusgui.setItem(19, i6);

    nexusgui.setItem(17, r);
    nexusgui.setItem(26, chest);

    p.openInventory(nexusgui);
  }

  public void openTurretShop(Player p) {
    champions = Bukkit.createInventory(null, 27, ChatColor.AQUA + "Turret Shop");

    ItemStack i1 = new ItemStack(Material.BOW);
    ItemMeta i1m = i1.getItemMeta();
    i1m.setDisplayName(ChatColor.AQUA + "Arrow Turret");
    ArrayList<String> i1l = new ArrayList<String>();
    i1l.add(ChatColor.GREEN + "Rapidly fires at anything other than");
    i1l.add(ChatColor.GREEN + "kingdom members. One target at a time.");
    i1l.add(ChatColor.BLUE + "Range: 7 blocks");
    i1l.add(ChatColor.BLUE + "Damage: 4 hearts/shot");
    i1l.add(ChatColor.BLUE + "Attack Speed: 1/sec");
    i1l.add(ChatColor.BLUE + "Targets: One random target in range");
    i1l.add(ChatColor.RED + "Cost: 100 resource points");
    i1l.add(ChatColor.LIGHT_PURPLE + "Turret");
    i1m.setLore(i1l);
    i1.setItemMeta(i1m);

    ItemStack i2 = new ItemStack(Material.BEACON);
    ItemMeta i2m = i2.getItemMeta();
    i2m.setDisplayName(ChatColor.AQUA + "Nexus Tower");
    ArrayList<String> i2l = new ArrayList<String>();
    i2l.add(ChatColor.GREEN + "Zaps all enemies in range with the");
    i2l.add(ChatColor.GREEN + "power of lightning");
    i2l.add(ChatColor.RED + "Can only be placed in the nexus chunk");
    i2l.add(ChatColor.BLUE + "Range: 5 blocks");
    i2l.add(ChatColor.BLUE + "Damage: 3 hearts/shot");
    i2l.add(ChatColor.BLUE + "Attack Speed: 1/sec");
    i2l.add(ChatColor.BLUE + "Targets: All targets in range");
    i2l.add(ChatColor.RED + "Cost: 300 resource points");
    i2l.add(ChatColor.LIGHT_PURPLE + "Turret");
    i2m.setLore(i2l);
    i2.setItemMeta(i2m);

    ItemStack i3 = new ItemStack(Material.LAVA_BUCKET);
    ItemMeta i3m = i3.getItemMeta();
    i3m.setDisplayName(ChatColor.AQUA + "Lava Tower");
    ArrayList<String> i3l = new ArrayList<String>();
    i3l.add(ChatColor.GREEN + "Zaps all enemies in range with the");
    i3l.add(ChatColor.GREEN + "power of lightning");
    i3l.add(ChatColor.BLUE + "Range: 5 blocks");
    i3l.add(ChatColor.BLUE + "Damage: 3 hearts/shot");
    i3l.add(ChatColor.BLUE + "Attack Speed: 1/sec");
    i3l.add(ChatColor.BLUE + "Targets: All targets in range");
    i3l.add(ChatColor.RED + "Cost: 300 resource points");
    i3l.add(ChatColor.LIGHT_PURPLE + "Turret");
    i3m.setLore(i3l);
    i3.setItemMeta(i3m);

    champions.addItem(i1);
    //champions.addItem(i2);
    p.openInventory(champions);
  }


  public void openChampionMenu(Player p) {
    champions = Bukkit.createInventory(null, 27, ChatColor.AQUA + plugin.getKingdom(p) + "'s Champion");

    ItemStack i1 = new ItemStack(Material.DIAMOND_SWORD);
    ItemMeta i1m = i1.getItemMeta();
    i1m.setDisplayName(ChatColor.AQUA + "Champion Weapon");
    ArrayList<String> i1l = new ArrayList<String>();
    i1l.add(ChatColor.GREEN + "Provides your champion with a better weapon");
    i1l.add(ChatColor.GREEN + "each upgrade. Max level is 4");
    i1l.add(ChatColor.DARK_PURPLE + "Current Champion Weapon Level: " + plugin.kingdoms
            .getInt(plugin.getKingdom(p) + ".champion.weapon"));
    i1l.add(ChatColor.RED + "Cost: 10 resource points");
    i1l.add(ChatColor.LIGHT_PURPLE + "Champion Upgrade");
    i1m.setLore(i1l);
    i1.setItemMeta(i1m);

    ItemStack i2 = new ItemStack(Material.DIAMOND_CHESTPLATE);
    ItemMeta i2m = i2.getItemMeta();
    i2m.setDisplayName(ChatColor.AQUA + "Champion Health");
    ArrayList<String> i2l = new ArrayList<String>();
    i2l.add(ChatColor.GREEN + "Each upgrade increases champion health");
    i2l.add(ChatColor.GREEN + "by 2");
    i2l.add(ChatColor.DARK_PURPLE + "Current Champion Health: " + plugin.kingdoms
            .getInt(plugin.getKingdom(p) + ".champion.health"));
    i2l.add(ChatColor.RED + "Cost: 2 resource points");
    i2l.add(ChatColor.LIGHT_PURPLE + "Champion Upgrade");
    i2m.setLore(i2l);
    i2.setItemMeta(i2m);

    ItemStack i3 = new ItemStack(Material.BRICK);
    ItemMeta i3m = i3.getItemMeta();
    i3m.setDisplayName(ChatColor.AQUA + "Champion Resistance");
    ArrayList<String> i3l = new ArrayList<String>();
    i3l.add(ChatColor.GREEN + "Increase Champion's resistance, with a");
    i3l.add(ChatColor.GREEN + "chance to prevent knockback. 20% each");
    i3l.add(ChatColor.GREEN + "upgrade. Max: 100%");
    i3l.add(ChatColor.RED + "Current Resistance: " + plugin.kingdoms
            .getInt(plugin.getKingdom(p) + ".champion.resist") + "%");
    i3l.add(ChatColor.RED + "Cost: 5 resource points");
    i3l.add(ChatColor.LIGHT_PURPLE + "Champion Upgrade");
    i3m.setLore(i3l);
    i3.setItemMeta(i3m);

    ItemStack i4 = new ItemStack(Material.QUARTZ);
    ItemMeta i4m = i4.getItemMeta();
    i4m.setDisplayName(ChatColor.AQUA + "Champion Speed");
    ArrayList<String> i4l = new ArrayList<String>();
    i4l.add(ChatColor.GREEN + "Increases Champion Speed. 30% more");
    i4l.add(ChatColor.GREEN + "per upgrade. Max level is 3.");
    i4l.add(ChatColor.RED + "Current Champion Speed Level: " + plugin.kingdoms
            .getInt(plugin.getKingdom(p) + ".champion.speed"));
    i4l.add(ChatColor.RED + "Cost: 20 resource points");
    i4l.add(ChatColor.LIGHT_PURPLE + "Champion Upgrade");
    i4m.setLore(i4l);
    i4.setItemMeta(i4m);

    ItemStack i5 = new ItemStack(Material.ENDER_PEARL);
    ItemMeta i5m = i5.getItemMeta();
    i5m.setDisplayName(ChatColor.AQUA + "Drag");
    ArrayList<String> i5l = new ArrayList<String>();
    i5l.add(ChatColor.GREEN + "When the player is more than 5 blocks");
    i5l.add(ChatColor.GREEN + "away, the champion pulls the player");
    i5l.add(ChatColor.GREEN + "to the champion's location.");
    i5l.add(ChatColor.RED + "Enabled: " + (plugin.kingdoms.getInt(plugin.getKingdom(p) + ".champion.drag") > 0));
    i5l.add(ChatColor.RED + "Cost: 30 resource points");
    i5l.add(ChatColor.LIGHT_PURPLE + "Champion Upgrade");
    i5m.setLore(i5l);
    i5.setItemMeta(i5m);

    ItemStack i6 = new ItemStack(Material.FEATHER);
    ItemMeta i6m = i6.getItemMeta();
    i6m.setDisplayName(ChatColor.AQUA + "Mock");
    ArrayList<String> i6l = new ArrayList<String>();
    i6l.add(ChatColor.GREEN + "While dueling the champion, the invader");
    i6l.add(ChatColor.GREEN + "cannot place or break blocks in a range");
    i6l.add(ChatColor.GREEN + "of the champion.");
    i6l.add(ChatColor.RED + "Current Mock Range: " + plugin.kingdoms
            .getInt(plugin.getKingdom(p) + ".champion.mock") + " blocks");
    i6l.add(ChatColor.RED + "Cost: 10 resource points");
    i6l.add(ChatColor.LIGHT_PURPLE + "Champion Upgrade");
    i6m.setLore(i6l);
    i6.setItemMeta(i6m);

    ItemStack i7 = new ItemStack(Material.GOLD_SWORD);
    ItemMeta i7m = i7.getItemMeta();
    i7m.setDisplayName(ChatColor.AQUA + "Death Duel");
    ArrayList<String> i7l = new ArrayList<String>();
    i7l.add(ChatColor.GREEN + "Only the invader can damage the champion");
    i7l.add(ChatColor.GREEN + "and the champion will only savagely");
    i7l.add(ChatColor.GREEN + "target the invader");
    i7l.add(ChatColor.RED + "Enabled: " + (plugin.kingdoms.getInt(plugin.getKingdom(p) + ".champion.duel") > 0));
    i7l.add(ChatColor.RED + "Cost: 100 resource points");
    i7l.add(ChatColor.LIGHT_PURPLE + "Champion Upgrade");
    i7m.setLore(i7l);
    i7.setItemMeta(i7m);

    ItemStack r = new ItemStack(Material.HAY_BLOCK);
    ItemMeta rm = r.getItemMeta();
    rm.setDisplayName(ChatColor.AQUA + "Resource Points");
    ArrayList<String> rl = new ArrayList<String>();
    rl.add(ChatColor.GREEN + "Your kingdom currently has");
    rl.add(ChatColor.DARK_AQUA + "" + plugin.rpm.getRp(plugin.getKingdom(p)) + ChatColor.GREEN + " Resource Points");
    rm.setLore(rl);
    r.setItemMeta(rm);

    ItemStack backbtn = new ItemStack(Material.REDSTONE_BLOCK);
    ItemMeta backbtnmeta = backbtn.getItemMeta();
    backbtnmeta.setDisplayName(ChatColor.RED + "Return to main menu");
    backbtn.setItemMeta(backbtnmeta);

    champions.setItem(0, i1);
    champions.setItem(1, i2);
    champions.setItem(2, i3);
    champions.setItem(3, i4);
    champions.setItem(4, i5);
    champions.setItem(5, i6);
    champions.setItem(6, i7);
    champions.setItem(17, r);
    champions.setItem(26, backbtn);

    p.openInventory(champions);
  }

  public void openMisMenu(Player p) {
    champions = Bukkit.createInventory(null, 27, ChatColor.AQUA + "Extra Upgrades");

    ItemStack i1 = new ItemStack(Material.SULPHUR);
    ItemMeta i1m = i1.getItemMeta();
    i1m.setDisplayName(ChatColor.AQUA + "Anti-Creeper");
    ArrayList<String> i1l = new ArrayList<String>();
    i1l.add(ChatColor.GREEN + "When a creeper explodes in your ");
    i1l.add(ChatColor.GREEN + "territory, it will do no block damage");
    i1l.add(ChatColor.GREEN + "or damage your members.");
    if (hasMisUpgrade(plugin.getKingdom(p), "anticreeper")) {
      i1l.add(ChatColor.DARK_PURPLE + "Enabled!");
    } else {
      i1l.add(ChatColor.RED + "Cost: 50 resource points");
    }
    i1l.add(ChatColor.LIGHT_PURPLE + "Mis Upgrade");
    i1m.setLore(i1l);
    i1.setItemMeta(i1m);

    ItemStack i2 = new ItemStack(Material.SEEDS);
    ItemMeta i2m = i2.getItemMeta();
    i2m.setDisplayName(ChatColor.AQUA + "Anti-Trample");
    ArrayList<String> i2l = new ArrayList<String>();
    i2l.add(ChatColor.GREEN + "Your farms can't be trampled");
    i2l.add(ChatColor.GREEN + "by mobs or players.");
    if (hasMisUpgrade(plugin.getKingdom(p), "antitrample")) {
      i2l.add(ChatColor.DARK_PURPLE + "Enabled!");
    } else {
      i2l.add(ChatColor.RED + "Cost: 10 resource points");
    }
    i2l.add(ChatColor.LIGHT_PURPLE + "Mis Upgrade");
    i2m.setLore(i2l);
    i2.setItemMeta(i2m);

    ItemStack i3 = new ItemStack(Material.IRON_AXE);
    ItemMeta i3m = i3.getItemMeta();
    i3m.setDisplayName(ChatColor.AQUA + "Nexus Guard");
    ArrayList<String> i3l = new ArrayList<String>();
    i3l.add(ChatColor.GREEN + "When an enemy mines your nexus,");
    i3l.add(ChatColor.GREEN + "a nexus guard will spawn to try");
    i3l.add(ChatColor.GREEN + "and defend it. Nexus guards");
    i3l.add(ChatColor.GREEN + "have 30 health and the weapon of");
    i3l.add(ChatColor.GREEN + "your champion.");
    if (hasMisUpgrade(plugin.getKingdom(p), "nexusguard")) {
      i3l.add(ChatColor.DARK_PURPLE + "Enabled!");
    } else {
      i3l.add(ChatColor.RED + "Cost: 100 resource points");
    }
    i3l.add(ChatColor.LIGHT_PURPLE + "Mis Upgrade");
    i3m.setLore(i3l);
    i3.setItemMeta(i3m);

    ItemStack i4 = new ItemStack(Material.NETHER_STAR);
    ItemMeta i4m = i4.getItemMeta();
    i4m.setDisplayName(ChatColor.AQUA + "Glory");
    ArrayList<String> i4l = new ArrayList<String>();
    i4l.add(ChatColor.GREEN + "When you kill something in your");
    i4l.add(ChatColor.GREEN + "land, gain three times the xp");
    i4l.add(ChatColor.GREEN + "from the mob.");
    if (hasMisUpgrade(plugin.getKingdom(p), "glory")) {
      i4l.add(ChatColor.DARK_PURPLE + "Enabled!");
    } else {
      i4l.add(ChatColor.RED + "Cost: 60 resource points");
    }
    i4l.add(ChatColor.LIGHT_PURPLE + "Mis Upgrade");
    i4m.setLore(i4l);
    i4.setItemMeta(i4m);

    ItemStack i5 = new ItemStack(Material.TNT);
    ItemMeta i5m = i5.getItemMeta();
    i5m.setDisplayName(ChatColor.AQUA + "Bomb Expertise");
    ArrayList<String> i5l = new ArrayList<String>();
    i5l.add(ChatColor.GREEN + "When an explosion goes off in your");
    i5l.add(ChatColor.GREEN + "land, the blocks regenerate after");
    i5l.add(ChatColor.GREEN + "some time. In the explosion,");
    i5l.add(ChatColor.GREEN + "nearby non-members are smited.");
    if (hasMisUpgrade(plugin.getKingdom(p), "bombshards")) {
      i5l.add(ChatColor.DARK_PURPLE + "Enabled!");
    } else {
      i5l.add(ChatColor.RED + "Cost: 100 resource points");
    }
    i5l.add(ChatColor.LIGHT_PURPLE + "Mis Upgrade");
    i5m.setLore(i5l);
    i5.setItemMeta(i5m);

    ItemStack r = new ItemStack(Material.HAY_BLOCK);
    ItemMeta rm = r.getItemMeta();
    rm.setDisplayName(ChatColor.AQUA + "Resource Points");
    ArrayList<String> rl = new ArrayList<String>();
    rl.add(ChatColor.GREEN + "Your kingdom currently has");
    rl.add(ChatColor.DARK_AQUA + "" + plugin.rpm.getRp(plugin.getKingdom(p)) + ChatColor.GREEN + " Resource Points");
    rm.setLore(rl);
    r.setItemMeta(rm);


    ItemStack backbtn = new ItemStack(Material.REDSTONE_BLOCK);
    ItemMeta backbtnmeta = backbtn.getItemMeta();
    backbtnmeta.setDisplayName(ChatColor.RED + "Return to main menu");
    backbtn.setItemMeta(backbtnmeta);

    champions.setItem(0, i1);
    champions.setItem(1, i2);
    champions.setItem(2, i3);
    champions.setItem(3, i4);
    champions.setItem(4, i5);
    champions.setItem(17, r);
    champions.setItem(26, backbtn);

    p.openInventory(champions);
  }

  public boolean hasMisUpgrade(String kingdom, String upgrade) {
    boolean boo = plugin.misupgrades.getBoolean(kingdom + "." + upgrade);

    return boo;
  }

  public void upgradeMis(Player p, String upgrade) {
    int cost = 0;
    String kingdom = plugin.getKingdom(p);
    if (plugin.isKing(p) || plugin.isMod(plugin.getKingdom(p), p)) {
      if (!hasMisUpgrade(kingdom, upgrade)) {
        if (upgrade.equalsIgnoreCase("anticreeper")) {
          cost = 50;
          if (plugin.rpm.hasAmtRp(kingdom, cost)) {
            plugin.rpm.minusRP(kingdom, cost);
            plugin.misupgrades.set(kingdom + "." + upgrade, true);
            plugin.saveMisupgrades();
            p.sendMessage(ChatColor.GREEN + "Anti-Creeper upgrade acquired!");
            p.closeInventory();
            openMisMenu(p);
          } else {
            p.sendMessage(ChatColor.RED + "You don't have enough resource points.");
          }
        } else if (upgrade.equalsIgnoreCase("antitrample")) {
          cost = 10;
          if (plugin.rpm.hasAmtRp(kingdom, cost)) {
            plugin.rpm.minusRP(kingdom, cost);
            plugin.misupgrades.set(kingdom + "." + upgrade, true);
            plugin.saveMisupgrades();
            p.sendMessage(ChatColor.GREEN + "Anti-Trample upgrade acquired!");
            p.closeInventory();
            openMisMenu(p);
          } else {
            p.sendMessage(ChatColor.RED + "You don't have enough resource points.");
          }
        } else if (upgrade.equalsIgnoreCase("nexusguard")) {
          cost = 100;
          if (plugin.rpm.hasAmtRp(kingdom, cost)) {
            plugin.rpm.minusRP(kingdom, cost);
            plugin.misupgrades.set(kingdom + "." + upgrade, true);
            plugin.saveMisupgrades();
            p.sendMessage(ChatColor.GREEN + "Nexus Guard upgrade acquired!");
            p.closeInventory();
            openMisMenu(p);
          } else {
            p.sendMessage(ChatColor.RED + "You don't have enough resource points.");
          }
        } else if (upgrade.equalsIgnoreCase("glory")) {
          cost = 60;
          if (plugin.rpm.hasAmtRp(kingdom, cost)) {
            plugin.rpm.minusRP(kingdom, cost);
            plugin.misupgrades.set(kingdom + "." + upgrade, true);
            plugin.saveMisupgrades();
            p.sendMessage(ChatColor.GREEN + "Glory upgrade acquired!");
            p.closeInventory();
            openMisMenu(p);
          } else {
            p.sendMessage(ChatColor.RED + "You don't have enough resource points.");
          }
        } else if (upgrade.equalsIgnoreCase("bombshards")) {
          cost = 100;
          if (plugin.rpm.hasAmtRp(kingdom, cost)) {
            plugin.rpm.minusRP(kingdom, cost);
            plugin.misupgrades.set(kingdom + "." + upgrade, true);
            plugin.saveMisupgrades();
            p.sendMessage(ChatColor.GREEN + "Bomb Expertise upgrade acquired!");
            p.closeInventory();
            openMisMenu(p);
          } else {
            p.sendMessage(ChatColor.RED + "You don't have enough resource points.");
          }
        }
      } else {
        p.sendMessage(ChatColor.RED + "This upgrade is already enabled!");
      }
    }
  }

  public void openKingdomChest(Player p) {
    if (plugin.hasKingdom(p)) {
      int chestlevel = plugin.kingdoms.getInt(plugin.getKingdom(p) + ".chestsize");
      this.kchest = Bukkit.createInventory(null, chestlevel, ChatColor.AQUA + "Kingdom Chest");
      if (plugin.chest.getList(plugin.getKingdom(p)) == null) {
        plugin.chest.set(plugin.getKingdom(p), new ArrayList<String>());
        plugin.saveChests();
      }
      for (Object obj : plugin.chest.getList(plugin.getKingdom(p))) {
        if (obj instanceof ItemStack) {
          kchest.addItem((ItemStack) obj);
        }
      }

      p.closeInventory();
      p.openInventory(kchest);
    }
  }
}
