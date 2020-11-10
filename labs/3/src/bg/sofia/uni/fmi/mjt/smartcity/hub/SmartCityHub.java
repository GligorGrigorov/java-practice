package bg.sofia.uni.fmi.mjt.smartcity.hub;

import bg.sofia.uni.fmi.mjt.smartcity.device.ASmartDevice;
import bg.sofia.uni.fmi.mjt.smartcity.device.SmartDevice;
import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.LocalDateTime;
import java.util.*;

public class SmartCityHub {

    private Map<String,SmartDevice> devices;
    private int devicesCounter[];
    public SmartCityHub() {
        devicesCounter = new int[DeviceType.values().length];
        devices = new TreeMap<>();
    }

    private Comparator<String> powerConsumptionComparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            LocalDateTime now = LocalDateTime.now();
            double first = java.time.Duration.between(devices.get(o2).getInstallationDateTime(), now).toHours() * devices.get(o2).getPowerConsumption();
            double second = java.time.Duration.between(devices.get(o1).getInstallationDateTime(), now).toHours() * devices.get(o1).getPowerConsumption();
            double check = second - first;
            if(check > 0){
                return 1;
            }else if (check < 0){
                return -1;
            }
            return 0;
        }
    };
    /**
     * Adds a @device to the SmartCityHub.
     *
     * @throws IllegalArgumentException         in case @device is null.
     * @throws DeviceAlreadyRegisteredException in case the @device is already registered.
     */
    public void register(SmartDevice device) throws DeviceAlreadyRegisteredException {
        if (device == null){
            throw new IllegalArgumentException();
        }
        if(devices.containsKey(device.getId())){
            throw new DeviceAlreadyRegisteredException();
        }
        devices.put(device.getId(),device);
        devicesCounter[device.getType().ordinal()]++;
    }

    /**
     * Removes the @device from the SmartCityHub.
     *
     * @throws IllegalArgumentException in case null is passed.
     * @throws DeviceNotFoundException  in case the @device is not found.
     */
    public void unregister(SmartDevice device) throws DeviceNotFoundException {
        if (device == null){
            throw new IllegalArgumentException();
        }
        if(!devices.containsKey(device.getId())){
            throw new DeviceNotFoundException();
        }
        devices.remove(device.getId());
        devicesCounter[device.getType().ordinal()]--;
    }

    /**
     * Returns a SmartDevice with an ID @id.
     *
     * @throws IllegalArgumentException in case @id is null.
     * @throws DeviceNotFoundException  in case device with ID @id is not found.
     */
    public SmartDevice getDeviceById(String id) throws DeviceNotFoundException {
        if (id == null){
            throw new IllegalArgumentException();
        }
        if(!devices.containsKey(id)){
            throw new DeviceNotFoundException();
        }
        return devices.get(id);
    }

    /**
     * Returns the total number of devices with type @type registered in SmartCityHub.
     *
     * @throws IllegalArgumentException in case @type is null.
     */
    public int getDeviceQuantityPerType(DeviceType type) {
        if (type == null){
            throw new IllegalArgumentException();
        }
        return devicesCounter[type.ordinal()];
    }

    /**
     * Returns a collection of IDs of the top @n devices which consumed
     * the most power from the time of their installation until now.
     * <p>
     * The total power consumption of a device is calculated by the hours elapsed
     * between the two LocalDateTime-s: the installation time and the current time (now)
     * multiplied by the stated nominal hourly power consumption of the device.
     * <p>
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     *
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<String> getTopNDevicesByPowerConsumption(int n) {
        if (n < 0){
            throw new IllegalArgumentException();
        }
        List<String> filteredDevices = new ArrayList<>(devices.size());
        filteredDevices.addAll(devices.keySet());
        Collections.sort(filteredDevices,powerConsumptionComparator);
        if (n >= filteredDevices.size()){
            return filteredDevices;
        }
        return filteredDevices.subList(0,n);
    }

    /**
     * Returns a collection of the first @n registered devices, i.e the first @n that were added
     * in the SmartCityHub (registration != installation).
     * <p>
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     *
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<SmartDevice> getFirstNDevicesByRegistration(int n) {
        if (n < 0){
            throw new IllegalArgumentException();
        }
        if (n >= devices.size()){
            return devices.values();
        }
        List<SmartDevice> firstNDevices = new ArrayList<>(n);
        int i = 0;
        for (Map.Entry<String,SmartDevice> entry:
             devices.entrySet()) {
            firstNDevices.add(entry.getValue());
            i++;
            if(i == n){
                break;
            }
        }
        return firstNDevices;
    }
}