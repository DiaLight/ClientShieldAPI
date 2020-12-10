package dialight.clientshield.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncLoginResumedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Connection connection;

    public AsyncLoginResumedEvent(Connection connection) {
        super(true);
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
