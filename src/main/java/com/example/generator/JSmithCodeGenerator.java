package com.example.generator;

import com.github.lombrozo.jsmith.RandomJavaClass;
import com.github.lombrozo.jsmith.CodeFormatter;
import com.github.lombrozo.jsmith.BatchGenerator;
import com.example.util.JavaSyntaxFixer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSmith代码生成器适配器
 * 封装JSmith的RandomJavaClass，提供与现有JavaCodeGenerator兼容的接口
 */
@Slf4j
@Component
public class JSmithCodeGenerator {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private final Random random;

    public JSmithCodeGenerator() {
        this.random = new Random();
    }

    /**
     * 使用JSmith生成随机Java类
     * @return 生成的Java代码字符串
     */
    public String generateRandomJavaClass() {
        // 尝试生成类，跳过接口
        int maxAttempts = 5;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            long seed = System.currentTimeMillis() + attempt * 500;
            String code = generateRandomJavaClass(seed);

            if (isJavaClass(code)) {
                return code;
            }
        }

        // 如果无法生成类，使用回退策略
        log.warn("Could not generate class with JSmith, using fallback");
        return generateFallbackJavaClass();
    }

    /**
     * 使用指定种子生成随机Java类
     * @param seed 随机种子
     * @return 生成的Java代码字符串
     */
    public String generateRandomJavaClass(long seed) {
        try {
            log.info("Generating random Java class using JSmith with seed: {}", seed);

            // 创建RandomJavaClass实例，直接传入种子
            RandomJavaClass randomJavaClass = new RandomJavaClass(seed);

            // 生成原始Java代码
            String rawCode = randomJavaClass.src();

            // 使用CodeFormatter格式化代码（就像BatchGenerator一样）
            String formattedCode = CodeFormatter.format(rawCode);

            // FIXED: 修复作用域错误
            String fixedCode = JavaSyntaxFixer.fixScopeErrors(formattedCode);

            log.info("Successfully generated and formatted Java class with {} characters", fixedCode.length());
            log.debug("Generated code:\n{}", fixedCode);

            return fixedCode;

        } catch (Exception e) {
            log.error("Failed to generate Java class using JSmith: {}", e.getMessage(), e);
            throw new RuntimeException("JSmith code generation failed", e);
        }
    }

    /**
     * 生成具有增强随机性的Java类
     * 通过多次尝试和参数调整来增加代码的多样性
     * @param seed 随机种子
     * @return 生成的Java代码字符串
     */
    public String generateRandomJavaClassWithEnhancedRandomness(long seed) {
        // 尝试不同的收敛因子来增加多样性
        double[] convergenceFactors = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};

        // 增加更多的尝试次数和种子变体
        for (int attempt = 0; attempt < 5; attempt++) {
            // 为每次尝试使用更大差异的种子变体
            long variantSeed = enhanceSeedRandomness(seed, attempt);

            // 随机选择收敛因子
            Random seedRandom = new Random(variantSeed);
            double factor = convergenceFactors[seedRandom.nextInt(convergenceFactors.length)];

            try {
                // 使用增强的生成方法
                String code = generateWithEnhancedRandomness(variantSeed, factor);

                // FIXED: 修复作用域错误
                String fixedCode = JavaSyntaxFixer.fixScopeErrors(code);

                // 检查生成的代码是否足够复杂和多样
                if (isCodeSufficientlyComplex(fixedCode)) {
                    log.info("Generated enhanced random Java class with seed: {}, factor: {}, attempt: {}",
                            variantSeed, factor, attempt + 1);
                    return fixedCode;
                }

                log.debug("Generated code not complex enough on attempt {}, trying again", attempt + 1);
            } catch (Exception e) {
                log.warn("Attempt {} failed for enhanced generation: {}", attempt + 1, e.getMessage());
            }
        }

        // 如果增强生成失败，回退到标准生成
        log.info("Enhanced generation failed after 5 attempts, falling back to standard generation");
        return generateRandomJavaClass(seed);
    }

    /**
     * 增强种子的随机性
     * @param baseSeed 基础种子
     * @param attempt 尝试次数
     * @return 增强后的种子
     */
    private long enhanceSeedRandomness(long baseSeed, int attempt) {
        // 使用多种方法增强种子随机性
        long nanoTime = System.nanoTime();
        long memoryHash = Runtime.getRuntime().freeMemory();
        long threadHash = Thread.currentThread().getId();

        // 复杂的种子变换
        long enhanced = baseSeed;
        enhanced ^= (nanoTime >>> (attempt * 8));
        enhanced *= (31L + attempt * 17L);
        enhanced ^= (memoryHash << (attempt * 4));
        enhanced += (threadHash * (attempt + 1) * 1009L);

        // 进一步的位混合
        enhanced ^= (enhanced >>> 33);
        enhanced *= 0xff51afd7ed558ccdL;
        enhanced ^= (enhanced >>> 33);
        enhanced *= 0xc4ceb9fe1a85ec53L;
        enhanced ^= (enhanced >>> 33);

        return enhanced;
    }

    /**
     * 使用增强随机性生成代码
     * @param seed 种子
     * @param factor 收敛因子
     * @return 生成的代码
     */
    private String generateWithEnhancedRandomness(long seed, double factor) {
        // 创建多个RandomJavaClass实例，选择最复杂的一个
        String bestCode = null;
        int bestComplexity = 0;

        for (int i = 0; i < 3; i++) {
            long variantSeed = seed + i * 7919L; // 使用质数增加差异

            try {
                RandomJavaClass randomJavaClass = new RandomJavaClass(variantSeed);
                String code = randomJavaClass.src();
                String formattedCode = CodeFormatter.format(code);

                // FIXED: 修复作用域错误
                String fixedCode = JavaSyntaxFixer.fixScopeErrors(formattedCode);

                // 计算复杂度分数
                int complexity = calculateComplexityScore(fixedCode);

                if (complexity > bestComplexity) {
                    bestComplexity = complexity;
                    bestCode = fixedCode;
                }

            } catch (Exception e) {
                log.debug("Variant {} failed: {}", i, e.getMessage());
            }
        }

        return bestCode != null ? bestCode : new RandomJavaClass(seed).src();
    }

    /**
     * 计算代码复杂度分数
     * @param code 代码
     * @return 复杂度分数
     */
    private int calculateComplexityScore(String code) {
        int score = 0;
        score += code.split("\n").length / 10; // 行数
        score += countPattern(code, "\\bfor\\b") * 3; // for循环
        score += countPattern(code, "\\bwhile\\b") * 3; // while循环
        score += countPattern(code, "\\bif\\b") * 2; // if语句
        score += countPattern(code, "\\bswitch\\b") * 4; // switch语句
        score += countPattern(code, "\\{[^}]*\\{") * 2; // 嵌套块
        score += countPattern(code, "\\w+\\s*\\(") / 2; // 方法调用
        return score;
    }

    /**
     * 生成具有特定复杂度的随机Java类
     * @param seed 随机种子
     * @param convergenceFactor 收敛因子，控制代码复杂度 (0.0-1.0)
     * @return 生成的Java代码字符串
     */
    public String generateRandomJavaClass(long seed, double convergenceFactor) {
        try {
            log.info("Generating random Java class using JSmith with seed: {} and convergence factor: {}",
                    seed, convergenceFactor);

            // 由于Params构造方法限制，我们使用反射或者简化的方法
            // 这里先使用默认的RandomJavaClass，后续可以通过其他方式设置收敛因子
            RandomJavaClass randomJavaClass = new RandomJavaClass(seed);

            // 生成原始Java代码
            String rawCode = randomJavaClass.src();

            // 使用CodeFormatter格式化代码
            String formattedCode = CodeFormatter.format(rawCode);

            // FIXED: 修复作用域错误
            String fixedCode = JavaSyntaxFixer.fixScopeErrors(formattedCode);

            log.info("Successfully generated Java class with {} characters (convergence factor {} noted but using default)",
                    fixedCode.length(), convergenceFactor);
            log.warn("Convergence factor customization not directly supported by current Params API, using default behavior");

            return fixedCode;

        } catch (Exception e) {
            log.error("Failed to generate Java class using JSmith with convergence factor: {}", e.getMessage(), e);
            throw new RuntimeException("JSmith code generation failed", e);
        }
    }

    /**
     * 批量生成随机Java类
     * @param count 生成数量
     * @return 生成的Java代码列表
     */
    public java.util.List<String> generateRandomJavaClasses(int count) {
        java.util.List<String> results = new java.util.ArrayList<>();

        for (int i = 0; i < count; i++) {
            // 使用无参方法，它会自动跳过接口
            String code = generateRandomJavaClass();
            results.add(code);
        }

        log.info("Generated {} random Java classes using JSmith", count);
        return results;
    }

    /**
     * 批量生成复杂的随机Java类（参考BatchGenerator的方式）
     * @param count 生成数量
     * @param baseSeed 基础种子
     * @return 生成的Java代码列表
     */
    public java.util.List<String> generateComplexJavaClasses(int count, long baseSeed) {
        java.util.List<String> results = new java.util.ArrayList<>();

        log.info("Generating {} complex Java classes using BatchGenerator approach with base seed: {}", count, baseSeed);

        for (int i = 0; i < count; i++) {
            try {
                // 使用不同的种子生成不同的代码（参考BatchGenerator）
                long currentSeed = baseSeed + i;
                RandomJavaClass clazz = new RandomJavaClass(currentSeed);
                String rawCode = clazz.src();

                // 使用CodeFormatter格式化代码
                String formattedCode = CodeFormatter.format(rawCode);

                // FIXED: 修复作用域错误
                String fixedCode = JavaSyntaxFixer.fixScopeErrors(formattedCode);

                // 检查是否为类（跳过接口）
                if (isJavaClass(fixedCode)) {
                    results.add(fixedCode);
                    log.debug("Generated complex class {} with seed {}", i + 1, currentSeed);
                } else {
                    // 如果是接口，尝试用不同种子重新生成
                    log.debug("Generated interface at attempt {}, retrying with different seed", i + 1);
                    for (int retry = 0; retry < 3; retry++) {
                        long retrySeed = currentSeed + (retry + 1) * 1000;
                        RandomJavaClass retryClazz = new RandomJavaClass(retrySeed);
                        String retryRawCode = retryClazz.src();
                        String retryFormattedCode = CodeFormatter.format(retryRawCode);

                        // FIXED: 修复作用域错误
                        String retryFixedCode = JavaSyntaxFixer.fixScopeErrors(retryFormattedCode);

                        if (isJavaClass(retryFixedCode)) {
                            results.add(retryFixedCode);
                            log.debug("Successfully generated class on retry {} with seed {}", retry + 1, retrySeed);
                            break;
                        }
                    }

                    // 如果重试都失败，使用回退策略
                    if (results.size() <= i) {
                        String fallbackCode = generateFallbackJavaClass();
                        results.add(fallbackCode);
                        log.warn("Used fallback class for generation {}", i + 1);
                    }
                }

            } catch (Exception e) {
                log.error("Failed to generate complex class {}: {}", i + 1, e.getMessage());
                // 使用回退策略
                String fallbackCode = generateFallbackJavaClass();
                results.add(fallbackCode);
            }
        }

        log.info("Successfully generated {} complex Java classes", results.size());
        return results;
    }

    /**
     * 生成适合切片测试的Java类
     * 跳过接口，只生成类
     * @return 生成的Java代码字符串
     */
    public String generateSliceableJavaClass() {
        // 尝试多次生成，直到得到一个类（而不是接口）
        int maxAttempts = 10;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            long seed = System.currentTimeMillis() + attempt * 1000; // 增加更大的种子差异
            String code = generateRandomJavaClass(seed);

            // 检查是否生成了类（而不是接口）
            if (isJavaClass(code)) {
                log.info("Successfully generated Java class on attempt {}", attempt + 1);
                return code; // generateRandomJavaClass已经包含了JavaSyntaxFixer修复
            } else {
                log.debug("Attempt {} generated interface, skipping and retrying", attempt + 1);
            }
        }

        // 如果多次尝试都没有生成类，使用回退策略
        log.warn("Could not generate a class after {} attempts, using fallback strategy", maxAttempts);
        return generateFallbackJavaClass();
    }

    /**
     * 检查生成的代码是否为Java类（而不是接口）
     * @param code 生成的Java代码
     * @return true如果是类，false如果是接口或其他
     */
    private boolean isJavaClass(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        // 检查是否包含class关键字且不是interface
        boolean hasClass = code.contains("class ");
        boolean isInterface = code.trim().startsWith("interface") ||
                code.contains("interface ") && !code.contains("class ");

        return hasClass && !isInterface;
    }

    /**
     * 生成回退的Java类（当JSmith无法生成合适的类时使用）
     * @return 简单的Java类代码
     */
    private String generateFallbackJavaClass() {
        String className = "GeneratedClass" + System.currentTimeMillis() % 1000;
        String fallbackCode = String.format(
                "public class %s {\n" +
                        "    private int value = 42;\n" +
                        "    private boolean flag = true;\n" +
                        "    \n" +
                        "    public void method1() {\n" +
                        "        int temp = value * 2;\n" +
                        "        if (flag) {\n" +
                        "            temp += 10;\n" +
                        "        }\n" +
                        "        value = temp;\n" +
                        "    }\n" +
                        "    \n" +
                        "    public int getValue() {\n" +
                        "        return value;\n" +
                        "    }\n" +
                        "    \n" +
                        "    public static void main(String[] args) {\n" +
                        "        // 调用类内方法\n" +
                        "        %s instance = new %s();\n" +
                        "        instance.method1();\n" +
                        "    }\n" +
                        "}\n",
                className, className, className
        );

        // 使用CodeFormatter格式化回退代码
        return CodeFormatter.format(fallbackCode);
    }

    /**
     * 批量生成复杂Java文件并保存到指定目录（完全参考BatchGenerator的逻辑）
     * @param count 生成数量
     * @param outputDir 输出目录
     * @param baseSeed 基础种子
     * @return 生成的文件路径列表
     */
    public java.util.List<String> generateComplexJavaFiles(int count, String outputDir, long baseSeed) {
        java.util.List<String> filePaths = new java.util.ArrayList<>();

        try {
            // 创建输出目录
            Path outputPath = Paths.get(outputDir);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
                log.info("Created output directory: {}", outputPath.toAbsolutePath());
            }

            int successCount = 0;
            int failCount = 0;
            long totalSize = 0;

            log.info("Starting batch generation of {} complex Java files", count);

            for (int i = 0; i < count; i++) {
                try {
                    // 使用不同的种子生成不同的代码（参考BatchGenerator）
                    long currentSeed = baseSeed + i;
                    RandomJavaClass clazz = new RandomJavaClass(currentSeed);
                    String rawCode = clazz.src();

                    // 格式化代码（就像BatchGenerator一样）
                    String code = CodeFormatter.format(rawCode);

                    // 提取类名（使用BatchGenerator的逻辑）
                    String className = extractClassName(code);
                    if (className == null) {
                        className = "GeneratedClass" + (i + 1);
                    }

                    // 生成文件名（完全参考BatchGenerator的命名方式）
                    String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
                    String fileName = className + "_" + timestamp + "_" + String.format("%03d", i + 1) + ".java";
                    Path filePath = outputPath.resolve(fileName);

                    // 写入文件
                    Files.write(filePath, code.getBytes());
                    long fileSize = Files.size(filePath);
                    totalSize += fileSize;

                    filePaths.add(filePath.toString());
                    successCount++;

                    System.out.printf("[%d/%d] Generated: %s (%d bytes)%n",
                            i + 1, count, fileName, fileSize);

                    // 短暂延迟以确保时间戳不同
                    Thread.sleep(10);

                } catch (Exception e) {
                    log.error("[{}/{}] Failed to generate file: {}", i + 1, count, e.getMessage());
                    failCount++;
                }
            }

            // 输出统计信息（完全参考BatchGenerator）
            log.info("Generation Summary:");
            log.info("Total files requested: {}", count);
            log.info("Successfully generated: {}", successCount);
            log.info("Failed: {}", failCount);
            log.info("Total size: {} bytes", totalSize);
            log.info("Average size: {} bytes", successCount > 0 ? totalSize / successCount : 0);
            log.info("Output directory: {}", outputPath.toAbsolutePath());

        } catch (Exception e) {
            log.error("Error during batch file generation: {}", e.getMessage(), e);
        }

        return filePaths;
    }

    /**
     * 生成单个复杂Java类（使用指定种子）
     * @param seed 种子值
     * @return 生成的Java代码
     */
    private String generateComplexJavaClass(long seed) {
        // 尝试生成类，跳过接口
        for (int attempt = 0; attempt < 5; attempt++) {
            try {
                long currentSeed = seed + attempt * 100;
                RandomJavaClass clazz = new RandomJavaClass(currentSeed);
                String rawCode = clazz.src();
                String formattedCode = CodeFormatter.format(rawCode);

                if (isJavaClass(formattedCode)) {
                    return formattedCode;
                }
            } catch (Exception e) {
                log.debug("Attempt {} failed for seed {}: {}", attempt + 1, seed, e.getMessage());
            }
        }

        // 如果所有尝试都失败，使用回退策略
        log.warn("All attempts failed for seed {}, using fallback", seed);
        return generateFallbackJavaClass();
    }

    /**
     * 从生成的Java代码中提取类名（使用BatchGenerator的逻辑）
     * @param code Java代码
     * @return 类名，如果未找到返回null
     */
    private String extractClassName(String code) {
        // 匹配 class 关键字后的类名（支持各种修饰符和特殊字符）
        Pattern pattern = Pattern.compile("(?:public\\s+|private\\s+|protected\\s+|abstract\\s+|final\\s+|strictfp\\s+)*class\\s+([a-zA-Z_$][a-zA-Z0-9_$]*)");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 检查生成的代码是否足够复杂和多样
     * @param code 生成的Java代码
     * @return 如果代码足够复杂则返回true
     */
    private boolean isCodeSufficientlyComplex(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        // 基本复杂度指标
        int lineCount = code.split("\n").length;
        int variableCount = countPattern(code, "\\b(boolean|int|long|double|float|String)\\s+\\w+");
        int loopCount = countPattern(code, "\\b(for|while)\\s*\\(");
        int conditionalCount = countPattern(code, "\\b(if|switch)\\s*\\(");
        int methodCallCount = countPattern(code, "\\w+\\s*\\(");

        // 多样性指标
        boolean hasLoops = loopCount > 0;
        boolean hasConditionals = conditionalCount > 0;
        boolean hasMultipleVariables = variableCount > 3;
        boolean hasMethodCalls = methodCallCount > 2;

        // 复杂度评分
        int complexityScore = 0;
        if (lineCount > 50) complexityScore += 2;
        if (lineCount > 100) complexityScore += 2;
        if (hasLoops) complexityScore += 3;
        if (hasConditionals) complexityScore += 2;
        if (hasMultipleVariables) complexityScore += 2;
        if (hasMethodCalls) complexityScore += 1;

        boolean isComplex = complexityScore >= 5;

        log.debug("Code complexity analysis: lines={}, vars={}, loops={}, conditionals={}, calls={}, score={}, complex={}",
                lineCount, variableCount, loopCount, conditionalCount, methodCallCount, complexityScore, isComplex);

        return isComplex;
    }

    /**
     * 计算代码中匹配特定模式的数量
     * @param code 代码字符串
     * @param pattern 正则表达式模式
     * @return 匹配的数量
     */
    private int countPattern(String code, String pattern) {
        try {
            return (int) java.util.regex.Pattern.compile(pattern)
                    .matcher(code)
                    .results()
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }
}