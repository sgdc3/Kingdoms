package me.leothepro555.kingdoms.commands.kingdoms;

import me.confuser.bukkitutil.commands.PlayerSubCommand;
import me.leothepro555.kingdoms.Kingdoms;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ShowSubCommand extends PlayerSubCommand<Kingdoms> {

  public ShowSubCommand() {
    super("show");
  }

  @Override
  public boolean onPlayerCommand(Player player, String[] args) {
    String kingdom = null;

    if (args.length == 0) {
      if (plugin.hasKingdom(player)) {
        kingdom = plugin.getKingdom(player);
      } else {
        player.sendMessage(ChatColor.RED + "You don't have a kingdom!");
        return true;
      }
    } else if (args.length == 1) {
      if (plugin.kingdoms.getKeys(false).contains(args[0])) {
        kingdom = args[0];
      } else {

      }
    }

    if (kingdom == null) {
      player.sendMessage(ChatColor.RED + "The kingdom " + args[1] + " does not exist!");
      return true;
    }

    boolean isMember = plugin.hasKingdom(player) && kingdom.equals(plugin.getKingdom(player));

    player.sendMessage(ChatColor.AQUA + "=-=-=-=-=--==[" + kingdom + "]==--=-=-=-=-=");
    player.sendMessage(ChatColor.AQUA + "| King: " + Bukkit
            .getOfflinePlayer((UUID.fromString(plugin.kingdoms.getString(kingdom + ".king")))).getName());
    player.sendMessage(ChatColor.AQUA + "| Might: " + plugin.kingdoms.getInt(kingdom + ".might"));
    player.sendMessage(ChatColor.AQUA + "| Land: " + plugin.getAmtLand(kingdom));


    if (isMember) {
      String allies = StringUtils.join(plugin.kingdoms.getStringList(kingdom + ".allies"), " ");
      String enemies = StringUtils.join(plugin.kingdoms.getStringList(kingdom + ".enemies"), " ");

      player.sendMessage(ChatColor.AQUA + "|" + " Allies:" + ChatColor.GREEN + allies);
      player.sendMessage(ChatColor.AQUA + "|" + " Enemies:" + ChatColor.RED + enemies);
    }

    player.sendMessage(ChatColor.AQUA + "|");
    player.sendMessage(ChatColor.AQUA + "|     " + ChatColor.UNDERLINE + "Members");
    player.sendMessage(ChatColor.AQUA + "|");
    player.sendMessage(ChatColor.AQUA + "|" + ChatColor.GREEN + " Online" + ChatColor.WHITE + " | " + ChatColor.RED + "Offline");

    // King
    OfflinePlayer king = Bukkit.getOfflinePlayer((UUID.fromString(plugin.kingdoms.getString(kingdom + ".king"))));
    ChatColor status = king.isOnline() ? ChatColor.GREEN : ChatColor.RED;

    player.sendMessage(ChatColor.AQUA + "| " + status + "☀" + "[King]" + king.getName());

    // Members/Mods
    for (String s : plugin.kingdoms.getStringList(kingdom + ".members")) {

      String ign = plugin.players.getString(s + ".ign");
      OfflinePlayer member = Bukkit.getOfflinePlayer(UUID.fromString(s));
      String ping;

      if (member.isOnline()) {
        ping = ChatColor.GREEN + "☀";
      } else {
        ping = ChatColor.RED + "☀";
      }

      if (plugin.isMod(kingdom, member)) {
        player.sendMessage(ChatColor.AQUA + "| " + ping + "[Mod] " + ign);
      } else {
        player.sendMessage(ChatColor.AQUA + "| " + ping + ign);
      }
    }

    player.sendMessage(ChatColor.AQUA + "=-=-=-=-=--==-==-==--=-=-=-=-=");

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
