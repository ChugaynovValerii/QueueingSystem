package functional;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Buffer {
    private final int bufMaxSize;
    private final boolean[] condition;
    private final PriorityQueue<Request> requests
            = new PriorityQueue<>(Comparator.comparingInt(Request::getSourceId).thenComparing(Request::getGenTime));
    
    
    public Buffer(int bufMaxSize) {
        this.bufMaxSize = bufMaxSize;
        condition = new boolean[bufMaxSize];
    }
    
    public void addRequest(Request request) {
        if (requests.size() == bufMaxSize) {
            return;
        }
        for (int i = 0; i < bufMaxSize; i++) {
            if (!condition[i]) {
                condition[i] = true;
                request.setBufferId(i);
                break;
            }
        }
        requests.add(request);
    }
    
    public Request extractPriorityRequest() {
        Request request = requests.poll();
        if (request != null) {
            condition[request.getBufferId()] = false;
        }
        return request;
    }
    
    public Request getPriorityRequest() {
        return requests.peek();
    }
    
    public int size() {
        return requests.size();
    }
}
