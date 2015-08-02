package me.leothepro555.kingdoms.commands.kingdoms;

import me.confuser.bukkitutil.commands.PlayerSubCommand;
import me.leothepro555.kingdoms.Kingdoms;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LeaveSubCommand extends PlayerSubCommand<Kingdoms> {

  public LeaveSubCommand() {
    super("leave");
  }

  @Override
  public boolean onPlayerCommand(Player player, String[] args) {
    if (args.length == 0) return false;

    if (!plugin.hasKingdom(player)) {
      player.sendMessage(ChatColor.RED + "You don't have a kingdom!");
      return true;
    }

    if (plugin.isKing(player)) {
      player.sendMessage(ChatColor.RED + "As a king, you must pass on your leadership to another member of the " +
              "kingdom with /k king [playername], or disband your kingdom with /k disband");
      return true;
    }

    plugin.quitKingdom(plugin.getKingdom(player), player);
    player.sendMessage(ChatColor.RED + "Left " + plugin.getKingdom(player));

    return true;
  }

  @Override
  public String getHelp() {
    return null;
  }

  @Override
  public String getPermission() {
    return "leave";
  }
}
