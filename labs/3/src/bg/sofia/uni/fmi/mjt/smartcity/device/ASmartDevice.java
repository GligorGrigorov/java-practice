package bg.sofia.uni.fmi.mjt.smartcity.device;

import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.LocalDateTime;

public abstract class ASmartDevice implements SmartDevice, Comparable<ASmartDevice> {

    private String id;
    private String name;
    private double powerConsumption;
    private LocalDateTime installationDateTime;
    private DeviceType type;
    private static int[] uniques = new int[DeviceType.values().length];
    private String generateID(String shortName, String name, DeviceType type){
        return shortName + "-" + name + "-" + uniques[type.ordinal()];
    }
    public ASmartDevice(String name, double powerConsumption, LocalDateTime installationDateTime, DeviceType type) {
        this.name = name;
        this.powerConsumption = powerConsumption;
        this.installationDateTime = installationDateTime;
        this.type = type;
        this.id = generateID(type.getShortName(), name, type);
        uniques[type.ordinal()]++;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPowerConsumption() {
        return powerConsumption;
    }

    @Override
    public LocalDateTime getInstallationDateTime() {
        return installationDateTime;
    }

    @Override
    public DeviceType getType() {
        return type;
    }

    @Override
    public int compareTo(ASmartDevice o) {
        return this.getInstallationDateTime().compareTo(o.getInstallationDateTime());
    }

    public boolean equals(ASmartDevice obj) {
        return (0 == this.getId().compareTo(obj.getId()));
    }


}
