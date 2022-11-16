package me.loaidev.teams.commands;

import me.loaidev.core.commands.PlayerCommand;
import me.loaidev.core.exceptions.DisplayException;
import me.loaidev.core.exceptions.commands.SyntaxException;
import me.loaidev.teams.Team;
import me.loaidev.teams.TeamsService;
import me.loaidev.teams.exceptions.NoTeamException;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamChatCommand extends PlayerCommand {

    public TeamChatCommand() {
        super("teamchat", "tc");
        setPermission("teams.chat");
        setUsage("/teamchat <message>");
    }

    @Override
    public void run(@NotNull Player player, @NotNull Command bukkitCommand, @NotNull String label, @NotNull String[] args) {
        Team team = TeamsService.getPlayerTeam(player.getUniqueId());
        if (team == null) throw new NoTeamException();
        if (args.length < 1) throw new SyntaxException(this);
        team.sendMessage(player, String.join(" ", args));
    }
}
