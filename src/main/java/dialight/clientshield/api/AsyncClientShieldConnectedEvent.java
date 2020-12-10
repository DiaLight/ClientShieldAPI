package dialight.clientshield.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Supplier;

public class AsyncClientShieldConnectedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Connection connection;
    private final Supplier<PauseLock> lockBuilder;
    private Result result = Result.RESUME_NOW;
    private String name;
    private UUID id;
    private String message = "";

    public AsyncClientShieldConnectedEvent(Connection connection, Supplier<PauseLock> lockBuilder) {
        super(true);
        this.connection = connection;
        this.lockBuilder = lockBuilder;
        this.name = connection.getName();
        this.id = connection.getId();
    }

    public Connection getConnection() {
        return connection;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return this.id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    @NotNull public Result getResult() {
        return this.result;
    }

    // for use in deferred
    public PauseLock pauseLoginProtocol() {
        this.result = Result.PAUSE_PROTOCOL;
        this.message = "";
        return lockBuilder.get();
    }

    public void disconnect(@NotNull String message) {
        this.result = Result.DISCONNECT;
        this.message = message;
    }

    @NotNull public String getDisconnectMessage() {
        return this.message;
    }

    public void setDisconnectMessage(@NotNull String message) {
        this.message = message;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum Result {
        RESUME_NOW,
        PAUSE_PROTOCOL,
        DISCONNECT;
    }

}
