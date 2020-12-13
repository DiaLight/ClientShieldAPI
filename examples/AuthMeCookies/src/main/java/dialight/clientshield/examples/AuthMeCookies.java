package dialight.clientshield.examples;

import dialight.clientshield.api.*;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.LoginEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

public class AuthMeCookies extends JavaPlugin implements Listener {

    private final SecureRandom sr = new SecureRandom();
    private final Map<String, String> tokens = new ConcurrentHashMap<>();

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
                .thenAccept(token -> {  // warning: executes in netty thread
                    if(token != null) {
                        connection.setUserData("AUTHME_TOKEN", token);
                        System.out.println(connection.getPlayer());
                        getLogger().info("client authme token is: " + token);
                    }
                    pausedProtocol.unlock();
                })
                .exceptionally(connection::printAndDisconnect);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        Connection connection = ClientShieldApi.getConnection(e.getPlayer());
        // assert connection == null;  // I'm sorry
        // Can't connect ClientShield connection with player at the time by bukkit design(
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Connection connection = ClientShieldApi.getConnection(e.getPlayer());
        if(connection == null) return;
        String authmeToken = (String) connection.getUserData("AUTHME_TOKEN");
        String validToken = tokens.get(connection.getName());
        if(Objects.equals(validToken, authmeToken)) {
            AuthMeApi.getInstance().forceLogin(e.getPlayer());
        } else {
            connection.removeUserData("AUTHME_TOKEN");
        }
    }
    @EventHandler
    private void onAuthMeLogin(LoginEvent e) {
        Connection connection = ClientShieldApi.getConnection(e.getPlayer());
        if(connection == null) return;
        String authmeToken = (String) connection.getUserData("AUTHME_TOKEN");
        if(authmeToken == null) {
            String validToken = Base64.getEncoder().encodeToString(sr.generateSeed(16));
            connection.setCookie("AUTHME_TOKEN", validToken)
                    .thenAccept(oldToken -> {
                        tokens.put(connection.getName(), validToken);
                    })
                    .exceptionally(connection::printAndDisconnect);
        }
    }

}
