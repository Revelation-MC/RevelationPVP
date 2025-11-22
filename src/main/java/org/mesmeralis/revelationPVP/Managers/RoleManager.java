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
        this.firstRoleChange.clear();
    }

    public void loadAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.loadUserAsync(player.getUniqueId());
        }
    }

    public void loadUserAsync(UUID uuid) {
        storage.getRole(uuid).thenAccept(role -> {
            System.out.println("[ROLE-DEBUG] RoleManager loaded " + uuid + " = " + role);
            if (role == null) {
                firstRoleChange.add(uuid);
                roles.put(uuid, Roles.CITIZEN);
            } else {
                roles.put(uuid, role);
            }
        });
    }

    @Deprecated
    private Roles loadUser(UUID uuid) {
        throw new UnsupportedOperationException("Use loadUserAsync instead");
    }


    public void setRole(UUID uuid, Roles role, Consumer<Roles> callback) {
        storage.setRole(uuid, role).thenAccept(v -> {
            roles.put(uuid, role);
            firstRoleChange.remove(uuid);
            callback.accept(role);
        });
    }

    public Roles getRole(UUID uuid) {
        Roles r = roles.get(uuid);
        System.out.println("[ROLE-DEBUG] getRole(" + uuid + ") -> " + r);
        return r;
    }

    public boolean isFirstRoleChange(UUID uuid) {
        return firstRoleChange.contains(uuid);
    }

    public void unload(UUID uuid) {
        firstRoleChange.remove(uuid);
        roles.remove(uuid);
    }

    public Set<UUID> getPlayersWithRole(Roles role) {
        Set<UUID> result = new HashSet<>();
        roles.forEach((uuid, r) -> {
            if (r == role) result.add(uuid);
        });
        return result;
    }
}
