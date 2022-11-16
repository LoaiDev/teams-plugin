package me.loaidev.teams;

import me.loaidev.core.providers.Players;
import me.loaidev.core.storage.YamlStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Team {

    protected final YamlStorage storage;

    public Team(YamlStorage storage) {
        this.storage = storage;
    }

    public String getName() {
        return storage.getString("name");
    }

    public UUID getUUID() {
        return storage.getUUID("uuid");
    }

    public Map<UUID, Role> getPlayersMap() {
        Map<String, Object> map = Objects.requireNonNull(storage.getConfigurationSection("players")).getValues(false);
        Map<UUID, Role> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            try {
                result.put(UUID.fromString(entry.getKey()), Role.valueOf(entry.getValue().toString()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return result;
    }

    public boolean hasUser(UUID player) {
        return storage.contains("players." + player);
    }

    public Role getRole(UUID player) {
        return Role.valueOf(storage.getString("players." + player));
    }

    public void addPlayer(UUID player, Role role) {
        storage.put("players." + player, role.toString()).save();
    }

    public void removePlayer(UUID player) {
        storage.put("players." + player, null).save();
    }

    public void sendMessage(Player player, String message) {
        sendComponent(Component.text("[TeamChat] ", NamedTextColor.GREEN)
                .append(Component.text(player.getName(), NamedTextColor.AQUA, TextDecoration.BOLD))
                .append(Component.text(" " + message, NamedTextColor.AQUA)));
    }

    public void sendMessage(String message) {
        sendComponent(Component.text("[Teams] ", NamedTextColor.GREEN)
                .append(Component.text(message, NamedTextColor.AQUA)));
    }

    public void sendComponent(Component component) {
        onlinePlayers().forEach(p -> p.sendMessage(component));
    }

    public List<Player> onlinePlayers() {
        List<Player> onlinePlayers = getPlayersMap().keySet().stream().map(Players::get).collect(Collectors.toList());
        onlinePlayers.removeAll(Collections.singleton(null));
        return onlinePlayers;
    }


    public boolean evict() {
        return true;
    }

    public void compute(Consumer<Team> consumer) {
        TeamsService.getTeams().asMap().compute(getUUID(), ((uuid1, team) -> {
            if (team != this) throw new IllegalStateException();
            consumer.accept(team);
            return team;
        }));
    }

    @Nullable
    public static Team load(@Nullable YamlStorage storage) {
        if (storage == null) return null;
        Team team = new Team(storage);
        if (team.getName() == null ||
                team.getUUID() == null ||
                team.getPlayersMap().isEmpty()) return null;
        return team;
    }

    public enum Role {
        LEADER(Component.text("Leader", NamedTextColor.GOLD)),
        MEMBER(Component.text("Member", NamedTextColor.BLUE)),
        ;

        private final TextComponent text;

        Role(TextComponent component) {
            text = component;
        }

        public TextComponent getText() {
            return text;
        }
    }
}
