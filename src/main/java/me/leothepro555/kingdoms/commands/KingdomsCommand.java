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
    registerSubCommand(new ClaimSubCommand());
    registerSubCommand(new CreateSubCommand());
    registerSubCommand(new InfoSubCommand());
    registerSubCommand(new NexusSubCommand());
    registerSubCommand(new TradableSubCommand());
  }
}
