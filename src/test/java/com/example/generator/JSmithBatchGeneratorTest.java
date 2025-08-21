package com.example.generator;

import com.github.lombrozo.jsmith.BatchGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 测试整合BatchGenerator功能的JSmithCodeGenerator
 */
public class JSmithBatchGeneratorTest {
    
    private JSmithCodeGenerator generator;
    private JavaCodeGenerator javaGenerator;
    
    @BeforeEach
    void setUp() {
        generator = new JSmithCodeGenerator();
        javaGenerator = new JavaCodeGenerator();
    }
    
    @Test
    void testGenerateComplexJavaClasses() {
        // 测试批量生成复杂Java类
        int count = 3;
        long baseSeed = 12345L;
        
        List<String> codes = generator.generateComplexJavaClasses(count, baseSeed);
        
        assertNotNull(codes, "Generated codes should not be null");
        assertEquals(count, codes.size(), "Should generate exactly " + count + " classes");
        
        for (int i = 0; i < codes.size(); i++) {
            String code = codes.get(i);
            assertNotNull(code, "Generated code " + i + " should not be null");
            assertFalse(code.trim().isEmpty(), "Generated code " + i + " should not be empty");
            assertTrue(code.contains("class") || code.contains("interface"), 
                "Generated code " + i + " should contain a class or interface declaration");
            
            System.out.println("=== Generated Complex Class " + (i + 1) + " ===");
            System.out.println(code);
            System.out.println();
        }
    }
    
    @Test
    void testGenerateComplexJavaFiles() {
        // 测试批量生成复杂Java文件
        String outputDir = "test-output";
        int count = 2;
        long baseSeed = 54321L;
        
        List<String> filePaths = generator.generateComplexJavaFiles(count, outputDir, baseSeed);
        
        assertNotNull(filePaths, "File paths should not be null");
        assertTrue(filePaths.size() <= count, "Should generate at most " + count + " files");
        
        // 验证文件是否真的被创建
        for (String filePath : filePaths) {
            assertTrue(Files.exists(Paths.get(filePath)), "File should exist: " + filePath);
            
            try {
                String content = new String(Files.readAllBytes(Paths.get(filePath)));
                assertFalse(content.trim().isEmpty(), "File content should not be empty");
                assertTrue(content.contains("class") || content.contains("interface"), 
                    "File should contain a class or interface declaration");
                
                System.out.println("Generated file: " + filePath);
                System.out.println("Content preview: " + content.substring(0, Math.min(200, content.length())) + "...");
                System.out.println();
                
            } catch (Exception e) {
                fail("Failed to read generated file: " + e.getMessage());
            }
        }
        
        // 不清理测试文件，让用户可以查看
        System.out.println("Files preserved in: " + outputDir);
        System.out.println("Generated files:");
        for (String filePath : filePaths) {
            System.out.println("- " + filePath);
        }
    }
    
    @Test
    void testJavaCodeGeneratorIntegration() {
        // 测试JavaCodeGenerator的整合功能
        String outputDir = "integration-test-output";
        int count = 2;
        
        List<String> filePaths = javaGenerator.generateComplexJavaFiles(outputDir, count);
        
        assertNotNull(filePaths, "File paths should not be null");
        
        System.out.println("JavaCodeGenerator integration test:");
        System.out.println("Generated " + filePaths.size() + " files:");
        
        for (String filePath : filePaths) {
            System.out.println("- " + filePath);
            
            if (Files.exists(Paths.get(filePath))) {
                try {
                    long fileSize = Files.size(Paths.get(filePath));
                    System.out.println("  Size: " + fileSize + " bytes");
                } catch (Exception e) {
                    System.out.println("  Size: unknown");
                }
            }
        }
        
        // 清理测试文件
        try {
            for (String filePath : filePaths) {
                Files.deleteIfExists(Paths.get(filePath));
            }
            Files.deleteIfExists(Paths.get(outputDir));
        } catch (Exception e) {
            System.err.println("Failed to cleanup integration test files: " + e.getMessage());
        }
    }
    
    @Test
    void testExtractClassName() {
        // 测试类名提取功能
        String code1 = "public class TestClass { }";
        String code2 = "abstract class AbstractTest { }";
        String code3 = "final strictfp class FinalTest { }";
        String code4 = "interface TestInterface { }";
        
        // 由于extractClassName是私有方法，我们通过生成文件来间接测试
        // 这里主要验证生成的文件名是否合理
        String outputDir = "classname-test-output";
        
        List<String> filePaths = generator.generateComplexJavaFiles(1, outputDir, System.currentTimeMillis());
        
        if (!filePaths.isEmpty()) {
            String filePath = filePaths.get(0);
            String fileName = Paths.get(filePath).getFileName().toString();
            
            assertTrue(fileName.endsWith(".java"), "File should have .java extension");
            assertTrue(fileName.length() > 5, "File name should be meaningful");
            
            System.out.println("Generated file name: " + fileName);
        }
        
        // 清理
        try {
            for (String filePath : filePaths) {
                Files.deleteIfExists(Paths.get(filePath));
            }
            Files.deleteIfExists(Paths.get(outputDir));
        } catch (Exception e) {
            System.err.println("Failed to cleanup classname test files: " + e.getMessage());
        }
    }

    @Test
    void testDirectBatchGenerator() {
        // 直接使用jsmith项目中的BatchGenerator
        String outputDir = "test-output-direct";
        int count = 2;
        long baseSeed = 12345L;
        
        try {
            // 创建输出目录
            Path outputPath = Paths.get(outputDir);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }
            
            // 直接使用BatchGenerator的静态方法
            BatchGenerator.generateBatch(count, outputDir, baseSeed);
            
            // 获取生成的文件列表
            List<String> filePaths = new java.util.ArrayList<>();
            try (java.util.stream.Stream<Path> paths = Files.walk(outputPath)) {
                paths.filter(Files::isRegularFile)
                     .filter(path -> path.toString().endsWith(".java"))
                     .forEach(path -> filePaths.add(path.toString()));
            }
            
            assertNotNull(filePaths, "File paths should not be null");
            assertTrue(filePaths.size() <= count, "Should generate at most " + count + " files");
            
            // 验证文件是否真的被创建
            for (String filePath : filePaths) {
                assertTrue(Files.exists(Paths.get(filePath)), "File should exist: " + filePath);
                
                try {
                    String content = new String(Files.readAllBytes(Paths.get(filePath)));
                    assertFalse(content.trim().isEmpty(), "File content should not be empty");
                    assertTrue(content.contains("class") || content.contains("interface"), 
                        "File should contain a class or interface declaration");
                    
                    System.out.println("Direct BatchGenerator file: " + filePath);
                    System.out.println("Content preview: " + content.substring(0, Math.min(200, content.length())) + "...");
                    System.out.println();
                    
                } catch (Exception e) {
                    fail("Failed to read generated file: " + e.getMessage());
                }
            }
            
            System.out.println("Direct BatchGenerator files preserved in: " + outputDir);
            System.out.println("Generated files:");
            for (String filePath : filePaths) {
                System.out.println("- " + filePath);
            }
            
        } catch (Exception e) {
            fail("Direct BatchGenerator test failed: " + e.getMessage());
        }
    }
} 