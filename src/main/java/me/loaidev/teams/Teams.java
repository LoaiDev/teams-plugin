package me.loaidev.teams;

import me.loaidev.core.commands.CommandHandler;
import me.loaidev.teams.commands.TeamChatCommand;
import me.loaidev.teams.commands.TeamCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Teams extends JavaPlugin {

    public static CommandHandler commandHandler;
    @Override
    public void onEnable() {
        commandHandler = new CommandHandler(this);
        commandHandler.registerCommand(new TeamChatCommand());
        commandHandler.registerCommand(new TeamCommand());
        TeamsService.init(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
