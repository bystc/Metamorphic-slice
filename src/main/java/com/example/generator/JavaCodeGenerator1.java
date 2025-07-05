package com.example.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class JavaCodeGenerator1 {

    private final JavaParser javaParser = new JavaParser();
    private final Random random = new Random();

    /**
     * 生成变异文件
     */
    public List<String> generateMutatedFiles(String sourceFile, int numMutations) {
        List<String> mutatedFiles = new ArrayList<>();
        try {
            // 读取源文件
            String sourceCode = Files.readString(Paths.get(sourceFile));
            CompilationUnit cu = javaParser.parse(sourceCode).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse source file"));

            // 创建变异目录
            Path mutatedDir = Paths.get("mutated");
            if (!Files.exists(mutatedDir)) {
                Files.createDirectories(mutatedDir);
            }

            // 生成变异文件
            for (int i = 0; i < numMutations; i++) {
                // 创建新的变异
                CompilationUnit mutatedCu = cu.clone();
                mutateCode(mutatedCu);

                // 保存变异文件
                String fileName = new File(sourceFile).getName();
                String mutatedFileName = fileName.replace(".java", "_mutated_" + i + ".java");
                Path mutatedFilePath = mutatedDir.resolve(mutatedFileName);

                Files.writeString(mutatedFilePath, mutatedCu.toString());
                mutatedFiles.add(mutatedFilePath.toString());
                log.info("Generated mutation file: {}", mutatedFilePath);
            }

        } catch (IOException e) {
            log.error("Error generating mutated files", e);
        }

        return mutatedFiles;
    }

    /**
     * 生成重命名文件
     */
    public List<String> generateRenamedFiles(String sourceFile, int numMutations) {
        List<String> renamedFiles = new ArrayList<>();
        try {
            // 读取源文件
            String sourceCode = Files.readString(Paths.get(sourceFile));
            CompilationUnit cu = javaParser.parse(sourceCode).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse source file"));

            // 创建重命名目录
            Path renamedDir = Paths.get("renamed");
            if (!Files.exists(renamedDir)) {
                Files.createDirectories(renamedDir);
            }

            // 生成重命名文件
            for (int i = 0; i < numMutations; i++) {
                // 创建新的重命名版本
                CompilationUnit renamedCu = cu.clone();
                renameVariables(renamedCu, i);

                // 保存重命名文件
                String fileName = new File(sourceFile).getName();
                String renamedFileName = fileName.replace(".java", "_renamed_" + i + ".java");
                Path renamedFilePath = renamedDir.resolve(renamedFileName);

                Files.writeString(renamedFilePath, renamedCu.toString());
                renamedFiles.add(renamedFilePath.toString());
                log.info("Generated renamed file: {}", renamedFilePath);
            }

        } catch (IOException e) {
            log.error("Error generating renamed files", e);
        }

        return renamedFiles;
    }

    /**
     * 变异代码
     */
    private void mutateCode(CompilationUnit cu) {
        // 获取所有方法
        List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);

        for (MethodDeclaration method : methods) {
            // 1. 变异循环条件
            method.findAll(WhileStmt.class).forEach(this::mutateWhileCondition);

            // 2. 变异 for 循环
            method.findAll(ForStmt.class).forEach(this::mutateForLoop);

            // 3. 变异 if 条件
            method.findAll(IfStmt.class).forEach(this::mutateIfCondition);

            // 4. 变异数值
            method.findAll(IntegerLiteralExpr.class).forEach(this::mutateInteger);

            // 5. 变异布尔值
            method.findAll(BooleanLiteralExpr.class).forEach(this::mutateBoolean);

            // 6. 变异字符串
            method.findAll(StringLiteralExpr.class).forEach(this::mutateString);

            // 7. 变异算术表达式
            method.findAll(BinaryExpr.class).forEach(this::mutateBinaryExpr);
        }
    }

    /**
     * 变异 while 循环条件
     */
    private void mutateWhileCondition(WhileStmt whileStmt) {
        if (whileStmt.getCondition() instanceof BinaryExpr) {
            BinaryExpr condition = (BinaryExpr) whileStmt.getCondition();
            mutateBinaryExpr(condition);
        }
    }

    /**
     * 变异 for 循环
     */
    private void mutateForLoop(ForStmt forStmt) {
        // 变异初始化表达式
        forStmt.getInitialization().forEach(expr -> {
            if (expr instanceof BinaryExpr) {
                mutateBinaryExpr((BinaryExpr) expr);
            }
        });

        // 变异条件表达式
        if (forStmt.getCompare().isPresent() && forStmt.getCompare().get() instanceof BinaryExpr) {
            mutateBinaryExpr((BinaryExpr) forStmt.getCompare().get());
        }

        // 变异更新表达式
        forStmt.getUpdate().forEach(expr -> {
            if (expr instanceof BinaryExpr) {
                mutateBinaryExpr((BinaryExpr) expr);
            }
        });
    }

    /**
     * 变异 if 条件
     */
    private void mutateIfCondition(IfStmt ifStmt) {
        if (ifStmt.getCondition() instanceof BinaryExpr) {
            mutateBinaryExpr((BinaryExpr) ifStmt.getCondition());
        }
    }

    /**
     * 变异整数值
     */
    private void mutateInteger(IntegerLiteralExpr intExpr) {
        int value = intExpr.asInt();
        // 随机变异数值
        if (random.nextBoolean()) {
            intExpr.setValue(String.valueOf(value + 1));
        } else {
            intExpr.setValue(String.valueOf(value - 1));
        }
    }

    /**
     * 变异布尔值
     */
    private void mutateBoolean(BooleanLiteralExpr boolExpr) {
        boolExpr.setValue(!boolExpr.getValue());
    }

    /**
     * 变异字符串
     */
    private void mutateString(StringLiteralExpr strExpr) {
        String value = strExpr.getValue();
        if (!value.isEmpty()) {
            // 随机选择一个变异策略
            int strategy = random.nextInt(3);
            switch (strategy) {
                case 0: // 添加字符
                    strExpr.setValue(value + "x");
                    break;
                case 1: // 删除最后一个字符
                    strExpr.setValue(value.substring(0, value.length() - 1));
                    break;
                case 2: // 替换字符
                    if (value.length() > 1) {
                        int pos = random.nextInt(value.length());
                        strExpr.setValue(value.substring(0, pos) + "x" + value.substring(pos + 1));
                    }
                    break;
            }
        }
    }

    /**
     * 变异二元表达式
     */
    private void mutateBinaryExpr(BinaryExpr expr) {
        switch (expr.getOperator()) {
            case LESS:
                expr.setOperator(BinaryExpr.Operator.LESS_EQUALS);
                break;
            case LESS_EQUALS:
                expr.setOperator(BinaryExpr.Operator.LESS);
                break;
            case GREATER:
                expr.setOperator(BinaryExpr.Operator.GREATER_EQUALS);
                break;
            case GREATER_EQUALS:
                expr.setOperator(BinaryExpr.Operator.GREATER);
                break;
            case EQUALS:
                expr.setOperator(BinaryExpr.Operator.NOT_EQUALS);
                break;
            case NOT_EQUALS:
                expr.setOperator(BinaryExpr.Operator.EQUALS);
                break;
            case PLUS:
                expr.setOperator(BinaryExpr.Operator.MINUS);
                break;
            case MINUS:
                expr.setOperator(BinaryExpr.Operator.PLUS);
                break;
            case MULTIPLY:
                expr.setOperator(BinaryExpr.Operator.DIVIDE);
                break;
            case DIVIDE:
                expr.setOperator(BinaryExpr.Operator.MULTIPLY);
                break;
            case AND:
                expr.setOperator(BinaryExpr.Operator.OR);
                break;
            case OR:
                expr.setOperator(BinaryExpr.Operator.AND);
                break;
        }
    }

    /**
     * 重命名变量
     */
    private void renameVariables(CompilationUnit cu, int index) {
        // 重命名所有变量声明
        cu.findAll(VariableDeclarator.class).forEach(vd -> {
            String oldName = vd.getNameAsString();
            vd.setName("var_" + index + "_" + oldName);
        });

        // 重命名所有变量使用
        cu.findAll(NameExpr.class).forEach(nameExpr -> {
            String oldName = nameExpr.getNameAsString();
            if (!oldName.equals("String") && !oldName.equals("System")) {
                nameExpr.setName("var_" + index + "_" + oldName);
            }
        });
    }
}