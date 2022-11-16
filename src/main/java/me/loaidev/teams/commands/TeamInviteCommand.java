package me.loaidev.teams.commands;

import me.loaidev.core.commands.PlayerCommand;
import me.loaidev.core.exceptions.DisplayException;
import me.loaidev.core.exceptions.commands.SyntaxException;
import me.loaidev.core.providers.Players;
import me.loaidev.teams.Team;
import me.loaidev.teams.TeamsService;
import me.loaidev.teams.exceptions.NoTeamException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeamInviteCommand extends PlayerCommand {

    public TeamInviteCommand() {
        super("invite", "inv");
        setUsage("/teams invite <player>");
        setPermission("teams.invite");
    }

    @Override
    public void run(@NotNull Player player, @NotNull Command bukkitCommand, @NotNull String label, @NotNull String[] args) {
        Team team = TeamsService.getPlayerTeam(player.getUniqueId());
        if (team == null || !team.hasUser(player.getUniqueId())) throw new NoTeamException();
        if (!team.getRole(player.getUniqueId()).equals(Team.Role.LEADER))
            throw new DisplayException("Only leaders can invite players.");
        if (args.length < 1) throw new SyntaxException(this);

        Player receiver = Players.get(args[0], player);
        if (TeamsService.hasTeam(receiver.getUniqueId()))
            throw new DisplayException("This player is already in a team.");

        // get the team that the user is already invited to if present.
        UUID invited = TeamsService.getInvites().getIfPresent(receiver.getUniqueId());
        if (invited != null) {
            if (invited.equals(team.getUUID())) throw new DisplayException("This player is already invited.");
            throw new DisplayException("This player has another pending team invite.");
        }
        TeamsService.getInvites().put(receiver.getUniqueId(), team.getUUID());

        player.sendMessage(Component.text("Invite sent to player.", NamedTextColor.GREEN));
        receiver.sendMessage(player.displayName().color(NamedTextColor.GOLD)
                .append(Component.text(" has invited you to join ", NamedTextColor.AQUA))
                .append(Component.text(team.getName())));
        receiver.sendMessage(Component.text("Use /teams accept to accept their invite.", NamedTextColor.AQUA));
    }
}
