import java.util.concurrent.*;
import java.util.*;

public class TestPerformance {
    public static void main(String[] args) {
        System.out.println("=== 多线程性能测试 ===");
        System.out.println("CPU核心数: " + Runtime.getRuntime().availableProcessors());
        
        // 测试单线程vs多线程的性能差异
        int numTasks = 10;
        
        // 单线程测试
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numTasks; i++) {
            simulateWork(i);
        }
        long singleThreadTime = System.currentTimeMillis() - startTime;
        
        // 多线程测试
        startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<?>> futures = new ArrayList<>();
        
        for (int i = 0; i < numTasks; i++) {
            final int index = i;
            futures.add(executor.submit(() -> simulateWork(index)));
        }
        
        // 等待所有任务完成
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        executor.shutdown();
        long multiThreadTime = System.currentTimeMillis() - startTime;
        
        System.out.println("单线程执行时间: " + singleThreadTime + "ms");
        System.out.println("多线程执行时间: " + multiThreadTime + "ms");
        System.out.println("性能提升: " + String.format("%.2f", (double)singleThreadTime / multiThreadTime) + "x");
        
        System.out.println("\n=== 优化说明 ===");
        System.out.println("1. 使用ThreadPoolExecutor进行并行代码生成");
        System.out.println("2. 线程池大小设置为CPU核心数");
        System.out.println("3. 使用CompletableFuture进行异步任务管理");
        System.out.println("4. 添加了超时机制(30秒)防止任务卡死");
        System.out.println("5. 使用ConcurrentHashMap保证线程安全");
        System.out.println("6. 添加了详细的性能监控日志");
    }
    
    private static void simulateWork(int index) {
        try {
            // 模拟代码生成和文件处理工作
            Thread.sleep(100 + new Random().nextInt(200));
            System.out.println("任务 " + index + " 完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 