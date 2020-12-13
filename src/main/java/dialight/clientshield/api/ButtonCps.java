package dialight.clientshield.api;

public class ButtonCps {

    private final long timeMs;
    private final float speedCps;

    public ButtonCps(long timeMs, float speedCps) {
        this.timeMs = timeMs;
        this.speedCps = speedCps;
    }

    /**
     * time in milliseconds since last ClientShield login
     */
    public long getTimeMs() {
        return timeMs;
    }

    public float getSpeedCps() {
        return speedCps;
    }

}
