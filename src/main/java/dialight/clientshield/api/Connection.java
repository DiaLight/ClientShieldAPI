package dialight.clientshield.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface Connection {

    String getVersion();
    String getPlatform();
    long getLoginTimeMs();

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
    CompletionStage<Map<Button, ButtonCps>> getMaxCps(Button... button);

    /**
     * Record captures 20 times / sec
     * Client records limit: 6000. You can set less limit.
     */
    CompletionStage<Map<Button, List<ButtonCps>>> getRecordedCps(int limit, Button... button);

    void disconnect(String message);

    default <T> T printAndDisconnect(Throwable ex) {
        ex.printStackTrace();
        this.disconnect(ex.getMessage());
        return null;
    }
    void setAnswerTimeout(Duration timeout);

    @Nullable
    Player getPlayer();

    Object getUserData(Object key);
    Object setUserData(Object key, Object value);
    Object removeUserData(Object key);

    String toString();

}
