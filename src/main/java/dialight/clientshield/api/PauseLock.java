package dialight.clientshield.api;

import java.util.UUID;

public interface PauseLock {

    void unlock();

    void setName(String name);
    void setId(UUID id);

}
