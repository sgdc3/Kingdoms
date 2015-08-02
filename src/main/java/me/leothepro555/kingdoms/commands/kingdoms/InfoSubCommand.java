package me.leothepro555.kingdoms.commands.kingdoms;

import me.confuser.bukkitutil.commands.SubCommand;
import me.leothepro555.kingdoms.Kingdoms;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class InfoSubCommand extends SubCommand<Kingdoms> {

  public InfoSubCommand() {
    super("info");
  }

  @Override
  public boolean onCommand(CommandSender sender, String[] args) {
    sender.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "======Kingdoms======");
    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Kingdoms allows a player to create a kingdom.");
    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Do /k create [kingdomname] to create your kingdom");
    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Once you're done, you can try /k show to see your kingdom's " +
            "information.");
    sender.sendMessage(ChatColor.LIGHT_PURPLE + "This will show your resource points, members of your kingdom, your " +
            "kingdom's might, enemies and allies." + " You can also do /k show [kingdom name /player name] to gather information on another kingdom. However, their enemies, allies and " + "resource points will be kept secret.");
    sender.sendMessage(ChatColor.LIGHT_PURPLE + "The next step is to place the nexus block somewhere safe. This " +
            "should" +
            " be " +
            "near your kingdom home. " + "The nexus block is the center of your operations: You can convert blocks to resourcepoints, upgrade kingdom bonuses" + " and upgrade champion stats");
    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "An enemy can steal up to 20 resource points each mine. " +
            "Nexus " +
            "blocks can be raided" + " even if your nexus block is on your land");
    sender.sendMessage(ChatColor.GOLD + "Kingdom might is how powerful your kingdom is. Might grows as you have more " +
            "land");
    sender.sendMessage(ChatColor.GREEN + "Allies can be made with /k ally [kingdom]. Allies can donate" + " to each " +
            "other's" +
            " nexuses and cannot damage each other.");
    sender.sendMessage(ChatColor.GRAY + "Can't trust another kingdom as an ally? Want to remove an enemy from your" +
            " " +
            "blacklist? Do /k neutral [kingdom]. This will remove a kingdom from your ally list or enemy list.");
    sender.sendMessage(ChatColor.RED + "Enemies can be made with /k enemy [kingdom]. Doing so will mark a kingdom as"
            + " " +
            "hostile, allowing both sides to harm each other. When an enemy mines your nexus, 20" + " resource points will be lost instead of 10");
    sender.sendMessage(ChatColor.AQUA + "The members of your kingdom show the number of players in your kingdom. " +
            "Players can help to collect your resources, build, open chests, furnaces and other amenities. Players can be " + "invited to your kingdom with /k invite [playername] " + "be careful who you invite as spies in your kingdom can do a lot of internal damage.");
    sender.sendMessage(ChatColor.RED + "To claim another kingdom's land, yoo duel with their champion with" +
            " /k invade. If you win, you can claim the land. If you lose, you leave empty-handed. Your champion's strength can be upgraded in the nexus block. " + "When you are online, you can fight your opponent with your champion in an event of an invasion. Your champion " + "will gain better stats, depending on the number of players fighting him.");

    return true;
  }

  @Override
  public String getHelp() {
    return null;
  }

  @Override
  public String getPermission() {
    return "info";
  }
}
