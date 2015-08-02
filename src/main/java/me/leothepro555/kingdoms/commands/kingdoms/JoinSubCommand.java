package me.leothepro555.kingdoms.commands.kingdoms;

import me.confuser.bukkitutil.commands.PlayerSubCommand;
import me.leothepro555.kingdoms.Kingdoms;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class JoinSubCommand extends PlayerSubCommand<Kingdoms> {

  public JoinSubCommand() {
    super("join");
  }

  @Override
  public boolean onPlayerCommand(Player player, String[] args) {
    if (args.length == 1) return false;

    if (plugin.hasKingdom(player)) {
      player.sendMessage(ChatColor.RED + "You have a kingdom!");
      return true;
    }

    if (!plugin.kingdoms.getKeys(false).contains(args[0])) {
      player.sendMessage(ChatColor.RED + args[0] + " is not a valid kingdom. Bear in mind that this is case sensitive");
      return true;
    }

    if (!player.hasMetadata("kinv " + args[0])) {
      player.sendMessage(ChatColor.RED + "You must be invited to join this kingdom. Notify the kingdom's owner or one of the mods.");
      return true;
    }

    plugin.joinKingdom(args[0], player);
    player.sendMessage(ChatColor.GREEN + "Joined " + args[1]);

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
