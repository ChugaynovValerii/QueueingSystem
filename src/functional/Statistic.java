package functional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Statistic {
    private final int sourceCount;
    private final int deviceCount;
    
    private final List<Request> allRequests = new ArrayList<>();
    
    public Statistic(int sourceCount, int deviceCount, int bufferSize) {
        this.sourceCount = sourceCount;
        this.deviceCount = deviceCount;
        Condition.setParameters(sourceCount, deviceCount, bufferSize);
    }
    
    public int getSourceCount() {
        return sourceCount;
    }
    
    public int getDeviceCount() {
        return deviceCount;
    }
    
    
    public void addRequest(Request request) {
        allRequests.add(request);
    }
    
    private final TreeMap<Double, Condition> conditions = new TreeMap<>();
    
    public Collection<Condition> fillConditions() {
        for (Request request : allRequests) {
            String reqUniqueNumber = request.getSourceId() + "." + request.getReqNumber();
            Condition conditionWhenGen = new Condition(request.getGenTime(), reqUniqueNumber).setGenerationEvent(request.getSourceId());//состояние при генерации заявки
            
            if (request.getEntryTime() == request.getGenTime()) {//обработка попадания на прибор сразу
                conditionWhenGen.setEntryEvent(request.getDeviceId());
            } else if (request.getEntryTime() != 0) {//обработка попадания в буфер
                conditionWhenGen.setBufferEvent(request.getBufferId());
                Condition conditionWhenEntry = new Condition(request.getEntryTime(), reqUniqueNumber).setEntryEvent(request.getDeviceId(), request.getBufferId());
                addCondition(conditionWhenEntry);
                //если из буфера на прибор
            } else {//обработка попадания в отказ
                conditionWhenGen.setFailureEvent();
            }
            conditions.put(request.getGenTime(), conditionWhenGen);
            if (request.getExitTime() != 0) {
                Condition conditionWhenExit = new Condition(request.getExitTime(), reqUniqueNumber).setExitEvent(request.getDeviceId());
                addCondition(conditionWhenExit);//добавляем в эксит события
            }
        }
        computeConditions();
        return conditions.values();
    }
    
    private void addCondition(Condition condition) {
        conditions.merge(condition.getTime(), condition, Condition::merge);
    }
    
    private void computeConditions() {
        Condition prevCondition = Condition.initCondition();
        for (Condition condition : conditions.values()) {
            prevCondition = condition.computeNewCondition(prevCondition);
        }
    }
    
    
    public List<SourceInfo> getSourcesStat() {
        List<SourceInfo> sourceInfoList = new ArrayList<>(sourceCount);
        int[] countFromSource = countFromSource();
        double[] failureProb = getAllFailureProb();
        double[] averageTimeInSystem = averageTimeInSystem();
        double[] averageTimeInDevice = averageTimeInDevice();
        double[] averageTimeInBuffer = averageTimeInBuffer();
        double[] dispersionServiceTime = dispersionServiceTime();
        double[] dispersionBufferTime = dispersionBufferTime();
        for (int i = 0; i < sourceCount; i++) {
            sourceInfoList.add(new SourceInfo(i + 1, countFromSource[i], failureProb[i], averageTimeInSystem[i],
                    averageTimeInBuffer[i], averageTimeInDevice[i], dispersionServiceTime[i], dispersionBufferTime[i]));
        }
        return sourceInfoList;
    }
    
    public List<DeviceInfo> getDeviceStat() {
        List<DeviceInfo> deviceInfoList = new ArrayList<>(deviceCount);
        double[] loadFactors = loadDeviceFactors();
        for (int i = 0; i < deviceCount; i++) {
            deviceInfoList.add(new DeviceInfo(i + 1, loadFactors[i]));
        }
        return deviceInfoList;
    }
    
    public double getCommonFailureProb() {
        int count = 0;
        int unhandledCount = 0;
        for (Request request : allRequests) {
            count++;
            if (request.getExitTime() == 0) {
                unhandledCount++;
            }
        }
        return (double) unhandledCount / count;
    }
    
    public int[] countFromSource() {
        int[] countFrom = new int[sourceCount];
        for (Request request : allRequests) {
            countFrom[request.getSourceId()]++;
        }
        return countFrom;
    }
    
    public double[] getAllFailureProb() {
        int[] countFrom = new int[sourceCount];
        int[] unhandledCountFrom = new int[sourceCount];
        double[] probabilitiesOfFail = new double[sourceCount];
        for (Request request : allRequests) {
            int srcId = request.getSourceId();
            countFrom[srcId]++;
            if (request.getExitTime() == 0) {
                unhandledCountFrom[srcId]++;
            }
        }
        for (int i = 0; i < sourceCount; i++) {
            probabilitiesOfFail[i] = (double) unhandledCountFrom[i] / countFrom[i];
        }
        return probabilitiesOfFail;
    }
    
    public double[] averageTimeInSystem() {
        double[] timeInSystem = new double[sourceCount];
        int[] countFrom = new int[sourceCount];
        for (Request request : allRequests) {
            int srcId = request.getSourceId();
            countFrom[srcId]++;
            timeInSystem[srcId] += Math.max(0, request.getExitTime() - request.getGenTime());
        }
        for (int i = 0; i < sourceCount; i++) {
            timeInSystem[i] = timeInSystem[i] / countFrom[i];
        }
        return timeInSystem;
    }
    
    public double[] averageTimeInBuffer() {
        double[] timeInBuffer = new double[sourceCount];
        int[] countFrom = new int[sourceCount];
        for (Request request : allRequests) {
            int srcId = request.getSourceId();
            countFrom[srcId]++;
            timeInBuffer[srcId] += Math.max(0, request.getEntryTime() - request.getGenTime());
        }
        for (int i = 0; i < sourceCount; i++) {
            timeInBuffer[i] = timeInBuffer[i] / countFrom[i];
        }
        return timeInBuffer;
    }
    
    public double[] averageTimeInDevice() {
        double[] timeInDevice = new double[sourceCount];
        int[] countFrom = new int[sourceCount];
        for (Request request : allRequests) {
            int srcId = request.getSourceId();
            countFrom[srcId]++;
            timeInDevice[srcId] += (request.getExitTime() - request.getEntryTime());
        }
        for (int i = 0; i < sourceCount; i++) {
            timeInDevice[i] = timeInDevice[i] / countFrom[i];
        }
        return timeInDevice;
    }
    
    public double[] loadDeviceFactors() {
        double[] loadDeviceFactors = new double[deviceCount];
        double systemTime = 0;
        for (Request request : allRequests) {
            if (request.getDeviceId() != -1) {
                loadDeviceFactors[request.getDeviceId()] += (request.getExitTime() - request.getEntryTime());
            }
            if (request.getExitTime() > systemTime) {
                systemTime = request.getExitTime();
            }
        }
        for (int i = 0; i < deviceCount; i++) {
            loadDeviceFactors[i] /= systemTime;
        }
        return loadDeviceFactors;
    }
    
    public double[] dispersionServiceTime() {
        List<List<Double>> serviceTime = IntStream.range(0, sourceCount)
                .mapToObj(i -> new ArrayList<Double>())
                .collect(Collectors.toList());
        double[] sumServiceTime = new double[sourceCount];
        
        int[] countFrom = new int[sourceCount];
        for (Request request : allRequests) {
            int srcId = request.getSourceId();
            countFrom[srcId]++;
            double requestServiceTime = request.getExitTime() - request.getEntryTime();
            sumServiceTime[srcId] += requestServiceTime;
            serviceTime.get(srcId).add(requestServiceTime);
        }
        
        double[] dispersionServiceTime = new double[sourceCount];
        for (int i = 0; i < sourceCount; i++) {
            double averageTime = sumServiceTime[i] / countFrom[i];
            double sum = serviceTime.get(i).stream()
                    .map(d -> Math.pow(d - averageTime, 2))
                    .mapToDouble(d -> d).sum();
            dispersionServiceTime[i] = sum / (countFrom[i] - 1);
        }
        return dispersionServiceTime;
    }
    
    public double[] dispersionBufferTime() {
        List<List<Double>> bufferTime = IntStream.range(0, sourceCount)
                .mapToObj(i -> new ArrayList<Double>())
                .collect(Collectors.toList());
        double[] sumBufferTime = new double[sourceCount];
        
        int[] countFrom = new int[sourceCount];
        for (Request request : allRequests) {
            int srcId = request.getSourceId();
            countFrom[srcId]++;
            double requestBufferTime = Math.max(0, request.getEntryTime() - request.getGenTime());
            sumBufferTime[srcId] += requestBufferTime;
            bufferTime.get(srcId).add(requestBufferTime);
        }
        
        double[] dispersionBufferTime = new double[sourceCount];
        for (int i = 0; i < sourceCount; i++) {
            double averageTime = sumBufferTime[i] / countFrom[i];
            double sum = bufferTime.get(i).stream()
                    .map(d -> Math.pow(d - averageTime, 2))
                    .mapToDouble(d -> d).sum();
            dispersionBufferTime[i] = sum / (countFrom[i] - 1);
        }
        return dispersionBufferTime;
    }
    
    public void printStat() {
        int[] countFrom = new int[sourceCount];
        double[] timeInSystem = new double[sourceCount];
        int[] unhandledCountFrom = new int[sourceCount];
        
        for (Request request : allRequests) {
            int srcId = request.getSourceId();
            countFrom[srcId - 1]++;
            if (request.getExitTime() == 0) {
                unhandledCountFrom[srcId - 1]++;
            } else {
                timeInSystem[srcId - 1] += (request.getExitTime() - request.getGenTime());
            }
        }
        for (int i = 0; i < sourceCount; i++) {
            System.out.println(unhandledCountFrom[i] + "\t" + countFrom[i] + "\t"
                    + (double) unhandledCountFrom[i] / countFrom[i] + "\t"
                    + timeInSystem[i] / (countFrom[i] - unhandledCountFrom[i]));
        }
    }
}
