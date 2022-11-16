package me.loaidev.teams.commands;

import me.loaidev.core.commands.PlayerCommand;
import me.loaidev.core.exceptions.DisplayException;
import me.loaidev.core.exceptions.commands.SyntaxException;
import me.loaidev.teams.Team;
import me.loaidev.teams.TeamsService;
import me.loaidev.teams.exceptions.HasTeamException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamCreateCommand extends PlayerCommand {

    public TeamCreateCommand() {
        super("create");
        setPermission("teams.create");
        setUsage("/teams create <name>");
    }

    @Override
    public void run(@NotNull Player player, @NotNull Command bukkitCommand, @NotNull String label, @NotNull String[] args) {
        if (TeamsService.hasTeam(player.getUniqueId())) throw new HasTeamException();
        if (args.length < 1) throw new SyntaxException(this);
        Team team = TeamsService.createTeam(args[0], player.getUniqueId());
        if (team == null) throw new DisplayException();
        player.sendMessage(Component.text("Team created successfully.", NamedTextColor.GREEN));
    }
}
