package dialight.clientshield.examples;

import dialight.clientshield.api.Connection;
import dialight.clientshield.api.PauseLock;
import dialight.clientshield.api.AsyncNewLoginConnectionEvent;
import dialight.clientshield.api.AsyncClientShieldConnectedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AuthMeCookies extends JavaPlugin implements Listener {

    private final SecureRandom sr = new SecureRandom();

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

        PauseLock pausedProtocol = e.pauseLoginProtocol();
        connection.getCookie("AUTHME_TOKEN")
                .thenCompose(token -> {  // warning: executes in netty thread
                    if(token != null) return CompletableFuture.completedFuture(token);
                    token = Base64.getEncoder().encodeToString(sr.generateSeed(16));
                    getLogger().info("send new token");
                    CompletionStage<String> writeDefer = connection.setCookie("AUTHME_TOKEN", token);
                    String tokenn = token;
                    return writeDefer.thenApply(oldToken -> {
                        getLogger().info("token is written to client. oldToken: " + oldToken);
                        return tokenn;
                    });  // will send cookie to client
                })
                .thenAccept(token -> {  // warning: executes in netty thread
                    getLogger().info("client authme token is: " + token);
                    pausedProtocol.unlock();
                })
                .exceptionally(connection::printAndDisconnect);
    }

}
