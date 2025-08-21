package com.example.jsmithdemo;

import com.github.lombrozo.jsmith.RandomJavaClass;
import java.io.FileWriter;
import java.io.IOException;

public class SimpleJSmithDemo {
    public static void main(String[] args) throws IOException {
        RandomJavaClass randomClass = new RandomJavaClass();
        String code = randomClass.src();
        System.out.println(code);
        try (FileWriter writer = new FileWriter("RandomJSmith.java")) {
            writer.write(code);
        }
        System.out.println("Java file generated: RandomJSmith.java");
    }
} 