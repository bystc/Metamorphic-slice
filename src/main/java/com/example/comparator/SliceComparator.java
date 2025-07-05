package com.example.comparator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.*;

@Slf4j
@Component
public class SliceComparator {
    
    public boolean compareSlices(String slice1, String slice2) {
        log.info("Starting slice comparison");
        log.info("Slice 1: {}", slice1);
        log.info("Slice 2: {}", slice2);
        
        if (slice1 == null || slice2 == null) {
            log.error("One or both slices are null");
            return false;
        }
        
        if (slice1.isEmpty() || slice2.isEmpty()) {
            log.error("One or both slices are empty");
            return false;
        }
        
        // 移除空白字符和注释
        String normalizedSlice1 = normalizeSlice(slice1);
        String normalizedSlice2 = normalizeSlice(slice2);
        log.info("Normalized Slice 1: {}", normalizedSlice1);
        log.info("Normalized Slice 2: {}", normalizedSlice2);
        
        // 提取变量名
        Set<String> variables1 = extractVariables(normalizedSlice1);
        Set<String> variables2 = extractVariables(normalizedSlice2);
        log.info("Variables in Slice 1: {}", variables1);
        log.info("Variables in Slice 2: {}", variables2);
        
        // 检查变量数量是否相同
        if (variables1.size() != variables2.size()) {
            log.info("Different number of variables: {} vs {}", variables1.size(), variables2.size());
            return false;
        }
        
        // 创建变量映射
        Map<String, String> variableMap = createVariableMap(variables1, variables2);
        log.info("Variable mapping: {}", variableMap);
        
        // 替换变量名后比较
        String replacedSlice1 = replaceVariables(normalizedSlice1, variableMap);
        String replacedSlice2 = normalizedSlice2;
        log.info("Replaced Slice 1: {}", replacedSlice1);
        log.info("Replaced Slice 2: {}", replacedSlice2);
        
        // 移除所有变量名后比较
        String strippedSlice1 = stripVariableNames(replacedSlice1);
        String strippedSlice2 = stripVariableNames(replacedSlice2);
        log.info("Stripped Slice 1: {}", strippedSlice1);
        log.info("Stripped Slice 2: {}", strippedSlice2);
        
        boolean areEqual = strippedSlice1.equals(strippedSlice2);
        log.info("Slices are {}equivalent", areEqual ? "" : "not ");
        
        if (!areEqual) {
            log.info("Differences found:");
            log.info("Slice 1 length: {}", strippedSlice1.length());
            log.info("Slice 2 length: {}", strippedSlice2.length());
            if (strippedSlice1.length() == strippedSlice2.length()) {
                for (int i = 0; i < strippedSlice1.length(); i++) {
                    if (strippedSlice1.charAt(i) != strippedSlice2.charAt(i)) {
                        log.info("Difference at position {}: '{}' vs '{}'", 
                            i, strippedSlice1.charAt(i), strippedSlice2.charAt(i));
                    }
                }
            }
        }
        
        return areEqual;
    }
    
    private String normalizeSlice(String slice) {
        // 移除注释
        String withoutComments = slice.replaceAll("//.*$", "")
                                    .replaceAll("/\\*[\\s\\S]*?\\*/", "");
        
        // 移除空白字符
        return withoutComments.replaceAll("\\s+", "");
    }
    
    private Set<String> extractVariables(String slice) {
        Set<String> variables = new HashSet<>();
        Pattern pattern = Pattern.compile("\\b([a-zA-Z_][a-zA-Z0-9_]*)\\b");
        Matcher matcher = pattern.matcher(slice);
        
        while (matcher.find()) {
            String var = matcher.group(1);
            // 排除Java关键字
            if (!isJavaKeyword(var)) {
                variables.add(var);
            }
        }
        
        return variables;
    }
    
    private boolean isJavaKeyword(String word) {
        Set<String> keywords = new HashSet<>(Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
            "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
            "finally", "float", "for", "if", "implements", "import", "instanceof", "int", "interface",
            "long", "native", "new", "package", "private", "protected", "public", "return", "short",
            "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
        ));
        return keywords.contains(word);
    }
    
    private Map<String, String> createVariableMap(Set<String> vars1, Set<String> vars2) {
        Map<String, String> map = new HashMap<>();
        List<String> list1 = new ArrayList<>(vars1);
        List<String> list2 = new ArrayList<>(vars2);
        
        // 按长度排序，优先匹配较长的变量名
        list1.sort((a, b) -> b.length() - a.length());
        list2.sort((a, b) -> b.length() - a.length());
        
        for (int i = 0; i < list1.size(); i++) {
            map.put(list1.get(i), list2.get(i));
            log.info("Mapping variable: {} -> {}", list1.get(i), list2.get(i));
        }
        
        return map;
    }
    
    private String replaceVariables(String slice, Map<String, String> variableMap) {
        String result = slice;
        for (Map.Entry<String, String> entry : variableMap.entrySet()) {
            String oldValue = result;
            result = result.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
            if (!oldValue.equals(result)) {
                log.info("Replaced '{}' with '{}'", entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
    
    private String stripVariableNames(String slice) {
        // 移除所有变量名，保留其他结构
        String result = slice.replaceAll("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b", "VAR");
        log.info("Stripped slice: {}", result);
        return result;
    }
} 