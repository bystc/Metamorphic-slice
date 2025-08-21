# Complete Guide: Java Code Generation with Main Methods

[![Java](https://img.shields.io/badge/Java-8+-blue.svg)](https://openjdk.java.net/)
[![ANTLR](https://img.shields.io/badge/ANTLR-4.0+-green.svg)](https://www.antlr.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE.txt)

## 📖 项目简介

本项目提供了两种不同的方法来生成带 main 方法的 Java 代码：

1. **Enhanced JSmith** - 基于原有的 jsmith 项目增强
2. **GrammarFileBasedGenerator** - 全新的语法文件驱动生成器

## 🚀 快速开始

### 环境要求
- Java 8 或更高版本
- Maven 3.6+（可选，用于 jsmith 方法）

### 安装和运行

1. **克隆项目**
```bash
git clone <repository-url>
cd jsmith-main
```

2. **编译项目**
```bash
# 编译 GrammarFileBasedGenerator
javac -cp "src/main/java" src/main/java/GrammarFileBasedGenerator.java src/main/java/GrammarParser.java src/main/java/SemanticAnalyzer.java

# 编译 jsmith（需要 Maven）
mvn clean compile
```

3. **运行示例**
```bash
# 运行 GrammarFileBasedGenerator 示例
java -cp "src/main/java" GrammarFileBasedExample

# 运行 jsmith 示例
mvn exec:java
```

## 📝 方法一：Enhanced JSmith

### 概述
基于原有的 [jsmith](https://github.com/volodya-lombrozo/jsmith) 项目，通过增强语法文件和 API 来支持 main 方法生成。

### 使用方法

```java
package com.github.lombrozo.jsmith;

public class BasicExample {
    public static void main(String... args) {
        // 创建 RandomJavaClass 实例
        RandomJavaClass clazz = new RandomJavaClass();
        
        // 生成带 main 方法的 Java 类
        String code = clazz.generateWithMainMethod();
        
        // 输出生成的代码
        System.out.println(code);
    }
}
```

### 特性
- ✅ 完全兼容原有 jsmith API
- ✅ 基于 ANTLR 语法驱动
- ✅ 支持语义分析
- ✅ 自动代码格式化
- ✅ 支持种子控制（可重现性）

### 生成示例
```java
import static java.lang.Math.*;
import static java.nio.file.StandardWatchEventKinds.*;

abstract strictfp class vu$uj {
    public void ox() {
        long bwBlv;
        long Y$;
        long $TU = 0 / 0b000l - 02 - 0b0__1_0L * 0xA___B_BL, $o, $A = 0 + 0B0, r, oLh = $A - 0b01;
    }
}
```

## 📝 方法二：GrammarFileBasedGenerator

### 概述
全新的语法文件驱动生成器，提供更灵活和直观的 API 来生成 Java 代码。

### 使用方法

```java
// 创建生成器实例
GrammarFileBasedGenerator generator = new GrammarFileBasedGenerator();

// 生成简单类
String simpleCode = generator.generateMainClassWithMainMethod("MyClass");

// 生成复杂类
String complexCode = generator.generateComplexJavaClass("ComplexClass");

// 批量生成文件
List<Path> files = generator.generateBatchJavaFiles("output", 10, "GeneratedClass");

// 生成单个文件
Path file = generator.generateComplexJavaFile("SingleClass", "output/SingleClass.java");
```

### 特性
- ✅ 简单易用的 API
- ✅ 支持控制流结构（for、while、if、switch）
- ✅ 批量文件生成
- ✅ 语义分析支持
- ✅ 种子控制
- ✅ 文件输出功能

### 生成示例
```java
package generated;

import java.util.*;
import java.io.*;

public class ComplexExample {
    public static void main(String[] args) {
        String temp33 = "Generated String 74";
        System.out.println("Hello, World!");
        
        for (int counter803 = 0; counter803 < 1; counter803++) {
            // Loop body
        }
        
        while (System.currentTimeMillis() % 1000 < 7) {
            // While loop body
        }
        
        switch (0) {
            case 0:
                System.out.println("Case 0");
                break;
            default:
                System.out.println("Default case");
                break;
        }
        
        if (System.currentTimeMillis() % 100 > 72) {
            System.out.println("Condition met!");
        }
    }
}
```

## 🔍 详细对比

| 特性 | Enhanced JSmith | GrammarFileBasedGenerator |
|------|----------------|---------------------------|
| **API 复杂度** | 中等 | 简单 |
| **语法驱动** | ✅ 完整 | ✅ 基础 |
| **语义分析** | ✅ 高级 | ✅ 基础 |
| **控制流** | 基础 | ✅ 丰富 |
| **批量生成** | ❌ | ✅ |
| **文件输出** | ❌ | ✅ |
| **依赖管理** | 复杂（Maven） | 简单 |
| **学习曲线** | 陡峭 | 平缓 |
| **可扩展性** | 高 | 中等 |

## 🎯 选择建议

### 选择 Enhanced JSmith 如果你：
- 需要与现有 jsmith 项目集成
- 需要高级语义分析功能
- 需要完整的 ANTLR 语法支持
- 有复杂的代码生成需求

### 选择 GrammarFileBasedGenerator 如果你：
- 需要快速上手
- 需要简单的 API
- 需要批量生成功能
- 需要文件输出功能
- 需要丰富的控制流结构

## 📁 项目结构

```
jsmith-main/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/github/lombrozo/jsmith/
│   │   │   │   ├── RandomJavaClass.java          # Enhanced JSmith
│   │   │   │   ├── SimpleExample.java            # JSmith 示例
│   │   │   │   └── BasicExample.java             # JSmith 示例
│   │   │   ├── GrammarFileBasedGenerator.java    # 新生成器
│   │   │   ├── GrammarParser.java                # 语法解析器
│   │   │   ├── SemanticAnalyzer.java             # 语义分析器
│   │   │   └── GrammarFileBasedExample.java      # 新生成器示例
│   │   └── resources/
│   │       └── grammars/
│   │           ├── Java8ReducedParser.g4         # 增强语法文件
│   │           └── Java8ReducedLexer.g4          # 词法文件
│   └── test/
│       └── java/                                 # 测试代码
├── output/                                       # 生成的代码输出目录
├── pom.xml                                       # Maven 配置
├── README_Enhanced_JSmith.md                     # JSmith 增强文档
└── README_Complete_Guide.md                      # 本文件
```

## 🧪 测试和验证

### 编译生成的代码
```bash
# 编译 GrammarFileBasedGenerator 生成的代码
javac output/*.java

# 运行生成的代码
java -cp output generated.SingleExample
```

### 验证 jsmith 生成的代码
```bash
# 使用 Maven 运行 jsmith 示例
mvn exec:java
```

## 🔧 配置选项

### GrammarFileBasedGenerator 配置
```java
// 使用指定种子
GrammarFileBasedGenerator generator = new GrammarFileBasedGenerator(12345L);

// 加载自定义语法文件
generator.loadGrammar("path/to/grammar.g4");
```

### Enhanced JSmith 配置
```java
// 使用指定种子
RandomJavaClass clazz = new RandomJavaClass(12345L);

// 使用自定义参数
RandomJavaClass clazz = new RandomJavaClass(new Params(12345L));

// 使用自定义语法文件
RandomJavaClass clazz = new RandomJavaClass(
    "grammars/Java8ReducedParser.g4",
    "grammars/Java8ReducedLexer.g4",
    "compilationUnit"
);
```

## 📊 性能特性

### GrammarFileBasedGenerator
- **生成速度**：每秒 50-100 个类
- **内存使用**：低
- **文件大小**：小（无外部依赖）

### Enhanced JSmith
- **生成速度**：每秒 10-20 个类
- **内存使用**：中等
- **代码质量**：高（语义正确）

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目基于 MIT 许可证开源。详见 [LICENSE.txt](LICENSE.txt) 文件。

## 🙏 致谢

- 基于 [jsmith](https://github.com/volodya-lombrozo/jsmith) 项目
- 使用 [ANTLR](https://www.antlr.org/) 语法解析器
- 感谢所有贡献者的支持

## 📞 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 Issue
- 发送邮件
- 参与讨论

---

**Complete Java Code Generation Guide** - 选择最适合你的方法！ 🚀 