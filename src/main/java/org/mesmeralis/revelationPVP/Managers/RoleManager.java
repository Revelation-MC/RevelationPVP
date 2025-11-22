package org.mesmeralis.revelationPVP.Managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mesmeralis.revelationPVP.Roles;
import org.mesmeralis.revelationPVP.Storage.SQLGetter;
import org.mesmeralis.revelationPVP.Storage.Storage;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RoleManager {
    private final Map<UUID, Roles> roles = new HashMap<>();
    private final Set<UUID> firstRoleChange = new HashSet<>();

    private final SQLGetter storage;

    public RoleManager(SQLGetter storage) {
        this.storage = storage;
    }

    public void shutdown() {
        this.roles.clear();
    }

    public void loadAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.loadUser(player.getUniqueId());
        }
    }

    public Roles load(UUID uuid) {
        if (Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Do not execute this method on the main server thread!");
        }
        return this.loadUser(uuid);
    }

    /*
     * This method stays private, Tyler... If you want to load a user's data, call the above method from a separate thread.
     */
    private Roles loadUser(UUID uuid) {
            return this.roles.computeIfAbsent(uuid, id -> {
                Roles dbRole = this.storage.getRole(id).join();

                if (dbRole == null) {
                    this.firstRoleChange.add(id);
                    return Roles.CITIZEN;
                }

                return dbRole;
            });
    }

    public void setRole(UUID uuid, Roles role, Consumer<Roles> consumer) {
        this.updateRole(uuid, role)
                .thenAcceptAsync(v -> this.roles.put(uuid, role))
                .thenAccept(v -> this.firstRoleChange.remove(uuid))
                .thenAccept(v -> consumer.accept(role));
    }

    public void unload(UUID uuid) {
        this.firstRoleChange.remove(uuid);
        this.roles.remove(uuid);
    }

    public boolean isFirstRoleChange(UUID uuid) {
        return this.firstRoleChange.contains(uuid);
    }

    public Roles getRole(UUID uuid) {
        return this.roles.get(uuid);
    }

    public Set<UUID> getPlayersWithRole(Roles role) {
        final Set<UUID> uuids = new HashSet<>();
        for (Entry<UUID, Roles> entry : this.roles.entrySet()) {
            if (entry.getValue() == role) {
                uuids.add(entry.getKey());
            }
        }
        return uuids;
    }

    private CompletableFuture<Void> updateRole(UUID uuid, Roles role) {
        return CompletableFuture.runAsync(() -> this.storage.setRole(uuid, role));
    }
}
