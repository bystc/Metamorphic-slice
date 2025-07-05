public class TestReorder {
    public static void main(String[] args) {
        // 测试语句重排序功能
        System.out.println("Testing statement reordering...");
        
        // 这些语句应该可以重排序
        int a = 10;
        int b = 20;
        int c = 30;
        
        // 这些循环应该可以重排序（因为它们不包含切片相关变量）
        for (int i = 0; i < 5; i++) {
            System.out.println("Loop 1: " + i);
        }
        
        for (int j = 0; j < 3; j++) {
            System.out.println("Loop 2: " + j);
        }
        
        // 这些输出语句应该可以重排序
        System.out.println("Output 1");
        System.out.println("Output 2");
        System.out.println("Output 3");
        
        // 这些是切片相关变量，不应该重排序
        int result = a + b + c;
        int sum = result * 2;
        int finalResult = sum + 100;
        
        System.out.println("Final result: " + finalResult);
    }
} 