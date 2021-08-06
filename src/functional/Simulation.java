package functional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Simulation {
    private static int N = 1000;
    private Statistic statistic;
    
    public Simulation(int sourceCount, int deviceCount, int bufferSize, double a, double b, double lambda1, boolean auto) {
        N = 2000;
        System.out.println("буф\tприб\tвер_отк\t\tзагр_при\t\t\tвремя");
        int devCost = 1000;
        int delta = 500;
        for (double lambda = 0.2; lambda <= 1; lambda += 0.2) {
            System.out.println("lambda=" + lambda);
            devCost = devCost + delta;
            delta += 500;
            for (int i = 0; i <= 20; i++) {//буфер
                for (int j = 1; j <= 20; j++) {//прибор
                    run(sourceCount, j, i, a, b, lambda);
                    double loadFactor = Arrays.stream(statistic.loadDeviceFactors()).sum() / j;//загрузка приборов
                    double sum = 0;
                    double[] timeInSystem = statistic.averageTimeInSystem();//время в системе массив
                    int[] count = statistic.countFromSource();
                    for (int k = 0; k < sourceCount; k++) {
                        sum += timeInSystem[k] * count[k];
                    }
                    if (statistic.getCommonFailureProb() < 0.1 && loadFactor > 0.9) {
                        System.out.println(i + "\t" + j + "\t\t" + statistic.getCommonFailureProb() + "\t\t" + loadFactor + "\t\t" + (sum / N) + "\t" + (devCost * j + 150 * i));
                    }
                }
                
            }
        }

//        if (auto) {
//            run(sourceCount, deviceCount, bufferSize, a, b, lambda);
//            double prevProbability = statistic.getCommonFailureProb();
//            while (prevProbability == 0) {
//                N *= 2;
//                run(sourceCount, deviceCount, bufferSize, a, b, lambda);
//                prevProbability = statistic.getCommonFailureProb();
//            }
//            N *= 2;
//            run(sourceCount, deviceCount, bufferSize, a, b, lambda);
//            double curProbability = statistic.getCommonFailureProb();
//            while (Math.abs(prevProbability - curProbability) / prevProbability > 0.1) {
//                System.out.println("prob=" + Math.abs(prevProbability - curProbability) / prevProbability);
//                N *= 2;
//                run(sourceCount, deviceCount, bufferSize, a, b, lambda);
//                prevProbability = curProbability;
//                curProbability = statistic.getCommonFailureProb();
//            }
//            System.out.println("prob=" + Math.abs(prevProbability - curProbability) / prevProbability);
//            System.out.println(N);
//        } else {
//            N = 1000;
//            run(sourceCount, deviceCount, bufferSize, a, b, lambda);
//        }
    }
    
    private void run(int sourceCount, int deviceCount, int bufferSize, double a, double b, double lambda) {
        this.statistic = new Statistic(sourceCount, deviceCount, bufferSize);
        
        List<Source> sources = IntStream.range(0, sourceCount)
                .mapToObj(i -> new Source(() -> Math.min(a, b) + Math.abs(b - a) * Math.random(), sourceCount))
                .collect(Collectors.toList());
        
        List<Device> devices = IntStream.range(0, deviceCount)
                .mapToObj(i -> new Device(() -> -1 / lambda * Math.log(Math.random()), deviceCount))
                .collect(Collectors.toList());
        
        RequestsHandler handler = new RequestsHandler(devices, bufferSize);
        
        for (int i = 0; i < N; i++) {
            Request request = Collections
                    .min(sources, Comparator.comparingDouble(src -> src.getLastRequest().getGenTime()))
                    .extractLastRequest();
            handler.addRequest(request);
            statistic.addRequest(request);
        }
        handler.clearBuffer();
    }
    
    public Statistic getStatistic() {
        return statistic;
    }
//    private static void printStat() {
//        System.out.println("\t\t\t\tDevice1\tDevice2\tDevice3");
//        System.out.print("LoadFactors\t\t");
//        double[] loadFactors = Statistic.loadDeviceFactors();
//        for (int i = 0; i < DEVICE_COUNT; i++) {
//            System.out.print(String.format("%.3f", loadFactors[i]) + "\t");
//        }
//
//        System.out.print("\n\n\t\t\tCount\t\tTSys\t\tPRef\t\tDevT\t\tBufT\t\tDSerT\t\tDBufT");
//        double[] failFactors = Statistic.getAllFailureProb();
//        double[] averageTimeInSystem = Statistic.averageTimeInSystem();
//        int[] countFromSource = Statistic.countFromSource();
//        double[] averageTimeInDevice = Statistic.averageTimeInDevice();
//        double[] averageTimeInBuffer = Statistic.averageTimeInBuffer();
//        double[] dispersionServiceTime = Statistic.dispersionServiceTime();
//        double[] dispersionBufferTime = Statistic.dispersionBufferTime();
//        for (int i = 0; i < sourceCount; i++) {
//            System.out.print("\nSourc" + i + "\t\t"
//                    + countFromSource[i]
//                    + String.format("\t\t%.3f", averageTimeInSystem[i])
//                    + String.format("\t\t%.3f", failFactors[i])
//                    + String.format("\t\t%.3f", averageTimeInDevice[i])
//                    + String.format("\t\t%.3f", averageTimeInBuffer[i])
//                    + String.format("\t\t%.3f", dispersionServiceTime[i])
//                    + String.format("\t\t%.3f", dispersionBufferTime[i]));
//        }
//        System.out.println();
//    }
}
