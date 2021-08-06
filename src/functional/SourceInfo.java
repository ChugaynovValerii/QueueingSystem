package functional;

public class SourceInfo {
    private final int id;
    private final int count;
    private final double failureProb;
    private final double averageTimeInSystem;
    private final double averageTimeInBuffer;
    private final double averageTimeInDevice;
    private final double dispersionServiceTime;
    private final double dispersionBufferTime;
    
    public SourceInfo(int id, int count, double failureProb, double averageTimeInSystem, double averageTimeInBuffer,
                      double averageTimeInDevice, double dispersionServiceTime, double dispersionBufferTime) {
        this.id = id;
        this.count = count;
        this.failureProb = failureProb;
        this.averageTimeInSystem = averageTimeInSystem;
        this.averageTimeInBuffer = averageTimeInBuffer;
        this.averageTimeInDevice = averageTimeInDevice;
        this.dispersionServiceTime = dispersionServiceTime;
        this.dispersionBufferTime = dispersionBufferTime;
    }
    
    public int getId() {
        return id;
    }
    
    public int getCount() {
        return count;
    }
    
    public double getFailureProb() {
        return failureProb;
    }
    
    
    public double getAverageTimeInSystem() {
        return averageTimeInSystem;
    }
    
    public double getAverageTimeInBuffer() {
        return averageTimeInBuffer;
    }
    
    public double getAverageTimeInDevice() {
        return averageTimeInDevice;
    }
    
    public double getDispersionServiceTime() {
        return dispersionServiceTime;
    }
    
    public double getDispersionBufferTime() {
        return dispersionBufferTime;
    }
}
