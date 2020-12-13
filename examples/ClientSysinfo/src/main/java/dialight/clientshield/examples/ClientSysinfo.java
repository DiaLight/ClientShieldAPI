package dialight.clientshield.examples;

import dialight.clientshield.api.SysinfoType;
import dialight.clientshield.api.Connection;
import dialight.clientshield.api.PauseLock;
import dialight.clientshield.api.AsyncNewLoginConnectionEvent;
import dialight.clientshield.api.AsyncClientShieldConnectedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ClientSysinfo extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
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
        CompletionStage<?> sysinfoFuture = connection
                .getSysinfo(
                        SysinfoType.CPU_ID, SysinfoType.CPU_NAME, SysinfoType.CPU_SIGNATURE,
                        SysinfoType.SYS_MODEL, SysinfoType.SYS_SERIAL, SysinfoType.SYS_MANUFACTURER,
                        SysinfoType.BOARD_MODEL, SysinfoType.BOARD_VERSION, SysinfoType.BOARD_MANUFACTURER, SysinfoType.BOARD_SERIAL,
                        SysinfoType.MEM_TOTAL, SysinfoType.MEM_AVAILABLE,
                        SysinfoType.OS_FAMILY, SysinfoType.OS_VERSION, SysinfoType.OS_BITNESS, SysinfoType.OS_MANUFACTURER,
                        SysinfoType.JSON_GPUS, SysinfoType.JSON_DISKS, SysinfoType.JSON_NETWORKS, SysinfoType.JSON_DISPLAYS, SysinfoType.JSON_POWERS
                )
                .thenAccept(map -> {  // warning: executes in netty thread
                    for (Map.Entry<SysinfoType, String> entry : map.entrySet()) {
                        getLogger().info(entry.getKey() + ": " + entry.getValue());
                    }
                    // exmaple:  // "unknown" means failed to resolve
//                    CPU_ID: ACC2FEAB102306B1
//                    CPU_NAME: Intel(R) Core(TM) i7-6400U CPU @ 2.50GHz
//                    CPU_SIGNATURE: Intel64 Family 6 Model 78 Stepping 3
//                    SYS_MODEL: Aspire E5-475 (version: V1.03)
//                    SYS_SERIAL: unknown
//                    SYS_MANUFACTURER: Acer
//                    BOARD_MODEL: Ironman_SK
//                    BOARD_VERSION: V1.03
//                    BOARD_MANUFACTURER: Acer
//                    BOARD_SERIAL: unknown
//                    MEM_TOTAL: 12430843904
//                    MEM_AVAILABLE: 3345141760
//                    OS_FAMILY: Ubuntu
//                    OS_VERSION: 20.04.1 LTS (Focal Fossa) build 5.4.0-54-generic
//                    OS_BITNESS: 64
//                    OS_MANUFACTURER: GNU/Linux
//                    JSON_GPUS: [{"deviseId":"0x1214","name":"Skylake GT2 [HD Graphics 510]","vendor":"Intel Corporation (0x8086)","versionInfo":"unknown","vram":268435456}]
//                    JSON_DISKS: [{"name":"/dev/sda","model":"TOSHIBA-TR100","serial":"14EA523BKBSN","size":480103981056}]
//                    JSON_NETWORKS: [{"name":"docker0","mac":"02:26:57:00:1f:02","display_name":"docker0"},{"name":"wlp3s0","mac":"00:26:57:00:1f:02","display_name":"wlp3s0"}]
//                    JSON_DISPLAYS: [{"edid":"00FFFFFFFFFFFF0006AF2D200000000000160104901D117802BC05A2554C9A250E5054000000010101010101010101010101010101011D3680A070381E4030208E0025A5100000181D3680087238664030208E0025A510000018000000FE0041554F0A202020202020202020000000FE004231333348414E30322E30200A0043"}]
//                    JSON_POWERS: [{"name":"BAT1","device_name":"AS23A4J","serial":"14351"},{"name":"hidpp_battery_0","device_name":"Wireless Mouse M560","serial":"412c-ba-cb-14-b6"}]
                });
        PauseLock pauseLock = e.pauseLoginProtocol(); // You will have to call PauseLock::unlock
        CompletableFuture
                .allOf(
                        sysinfoFuture.toCompletableFuture()
                )
                .thenAccept(ignore -> {  // warning: executes in netty thread
                    getLogger().info("unlock");
                    pauseLock.unlock();
                })
                .exceptionally(connection::printAndDisconnect);  // warning: executes in netty or Bukkit async thread
    }

}
