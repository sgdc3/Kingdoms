package me.leothepro555.kingdoms.commands.kingdoms;

import me.confuser.bukkitutil.commands.PlayerSubCommand;
import me.leothepro555.kingdoms.Kingdoms;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UnclaimSubCommand extends PlayerSubCommand<Kingdoms> {

  public UnclaimSubCommand() {
    super("unclaim");
  }

  @Override
  public boolean onPlayerCommand(Player player, String[] args) {
    if (!plugin.hasKingdom(player)) {
      player.sendMessage(ChatColor.RED + "You don't have a kingdom!");
      return true;
    }

    if (!plugin.isMod(plugin.getKingdom(player), player) || !plugin.isKing(player)) {
      player.sendMessage(ChatColor.RED + "Your rank is too low to unclaim land!");
      return true;
    }

    if (plugin.isNexusChunk(player.getLocation().getChunk())) {
      player.sendMessage(ChatColor.RED + "You can't unclaim your nexus land! You must move your nexus with /k nexus before unclaiming this patch of land!");
      return true;
    }

    plugin.unclaimCurrentPosition(player);
    
    return true;
  }

  @Override
  public String getHelp() {
    return null;
  }

  @Override
  public String getPermission() {
    return null;
  }
}
