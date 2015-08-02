package me.leothepro555.kingdoms.commands.kingdoms;

import me.confuser.bukkitutil.commands.PlayerSubCommand;
import me.leothepro555.kingdoms.Kingdoms;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class NexusSubCommand extends PlayerSubCommand<Kingdoms> {

  public NexusSubCommand() {
    super("nexus");
  }

  @Override
  public boolean onPlayerCommand(Player player, String[] args) {
    if (!plugin.hasKingdom(player)) {
      player.sendMessage(ChatColor.RED + "You don't have a kingdom!");
      return true;
    }

    if (!plugin.isKing(player)) {
      player.sendMessage(ChatColor.RED + "You must be your kingdom's king to set your nexus!");
      return true;
    }

    plugin.placingnexusblock.add(player.getUniqueId());
    player.sendMessage(ChatColor.RED + "=======================================");
    player.sendMessage(ChatColor.RED + "");
    player.sendMessage(ChatColor.BLUE + "Right click to replace a clicked block with your nexus. Left click to cancel. Be careful not to click on chests/important blocks.");
    player.sendMessage(ChatColor.RED + "");
    player.sendMessage(ChatColor.RED + "=======================================");

    return true;
  }

  @Override
  public String getHelp() {
    return null;
  }

  @Override
  public String getPermission() {
    return "nexus";
  }
}
