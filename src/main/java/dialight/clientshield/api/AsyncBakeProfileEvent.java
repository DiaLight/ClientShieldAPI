package dialight.clientshield.api;

import dialight.clientshield.api.Connection;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class AsyncBakeProfileEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Connection connection;
    private String name;
    private UUID id;

    public AsyncBakeProfileEvent(Connection connection) {
        super(true);
        this.connection = connection;
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

    @Override public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
