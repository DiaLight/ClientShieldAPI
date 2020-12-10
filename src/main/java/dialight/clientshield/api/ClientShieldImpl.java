package dialight.clientshield.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface ClientShieldImpl {

    @Nullable
    Connection getConnection(Player player);

}
