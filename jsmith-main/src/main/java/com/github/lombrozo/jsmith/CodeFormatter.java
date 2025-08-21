package com.github.lombrozo.jsmith;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Java代码格式化工具
 */
public class CodeFormatter {

    private static final String INDENT = "    "; // 4个空格缩进
    
    /**
     * 格式化Java代码
     */
    public static String format(String code) {
        if (code == null || code.trim().isEmpty()) {
            return code;
        }

        // 预处理：移除多余的空行和空格
        code = preprocessCode(code);

        // 格式化代码结构
        code = formatCodeStructure(code);

        // 添加适当的缩进
        code = addIndentation(code);

        // 后处理：清理格式
        code = postprocessCode(code);

        return code;
    }
    
    /**
     * 预处理代码
     */
    private static String preprocessCode(String code) {
        // 移除多余的空行
        code = code.replaceAll("\\n\\s*\\n\\s*\\n", "\n\n");
        
        // 标准化分号后的换行
        code = code.replaceAll(";\\s*\\n", ";\n");
        
        // 标准化大括号
        code = code.replaceAll("\\{\\s*\\n", "{\n");
        code = code.replaceAll("\\}\\s*\\n", "}\n");
        
        return code.trim();
    }
    
    /**
     * 格式化代码结构
     */
    private static String formatCodeStructure(String code) {
        StringBuilder formatted = new StringBuilder();
        String[] lines = code.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.isEmpty()) {
                continue;
            }

            // 处理包声明
            if (line.startsWith("package ")) {
                formatted.append(line).append("\n\n");
                continue;
            }

            // 处理导入语句
            if (line.startsWith("import ")) {
                formatted.append(line).append("\n");
                // 如果下一行不是import，添加空行
                if (i + 1 < lines.length && !lines[i + 1].trim().startsWith("import")) {
                    formatted.append("\n");
                }
                continue;
            }

            // 处理类声明
            if (line.contains("class ") && line.contains("{")) {
                formatted.append(line).append("\n");
                continue;
            }

            // 处理方法声明
            if (isMethodDeclaration(line)) {
                formatted.append("\n").append(line).append("\n");
                continue;
            }

            // 处理构造函数
            if (isConstructorDeclaration(line)) {
                formatted.append("\n").append(line).append("\n");
                continue;
            }

            // 处理控制流语句
            if (isControlFlowStatement(line)) {
                formatted.append(line).append("\n");
                continue;
            }

            // 处理单独的分号和大括号
            if (line.equals(";") || line.equals(";;")) {
                continue; // 跳过多余的分号
            }

            // 处理普通语句
            formatted.append(line).append("\n");
        }

        return formatted.toString();
    }
    
    /**
     * 添加缩进
     */
    private static String addIndentation(String code) {
        StringBuilder indented = new StringBuilder();
        String[] lines = code.split("\n");
        int indentLevel = 0;

        for (String line : lines) {
            line = line.trim();

            if (line.isEmpty()) {
                indented.append("\n");
                continue;
            }

            // 减少缩进（在处理行之前）
            if (line.equals("}") || line.equals("};") || line.startsWith("} else")) {
                indentLevel = Math.max(0, indentLevel - 1);
            }

            // 处理else语句的特殊缩进
            if (line.startsWith("else ")) {
                // else与对应的if保持同一缩进级别
                for (int i = 0; i < indentLevel; i++) {
                    indented.append(INDENT);
                }
                indented.append(line).append("\n");
                if (line.endsWith("{")) {
                    indentLevel++;
                }
                continue;
            }

            // 添加缩进
            for (int i = 0; i < indentLevel; i++) {
                indented.append(INDENT);
            }
            indented.append(line).append("\n");

            // 增加缩进（在处理行之后）
            if (line.endsWith("{")) {
                indentLevel++;
            }
        }

        return indented.toString();
    }
    
    /**
     * 后处理代码
     */
    private static String postprocessCode(String code) {
        // 移除多余的空行
        code = code.replaceAll("\\n{3,}", "\n\n");

        // 修复大括号后的分号
        code = code.replaceAll("\\}\\s*;\\s*;", "}");
        code = code.replaceAll("\\}\\s*;", "}");

        // 修复方法声明中的空格
        code = code.replaceAll("\\(\\s+", "(");
        code = code.replaceAll("\\s+\\)", ")");
        code = code.replaceAll("\\s+,", ",");
        code = code.replaceAll(",\\s+", ", ");

        // 修复运算符周围的空格
        code = code.replaceAll("([^\\s])=([^\\s=])", "$1 = $2");
        code = code.replaceAll("([^\\s])\\+([^\\s\\+])", "$1 + $2");
        code = code.replaceAll("([^\\s])-([^\\s-])", "$1 - $2");
        code = code.replaceAll("([^\\s])\\*([^\\s\\*])", "$1 * $2");
        code = code.replaceAll("([^\\s])/([^\\s/])", "$1 / $2");

        // 修复递增递减操作符
        code = code.replaceAll("\\+\\s+\\+", "++");
        code = code.replaceAll("-\\s+-", "--");
        code = code.replaceAll("\\s+\\+\\+", "++");
        code = code.replaceAll("\\s+--", "--");

        // 清理特殊字符以兼容SDG工具
        code = cleanSpecialCharacters(code);

        // 添加类内方法调用到main方法
        code = addMethodCallsToMain(code);

        // 确保文件以换行符结尾
        if (!code.endsWith("\n")) {
            code += "\n";
        }

        return code;
    }
    
    /**
     * 判断是否为方法声明
     */
    private static boolean isMethodDeclaration(String line) {
        return (line.contains("public ") || line.contains("private ") || line.contains("protected ")) 
               && line.contains("(") && line.contains(")") && line.contains("{");
    }
    
    /**
     * 判断是否为构造函数声明
     */
    private static boolean isConstructorDeclaration(String line) {
        return line.matches(".*\\s+\\w+\\s*\\(.*\\)\\s*\\{.*");
    }
    
    /**
     * 判断是否为控制流语句
     */
    private static boolean isControlFlowStatement(String line) {
        return line.startsWith("if ") || line.startsWith("for ") ||
               line.startsWith("while ") || line.startsWith("else");
    }

    /**
     * 添加类内方法调用到main方法
     */
    private static String addMethodCallsToMain(String code) {
        // 检查类是否为abstract
        if (isAbstractClass(code)) {
            return code; // abstract类不能实例化，跳过
        }

        // 检查是否有自定义构造方法
        if (hasCustomConstructor(code)) {
            return code; // 有自定义构造方法的类跳过，避免参数问题
        }

        // 提取类名
        String className = extractClassName(code);
        if (className == null) {
            return code;
        }

        // 提取所有实例方法名
        java.util.List<String> instanceMethods = extractInstanceMethods(code);
        if (instanceMethods.isEmpty()) {
            return code;
        }

        // 在main方法中添加方法调用
        return insertMethodCallsIntoMain(code, className, instanceMethods);
    }

    /**
     * 清理特殊字符以兼容SDG工具
     */
    private static String cleanSpecialCharacters(String code) {
        // 创建标识符映射表
        java.util.Map<String, String> identifierMap = new java.util.HashMap<>();
        int counter = 1;

        // 找到所有包含$的标识符
        Pattern dollarPattern = Pattern.compile("\\b([a-zA-Z][a-zA-Z0-9$]*\\$[a-zA-Z0-9$]*)\\b");
        Matcher matcher = dollarPattern.matcher(code);

        while (matcher.find()) {
            String originalId = matcher.group(1);
            if (!identifierMap.containsKey(originalId)) {
                // 生成新的标识符：移除$并添加数字后缀
                String cleanId = originalId.replaceAll("\\$", "") + counter;
                identifierMap.put(originalId, cleanId);
                counter++;
            }
        }

        // 替换所有映射的标识符
        String result = code;
        for (java.util.Map.Entry<String, String> entry : identifierMap.entrySet()) {
            result = result.replaceAll("\\b" + Pattern.quote(entry.getKey()) + "\\b", entry.getValue());
        }

        return result;
    }

    /**
     * 检查是否有自定义构造方法
     */
    private static boolean hasCustomConstructor(String code) {
        // 提取类名
        String className = extractClassName(code);
        if (className == null) {
            return false;
        }

        // 查找构造方法：public/private ClassName(...)
        Pattern constructorPattern = Pattern.compile("(?:public|private|protected)\\s+" + Pattern.quote(className) + "\\s*\\(");
        return constructorPattern.matcher(code).find();
    }

    /**
     * 检查类是否为abstract
     */
    private static boolean isAbstractClass(String code) {
        Pattern abstractPattern = Pattern.compile("abstract\\s+.*?class\\s+[\\w$]+");
        return abstractPattern.matcher(code).find();
    }

    /**
     * 提取类名
     */
    private static String extractClassName(String code) {
        Pattern classPattern = Pattern.compile("(?:public\\s+|private\\s+|protected\\s+|abstract\\s+|final\\s+|strictfp\\s+)*class\\s+([\\w$]+)");
        Matcher matcher = classPattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 提取实例方法名
     */
    private static java.util.List<String> extractInstanceMethods(String code) {
        java.util.List<String> methods = new java.util.ArrayList<>();

        // 匹配public void methodName() 格式的方法，支持$符号
        Pattern methodPattern = Pattern.compile("public\\s+void\\s+([\\w$]+)\\s*\\(\\s*\\)\\s*\\{");
        Matcher matcher = methodPattern.matcher(code);

        while (matcher.find()) {
            String methodName = matcher.group(1);
            // 排除main方法
            if (!"main".equals(methodName)) {
                methods.add(methodName);
            }
        }

        return methods;
    }

    /**
     * 在main方法中插入方法调用
     */
    private static String insertMethodCallsIntoMain(String code, String className, java.util.List<String> methods) {
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
