package dialight.clientshield.examples;

import dialight.clientshield.api.Connection;
import dialight.clientshield.api.PauseLock;
import dialight.clientshield.api.AsyncBakeProfileEvent;
import dialight.clientshield.api.AsyncClientShieldConnectedEvent;
import dialight.clientshield.api.AsyncLoginResumedEvent;
import dialight.clientshield.api.AsyncNewLoginConnectionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class MiscExamples extends JavaPlugin implements Listener {

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
        getLogger().info(String.format("login %s %s", e.getId(), e.getName()));
        e.setId(UUID.fromString("00112233-4455-6677-8899-AABBCCDDEEFF"));
        e.setName("Example");
        e.pauseAndConnectClientShield();  // AsyncClientShieldConnectedEvent will fire
//        e.disconnect("You fired");
    }

    @EventHandler
    public void onClientShieldConnected(AsyncClientShieldConnectedEvent e) {  // warning: executes in netty thread
        getLogger().info(String.format("clientshield connected %s %s", e.getConnection(), e.getId()));
//        e.setId(UUID.fromString("00112233-4455-6677-8899-AABBCCDDEEFF"));
//        e.setName("Example");
//        e.getId()
//        e.getName()
//        e.getConnection().getId()
//        e.getConnection().getName()
//        e.disconnect("You fired")

        // connecting screen will continue until you call pausedProtocol.resume()
        PauseLock pauseLock = e.pauseLoginProtocol();

        Connection connection = e.getConnection();
        CompletionStage<Void> pingFuture = connection.ping().thenAccept(ping -> {  // warning: executes in netty thread
            getLogger().info("ping: " + ping + "ms.");
        });
        CompletionStage<?> tokenFuture = connection.getCookie("TOKEN")
                .thenCompose(token -> {  // warning: executes in netty thread
                    if(token != null) return CompletableFuture.completedFuture(token);
                    token = Base64.getEncoder().encodeToString(sr.generateSeed(16));
                    getLogger().info("send new token");
                    CompletionStage<String> writeDefer = connection.setCookie("TOKEN", token);
                    String tokenn = token;
                    return writeDefer.thenApply(oldToken -> {
                        getLogger().info("token is written to client. oldToken: " + oldToken);
                        return tokenn;
                    });  // will send cookie to client
                })
                .thenAccept(token -> {  // warning: executes in netty thread
                    getLogger().info("client token is: " + token);
                    // you can use PauseLock object to set id and name in CompletionStage
//                    pauseLock.setId(UUID.fromString("00112233-4455-6677-8899-AABBCCDDEEFF"));
//                    pauseLock.setName("Example");
                });
        connection.setAnswerTimeout(Duration.ofSeconds(10));
        CompletableFuture
                .allOf(
                        tokenFuture.toCompletableFuture(),
                        pingFuture.toCompletableFuture()
                )
                .thenAccept(ignore -> {  // warning: executes in netty thread
//                    pauseLock.setId(UUID.fromString("00112233-4455-6677-8899-AABBCCDDEEFF"));
//                    pauseLock.setName("Example");
                    getLogger().info("unlock");
                    pauseLock.unlock();  // unpause login protocol
                })
                .exceptionally(connection::printAndDisconnect);
    }

    @EventHandler
    public void onBakeProfile(AsyncBakeProfileEvent e) {  // warning: executes in netty thread
        // last chance to change id and name of player
//        e.setId(UUID.fromString("00112233-4455-6677-8899-AABBCCDDEEFF"));
//        e.setName("Example");
        getLogger().info("profile is baked: " + e.getName() + " " + e.getId());
    }

    @EventHandler
    public void onLoginResume(AsyncLoginResumedEvent e) {  // warning: executes in netty thread
        // just for you know when the connection continue
    }

}
