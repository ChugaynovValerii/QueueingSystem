package functional;

public class Request {
    private final double genTime;
    private double exitTime;
    private final int sourceId;
    private int deviceId = -1;
    private double entryTime;
    private int bufferId = -1;
    private final int reqNumber;
    
    public Request(double genTime, int sourceId, int reqNumber) {
        this.sourceId = sourceId;
        this.genTime = genTime;
        this.reqNumber = reqNumber;
    }
    
    public double getGenTime() {
        return genTime;
    }
    
    public void setExitTime(double exitTime) {
        this.exitTime = exitTime;
    }
    
    public double getExitTime() {
        return exitTime;
    }
    
    public int getSourceId() {
        return sourceId;
    }
    
    public int getReqNumber() {
        return reqNumber;
    }
    
    public void setDeviceId(int id) {
        this.deviceId = id;
    }
    
    public void setEntryTime(double entryTime) {
        this.entryTime = entryTime;
    }
    
    public int getDeviceId() {
        return deviceId;
    }
    
    public double getEntryTime() {
        return entryTime;
    }
    
    public int getBufferId() {
        return bufferId;
    }
    
    public void setBufferId(int bufferId) {
        this.bufferId = bufferId;
    }
    
    @Override
    public String toString() {
        return "functional.Request{" +
                String.format("genTime=%.3f", genTime) +
                String.format(", exitTime=%.3f", exitTime) +
                ", sourceId=" + sourceId +
                ", deviceId=" + deviceId +
                String.format(", entryTime=%.3f", entryTime) +
                '}';
    }
    
}
