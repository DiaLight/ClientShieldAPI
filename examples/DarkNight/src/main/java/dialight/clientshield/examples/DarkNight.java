package dialight.clientshield.examples;

import dialight.clientshield.api.*;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.EnumMap;
import java.util.Map;

public class DarkNight extends JavaPlugin implements Listener {

    private final Map<ConfigKey, Object> lightningConfig = new EnumMap<>(ConfigKey.class);
    private BukkitTask bukkitTask;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        bukkitTask = getServer().getScheduler().runTaskTimer(this, this::tick, 20, 20);
    }

    @Override
    public void onDisable() {
        if(bukkitTask != null) bukkitTask.cancel();
    }

    @EventHandler
    public void onNewLogin(AsyncNewLoginConnectionEvent e) {  // warning: executes in netty thread
        e.pauseAndConnectClientShield();  // AsyncClientShieldConnectedEvent will fire
    }

    @EventHandler
    public void onClientShieldConnected(AsyncClientShieldConnectedEvent e) {  // warning: executes in netty thread
        Connection connection = e.getConnection();
        PauseLock pauseLock = e.pauseLoginProtocol();
        connection.updateConfig(lightningConfig)
                .thenAccept(o -> pauseLock.unlock())  // pass to server if succeed
                .exceptionally(connection::printAndDisconnect);  // disconnect if failed to update
    }

    private float calcLightning(World overworld) {
        float lightning = (float) overworld.getTime() / 1000f;  // hour
        lightning /= 24;
        lightning *= 4;
        if(lightning > 1) lightning = -lightning + 2;
        if(lightning < -1) lightning = -lightning - 2;
        return lightning;
    }

    private void tick() {
        World overworld = getServer().getWorlds().get(0);
        float lightning = calcLightning(overworld);  // [-1, 1] -1:night 1:day
        // gamma:[-0.6, 1]
        lightning *= 0.8f;
        lightning += 0.2f;
        if(lightning == 0) lightning = 0.00001f;

        ConfigBuilder builder = new ConfigBuilder();
        builder.setGamma(lightning);
        lightningConfig.putAll(builder.build());
        for (Connection con : ClientShieldApi.getConnectedPlayers()) {
            con.updateConfig(lightningConfig);
        }
    }

}
