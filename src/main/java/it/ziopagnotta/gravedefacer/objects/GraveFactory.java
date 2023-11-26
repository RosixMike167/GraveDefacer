package it.ziopagnotta.gravedefacer.objects;

import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GraveFactory {
    private final List<Grave> gravesCache;

    public GraveFactory() {
        gravesCache = new CopyOnWriteArrayList<>();
    }

    public List<Grave> getGravesCache() {
        return gravesCache;
    }

    public void put(Grave grave) {
        gravesCache.add(grave);
    }

    public void remove(Grave grave) {
        gravesCache.remove(grave);
    }

    public Optional<Grave> getGraveByEntity(@NotNull ArmorStand entity) {
        return getGravesCache().stream()
                .filter(grave -> grave.getModel().getUniqueId().equals(entity.getUniqueId()))
                .findAny();
    }

    public List<Grave> getGravesByOwner(String owner) {
        return gravesCache.stream().filter(grave -> grave.getOwner().equals(owner)).collect(Collectors.toList());
    }

    public int getNumberGravesByOwner(String owner) {
        return getGravesByOwner(owner).size();
    }

    public void clear() {
        for(Grave grave : getGravesCache()) {
            grave.despawn();
            gravesCache.remove(grave);
        }
    }
}
