package dialight.clientshield.api;

import java.util.EnumMap;
import java.util.Map;

public class ConfigBuilder {

    private final Map<ConfigKey, Object> config = new EnumMap<>(ConfigKey.class);

    public ConfigBuilder setLeftCooldownMs(int val) {
        config.put(ConfigKey.LEFT_COOLDOWN, val);
        return this;
    }

    public ConfigBuilder setRightCooldownMs(int val) {
        config.put(ConfigKey.RIGHT_COOLDOWN, val);
        return this;
    }

    public ConfigBuilder setChunkReloadCooldownMs(int val) {
        config.put(ConfigKey.CHUNK_RELOAD_COOLDOWN, val);
        return this;
    }

    public ConfigBuilder setHideSubtitles(boolean val) {
        config.put(ConfigKey.HIDE_SUBTITLES, val);
        return this;
    }

    /**
     * recommended [-180, 180],  0: user choice
     */
    public ConfigBuilder setFov(float val) {
        config.put(ConfigKey.FOV, val);
        return this;
    }

    /**
     * recommended [-2, 2],  0: user choice
     */
    public ConfigBuilder setGamma(float val) {
        config.put(ConfigKey.GAMMA, val);
        return this;
    }

    /**
     * available [-1, 2],  -1: user choice
     */
    public ConfigBuilder setThirdPersonView(int val) {
        config.put(ConfigKey.THIRD_PERSON_VIEW, val);
        return this;
    }

    public ConfigBuilder setStopRenderHitBoxes(boolean val) {
        config.put(ConfigKey.STOP_RENDER_HIT_BOXES, val);
        return this;
    }

    public Map<ConfigKey, Object> build() {
        return config;
    }

}
