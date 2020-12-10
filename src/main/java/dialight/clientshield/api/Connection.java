package dialight.clientshield.api;

import dialight.clientshield.api.ConfigKey;
import dialight.clientshield.api.SysinfoType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface Connection {

    String getVersion();

    InetAddress getAddress();

    @Nullable
    String getName();

    @Nullable
    UUID getId();

    CompletionStage<Map<String, String>> getCookies();

    CompletionStage<Map<SysinfoType, String>> getSysinfo(SysinfoType... sysinfo);

    CompletionStage<String> getCookie(String key);
    CompletionStage<String> setCookie(String key, String value);
    CompletionStage<Object> clearCookies();
    CompletionStage<Long> ping();
    CompletionStage<Object> updateConfig(Map<ConfigKey, Object> config);

    void disconnect(String message);

    default <T> T printAndDisconnect(Throwable ex) {
        ex.printStackTrace();
        this.disconnect(ex.getMessage());
        return null;
    }
    void setAnswerTimeout(Duration timeout);

    String getPlatform();

    @Nullable
    Player getPlayer();

}
