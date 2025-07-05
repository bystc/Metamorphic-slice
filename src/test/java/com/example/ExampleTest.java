package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExampleTest {
    
    @Test
    public void testMain() {
        // 测试主方法
        assertDoesNotThrow(() -> {
            Example.main(new String[]{});
        });
    }
    
    @Test
    public void testLoop() {
        // 测试循环
        Example example = new Example();
        assertDoesNotThrow(() -> {
            example.loop();
        });
    }
} 