import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;

public class TestFormatter {
    public static void main(String[] args) throws Exception {
        String code = new String(Files.readAllBytes(Paths.get("test_method_call.java")));
        System.out.println("Original code:");
        System.out.println(code);
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Test method extraction
        List<String> methods = extractInstanceMethods(code);
        System.out.println("Extracted methods: " + methods);
        
        // Test class name extraction
        String className = extractClassName(code);
        System.out.println("Extracted class name: " + className);
        
        if (!methods.isEmpty() && className != null) {
            String enhanced = insertMethodCallsIntoMain(code, className, methods);
            System.out.println("\nEnhanced code:");
            System.out.println(enhanced);
        }
    }
    
    private static List<String> extractInstanceMethods(String code) {
        List<String> methods = new ArrayList<>();
        Pattern methodPattern = Pattern.compile("public\\s+void\\s+([\\w$]+)\\s*\\(\\s*\\)\\s*\\{");
        Matcher matcher = methodPattern.matcher(code);
        
        while (matcher.find()) {
            String methodName = matcher.group(1);
            if (!"main".equals(methodName)) {
                methods.add(methodName);
            }
        }
        return methods;
    }
    
    private static String extractClassName(String code) {
        Pattern classPattern = Pattern.compile("(?:public\\s+|private\\s+|protected\\s+|abstract\\s+|final\\s+|strictfp\\s+)*class\\s+([\\w$]+)");
        Matcher matcher = classPattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    private static String insertMethodCallsIntoMain(String code, String className, List<String> methods) {
        // 找到main方法的开始和结束位置，使用更精确的匹配
        int mainStart = code.indexOf("public static void main");
        if (mainStart == -1) {
            return code;
        }

        // 找到main方法的开始大括号
        int braceStart = code.indexOf('{', mainStart);
        if (braceStart == -1) {
            return code;
        }

        // 找到main方法的结束大括号（匹配的大括号）
        int braceCount = 1;
        int braceEnd = braceStart + 1;
        while (braceEnd < code.length() && braceCount > 0) {
            char c = code.charAt(braceEnd);
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
            }
            braceEnd++;
        }

        if (braceCount != 0) {
            return code; // 大括号不匹配
        }

        // 在main方法结束前插入方法调用
        StringBuilder methodCalls = new StringBuilder();
        methodCalls.append("\n        // 调用类内方法\n");
        methodCalls.append("        ").append(className).append(" instance = new ").append(className).append("();\n");

        for (String method : methods) {
            methodCalls.append("        instance.").append(method).append("();\n");
        }

        // 构建新的代码
        String beforeMain = code.substring(0, braceEnd - 1);
        String afterMain = code.substring(braceEnd - 1);

        return beforeMain + methodCalls.toString() + afterMain;
    }
}
