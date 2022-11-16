package me.loaidev.teams.commands;

import me.loaidev.core.commands.PlayerCommand;
import me.loaidev.core.exceptions.commands.SyntaxException;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class TeamCommand extends PlayerCommand {

    public TeamCommand() {
        super("teams");
        setPermission("teams.teams");
        setUsage("/teams <create|invite|accept>");
        registerSubCommand(new TeamCreateCommand());
        registerSubCommand(new TeamInviteCommand());
        registerSubCommand(new TeamAcceptCommand());
    }

    @Override
    public void run(@NotNull Player player, @NotNull Command bukkitCommand, @NotNull String label, @NotNull String[] args) {
        throw new SyntaxException(this);
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull Command bukkitCommand, @NotNull String label, @NotNull String[] args) {
        return Arrays.asList("create", "invite", "accept");
    }
}
