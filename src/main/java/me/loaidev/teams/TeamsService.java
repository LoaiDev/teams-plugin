package me.loaidev.teams;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.github.benmanes.caffeine.cache.Weigher;
import com.github.f4b6a3.uuid.UuidCreator;
import me.loaidev.core.Core;
import me.loaidev.core.exceptions.DisplayException;
import me.loaidev.core.storage.YamlStorage;
import me.loaidev.core.storage.YamlStorageProvider;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TeamsService {

    private static YamlStorageProvider storage;
    private static LoadingCache<UUID, Team> teams;
    private static LoadingCache<UUID, UUID> players;
    private static Cache<UUID, UUID> invites;

    public static void init(Teams plugin) {
        storage = new YamlStorageProvider(plugin.getDataFolder(), "teams");
        invites = Caffeine.newBuilder()
                .scheduler(Scheduler.systemScheduler())
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .build();
        players = Caffeine.newBuilder()
                .expireAfterAccess(30, TimeUnit.SECONDS)
                .scheduler(Scheduler.systemScheduler())
                .removalListener((key, value, cause) -> Core.debugAsync("Removed player team " + (key != null ? key.toString() : "unknown")))
                .build(uuid -> storage.getOrCreate("list").getUUID(uuid.toString()));
        teams = Caffeine.newBuilder()
                .maximumWeight(100)
                .weigher(new CustomWeigher())
                .expireAfter(new CustomExpiry())
                .scheduler(Scheduler.systemScheduler())
                .removalListener((key, value, cause) -> Core.debugAsync("Removed team " + (value != null ? value.getName() : "unknown")))
                .build(k -> Team.load(storage.getStorage(k)));
    }

    public static LoadingCache<UUID, Team> getTeams() {
        return teams;
    }

    public static LoadingCache<UUID, UUID> getPlayers() {
        return players;
    }

    public static Cache<UUID, UUID> getInvites() {
        return invites;
    }

    @Nullable
    public static Team compute(@NotNull UUID uuid, Consumer<Team> consumer) {
        return getTeams().asMap().compute(uuid, ((uuid1, team) -> {
            if (team == null) {
                team = Team.load(storage.getStorage(uuid1));
                if (team == null) return null;
            }
            consumer.accept(team);
            return team;
        }));
    }

    public static boolean hasTeam(@NotNull UUID player) {
        return getPlayers().get(player) != null;
    }

    @Nullable
    public static Team getPlayerTeam(@NotNull UUID player) {
        UUID uuid = getPlayers().get(player);
        if (uuid == null) return null;
        return getTeams().get(uuid);
    }

    @Nullable
    public static Team createTeam(@NotNull String name, @NotNull UUID leader) {
        UUID uuid = UuidCreator.getNameBasedSha1(name);
        if (storage.exists(uuid.toString())) {
            throw new DisplayException("There is already a a team with this name.");
        }
        YamlStorage data = storage.getOrCreate(uuid).put("uuid", uuid.toString()).put("name", name)
                .put("players." + leader, Team.Role.LEADER.toString());
        data.save();
        Team team = Team.load(data);
        if (team == null) return null;
        storage.getOrCreate("list").put(leader.toString(), uuid.toString()).save();
        return team;
    }

    public static void setPlayerTeam(@NotNull UUID player, @Nullable UUID team) {
        getPlayers().invalidate(player);
        storage.getOrCreate("list").put(player.toString(), team != null ? team.toString() : null).save();
    }

    private static class CustomWeigher implements Weigher<UUID, Team> {
        @Override
        public @NonNegative int weigh(UUID key, Team value) {
            return value.evict() ? 1 : 0;
        }
    }

    private static class CustomExpiry implements Expiry<UUID, Team> {
        @Override
        public long expireAfterCreate(UUID key, Team value, long currentTime) {
            return getSeconds(value);
        }

        @Override
        public long expireAfterUpdate(UUID key, Team value, long currentTime, @NonNegative long currentDuration) {
            return getSeconds(value);
        }

        @Override
        public long expireAfterRead(UUID key, Team value, long currentTime, @NonNegative long currentDuration) {
            return getSeconds(value);
        }

        private long getSeconds(Team value) {
            return value.evict() ? TimeUnit.SECONDS.toNanos(10) : Long.MAX_VALUE;
        }
    }
}
