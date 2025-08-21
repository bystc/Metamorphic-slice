# jsmith - 基于语法的Java代码生成器

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.volodya-lombrozo/jsmith/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.volodya-lombrozo/jsmith)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/volodya-lombrozo/jsmith/blob/main/LICENSE.txt)
[![Hits-of-Code](https://hitsofcode.com/github/volodya-lombrozo/jsmith?branch=main&label=Hits-of-Code)](https://hitsofcode.com/github/volodya-lombrozo/jsmith/view?branch=main&label=Hits-of-Code)
[![codecov](https://codecov.io/gh/volodya-lombrozo/jsmith/branch/main/graph/badge.svg)](https://codecov.io/gh/volodya-lombrozo/jsmith)

jsmith是一个先进的Java代码生成器，使用ANTLR语法文件生成**语法和语义都正确**的Java程序。该项目很大程度上受到了C语言工具[csmith](https://github.com/csmith-project/csmith)的启发。

## 🚀 核心特性

- **语法驱动生成**: 使用ANTLR4语法文件定义代码结构
- **语义分析**: 高级语义验证，包括变量作用域和类型检查
- **错误恢复**: 智能重试机制和收敛策略
- **依赖链生成**: 创建复杂的变量依赖链用于测试
- **高质量代码**: 生成的代码保证能够编译和运行

该库的主要目的是为测试Java编译器、静态分析器或程序分析工具提供随机Java程序生成功能。

## 🏗️ 架构设计

jsmith采用复杂的多层架构来保证语法正确性：

```
ANTLR语法文件 → 语法解析器 → 语义分析器 → 代码生成器 → 错误重试系统
```

### 正确性保证机制

1. **词法层**: ANTLR确保token符合词法规则
2. **语法层**: 语法规则确保结构正确性
3. **语义层**: 语义分析器验证变量、类型和作用域
4. **生成层**: 重试机制处理语义冲突

### 语义注解系统

jsmith在语法文件中使用特殊注释来添加语义约束：

```antlr
localVariableDeclarationStatement
    : /* $jsmith-type */ localVariableType SPACE 
      /* $jsmith-var-decl */ /* $jsmith-unique */ /* $jsmith-var-init */ Identifier 
      SPACE '=' SPACE expression ';'
    ;
```

**语义注解说明**:
- `/* $jsmith-var-decl */`: 变量声明
- `/* $jsmith-var-init */`: 变量初始化  
- `/* $jsmith-var-use */`: 变量使用
- `/* $jsmith-type */`: 类型信息
- `/* $jsmith-unique */`: 唯一标识符生成
- `/* $jsmith-predicate(type) */`: 类型谓词

## 安装

该库已发布到Maven Central。要将其添加到您的项目中，请在`pom.xml`中添加以下依赖：

```xml
<dependency>
  <groupId>com.github.volodya-lombrozo</groupId>
  <artifactId>jsmith</artifactId>
  <version>0.1.3</version>
</dependency>
```

## 🎯 使用方法

### 基础代码生成

该库提供了一系列用于生成随机Java程序的类。要生成随机类，可以使用`new RandomJavaClass().src()`命令：

```java
public class BasicExample {
    public static void main(String... args) {
        RandomJavaClass clazz = new RandomJavaClass();
        String code = clazz.src();
        System.out.println(code);
    }
}
```

### 带参数的高级生成

您也可以向`RandomJavaClass`构造函数传递`seed`参数来每次生成相同的类：

```java
public class ReproducibleExample {
    public static void main(String... args) {
        RandomJavaClass clazz = new RandomJavaClass(12345L);
        String code = clazz.src();
        System.out.println(code);
    }
}
```

### 批量生成

使用BatchGenerator生成多个文件：

```bash
# 生成5个随机Java文件
mvn exec:java -Dexec.mainClass="com.github.lombrozo.jsmith.BatchGenerator" -Dexec.args="5"
```

### 基于语法的生成

使用底层API进行基于语法的生成：

```java
import com.github.lombrozo.jsmith.RandomScript;
import com.github.lombrozo.jsmith.Params;

// 使用自定义参数创建生成器
Params params = new Params()
    .withSeed(12345)
    .withMaxDepth(10)
    .withConvergenceFactor(0.8);

RandomScript generator = new RandomScript(params);
String javaCode = generator.generate("compilationUnit").output();
System.out.println(javaCode);
```

**注意**: 单个`RandomJavaClass`实例只能生成一个随机类，多次调用`src()`方法将返回相同的类：

```java
public class SameExample {
    public static void main(String... args) {
        RandomJavaClass clazz = new RandomJavaClass();
        assert clazz.src().equals(clazz.src()); // true
    }
}
```

## 🧪 高级特性

### 依赖链生成

jsmith可以生成复杂的变量依赖链用于测试：

```java
// 生成的依赖链示例
public class GeneratedClass {
    public static void main(String[] args) {
        long initial = 0x8;
        long val1 = initial * 4;
        long result2 = Math.max(val1, 50);
        long final_val = (result2 > 0) ? result2 + 3 : 1;
        System.out.println(final_val);
    }
}
```

### 基于模板的生成

使用预定义模板生成特定的依赖模式：

```java
TemplateDependencyChainGenerator generator = new TemplateDependencyChainGenerator();
String code = generator.generateFromTemplate("INIT -> ADD -> MUL -> OUTPUT");
```

### 错误恢复和验证

jsmith使用智能重试机制：

- **默认10次重试**每个语法规则
- **收敛策略**避免无限递归
- **语义验证**变量作用域和类型
- **类型检查**确保兼容性

## 📊 生成代码示例

### 带方法调用的简单类

```java
public class Example {
    public static void main(String[] args) {
        helperMethod();
        long x = 42;
        System.out.println(x);
    }
    
    private static void helperMethod() {
        // 生成的方法体
    }
}
```

### 复杂依赖链

```java
public class ComplexExample {
    public static void main(String[] args) {
        long base = 0b1010;
        long step1 = base << 2;
        long step2 = Math.abs(step1 - 100);
        long step3 = (step2 % 2 == 0) ? step2 * 3 : step2 + 1;
        long result = Math.min(step3, 1000);
        System.out.println(result);
    }
}
```

## 🔍 内部机制

### 语法处理流水线

1. **ANTLR语法解析**: 解析`.g4`文件并构建抽象语法树
2. **规则注册**: 在`Unparser`和`Unlexer`中注册语法规则
3. **语义分析**: 处理语义注解并验证约束
4. **代码生成**: 按照语法规则生成Java代码
5. **错误恢复**: 使用重试机制处理失败

### 作用域管理

```java
// 语义分析器中的变量生命周期
context.scope().declare("variable", "long");  // 声明
context.scope().init("variable");             // 初始化
context.scope().initialized("long");          // 使用验证
```

### 收敛策略

jsmith使用收敛算法来：
- **防止无限递归**在语法规则中
- **确保生成过程终止**
- **保持生成代码的多样性**
- **通过动态权重平衡规则选择**

### 多次尝试生成

```java
// 处理语义冲突的重试机制
SeveralAttempts attempts = new SeveralAttempts(10, "ruleName", generator);
Node result = attempts.choose(); // 重试直到成功或达到最大尝试次数
```

### 语义验证流程

jsmith的语义验证确保生成的代码在语义上正确：

1. **变量声明验证**: 确保变量在使用前已声明
2. **类型匹配检查**: 验证变量类型与操作的兼容性
3. **作用域可见性**: 确保变量在当前作用域内可见
4. **初始化状态**: 跟踪变量的初始化状态

### 配置参数

```java
Params params = new Params()
    .withSeed(42)                    // 随机种子，用于可重现性
    .withMaxDepth(15)                // 最大递归深度
    .withConvergenceFactor(0.75)     // 规则选择的收敛率
    .withMaxAttempts(10);            // 每个规则的最大重试次数
```

如果您有兴趣了解该库的内部机制，可以查看[底层原理](docs/under_the_hood.md)指南，该指南深入解释了核心设计和生成逻辑。此外，[语义感知生成](docs/semantic_aware_generation.md)过程解释了该库如何生成具有语义感知的程序。更多详细的技术文档，请访问[docs](docs)目录。

## 🎓 应用场景

### 编译器测试

jsmith生成的代码可用于测试Java编译器的正确性：

```java
// 生成大量测试用例
for (int i = 0; i < 1000; i++) {
    RandomJavaClass testCase = new RandomJavaClass(i);
    String code = testCase.src();
    // 使用不同编译器编译并比较结果
}
```

### 静态分析工具测试

为静态分析工具提供多样化的测试输入：

```java
// 生成包含复杂控制流的代码
Params complexParams = new Params()
    .withMaxDepth(20)
    .withConvergenceFactor(0.6);

RandomScript generator = new RandomScript(complexParams);
String complexCode = generator.generate("compilationUnit").output();
```

### 性能基准测试

生成不同复杂度的代码用于性能测试：

```java
// 生成不同大小的类用于性能测试
for (int depth = 5; depth <= 25; depth += 5) {
    Params params = new Params().withMaxDepth(depth);
    RandomScript generator = new RandomScript(params);
    String code = generator.generate("compilationUnit").output();
    // 测量编译时间、分析时间等
}
```

## 🤝 贡献

我们欢迎贡献！以下是您可以帮助的方式：

### 开发环境设置

```bash
# 克隆和设置
git clone https://github.com/volodya-lombrozo/jsmith.git
cd jsmith

# 运行测试
mvn test

# 生成ANTLR语法文件
mvn antlr4:antlr4

# 使用质量检查构建
mvn clean install -Pqulice
```

### 贡献指南

1. Fork仓库
2. 创建功能分支 (`git checkout -b feature/amazing-feature`)
3. 进行更改
4. 为新功能添加测试
5. 确保所有测试通过 (`mvn test`)
6. 运行质量检查 (`mvn clean install -Pqulice`)
7. 提交更改 (`git commit -m 'Add amazing feature'`)
8. 推送到分支 (`git push origin feature/amazing-feature`)
9. 打开Pull Request

### 贡献领域

- **语法规则**: 扩展Java语法覆盖范围
- **语义注解**: 添加新的语义约束
- **代码生成器**: 实现新的生成策略
- **测试**: 添加测试用例并提高覆盖率
- **文档**: 改进文档和示例

### 要求

- [Maven 3.3+](https://maven.apache.org)
- 安装Java 8+
- 了解ANTLR4语法文件（用于语法贡献）

---

**jsmith** - 通过基于语法的生成和高级语义验证生成语法和语义都正确的Java代码。
