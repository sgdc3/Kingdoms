package me.leothepro555.kingdoms.commands;

import me.confuser.bukkitutil.commands.MultiCommandHandler;
import me.leothepro555.kingdoms.Kingdoms;
import me.leothepro555.kingdoms.commands.kingdoms.*;

public class KingdomsCommand extends MultiCommandHandler<Kingdoms> {

  public KingdomsCommand() {
    super("kingdoms");

    registerCommands();
  }

  @Override
  public void registerCommands() {
    registerSubCommand(new AutoClaimSubCommand());
    registerSubCommand(new ClaimSubCommand());
    registerSubCommand(new CreateSubCommand());
    registerSubCommand(new InfoSubCommand());
    registerSubCommand(new JoinSubCommand());
    registerSubCommand(new LeaveSubCommand());
    registerSubCommand(new NexusSubCommand());
    registerSubCommand(new ShowSubCommand());
    registerSubCommand(new TradableSubCommand());
    registerSubCommand(new UnclaimSubCommand());
  }
}
