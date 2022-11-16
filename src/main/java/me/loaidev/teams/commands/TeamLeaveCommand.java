package me.loaidev.teams.commands;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import me.loaidev.core.commands.PlayerCommand;
import me.loaidev.teams.Team;
import me.loaidev.teams.TeamsService;
import me.loaidev.teams.exceptions.NoTeamException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TeamLeaveCommand extends PlayerCommand {

    private final Cache<UUID, Boolean> confirmation;

    public TeamLeaveCommand() {
        super("leave");
        setPermission("teams.leave");
        setUsage("/teams leave");
        confirmation = Caffeine.newBuilder().expireAfterWrite(15, TimeUnit.SECONDS).scheduler(Scheduler.systemScheduler()).build();
    }

    @Override
    public void run(@NotNull Player player, @NotNull Command bukkitCommand, @NotNull String label, @NotNull String[] args) {
        Team team = TeamsService.getPlayerTeam(player.getUniqueId());
        if (team == null) throw new NoTeamException();
        if (!confirmation.asMap().containsKey(player.getUniqueId())) {
            player.sendMessage(Component.text("Type /teams leave again to confirm.", NamedTextColor.GRAY));
            return;
        }
        if (team.getRole(player.getUniqueId()).equals(Team.Role.LEADER) && team.getPlayersMap().size() > 1) {
            List<UUID> keys = new ArrayList<>(team.getPlayersMap().keySet());
            keys.remove(player.getUniqueId());
            Random rand = new Random();
            UUID random = keys.get(rand.nextInt(keys.size()));
            team.addPlayer(random, Team.Role.LEADER);
            OfflinePlayer leader =
        }
        team.removePlayer(player.getUniqueId());
        TeamsService.setPlayerTeam(player.getUniqueId(), null);

    }
}
