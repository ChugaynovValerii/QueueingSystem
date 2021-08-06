package functional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RequestsHandler {
    private final Buffer buffer;
    private final List<Device> devices;
    private double curTime;
    
    public RequestsHandler(List<Device> devices, int bufferSize) {
        this.buffer = new Buffer(bufferSize);
        this.devices = devices;
    }
    
    public void addRequest(Request request) {
        curTime = request.getGenTime();
        updateAllForTime(curTime);
        
        Device deviceForRequest = devices.stream()
                .filter(device -> device.getExitTime() < curTime)
                .min(Comparator.comparingInt(Device::getId)).orElse(null);
        if (buffer.size() == 0 && deviceForRequest != null) {
            deviceForRequest.putRequest(request, curTime);
        } else {
            buffer.addRequest(request);
        }
    }
    
    private void updateAllForTime(double curTime) {
        Request requestFromBuffer = buffer.getPriorityRequest();
        Device deviceForRequest = getAppropriateDevice(curTime);
        
        while (deviceForRequest != null && requestFromBuffer != null) {
            deviceForRequest.putRequest(buffer.extractPriorityRequest(), curTime);
            
            deviceForRequest = getAppropriateDevice(curTime);
            requestFromBuffer = buffer.getPriorityRequest();
        }
    }
    
    private Device getAppropriateDevice(double curTime) {
        return devices.stream()
                .filter(device -> device.getExitTime() < curTime)
                .min(Comparator.comparingDouble(Device::getExitTime)).orElse(null);
    }
    
    public void clearBuffer() {
        while (buffer.size() != 0) {
            Device closestDevice = Collections.min(devices, Comparator.comparingDouble(Device::getExitTime));
            closestDevice.putRequest(buffer.extractPriorityRequest(), closestDevice.getExitTime());
        }
    }
}
