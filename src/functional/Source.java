package functional;

import java.util.function.Supplier;

public class Source {
    private Request lastRequest;
    private double lastGenTime;
    
    private final Supplier<Double> distribution;
    
    private final int id;
    private static int idCounter;
    private int reqCounter;
    
    public Source(Supplier<Double> distribution, int sourceCount) {
        this.id = idCounter % sourceCount;
        idCounter++;
        this.distribution = distribution;
        lastRequest = generateNextRequest();
    }
    
    private Request generateNextRequest() {
        lastRequest = new Request(lastGenTime + distribution.get(), id, ++reqCounter);
        lastGenTime = lastRequest.getGenTime();
        return lastRequest;
    }
    
    public Request getLastRequest() {
        return lastRequest;
    }
    
    public Request extractLastRequest() {
        Request prevRequest = lastRequest;
        lastRequest = generateNextRequest();
        return prevRequest;
    }
}
