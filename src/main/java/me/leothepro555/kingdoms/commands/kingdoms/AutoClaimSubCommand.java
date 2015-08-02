package me.leothepro555.kingdoms.commands.kingdoms;

import me.confuser.bukkitutil.commands.PlayerSubCommand;
import me.leothepro555.kingdoms.Kingdoms;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AutoClaimSubCommand extends PlayerSubCommand<Kingdoms> {

  public AutoClaimSubCommand() {
    super("autoclaim");
  }

  @Override
  public boolean onPlayerCommand(Player player, String[] args) {
    if (!plugin.hasKingdom(player)) {
      player.sendMessage(ChatColor.RED + "You don't have a kingdom!");
      return true;
    }

    if (!plugin.isMod(plugin.getKingdom(player), player) || !plugin.isKing(player)) {
      player.sendMessage(ChatColor.RED + "Your rank is too low to claim land!");
      return true;
    }

    plugin.claimCurrentPosition(player);

    if (!plugin.rapidclaiming.contains(player.getUniqueId())) {
      plugin.rapidclaiming.add(player.getUniqueId());
      player.sendMessage(ChatColor.GREEN + "Enabled auto-claiming");
    } else {
      plugin.rapidclaiming.remove(player.getUniqueId());
      player.sendMessage(ChatColor.RED + "Disabled auto-claiming");
    }

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
