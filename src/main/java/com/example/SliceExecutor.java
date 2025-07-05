package com.example;

import java.io.*;
import java.nio.file.*;
import java.util.logging.*;

public class SliceExecutor {
    private static final Logger logger = Logger.getLogger(SliceExecutor.class.getName());
    private final String slicingToolPath;
    
    public SliceExecutor(String slicingToolPath) {
        this.slicingToolPath = slicingToolPath;
        setupLogger();
    }
    
    private void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler("slice_executor.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Failed to setup logger: " + e.getMessage());
        }
    }
    
    public String executeSlice(String sourceFile) throws IOException, InterruptedException {
        logger.info("Starting slice execution for file: " + sourceFile);
        
        // 检查源文件是否存在
        if (!Files.exists(Paths.get(sourceFile))) {
            String error = "Source file does not exist: " + sourceFile;
            logger.severe(error);
            throw new FileNotFoundException(error);
        }
        
        // 检查切片工具是否存在
        if (!Files.exists(Paths.get(slicingToolPath))) {
            String error = "Slicing tool not found: " + slicingToolPath;
            logger.severe(error);
            throw new FileNotFoundException(error);
        }
        
        String outputFile = sourceFile.replace(".java", "_slice.java");
        
        // 构建切片命令
        ProcessBuilder processBuilder = new ProcessBuilder(
            "java", "-jar", slicingToolPath,
            "--source", sourceFile,
            "--output", outputFile
        );
        
        // 设置工作目录
        processBuilder.directory(new File("."));
        
        // 合并标准输出和错误输出
        processBuilder.redirectErrorStream(true);
        
        logger.info("Executing command: " + String.join(" ", processBuilder.command()));
        
        // 执行命令
        Process process = processBuilder.start();
        
        // 读取输出
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                logger.info("Tool output: " + line);
            }
        }
        
        // 等待进程完成
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            String error = "Slice execution failed with exit code: " + exitCode + "\nOutput: " + output;
            logger.severe(error);
            throw new RuntimeException(error);
        }
        
        // 检查输出文件是否存在
        if (!Files.exists(Paths.get(outputFile))) {
            String error = "Slice output file was not created: " + outputFile;
            logger.severe(error);
            throw new FileNotFoundException(error);
        }
        
        // 读取切片结果
        String sliceContent = new String(Files.readAllBytes(Paths.get(outputFile)));
        logger.info("Successfully read slice content from: " + outputFile);
        
        return sliceContent;
    }
} 