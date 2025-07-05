package com.example.slicer;

import com.example.generator.JavaCodeGenerator;
import com.example.generator.JavaCodeGenerator.VariableInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.logging.*;
import java.util.Map;

@Slf4j
@Component
public class SliceExecutor {
    private static final Logger logger = Logger.getLogger(SliceExecutor.class.getName());
    private final String slicingToolPath;
    private final String sliceOutputDir;

    @Autowired
    private JavaCodeGenerator codeGenerator;

    public SliceExecutor() {
        this.slicingToolPath = "src/main/java/sdg-cli-1.3.0-jar-with-dependencies.jar";
        this.sliceOutputDir = "slice";
        setupLogger();
        createSliceDirectory();
    }

    private void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler("slice_executor.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            log.error("Failed to setup logger", e);
        }
    }

    private void createSliceDirectory() {
        try {
            Files.createDirectories(Paths.get(sliceOutputDir));
            log.info("Created slice directory: {}", sliceOutputDir);
        } catch (IOException e) {
            log.error("Failed to create slice directory", e);
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

        // 查找合适的变量和行号
        VariableInfo variableInfo = codeGenerator.findVariableForSlicing(sourceFile);
        if (variableInfo == null) {
            String error = "No suitable variable found for slicing in file: " + sourceFile;
            logger.severe(error);
            throw new RuntimeException(error);
        }

        String variableName = variableInfo.getVariableName();
        int lineNumber = variableInfo.getLineNumber();

        logger.info("Found variable for slicing: " + variableName + " at line " + lineNumber);

        // 构建切片命令
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java", "-jar", slicingToolPath,
                "-c", sourceFile + "#" + lineNumber + ":" + variableName
        );

        // 设置工作目录
        processBuilder.directory(new File("."));

        // 设置环境变量，确保正确处理中文
        Map<String, String> env = processBuilder.environment();
        env.put("LANG", "zh_CN.UTF-8");
        env.put("LC_ALL", "zh_CN.UTF-8");

        // 合并标准输出和错误输出
        processBuilder.redirectErrorStream(true);

        // 打印完整的命令
        String command = String.join(" ", processBuilder.command());
        System.out.println("\n=== Slice Command ===");
        System.out.println("Working Directory: " + processBuilder.directory().getAbsolutePath());
        System.out.println("Command: " + command);
        System.out.println("===================\n");

        logger.info("Executing command: " + command);

        // 执行命令
        Process process = processBuilder.start();

        // 读取输出
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                logger.info("Tool output: " + line);
                System.out.println("Tool output: " + line);  // 同时打印到控制台
            }
        }

        // 等待进程完成
        int exitCode = process.waitFor();
        
        // 记录完整的输出用于调试
        String fullOutput = output.toString();
        log.info("Slice tool exit code: {}", exitCode);
        log.info("Slice tool full output: {}", fullOutput);
        
        if (exitCode != 0) {
            String error = "Slice execution failed with exit code: " + exitCode + "\nOutput: " + fullOutput;
            logger.severe(error);
            System.err.println(error);  // 同时打印到控制台
            throw new RuntimeException(error);
        }

        // 检查可能的输出文件路径
        String[] possibleOutputFiles = {
                sliceOutputDir + "/com/example/" + new File(sourceFile).getName(),  // slice/com/example目录下
                sliceOutputDir + "/" + new File(sourceFile).getName(),              // slice目录下
                new File(sourceFile).getName()                                      // 当前目录
        };

        String outputFile = null;
        for (String path : possibleOutputFiles) {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                outputFile = path;
                log.info("Found slice output file at: {}", path);
                // 打印文件的基本信息
                log.info("File size: {} bytes", Files.size(filePath));
                log.info("File last modified: {}", Files.getLastModifiedTime(filePath));
                break;
            } else {
                log.info("Slice output file not found at: {}", path);
            }
        }

        if (outputFile == null) {
            // 如果找不到输出文件，记录详细信息并返回空字符串
            log.warn("No slice output file found for: {}", sourceFile);
            log.warn("Slice tool exit code: {}", exitCode);
            log.warn("Slice tool output: {}", fullOutput);
            log.warn("Checked paths: {}", String.join(", ", possibleOutputFiles));
            return "";
        }

        // 读取切片结果
        String sliceContent = new String(Files.readAllBytes(Paths.get(outputFile)), StandardCharsets.UTF_8);
        log.info("Successfully read slice content from: {}", outputFile);
        log.info("Slice content length: {}", sliceContent.length());
        log.info("Slice content: {}", sliceContent);  // 打印完整的切片内容

        return sliceContent;
    }

    public String executeSliceWithVariable(String sourceFile, String variableName, int lineNumber) throws IOException, InterruptedException {
        logger.info("Starting slice execution for file: " + sourceFile + " with variable: " + variableName + " at line: " + lineNumber);

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

        logger.info("Using specified variable for slicing: " + variableName + " at line " + lineNumber);

        // 构建切片命令
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java", "-jar", slicingToolPath,
                "-c", sourceFile + "#" + lineNumber + ":" + variableName
        );

        // 设置工作目录
        processBuilder.directory(new File("."));

        // 设置环境变量，确保正确处理中文
        Map<String, String> env = processBuilder.environment();
        env.put("LANG", "zh_CN.UTF-8");
        env.put("LC_ALL", "zh_CN.UTF-8");

        // 合并标准输出和错误输出
        processBuilder.redirectErrorStream(true);

        // 打印完整的命令
        String command = String.join(" ", processBuilder.command());
        System.out.println("\n=== Slice Command ===");
        System.out.println("Working Directory: " + processBuilder.directory().getAbsolutePath());
        System.out.println("Command: " + command);
        System.out.println("===================\n");

        logger.info("Executing command: " + command);

        // 执行命令
        Process process = processBuilder.start();

        // 读取输出
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                logger.info("Tool output: " + line);
                System.out.println("Tool output: " + line);  // 同时打印到控制台
            }
        }

        // 等待进程完成
        int exitCode = process.waitFor();
        
        // 记录完整的输出用于调试
        String fullOutput = output.toString();
        log.info("Slice tool exit code: {}", exitCode);
        log.info("Slice tool full output: {}", fullOutput);
        
        if (exitCode != 0) {
            String error = "Slice execution failed with exit code: " + exitCode + "\nOutput: " + fullOutput;
            logger.severe(error);
            System.err.println(error);  // 同时打印到控制台
            throw new RuntimeException(error);
        }

        // 检查可能的输出文件路径
        String[] possibleOutputFiles = {
                sliceOutputDir + "/com/example/" + new File(sourceFile).getName(),  // slice/com/example目录下
                sliceOutputDir + "/" + new File(sourceFile).getName(),              // slice目录下
                new File(sourceFile).getName()                                      // 当前目录
        };

        String outputFile = null;
        for (String path : possibleOutputFiles) {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                outputFile = path;
                log.info("Found slice output file at: {}", path);
                // 打印文件的基本信息
                log.info("File size: {} bytes", Files.size(filePath));
                log.info("File last modified: {}", Files.getLastModifiedTime(filePath));
                break;
            } else {
                log.info("Slice output file not found at: {}", path);
            }
        }

        if (outputFile == null) {
            // 如果找不到输出文件，记录详细信息并返回空字符串
            log.warn("No slice output file found for: {}", sourceFile);
            log.warn("Slice tool exit code: {}", exitCode);
            log.warn("Slice tool output: {}", fullOutput);
            log.warn("Checked paths: {}", String.join(", ", possibleOutputFiles));
            return "";
        }

        // 读取切片结果
        String sliceContent = new String(Files.readAllBytes(Paths.get(outputFile)), StandardCharsets.UTF_8);
        log.info("Successfully read slice content from: {}", outputFile);
        log.info("Slice content length: {}", sliceContent.length());
        log.info("Slice content: {}", sliceContent);  // 打印完整的切片内容

        return sliceContent;
    }
}