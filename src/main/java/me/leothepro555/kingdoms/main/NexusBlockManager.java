package me.leothepro555.kingdoms.main;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class NexusBlockManager implements Listener{

	private Kingdoms plugin;
	private Inventory nexusgui;
	private Inventory dumpgui;
	private Inventory champions;
	private int rpi = 10;
	public NexusBlockManager(Kingdoms plugin){
		this.plugin = plugin;
		if(plugin.getConfig().get("items-needed-for-one-resource-point") != null){
			this.rpi = plugin.getConfig().getInt("items-needed-for-one-resource-point");
		}
	}
	
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
		if(event.getClickedBlock().getType() == Material.BEACON){
			if(event.getClickedBlock().hasMetadata("nexusblock")){
				event.setCancelled(true);
				if(!plugin.getChunkKingdom(event.getClickedBlock().getLocation().getChunk()).equals(plugin.getKingdom(p))){
					if(!plugin.isAlly(plugin.getChunkKingdom(event.getClickedBlock().getLocation().getChunk()),p)){
					p.sendMessage(ChatColor.RED + "You can't use a nexus that doesn't belong to your kingdom!");
				}else{
					dumpgui = Bukkit.createInventory(null, 54, ChatColor.DARK_BLUE + "Donate to " + ChatColor.DARK_GREEN + plugin.getChunkKingdom(event.getClickedBlock().getLocation().getChunk()));
				p.openInventory(dumpgui);
				}
					if(plugin.isKAlly(plugin.getKingdom(p), plugin.getChunkKingdom(event.getClickedBlock().getLocation().getChunk()))){
					p.sendMessage(ChatColor.RED + "You marked " + plugin.getChunkKingdom(event.getClickedBlock().getLocation().getChunk()) + " as an ally, but they did not mark your kingdom as their ally");
				}
				}else if(plugin.getChunkKingdom(event.getClickedBlock().getLocation().getChunk()).equals(plugin.getKingdom(p))){
					
					openNexusGui(p);
					
					
					
				}
			}
			}
		}
	}
	
	@SuppressWarnings("unused")
	@EventHandler
	public void onInventoryMove(InventoryClickEvent event){
		try{
		Player p = (Player) event.getWhoClicked();
		if(event.getInventory().getName().endsWith(ChatColor.AQUA + plugin.getKingdom(p) + "'s nexus")){
			event.setCancelled(true);
			if(event.getCurrentItem() != null){
				if(event.getCurrentItem().getItemMeta() != null){
					if(event.getCurrentItem().getItemMeta().getLore() != null){
						if(event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "Nexus Upgrade")){
							event.setCancelled(true);
							ItemStack item = event.getCurrentItem();
							int rp = plugin.kingdoms.getInt(plugin.getKingdom(p) + ".resourcepoints");
							if(plugin.isMod(plugin.getKingdom(p), p) ||
									plugin.isKing(p)){
							
							if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "Damage Reduction")){
								if(plugin.hasAmtRp(plugin.getKingdom(p), 10)){
									plugin.minusRP(plugin.getKingdom(p), 10);
									upgradePowerup(plugin.getKingdom(p), "dmg-reduction", 1);
									p.sendMessage(ChatColor.GREEN + "Damage reduction upgraded! Total: " + plugin.powerups.getInt(plugin.getKingdom(p) + ".dmg-reduction"));
								p.closeInventory();
								openNexusGui(p);
								}else{
									p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
								}
							}
							
							if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "Regeneration Boost")){
								if(plugin.hasAmtRp(plugin.getKingdom(p), 20)){
									plugin.minusRP(plugin.getKingdom(p), 20);
									upgradePowerup(plugin.getKingdom(p), "regen-boost", 5);
									p.sendMessage(ChatColor.GREEN + "Regeneration boost upgraded! Total: " + plugin.powerups.getInt(plugin.getKingdom(p) + ".regen-boost"));
								p.closeInventory();
								openNexusGui(p);
								}else{
									p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
								}
							}
							
							if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "Damage Boost")){
								if(plugin.hasAmtRp(plugin.getKingdom(p), 20)){
									plugin.minusRP(plugin.getKingdom(p), 20);
									upgradePowerup(plugin.getKingdom(p), "dmg-boost", 1);
									p.sendMessage(ChatColor.GREEN + "Damage boost upgraded! Total: " + plugin.powerups.getInt(plugin.getKingdom(p) + ".dmg-boost"));
								p.closeInventory();
								openNexusGui(p);
								}else{
									p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
								}
							}
						}else{
							p.sendMessage(ChatColor.RED + "Only kingdom kings and mods can upgrade bonuses");						
							}
						}else if(event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "Nexus Option")){
							event.setCancelled(true);
							ItemStack item = event.getCurrentItem();
							
							if(item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Convert items to resource points")){
								
								dumpgui = Bukkit.createInventory(null, 54, ChatColor.DARK_BLUE + "Close inventory to confirm.");
								p.openInventory(dumpgui);
							}
							
						}else if(event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "Click to open Champion upgrades")){
							if(plugin.isMod(plugin.getKingdom(p), p) ||
									plugin.isKing(p)){
							event.setCancelled(true);
							openChampionMenu(p);
							}else{
								p.sendMessage(ChatColor.RED + "Only kingdom kings and mods can upgrade bonuses");
								p.closeInventory();
								}
						}else if(event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "Click to open Miscellaneous upgrades")){
							if(plugin.isMod(plugin.getKingdom(p), p) ||
									plugin.isKing(p)){
							event.setCancelled(true);
							openMisMenu(p);
							}else{
								p.sendMessage(ChatColor.RED + "Only kingdom kings and mods can upgrade bonuses");
								p.closeInventory();
								}
						}
					}
				}
			}
		}else if(event.getInventory().getName().equals(ChatColor.AQUA + plugin.getKingdom(p) + "'s Champion")){
			event.setCancelled(true);
			ItemStack item = event.getCurrentItem();
			
			if(item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Champion Weapon")){
				if(getChampionUpgrade(plugin.getKingdom(p), "weapon") < 4){
				if(plugin.hasAmtRp(plugin.getKingdom(p), 10)){
					plugin.minusRP(plugin.getKingdom(p), 10);
					upgradeChampion(plugin.getKingdom(p), "weapon", 1);
					p.sendMessage(ChatColor.GREEN + "Champion Weapon upgraded! Total: " + getChampionUpgrade(plugin.getKingdom(p), "weapon"));
				p.closeInventory();
				openChampionMenu(p);
				}else{
					p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
				}
			}else{
				p.sendMessage(ChatColor.RED + "This upgrade is at its maximum level.");
			}
			}
			
			if(item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Champion Resistance")){
				if(getChampionUpgrade(plugin.getKingdom(p), "resist") < 100){
				if(plugin.hasAmtRp(plugin.getKingdom(p), 5)){
					plugin.minusRP(plugin.getKingdom(p), 5);
					upgradeChampion(plugin.getKingdom(p), "resist", 20);
					p.sendMessage(ChatColor.GREEN + "Champion Resistance upgraded! Total: " + getChampionUpgrade(plugin.getKingdom(p), "resist"));
				p.closeInventory();
				openChampionMenu(p);
				}else{
					p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
				}
			}else{
				p.sendMessage(ChatColor.RED + "This upgrade is at its maximum level.");
			}
			}
			
			if(item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Champion Speed")){
				if(getChampionUpgrade(plugin.getKingdom(p), "speed") < 3){
				if(plugin.hasAmtRp(plugin.getKingdom(p), 20)){
					plugin.minusRP(plugin.getKingdom(p), 20);
					upgradeChampion(plugin.getKingdom(p), "speed", 1);
					p.sendMessage(ChatColor.GREEN + "Champion Speed upgraded! Total: " + getChampionUpgrade(plugin.getKingdom(p), "speed"));
				p.closeInventory();
				openChampionMenu(p);
				}else{
					p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
				}
			}else{
				p.sendMessage(ChatColor.RED + "This upgrade is at its maximum level.");
			}
			}
			
			if(item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Champion Health")){
				if(plugin.hasAmtRp(plugin.getKingdom(p), 2)){
					plugin.minusRP(plugin.getKingdom(p), 2);
					upgradeChampion(plugin.getKingdom(p), "health", 2);
					p.sendMessage(ChatColor.GREEN + "Champion Health upgraded! Total: " + getChampionUpgrade(plugin.getKingdom(p), "health"));
				p.closeInventory();
				openChampionMenu(p);
				}else{
					p.sendMessage(ChatColor.RED + "You don't have enough resource points for this upgrade!");
				}

			}
			
		}else if(event.getInventory().getName().equals(ChatColor.AQUA + "Extra Upgrades")){
			event.setCancelled(true);
			ItemStack item = event.getCurrentItem();
			
			if(item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Anti-Creeper")){
				this.upgradeMis(p, "anticreeper");
			}
			
			if(item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Anti-Trample")){
				this.upgradeMis(p, "antitrample");
			}
			
			if(item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Nexus Guard")){
				this.upgradeMis(p, "nexusguard");
			}
			
			if(item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Glory")){
				this.upgradeMis(p, "glory");
			}
			
			if(item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Bomb Expertise")){
				this.upgradeMis(p, "bombshards");
			}
		}
	}catch(NullPointerException e){
	}
	}
	
	public int getChampionUpgrade(String kingdom, String powerup){
		return plugin.kingdoms.getInt(kingdom + ".champion." + powerup);
	}
	
	public void upgradeChampion(String kingdom, String powerup, int amount){
		plugin.kingdoms.set(kingdom + ".champion." + powerup, plugin.kingdoms.getInt(kingdom + ".champion." + powerup) + amount);
	plugin.saveKingdoms();
	}
	
	public void upgradePowerup(String kingdom, String powerup, int amount){
		plugin.powerups.set(kingdom + "." + powerup, plugin.powerups.getInt(kingdom + "." + powerup) + amount);
	plugin.savePowerups();
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		Player p = (Player) event.getPlayer();
		if(event.getInventory().getName().equals(ChatColor.DARK_BLUE + "Close inventory to confirm.")){
			int donatedamt = donateItems(event.getInventory().getContents(), plugin.getKingdom(p),p);
		((Player) event.getPlayer()).sendMessage(ChatColor.GREEN + "Your kingdom gained " + donatedamt + " resource points");
		}else if(event.getInventory().getName().startsWith(ChatColor.DARK_BLUE + "Donate to " + ChatColor.DARK_GREEN)){
			String[] nsplit = event.getInventory().getName().split(" ");
			String kingdom = ChatColor.stripColor(nsplit[nsplit.length - 1]);
			if(event.getInventory().getContents().length > 0){
				int donatedamt = donateItems(event.getInventory().getContents(), kingdom,p);
		
		((Player) event.getPlayer()).sendMessage(ChatColor.GREEN + kingdom + " has gained " + donatedamt + " resource points. Thank you for your donation!");
			plugin.messageKingdomPlayers(kingdom, ChatColor.GREEN + event.getPlayer().getName() + " has donated " + donatedamt + " resource points");
		}else{
			((Player) event.getPlayer()).sendMessage(ChatColor.GREEN + kingdom + " has gained 0 resource points.");
		}
		}
	}
	
	public int donateItems(ItemStack[] items, String kingdom, Player p){
		int rpdonated = 0;
		for(ItemStack item:items){
			if(item != null){
			if(!plugin.blacklistitems.contains(item.getType())){
				int amt = item.getAmount();
				if(!plugin.specialcaseitems.containsKey(item.getType())){
				rpdonated = rpdonated + amt;
				}else{
					amt = amt * plugin.specialcaseitems.get(item.getType());
					rpdonated = rpdonated + amt;
				}
			}else{
				if(p != null){
					p.sendMessage(ChatColor.RED + item.getType().name() + " cannot be traded for resource points.");
				    p.getWorld().dropItemNaturally(p.getLocation(), item);
				}
			}
		}
			}
		float i = rpdonated/rpi;
		String newint = Float.toString(i);
		String[] split = newint.split("\\.");
		plugin.addRP(kingdom, Integer.parseInt(split[0]));
		plugin.saveKingdoms();
		return Integer.parseInt(split[0]);
		
	}
	
	public void openNexusGui(Player p){
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
		i2l.add(ChatColor.RED + "Current Damage Reduction: " + plugin.powerups.getInt(plugin.getKingdom(p) + ".dmg-reduction")  + "%");
		i2l.add(ChatColor.RED + "Cost: 10 resource points");
		i2l.add(ChatColor.LIGHT_PURPLE + "Nexus Upgrade");
		i2m.setLore(i2l);
		i2.setItemMeta(i2m);
		
		ItemStack i3 = new ItemStack(Material.RED_ROSE);
		ItemMeta i3m = i3.getItemMeta();
		i3m.setDisplayName(ChatColor.AQUA + "Regeneration Boost");
		ArrayList<String> i3l = new ArrayList<String>();
		i3l.add(ChatColor.GREEN + "While in your own land, boost your health regeneration");
		i3l.add(ChatColor.GREEN + "by 5% more.");
		i3l.add(ChatColor.RED + "Current Regeneration Boost: " + plugin.powerups.getInt(plugin.getKingdom(p) + ".regen-boost")  + "%");
		i3l.add(ChatColor.RED + "Cost: 20 resource points");
		i3l.add(ChatColor.LIGHT_PURPLE + "Nexus Upgrade");
		i3m.setLore(i3l);
		i3.setItemMeta(i3m);
		
		ItemStack i4 = new ItemStack(Material.IRON_SWORD);
		ItemMeta i4m = i4.getItemMeta();
		i4m.setDisplayName(ChatColor.AQUA + "Damage Boost");
		ArrayList<String> i4l = new ArrayList<String>();
		i4l.add(ChatColor.GREEN + "Increase your damage by 1% more");
		i4l.add(ChatColor.RED + "Current Damage Boost: " + plugin.powerups.getInt(plugin.getKingdom(p) + ".dmg-boost")  + "%");
		i4l.add(ChatColor.RED + "Cost: 20 resource points");
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
		
		ItemStack r = new ItemStack(Material.HAY_BLOCK);
		ItemMeta rm = r.getItemMeta();
		rm.setDisplayName(ChatColor.AQUA + "Resource Points");
		ArrayList<String> rl = new ArrayList<String>();
		rl.add(ChatColor.GREEN + "Your kingdom currently has");
		rl.add(ChatColor.DARK_AQUA + "" + plugin.getRp(plugin.getKingdom(p)) + ChatColor.GREEN + " Resource Points");
		rm.setLore(rl);
		r.setItemMeta(rm);
		
		nexusgui.setItem(0, i1);
		nexusgui.setItem(9, i2);
		nexusgui.setItem(10, i3);
		nexusgui.setItem(11, i4);
		nexusgui.setItem(18, i5);
		nexusgui.setItem(19, i6);
		
		nexusgui.setItem(17, r);
		
		p.openInventory(nexusgui);
	}
	
	
	public void openChampionMenu(Player p){
		champions = Bukkit.createInventory(null, 27, ChatColor.AQUA + plugin.getKingdom(p) + "'s Champion");
		
		ItemStack i1 = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta i1m = i1.getItemMeta();
		i1m.setDisplayName(ChatColor.AQUA + "Champion Weapon");
		ArrayList<String> i1l = new ArrayList<String>();
		i1l.add(ChatColor.GREEN + "Provides your champion with a better weapon");
		i1l.add(ChatColor.GREEN + "each upgrade. Max level is 4");
		i1l.add(ChatColor.DARK_PURPLE + "Current Champion Weapon Level: " + plugin.kingdoms.getInt(plugin.getKingdom(p) + ".champion.weapon"));
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
		i2l.add(ChatColor.DARK_PURPLE + "Current Champion Health: " + plugin.kingdoms.getInt(plugin.getKingdom(p) + ".champion.health"));
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
		i3l.add(ChatColor.RED + "Current Resistance: " + plugin.kingdoms.getInt(plugin.getKingdom(p) + ".champion.resist")  + "%");
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
		i4l.add(ChatColor.RED + "Current Champion Speed Level: " + plugin.kingdoms.getInt(plugin.getKingdom(p) + ".champion.speed"));
		i4l.add(ChatColor.RED + "Cost: 20 resource points");
		i4l.add(ChatColor.LIGHT_PURPLE + "Champion Upgrade");
		i4m.setLore(i4l);
		i4.setItemMeta(i4m);
		
		ItemStack r = new ItemStack(Material.HAY_BLOCK);
		ItemMeta rm = r.getItemMeta();
		rm.setDisplayName(ChatColor.AQUA + "Resource Points");
		ArrayList<String> rl = new ArrayList<String>();
		rl.add(ChatColor.GREEN + "Your kingdom currently has");
		rl.add(ChatColor.DARK_AQUA + "" + plugin.getRp(plugin.getKingdom(p)) + ChatColor.GREEN + " Resource Points");
		rm.setLore(rl);
		r.setItemMeta(rm);
		
		champions.setItem(0, i1);
		champions.setItem(1, i2);
		champions.setItem(2, i3);
		champions.setItem(3, i4);
		champions.setItem(17, r);
		
		p.openInventory(champions);
	}
	
	public void openMisMenu(Player p){
		champions = Bukkit.createInventory(null, 27, ChatColor.AQUA + "Extra Upgrades");
		
		ItemStack i1 = new ItemStack(Material.SULPHUR);
		ItemMeta i1m = i1.getItemMeta();
		i1m.setDisplayName(ChatColor.AQUA + "Anti-Creeper");
		ArrayList<String> i1l = new ArrayList<String>();
		i1l.add(ChatColor.GREEN + "When a creeper explodes in your ");
		i1l.add(ChatColor.GREEN + "territory, it will do no block damage");
		i1l.add(ChatColor.GREEN + "or damage your members.");
		if(hasMisUpgrade(plugin.getKingdom(p), "anticreeper")){
			i1l.add(ChatColor.DARK_PURPLE + "Enabled!");
		}else{
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
		if(hasMisUpgrade(plugin.getKingdom(p), "antitrample")){
			i2l.add(ChatColor.DARK_PURPLE + "Enabled!");
		}else{
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
		if(hasMisUpgrade(plugin.getKingdom(p), "nexusguard")){
			i3l.add(ChatColor.DARK_PURPLE + "Enabled!");
		}else{
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
		if(hasMisUpgrade(plugin.getKingdom(p), "glory")){
			i4l.add(ChatColor.DARK_PURPLE + "Enabled!");
		}else{
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
		if(hasMisUpgrade(plugin.getKingdom(p), "bombshards")){
			i5l.add(ChatColor.DARK_PURPLE + "Enabled!");
		}else{
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
		rl.add(ChatColor.DARK_AQUA + "" + plugin.getRp(plugin.getKingdom(p)) + ChatColor.GREEN + " Resource Points");
		rm.setLore(rl);
		r.setItemMeta(rm);
		
		champions.setItem(0, i1);
		champions.setItem(1, i2);
		champions.setItem(2, i3);
		champions.setItem(3, i4);
		champions.setItem(4, i5);
		champions.setItem(17, r);
		
		p.openInventory(champions);
	}
	
	public boolean hasMisUpgrade(String kingdom, String upgrade){
		boolean boo = plugin.misupgrades.getBoolean(kingdom + "." + upgrade);
		
		return boo;
	}
	
	public void upgradeMis(Player p, String upgrade){
		int cost = 0;
		String kingdom = plugin.getKingdom(p);
		if(plugin.isKing(p) || plugin.isMod(plugin.getKingdom(p), p)){
			if(!hasMisUpgrade(kingdom, upgrade)){
		if(upgrade.equalsIgnoreCase("anticreeper")){
			cost = 50;
			if(plugin.hasAmtRp(kingdom, cost)){
				plugin.minusRP(kingdom, cost);
				plugin.misupgrades.set(kingdom + "." + upgrade, true);
				plugin.saveMisupgrades();
				p.sendMessage(ChatColor.GREEN + "Anti-Creeper upgrade acquired!");
				p.closeInventory();
			    openMisMenu(p);
			}else{
				p.sendMessage(ChatColor.RED + "You don't have enough resource points.");
			}
		}else if(upgrade.equalsIgnoreCase("antitrample")){
			cost = 10;
			if(plugin.hasAmtRp(kingdom, cost)){
				plugin.minusRP(kingdom, cost);
				plugin.misupgrades.set(kingdom + "." + upgrade, true);
				plugin.saveMisupgrades();
				p.sendMessage(ChatColor.GREEN + "Anti-Trample upgrade acquired!");
			}else{
				p.sendMessage(ChatColor.RED + "You don't have enough resource points.");
			}
		}else if(upgrade.equalsIgnoreCase("nexusguard")){
			cost = 100;
			if(plugin.hasAmtRp(kingdom, cost)){
				plugin.minusRP(kingdom, cost);
				plugin.misupgrades.set(kingdom + "." + upgrade, true);
				plugin.saveMisupgrades();
				p.sendMessage(ChatColor.GREEN + "Nexus Guard upgrade acquired!");
			}else{
				p.sendMessage(ChatColor.RED + "You don't have enough resource points.");
			}
		}else if(upgrade.equalsIgnoreCase("glory")){
			cost = 60;
			if(plugin.hasAmtRp(kingdom, cost)){
				plugin.minusRP(kingdom, cost);
				plugin.misupgrades.set(kingdom + "." + upgrade, true);
				plugin.saveMisupgrades();
				p.sendMessage(ChatColor.GREEN + "Glory upgrade acquired!");
			}else{
				p.sendMessage(ChatColor.RED + "You don't have enough resource points.");
			}
		}else if(upgrade.equalsIgnoreCase("bombshards")){
			cost = 100;
			if(plugin.hasAmtRp(kingdom, cost)){
				plugin.minusRP(kingdom, cost);
				plugin.misupgrades.set(kingdom + "." + upgrade, true);
				plugin.saveMisupgrades();
				p.sendMessage(ChatColor.GREEN + "Bomb Expertise upgrade acquired!");
			}else{
				p.sendMessage(ChatColor.RED + "You don't have enough resource points.");
			}
		} 
		}else{
			p.sendMessage(ChatColor.RED + "This upgrade is already enabled!");
		}
	}
	}
	
}
