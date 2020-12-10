package dialight.clientshield.api;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClientShieldApi {

    private static ClientShieldApi instance;
    private final Plugin plugin;
    private final ClientShieldImpl impl;

    public ClientShieldApi(Plugin plugin, ClientShieldImpl impl) {
        this.plugin = plugin;
        this.impl = impl;
        instance = this;
    }

    public static List<Connection> getConnectedPlayers() {
        return instance.plugin.getServer().getOnlinePlayers().stream()
                .map(instance.impl::getConnection)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<Player> getNotConnectedPlayers() {
        return instance.plugin.getServer().getOnlinePlayers().stream()
                .filter(p -> instance.impl.getConnection(p) == null)
                .collect(Collectors.toList());
    }

    @Nullable
    public static Connection getConnection(Player p) {
        return instance.impl.getConnection(p);
    }

}
