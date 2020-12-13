package dialight.clientshield.examples;

import dialight.clientshield.api.ConfigKey;
import dialight.clientshield.api.ClientShieldApi;
import dialight.clientshield.api.ConfigBuilder;
import dialight.clientshield.api.Connection;
import dialight.clientshield.api.PauseLock;
import dialight.clientshield.api.AsyncNewLoginConnectionEvent;
import dialight.clientshield.api.AsyncClientShieldConnectedEvent;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public class ClientControl extends JavaPlugin implements Listener {

    private final Map<ConfigKey, Object> globalConfig = new EnumMap<>(ConfigKey.class);

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        PluginCommand clientcontrol = getServer().getPluginCommand("clientcontrol");
        clientcontrol.setExecutor((sender, command, label, args) -> {
            LinkedList<String> largs = new LinkedList<>(Arrays.asList(args));
            if(largs.isEmpty()) return false;
            String cmd = largs.pollFirst();
            if(largs.isEmpty()) return false;
            String strValue = largs.pollFirst();
            ConfigBuilder builder = new ConfigBuilder();
            if(cmd.equals("leftcooldown")) {
                int value = Integer.parseInt(strValue);
                builder.setLeftCooldownMs(value);
            } else if(cmd.equals("rightcooldown")) {
                int value = Integer.parseInt(strValue);
                builder.setRightCooldownMs(value);
            } else if(cmd.equals("chunkreloading")) {
                int value = Integer.parseInt(strValue);
                builder.setChunkReloadCooldownMs(value);
            } else if(cmd.equals("subtitles")) {
                boolean value = strValue.equals("0");
                builder.setHideSubtitles(value);
            } else if(cmd.equals("fov")) {  // recommended [-180, 180],  0: user choice
                float value = Float.parseFloat(strValue);
                builder.setFov(value);
            } else if(cmd.equals("gamma")) {  // recommended [-2, 2],  0: user choice
                float value = Float.parseFloat(strValue);
                builder.setGamma(value);
            } else if(cmd.equals("thirdpersonview")) {  // available [-1, 2],  -1: user choice
                int value = Integer.parseInt(strValue);
                builder.setThirdPersonView(value);
            } else if(cmd.equals("stoprenderhitboxes")) {
                boolean value = strValue.equals("0");
                builder.setStopRenderHitBoxes(value);
            }
            Map<ConfigKey, Object> config = builder.build();
            globalConfig.putAll(config);
            for (Connection con : ClientShieldApi.getConnectedPlayers()) {
                con.updateConfig(config)
                        .exceptionally(con::printAndDisconnect);
            }
            return true;
        });
        List<String> cmds = Arrays.asList(
                "leftcooldown",
                "rightcooldown",
                "chunkreloading",
                "subtitles",
                "fov",
                "gamma",
                "thirdpersonview",
                "stoprenderhitboxes"
        );
        clientcontrol.setTabCompleter((sender, command, alias, args) -> {
            if(args.length == 0) return cmds;
            if(args.length != 1) return Collections.emptyList();
            return cmds.stream().filter(cmd -> cmd.startsWith(args[0])).collect(Collectors.toList());
        });
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onNewLogin(AsyncNewLoginConnectionEvent e) {  // warning: executes in netty thread
        e.pauseAndConnectClientShield();  // AsyncClientShieldConnectedEvent will fire
    }

    @EventHandler
    public void onClientShieldConnected(AsyncClientShieldConnectedEvent e) {  // warning: executes in netty thread
        Connection connection = e.getConnection();
        PauseLock pauseLock = e.pauseLoginProtocol();
        connection.updateConfig(globalConfig)
                .thenAccept(ignore -> {
                    getLogger().info("unlock");
                    pauseLock.unlock();
                })
                .exceptionally(connection::printAndDisconnect);
    }

}
