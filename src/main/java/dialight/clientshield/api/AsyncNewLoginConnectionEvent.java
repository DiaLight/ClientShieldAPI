package dialight.clientshield.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AsyncNewLoginConnectionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Result result = Result.RESUME_NOW;
    private String name;
    private UUID id;
    private String message = "";

    public AsyncNewLoginConnectionEvent(Connection connection) {
        super(true);
        this.name = connection.getName();
        this.id = connection.getId();
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

    public void resumeNow() {
        if(this.result == Result.DISCONNECT) return;
        if(this.result == Result.CONNECT_CLIENTSHIELD) return;
        this.result = Result.RESUME_NOW;
        this.message = "";
    }

    public void pauseAndConnectClientShield() {
        if(this.result == Result.DISCONNECT) return;
        this.result = Result.CONNECT_CLIENTSHIELD;
        this.message = "";
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
        CONNECT_CLIENTSHIELD,
        DISCONNECT;
    }

}
