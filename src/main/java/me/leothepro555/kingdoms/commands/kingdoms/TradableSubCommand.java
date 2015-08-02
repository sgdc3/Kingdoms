package me.leothepro555.kingdoms.commands.kingdoms;

import me.confuser.bukkitutil.commands.SubCommand;
import me.leothepro555.kingdoms.Kingdoms;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

public class TradableSubCommand extends SubCommand<Kingdoms> {

  public TradableSubCommand() {
    super("tradable");
  }

  @Override
  public boolean onCommand(CommandSender sender, String[] args) {
    if (plugin.usewhitelist) {
      sender.sendMessage(ChatColor.GREEN + "Conversion ratio: 1 resource point per " + plugin.rpi + " items");
      sender.sendMessage(ChatColor.GREEN + "===Enabled Trades===");
      for (Material mat : plugin.whitelistitems.keySet()) {
        sender.sendMessage(ChatColor.GREEN + mat.toString() + " | Worth " + plugin.whitelistitems.get(mat) + " item" +
                "(s)");
      }
    } else {
      sender.sendMessage(ChatColor.RED + "===Disabled Trades===");
      for (Material mat : plugin.blacklistitems) {
        sender.sendMessage(ChatColor.RED + mat.toString());
      }
      sender.sendMessage("");
      sender.sendMessage(ChatColor.GREEN + "===Special Trades===");
      for (Material mat : plugin.specialcaseitems.keySet()) {
        sender.sendMessage(ChatColor.GREEN + mat.toString() + " | Worth " + plugin.whitelistitems.get(mat) + " item" +
                "(s)");
      }
    }

    return true;
  }

  @Override
  public String getHelp() {
    return null;
  }

  @Override
  public String getPermission() {
    return "tradable";
  }
}
