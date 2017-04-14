package pl.pwr.measurement.data;

import java.time.LocalTime;

public class Data {

    private double speed;
    private double clamp;
    private LocalTime time;

    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public double getClamp() {
        return clamp;
    }
    public void setClamp(double clamp) {
        this.clamp = clamp;
    }
    public void setTime(LocalTime time) {
        this.time = time;
    }
    public LocalTime getTime() {
        return time;
    }
    public Data() {
        speed = 0;
        clamp = 0;
        setTime(LocalTime.now());
    }
}
