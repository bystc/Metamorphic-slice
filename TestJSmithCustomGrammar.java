import com.example.service.JSmithService;

public class TestJSmithCustomGrammar {
    public static void main(String[] args) {
        JSmithService service = new JSmithService();
        String code = service.generateComplexWithCustomGrammar();
        System.out.println("\n=== Generated Complex Java Code ===\n");
        System.out.println(code);
    }
} 