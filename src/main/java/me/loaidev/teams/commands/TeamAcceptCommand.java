package me.loaidev.teams.commands;

import me.loaidev.core.commands.PlayerCommand;
import me.loaidev.core.exceptions.DisplayException;
import me.loaidev.teams.Team;
import me.loaidev.teams.TeamsService;
import me.loaidev.teams.exceptions.HasTeamException;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeamAcceptCommand extends PlayerCommand {
    public TeamAcceptCommand() {
        super("accept");
        setPermission("teams.accept");
        setUsage("/teams accept");
    }

    @Override
    public void run(@NotNull Player player, @NotNull Command bukkitCommand, @NotNull String label, @NotNull String[] args) {
        UUID teamUUID = TeamsService.getInvites().getIfPresent(player.getUniqueId());
        if (teamUUID == null) throw new DisplayException("You don't have any pending team invite.");
        if (TeamsService.hasTeam(player.getUniqueId())) throw new HasTeamException();
        Team team = TeamsService.getTeams().get(teamUUID);
        if (team == null) throw new DisplayException("That team doesn't exist.");
        TeamsService.setPlayerTeam(player.getUniqueId(), team.getUUID());
        team.addPlayer(player.getUniqueId(), Team.Role.MEMBER);
        team.sendMessage(String.format("Player %s has joined the team.", player.getName()));
    }
}
