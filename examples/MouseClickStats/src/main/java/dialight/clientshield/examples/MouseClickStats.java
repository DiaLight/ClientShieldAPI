package dialight.clientshield.examples;

import dialight.clientshield.api.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class MouseClickStats extends JavaPlugin implements Listener {

    private void formatTime(long timeMs, List<String> items) {
        items.add(String.format("%dms", timeMs % 1000));
        timeMs = timeMs / 1000;
        if(timeMs == 0) return;
        items.add(String.format("%ds", timeMs % 60));
        timeMs = timeMs / 60;
        if(timeMs == 0) return;
        items.add(String.format("%dm", timeMs % 60));
        timeMs = timeMs / 60;
        if(timeMs == 0) return;
        items.add(String.format("%dh", timeMs % 60));
        timeMs = timeMs / 60;
        if(timeMs == 0) return;
        items.add(String.format("%dd", timeMs % 24));
        timeMs = timeMs / 24;
        if(timeMs == 0) return;
        items.add(String.format("%d month", timeMs % 30));
        timeMs = timeMs / 30;
        if(timeMs == 0) return;
        items.add(String.format("%dy", timeMs % 12));
        timeMs = timeMs / 12;
        if(timeMs == 0) return;
    }
    private String formatTime(long timeMs) {
        LinkedList<String> items = new LinkedList<>();
        formatTime(timeMs, items);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            if(i != 0) sb.append(' ');
            String item = items.pollLast();
            if(item == null) break;
            sb.append(item);
        }
        return sb.toString();
    }
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        PluginCommand clientcontrol = getServer().getPluginCommand("cps");
        clientcontrol.setExecutor((sender, command, label, args) -> {
            if(!(sender instanceof Player)) return false;
            Player player = (Player) sender;
            Connection connection = ClientShieldApi.getConnection(player);
            if(connection == null) {
                player.sendMessage("You are not connected to ClientShield");
                return false;
            }
            LinkedList<String> largs = new LinkedList<>(Arrays.asList(args));
            if(largs.isEmpty()) return false;
            String cmd = largs.pollFirst();
            if(cmd.equals("max")) {
                connection.getMaxCps(Button.MOUSE_LEFT, Button.MOUSE_RIGHT)
                        .thenAccept(cpsMap -> {
                            for (Map.Entry<Button, ButtonCps> entry : cpsMap.entrySet()) {
                                ButtonCps rec = entry.getValue();
                                player.sendMessage(String.format("%s time(since last CS login) %s. sped %.1f click/second", entry.getKey(), formatTime(rec.getTimeMs()), rec.getSpeedCps()));
                            }
                        })
                        .exceptionally(connection::printAndDisconnect);
            } else if(cmd.equals("calcMax")) {
                connection.getRecordedCps(-1, Button.MOUSE_LEFT, Button.MOUSE_RIGHT)
                        .thenAccept(cpsMap -> {
                            for (Map.Entry<Button, List<ButtonCps>> entry : cpsMap.entrySet()) {
                                List<ButtonCps> recs = entry.getValue();

                                ButtonCps maxPike = new ButtonCps(0, 0);
                                for (ButtonCps cps : recs) {
                                    if(maxPike.getSpeedCps() > cps.getSpeedCps()) continue;
                                    maxPike = cps;
                                }
                                player.sendMessage(String.format("%s time(since last CS login) %s. sped %.1f click/second", entry.getKey(), formatTime(maxPike.getTimeMs()), maxPike.getSpeedCps()));
                            }
                        })
                        .exceptionally(connection::printAndDisconnect);
            }
            return true;
        });
        List<String> cmds = Arrays.asList(
                "max",
                "calcMax"
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

}
