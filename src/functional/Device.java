package functional;

import java.util.function.Supplier;

public class Device {
    private static int idCounter;
    private final int id;
    private double exitTime;
    
    private final Supplier<Double> distribution;
    
    
    public Device(Supplier<Double> distribution, int deviceCount) {
        this.id = idCounter % deviceCount;
        idCounter++;
        this.distribution = distribution;
    }
    
    public void putRequest(Request request, double curTime) {
        if (curTime >= exitTime && request != null) {
            double entryTime = Math.max(exitTime, request.getGenTime());
            exitTime = entryTime + distribution.get();
            
            request.setEntryTime(entryTime);
            request.setExitTime(exitTime);
            request.setDeviceId(id);
            //System.out.println("functional.Device" + this.id + " receive " + request + String.format(" when t = %.3f", curTime));
        }
    }
    
    public double getExitTime() {
        return exitTime;
    }
    
    public int getId() {
        return id;
    }
}
