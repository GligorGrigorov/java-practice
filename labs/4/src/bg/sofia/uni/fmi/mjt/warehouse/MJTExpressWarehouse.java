package bg.sofia.uni.fmi.mjt.warehouse;

import bg.sofia.uni.fmi.mjt.warehouse.exceptions.CapacityExceededException;
import bg.sofia.uni.fmi.mjt.warehouse.exceptions.ParcelNotFoundException;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;


public class MJTExpressWarehouse<L, P> implements DeliveryServiceWarehouse<L, P> {
    private int capacity;
    private int retentionPeriod;
    private Map<L, P> parcels;
    private Map<L, LocalDateTime> submissionDates;

    public MJTExpressWarehouse(int capacity, int retentionPeriod) {
        this.capacity = capacity;
        this.retentionPeriod = retentionPeriod;
        parcels = new HashMap<>();
        submissionDates = new HashMap<>();
    }

    private boolean removeRetainedElement() {
        Iterator<Map.Entry<L, P>> it = parcels.entrySet().iterator();
        L temp = null;
        while (it.hasNext()) {
            L key = it.next().getKey();
            if (submissionDates.get(key).plusDays(retentionPeriod).isBefore(LocalDateTime.now())) {
                temp = key;
                break;
            }
        }
        if (temp != null) {
            submissionDates.remove(temp);
            parcels.remove(temp);
            return true;
        }
        return false;
    }

    @Override
    public void submitParcel(L label, P parcel, LocalDateTime submissionDate) throws CapacityExceededException {
        if (label == null || parcel == null || submissionDate == null || LocalDateTime.now().isBefore(submissionDate)) {
            throw new IllegalArgumentException("Date is the future, or some of the parameters are null.");
        }

        if (capacity == parcels.size() && !removeRetainedElement()) {
            throw new CapacityExceededException("Capacity exceeded in submitParcel.");
        }
        parcels.put(label, parcel);
        submissionDates.put(label, submissionDate);
    }

    @Override
    public P getParcel(L label) {
        if (label == null) {
            throw new IllegalArgumentException("Label is null.");
        }
        if (!parcels.containsKey(label)) {
            return null;
        }
        return parcels.get(label);
    }

    @Override
    public P deliverParcel(L label) throws ParcelNotFoundException {
        if (label == null) {
            throw new IllegalArgumentException("Label is null.");
        }
        if (!parcels.containsKey(label)) {
            throw new ParcelNotFoundException("Parse not found in warehouse.");
        }
        submissionDates.remove(label);
        return parcels.remove(label);

    }

    @Override
    public double getWarehouseSpaceLeft() {
        return (Math.round((capacity - parcels.size()) / (double) capacity * 100.0) / 100.0);
    }

    @Override
    public Map<L, P> getWarehouseItems() {
        return parcels;
    }

    @Override
    public Map<L, P> deliverParcelsSubmittedBefore(LocalDateTime before) {
        if (before == null) {
            throw new IllegalArgumentException("Date is null.");
        }
        Map<L, P> tempParcels = new HashMap<>();
        if (before.isAfter(LocalDateTime.now())) {
            return parcels;
        }
        Iterator<Map.Entry<L, LocalDateTime>> it = submissionDates.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<L, LocalDateTime> entry = it.next();
            if (entry.getValue().isBefore(before)) {
                tempParcels.put(entry.getKey(), parcels.get(entry.getKey()));
            }
        }
        for (L key : tempParcels.keySet()) {
            submissionDates.remove(key);
            parcels.remove(key);
        }
        return tempParcels;
    }

    @Override
    public Map<L, P> deliverParcelsSubmittedAfter(LocalDateTime after) {
        if (after == null) {
            throw new IllegalArgumentException("Date is null.");
        }
        Map<L, P> tempParcels = new HashMap<>();
        if (after.isAfter(LocalDateTime.now())) {
            return tempParcels;
        }
        Iterator<Map.Entry<L, LocalDateTime>> it = submissionDates.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<L, LocalDateTime> entry = it.next();
            if (entry.getValue().isAfter(after)) {
                tempParcels.put(entry.getKey(), parcels.get(entry.getKey()));
            }
        }
        for (L key : tempParcels.keySet()) {
            submissionDates.remove(key);
            parcels.remove(key);
        }
        return tempParcels;
    }
}
