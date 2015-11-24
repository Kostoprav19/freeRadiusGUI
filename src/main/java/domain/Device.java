package domain;

import java.time.LocalDateTime;

/**
 * Created by daniels on 24.11.2015.
 */
public class Device {
    private int id;
    private String mac;
    private String name;
    private String description;
    private int onSwitch;
    private int onPort;
    private int speed;
    private LocalDateTime timeOfRegistration;
    private LocalDateTime lastSeen;
    private int access;

}
