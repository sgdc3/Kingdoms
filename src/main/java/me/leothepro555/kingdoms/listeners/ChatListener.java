package me.leothepro555.kingdoms.listeners;

import me.confuser.bukkitutil.listeners.Listeners;
import me.leothepro555.kingdoms.Kingdoms;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener extends Listeners<Kingdoms> {

  @EventHandler(ignoreCancelled = true)
  public void onPlayerChat(AsyncPlayerChatEvent event) {
    Player p = event.getPlayer();
    if (plugin.getChatOption(p).equals("kingdom")) {
      event.setCancelled(true);
      plugin.messageKingdomPlayers(plugin.getKingdom(p), ChatColor.GREEN + "[" + p.getName() + "]: " + event
              .getMessage());
    } else if (plugin.getChatOption(p).equals("ally")) {
      event.setCancelled(true);
      plugin.messageKingdomPlayers(plugin.getKingdom(p), ChatColor.LIGHT_PURPLE + "[" + plugin.getKingdom(p) + "][" + p
              .getName() + "]: " + event.getMessage());
      for (String ally : plugin.getAllies(plugin.getKingdom(p))) {
        plugin.messageKingdomPlayers(ally, ChatColor.LIGHT_PURPLE + "[" + plugin.getKingdom(p) + "][" + p.getName() +"]: " + event.getMessage());
      }
    }
  }
}
