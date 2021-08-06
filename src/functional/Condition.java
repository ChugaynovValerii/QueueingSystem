package functional;

import java.util.Arrays;

public class Condition {
    private static int SOURCE_COUNT;
    private static int DEVICE_COUNT;
    private static int BUFFER_SIZE;
    
    public static int getSourceCount() {
        return SOURCE_COUNT;
    }
    
    public static int getDeviceCount() {
        return DEVICE_COUNT;
    }
    
    public static int getBufferSize() {
        return BUFFER_SIZE;
    }
    
    public static void setParameters(int sourceCount, int deviceCount, int bufferSize) {
        SOURCE_COUNT = sourceCount;
        DEVICE_COUNT = deviceCount;
        BUFFER_SIZE = bufferSize;
    }
    
    private double time;
    private Integer srcId;
    private Integer deviceId;
    private boolean deviceValue;
    private Integer bufferId;
    private boolean bufferValue;
    private String reqUniqueNumber;
    
    //при смене одной заявки другой из буфера на прибор deviceValue не меняется и остается тру
    
    private boolean[] devicesCondition;
    private boolean[] sourcesCondition;
    private boolean[] buffersCondition;
    private boolean failure;
    
    public Condition(double time, String reqUniqueNumber) {
        this.time = time;
        this.reqUniqueNumber = reqUniqueNumber;
    }
    
    public static Condition initCondition() {
        Condition condition = new Condition(0, "");
        condition.sourcesCondition = new boolean[SOURCE_COUNT];
        condition.devicesCondition = new boolean[DEVICE_COUNT];
        condition.buffersCondition = new boolean[BUFFER_SIZE];
        return condition;
    }
    
    public double getTime() {
        return time;
    }
    
    public String getReqUniqueNumber() {
        return reqUniqueNumber;
    }
    
    public boolean[] getDevicesCondition() {
        return devicesCondition;
    }
    
    public boolean[] getSourcesCondition() {
        return sourcesCondition;
    }
    
    public boolean[] getBuffersCondition() {
        return buffersCondition;
    }
    
    public boolean isFailure() {
        return failure;
    }
    
    public Integer getSrcId() {
        return srcId;
    }
    
    public Integer getDeviceId() {
        return deviceId;
    }
    
    public Integer getBufferId() {
        return bufferId;
    }
    
    public Condition setGenerationEvent(int srcId) {
        this.srcId = srcId;
        return this;
    }
    
    public Condition setExitEvent(int deviceId) {
        this.deviceId = deviceId;
        deviceValue = false;
        return this;
    }
    
    public Condition setEntryEvent(int deviceId) {
        this.deviceId = deviceId;
        deviceValue = true;
        return this;
    }
    
    public Condition setEntryEvent(int deviceId, int bufferId) {
        this.deviceId = deviceId;
        this.bufferId = bufferId;
        deviceValue = true;
        bufferValue = false;
        return this;
    }
    
    public Condition setBufferEvent(int bufferId) {
        this.bufferId = bufferId;
        bufferValue = true;
        return this;
    }
    
    public Condition computeNewCondition(Condition prevCondition) {
        this.sourcesCondition = new boolean[SOURCE_COUNT];
        this.devicesCondition = Arrays.copyOf(prevCondition.devicesCondition, DEVICE_COUNT);
        this.buffersCondition = Arrays.copyOf(prevCondition.buffersCondition, BUFFER_SIZE);
        if (deviceId != null) {
            this.devicesCondition[deviceId] = deviceValue;
        }
        if (bufferId != null) {
            this.buffersCondition[bufferId] = bufferValue;
        }
        if (srcId != null) {
            this.sourcesCondition[srcId] = true;
        }
        
        return this;
    }
    
    public void setFailureEvent() {
        this.failure = true;
    }
    
    public Condition merge(Condition cur) {
        return bufferId != null ? this : cur;
    }
}
