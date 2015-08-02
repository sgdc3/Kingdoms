package me.leothepro555.kingdoms.commands.kingdoms;

import me.confuser.bukkitutil.commands.PlayerSubCommand;
import me.leothepro555.kingdoms.Kingdoms;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CreateSubCommand extends PlayerSubCommand<Kingdoms> {

  public CreateSubCommand() {
    super("create");
  }

  @Override
  public boolean onPlayerCommand(Player sender, String[] args) {
    if (args.length == 0) return false;

    if (plugin.hasKingdom(sender)) {
      sender.sendMessage(ChatColor.RED + "You already have a kingdom! You must leave before creating a new Kingdom");
    }

    if (plugin.kingdoms.get(args[0]) != null) {
      sender.sendMessage(ChatColor.RED + args[0] + " already exists!");
    }

    if (!args[0].equalsIgnoreCase("SafeZone") && !args[0].equalsIgnoreCase("WarZone")) {
      plugin.newKingdom(sender.getUniqueId(), args[0]);
    }

    return true;
  }

  @Override
  public String getHelp() {
    return null;
  }

  @Override
  public String getPermission() {
    return "create";
  }
}
