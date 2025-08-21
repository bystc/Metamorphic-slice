package com.example.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JSmithCodeGenerator测试类
 */
public class JSmithCodeGeneratorTest {
    
    private JSmithCodeGenerator generator;
    
    @BeforeEach
    void setUp() {
        generator = new JSmithCodeGenerator();
    }
    
    @Test
    void testGenerateRandomJavaClass() {
        // 测试基本的随机Java类生成
        String code = generator.generateRandomJavaClass();
        
        assertNotNull(code, "Generated code should not be null");
        assertFalse(code.trim().isEmpty(), "Generated code should not be empty");
        assertTrue(code.contains("class") || code.contains("interface"), "Generated code should contain a class or interface declaration");
        
        System.out.println("Generated Java class:");
        System.out.println(code);
    }
    
    @Test
    void testGenerateRandomJavaClassWithSeed() {
        // 测试使用种子生成的可重现性
        long seed = 12345L;
        String code1 = generator.generateRandomJavaClass(seed);
        String code2 = generator.generateRandomJavaClass(seed);
        
        assertNotNull(code1, "First generated code should not be null");
        assertNotNull(code2, "Second generated code should not be null");
        assertEquals(code1, code2, "Same seed should generate identical code");
        
        System.out.println("Generated Java class with seed " + seed + ":");
        System.out.println(code1);
    }
    
    @Test
    void testGenerateSliceableJavaClass() {
        // 测试生成适合切片的Java类
        String code = generator.generateSliceableJavaClass();
        
        assertNotNull(code, "Generated sliceable code should not be null");
        assertFalse(code.trim().isEmpty(), "Generated sliceable code should not be empty");
        assertTrue(code.contains("class") || code.contains("interface"), "Generated code should contain a class or interface declaration");
        
        System.out.println("Generated sliceable Java class:");
        System.out.println(code);
    }
    
    @Test
    void testGenerateMultipleClasses() {
        // 测试批量生成
        int count = 3;
        java.util.List<String> codes = generator.generateRandomJavaClasses(count);
        
        assertNotNull(codes, "Generated codes list should not be null");
        assertEquals(count, codes.size(), "Should generate exactly " + count + " classes");
        
        for (int i = 0; i < codes.size(); i++) {
            String code = codes.get(i);
            assertNotNull(code, "Generated code " + i + " should not be null");
            assertFalse(code.trim().isEmpty(), "Generated code " + i + " should not be empty");
            assertTrue(code.contains("class") || code.contains("interface"), "Generated code " + i + " should contain a class or interface declaration");
        }
        
        System.out.println("Generated " + count + " Java classes:");
        for (int i = 0; i < codes.size(); i++) {
            System.out.println("=== Class " + (i + 1) + " ===");
            System.out.println(codes.get(i));
        }
    }
    
    @Test
    void testGeneratedCodeCompilability() {
        // 测试生成的代码是否包含基本的Java语法结构
        String code = generator.generateRandomJavaClass();
        
        // 基本语法检查
        assertTrue(code.contains("class") || code.contains("interface"), "Should contain class or interface declaration");
        
        // 检查是否有平衡的大括号
        long openBraces = code.chars().filter(ch -> ch == '{').count();
        long closeBraces = code.chars().filter(ch -> ch == '}').count();
        assertEquals(openBraces, closeBraces, "Braces should be balanced");
        
        // 检查是否有平衡的小括号
        long openParens = code.chars().filter(ch -> ch == '(').count();
        long closeParens = code.chars().filter(ch -> ch == ')').count();
        assertEquals(openParens, closeParens, "Parentheses should be balanced");
    }
} 