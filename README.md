# Metamorphic Slice - Java代码生成与切片测试平台

## 项目概述

这是一个基于Spring Boot的Java代码生成与切片测试平台，集成了强大的JSmith随机Java代码生成器。

## 核心功能

### 1. JSmith集成
- **完美融入**：jsmith-main项目已完全集成到当前项目中
- **本地依赖**：使用本地构建的jsmith 1.0-SNAPSHOT版本
- **全功能访问**：可以直接使用jsmith项目中的所有类和方法

### 2. 代码生成功能
- **随机Java类生成**：使用JSmith生成语法正确的随机Java代码
- **批量文件生成**：支持批量生成多个Java文件
- **代码格式化**：自动使用CodeFormatter格式化生成的代码
- **智能命名**：自动提取类名并生成合适的文件名

### 3. 生成的代码特点
- **语法正确**：所有生成的代码都是语法正确的Java代码
- **复杂结构**：包含类、方法、变量、控制流等复杂结构
- **多样化**：支持各种Java语法特性（抽象类、接口、泛型等）
- **格式化**：代码格式规范，便于阅读和分析

## 技术架构

### 依赖集成
```xml
<!-- Jsmith for random Java code generation (local version) -->
<dependency>
    <groupId>com.github.volodya-lombrozo</groupId>
    <artifactId>jsmith</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 核心组件
- **JSmithCodeGenerator**：封装JSmith功能的代码生成器
- **BatchGenerator**：直接使用jsmith项目的批量生成器
- **CodeFormatter**：使用jsmith项目的代码格式化工具

## 使用示例

### 1. 使用封装的JSmithCodeGenerator
```java
@Autowired
private JSmithCodeGenerator generator;

// 生成单个Java类
String code = generator.generateRandomJavaClass();

// 批量生成文件
List<String> filePaths = generator.generateComplexJavaFiles(5, "output", 12345L);
```

### 2. 直接使用jsmith的BatchGenerator
```java
import com.github.lombrozo.jsmith.BatchGenerator;

// 直接使用BatchGenerator
BatchGenerator.generateBatch(5, "output", 12345L);
```

### 3. 使用CodeFormatter
```java
import com.github.lombrozo.jsmith.CodeFormatter;

// 格式化代码
String formattedCode = CodeFormatter.format(rawCode);
```

## 生成文件示例

### 示例1：抽象类
```java
package lqL;

import java.util.concurrent.ConcurrentHashMap;

abstract strictfp class H {

    public static void main(String[] args){
        if (false) {
            boolean f47T6 = true;
        }
        long q8622 = 2;
        f47T6 = !f47T6;
        // ... 更多代码
    }

    public void d2X6() {
        long f326 = 0x1;
        boolean t7K75 = false;
        // ... 方法实现
    }
}
```

### 示例2：普通类
```java
package r7;

class Y5647 {

    public static void main(String[] args) {
        boolean w = false;
        if (true) {
            boolean m = true;
        }
        // ... 更多代码
    }
}
```

## 测试验证

项目包含完整的测试套件：
- **JSmithBatchGeneratorTest**：测试封装的代码生成器
- **testDirectBatchGenerator**：测试直接使用jsmith的BatchGenerator
- **文件生成验证**：验证生成的文件语法正确性和格式化效果

## 运行测试

```bash
# 测试封装的代码生成器
mvn test -Dtest=JSmithBatchGeneratorTest#testGenerateComplexJavaFiles

# 测试直接使用BatchGenerator
mvn test -Dtest=JSmithBatchGeneratorTest#testDirectBatchGenerator

# 运行所有测试
mvn test
```

## 项目优势

1. **完美集成**：jsmith-main项目无缝融入，无需额外配置
2. **功能完整**：可以使用jsmith的所有功能和类
3. **代码质量**：生成的代码格式规范，语法正确
4. **易于扩展**：基于Spring Boot，便于添加新功能
5. **测试完备**：包含完整的测试用例验证功能

## 开发环境

- Java 11+
- Maven 3.6+
- Spring Boot 2.7.0
- JSmith 1.0-SNAPSHOT (本地构建)

## 构建说明

1. 首先构建jsmith-main项目：
```bash
cd jsmith-main
mvn clean install -DskipTests
```

2. 然后构建主项目：
```bash
mvn clean compile
mvn test
```

这样就完成了jsmith-main项目与当前项目的完美融合！ 