import com.github.lombrozo.jsmith.RandomJavaClass;

public class TestBasicExample {
    public static void main(String... args) {
        try {
            RandomJavaClass clazz = new RandomJavaClass();
            String code = clazz.src();
            System.out.println("Generated Java code:");
            System.out.println("===================");
            System.out.println(code);
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
