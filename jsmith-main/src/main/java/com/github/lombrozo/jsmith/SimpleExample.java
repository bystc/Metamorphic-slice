package com.github.lombrozo.jsmith;

/**
 * Simple example demonstrating the enhanced RandomJavaClass functionality.
 * This shows how to use the exact API you requested.
 * 
 * @since 0.2
 */
public class SimpleExample {
    
    /**
     * Main method demonstrating the exact API you requested.
     * @param args command line arguments
     */
    public static void main(String... args) {
        // Create a RandomJavaClass instance
        RandomJavaClass clazz = new RandomJavaClass();
        
        // Generate Java class with main method using the existing API
        String code = clazz.src();
        
        // Print the generated code
        System.out.println(code);
    }
} 