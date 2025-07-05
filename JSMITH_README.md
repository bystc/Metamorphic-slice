# JSmith Code Generator

## 概述

JSmith是一个Java代码生成器，用于创建随机的、语法正确的Java程序。它基于[JSmith库](https://github.com/volodya-lombrozo/jsmith)构建，主要用于测试编译器、静态分析器和其他Java工具。

## 功能特性

### 1. 多种代码生成类型

- **Random (随机)**: 完全随机的Java代码，复杂度可变
- **Complex (复杂)**: 包含多个方法、字段、继承和高级Java特性的复杂类
- **Simple (简单)**: 包含基本操作的简单类，适合初学者
- **Method (方法)**: 专注于方法定义和方法调用的类
- **Expression (表达式)**: 包含复杂表达式、计算和数学运算的类

### 2. 配置选项

JSmith支持多种配置参数来控制生成的代码：

```java
JSmithConfig config = JSmithConfig.builder()
    .maxClasses(1 + random.nextInt(3))      // 1-3个类
    .maxMethods(2 + random.nextInt(5))      // 2-6个方法
    .maxStatements(5 + random.nextInt(10))  // 5-14个语句
    .maxExpressions(3 + random.nextInt(8))  // 3-10个表达式
    .maxVariables(2 + random.nextInt(6))    // 2-7个变量
    .build();
```

## 使用方法

### 1. Web界面

访问 `http://localhost:8080/jsmith` 使用Web界面：

1. 选择要生成的文件数量
2. 选择代码生成类型
3. 点击相应的按钮生成代码

### 2. API接口

#### 生成随机代码
```bash
POST /jsmith/generate-random
Content-Type: application/x-www-form-urlencoded

numFiles=5
```

#### 生成复杂代码
```bash
POST /jsmith/generate-complex
Content-Type: application/x-www-form-urlencoded

numFiles=3
```

#### 生成特定类型代码
```bash
POST /jsmith/generate
Content-Type: application/x-www-form-urlencoded

numFiles=5&type=simple
```

#### 列出生成的文件
```bash
GET /jsmith/list
```

#### 清理生成的文件
```bash
POST /jsmith/cleanup
```

#### 获取JSmith信息
```bash
GET /jsmith/info
```

### 3. 编程接口

```java
@Autowired
private JSmithService jsmithService;

// 生成随机Java代码
List<String> randomFiles = jsmithService.generateRandomJavaCode(5);

// 生成特定类型的代码
List<String> complexFiles = jsmithService.generateTypedJavaCode(3, "complex");

// 列出所有生成的文件
List<String> allFiles = jsmithService.listGeneratedFiles();

// 清理生成的文件
jsmithService.cleanupGeneratedFiles();
```

## 代码示例

### 生成的随机代码示例

```java
public class JSmithRandom0 {
    public static void main(String[] args) {
        int var0 = 42;
        int var1 = 17;
        int var2 = 89;
        
        var0 = var0 + var1;
        var1 = var1 * var2;
        
        System.out.println("Result: " + var0);
    }
}
```

### 生成的复杂代码示例

```java
public class JSmithComplex0 {
    private int value;
    private String name;

    public JSmithComplex0(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int calculate(int x, int y) {
        int result = x + y;
        if (result > 100) {
            result = result * 2;
        } else {
            result = result / 2;
        }
        return result;
    }

    public static void main(String[] args) {
        JSmithComplex0 obj = new JSmithComplex0(50, "test");
        int result = obj.calculate(30, 40);
        System.out.println("Complex result: " + result);
    }
}
```

## 测试

运行测试类来验证JSmith功能：

```bash
javac -cp ".:target/classes" TestJSmith.java
java -cp ".:target/classes" TestJSmith
```

## 目录结构

```
jsmith-generated/
├── JSmithRandom_0.java
├── JSmithRandom_1.java
├── JSmithComplex_0.java
├── JSmithSimple_0.java
├── JSmithMethod_0.java
└── JSmithExpression_0.java
```

## 依赖

项目使用以下JSmith依赖：

```xml
<dependency>
    <groupId>com.github.volodya-lombrozo</groupId>
    <artifactId>jsmith</artifactId>
    <version>0.1.3</version>
</dependency>
```

## 故障排除

### 常见问题

1. **JSmith生成失败**: 如果JSmith库生成失败，系统会自动使用备用代码生成器
2. **文件权限问题**: 确保应用有权限在项目根目录创建`jsmith-generated`文件夹
3. **内存不足**: 生成大量文件时可能需要增加JVM内存

### 日志

查看应用日志以获取详细的错误信息：

```bash
tail -f slice_executor.log
```

## 扩展

### 添加新的代码类型

1. 在`JSmithService`中添加新的生成方法
2. 在`JSmithController`中添加新的端点
3. 更新前端界面以支持新类型

### 自定义配置

修改`JSmithConfig`参数以调整生成的代码特性：

```java
JSmithConfig customConfig = JSmithConfig.builder()
    .maxClasses(5)
    .maxMethods(10)
    .maxStatements(20)
    .maxExpressions(15)
    .maxVariables(8)
    .build();
```

## 参考资料

- [JSmith GitHub Repository](https://github.com/volodya-lombrozo/jsmith)
- [JSmith Documentation](https://github.com/volodya-lombrozo/jsmith/blob/main/docs/under_the_hood.md)
- [JavaParser Documentation](https://javaparser.org/) 