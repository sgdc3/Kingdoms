package me.leothepro555.kingdoms.main;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Soldier {
	public Kingdoms plugin = new Kingdoms();
	public HashMap<UUID, String> soldiers = new HashMap<UUID,String>();
	public void spawnSoldier(Location location, String kingdom){
		Zombie champion = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
		champion.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
		champion.getEquipment().setHelmetDropChance(0.0F);
		champion.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
		champion.getEquipment().setChestplateDropChance(0.0F);
		
		champion.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, plugin.kingdoms.getInt(kingdom + ".champion.speed")));
		
	}
	
	public void onSoldierDie(){
		
	}
	
}
