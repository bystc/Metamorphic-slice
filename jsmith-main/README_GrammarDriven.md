# Grammar-Driven Code Generation

## 概述

新的 `GrammarFileBasedGenerator` 采用了与 JSmith 相同的**语法驱动**方法，完全摒弃了硬编码模板，实现了真正的基于 ANTLR 语法文件的代码生成。

## 设计理念

### 🎯 **语法驱动 vs 模板驱动**

#### 旧的模板驱动方法（已废弃）
```java
// ❌ 硬编码模板
StringBuilder code = new StringBuilder();
code.append("package generated;\n\n");
code.append("import java.util.*;\n");
code.append("public class ").append(className).append(" {\n");
// ... 更多硬编码内容
```

#### 新的语法驱动方法（推荐）
```java
// ✅ 基于语法规则
public String generateMainClassWithMainMethod(String className) {
    StringBuilder code = new StringBuilder();
    
    // 使用语法规则生成各个部分
    code.append(generateFromRule("packageDeclaration"));
    code.append(generateFromRule("importDeclaration"));
    code.append("public class ").append(className).append(" {\n");
    code.append(generateFromRule("mainMethod"));
    code.append("}\n");
    
    return code.toString();
}
```

## 核心组件

### 1. **GrammarFileBasedGenerator**
主要的生成器类，负责：
- 解析 ANTLR 语法文件
- 管理语法规则
- 协调代码生成过程
- 处理语义分析

### 2. **GrammarRule**
表示语法规则，包含：
- 规则名称
- 多个可选的产生式（alternatives）
- 生成逻辑

### 3. **GrammarAlternative**
表示规则的一个产生式，包含：
- 多个语法元素
- 顺序生成逻辑

### 4. **GrammarElement**
语法元素的基础类，支持：
- **LiteralElement**: 字面量字符串
- **TerminalElement**: 终结符
- **RuleReferenceElement**: 规则引用
- **SemanticElement**: 语义注解

## 语法文件格式

### 基本语法规则
```antlr
// 规则定义
ruleName
    : alternative1
    | alternative2
    | alternative3
    ;

// 语义注解
ruleWithSemantics
    : 'keyword' /* $jsmith-unique */ Identifier
    ;
```

### 语义注解
```antlr
// 生成唯一标识符
/* $jsmith-unique */

// 变量声明
/* $jsmith-var-decl */

// 变量使用
/* $jsmith-var-use */

// 类型特定的值生成
/* $jsmith-predicate(int) */
/* $jsmith-predicate(String) */
/* $jsmith-predicate(boolean) */

// 作用域管理
/* $jsmith-scope */
```

## 使用示例

### 1. 基本使用
```java
// 创建生成器
GrammarFileBasedGenerator generator = new GrammarFileBasedGenerator(12345L);

// 加载语法文件
generator.loadGrammar("src/main/resources/grammars/JavaWithMain.g4");

// 生成代码
String code = generator.generateMainClassWithMainMethod("MyClass");
```

### 2. 从特定规则生成
```java
// 生成特定的语法结构
if (generator.hasRule("statement")) {
    String statement = generator.generateFromRule("statement");
    System.out.println("Generated statement: " + statement);
}
```

### 3. 批量生成
```java
// 批量生成多个文件
List<Path> files = generator.generateBatchJavaFiles("output", 5, "BatchClass");
```

## 语法文件示例

### JavaWithMain.g4
```antlr
grammar JavaWithMain;

compilationUnit
    : packageDeclaration? importDeclaration* classDeclaration EOF
    ;

packageDeclaration
    : 'package' SPACE packageName ';' NL
    ;

packageName
    : /* $jsmith-unique */ Identifier
    | packageName '.' /* $jsmith-unique */ Identifier
    ;

classDeclaration
    : 'public' SPACE 'class' SPACE /* $jsmith-unique */ /* $jsmith-var-decl */ Identifier classBody
    ;

classBody /* $jsmith-scope */
    : '{' mainMethodDeclaration '}' NL
    ;

mainMethodDeclaration
    : 'public' SPACE 'static' SPACE 'void' SPACE 'main' '(' 'String' SPACE '[' ']' SPACE 'args' ')' methodBody
    ;

statement
    : printStatement
    | variableDeclarationStatement
    | forLoopStatement
    | ifStatement
    ;

variableDeclarationStatement
    : type /* $jsmith-unique */ /* $jsmith-var-decl */ Identifier '=' expression ';'
    ;

// ... 更多规则
```

## 与 JSmith 的对比

| 特性 | JSmith | GrammarFileBasedGenerator |
|------|--------|---------------------------|
| **语法文件支持** | ✅ 完整 ANTLR | ✅ 简化 ANTLR |
| **语义分析** | ✅ 高级 | ✅ 基础 |
| **复杂度** | 高 | 中等 |
| **学习曲线** | 陡峭 | 平缓 |
| **可扩展性** | 高 | 中等 |
| **性能** | 中等 | 高 |
| **维护成本** | 高 | 中等 |

## 优势

### 1. **真正的语法驱动**
- 完全基于 ANTLR 语法文件
- 支持复杂的语法规则
- 可扩展的语法定义

### 2. **语义支持**
- 变量作用域管理
- 类型检查
- 唯一标识符生成
- 语义注解处理

### 3. **灵活性**
- 支持自定义语法规则
- 可扩展的语义分析
- 多种生成策略

### 4. **可维护性**
- 清晰的代码结构
- 模块化设计
- 易于调试和扩展

## 扩展指南

### 1. 添加新的语法规则
```antlr
// 在语法文件中添加新规则
customStatement
    : 'custom' '(' /* $jsmith-predicate(String) */ ')' ';'
    ;
```

### 2. 扩展语义分析
```java
// 在 SemanticAnalyzer 中添加新的类型支持
public String generateCustomValue(String type) {
    switch (type) {
        case "custom":
            return "customValue" + random.nextInt(100);
        default:
            return generateRandomValue(type);
    }
}
```

### 3. 添加新的语法元素类型
```java
// 创建新的语法元素类型
class CustomElement extends GrammarElement {
    @Override
    public String generate(GrammarFileBasedGenerator generator) {
        // 自定义生成逻辑
        return "custom generated content";
    }
}
```

## 最佳实践

### 1. **语法文件设计**
- 使用清晰的规则命名
- 合理使用语义注解
- 避免过于复杂的嵌套

### 2. **语义分析**
- 保持变量作用域的一致性
- 合理使用类型检查
- 避免标识符冲突

### 3. **性能优化**
- 缓存常用的语法规则
- 避免重复的解析操作
- 合理使用随机种子

## 总结

新的 `GrammarFileBasedGenerator` 成功地将 JSmith 的语法驱动理念应用到更简单的场景中，提供了：

1. **真正的语法驱动**：完全基于 ANTLR 语法文件
2. **语义支持**：变量作用域和类型管理
3. **易于使用**：简化的 API 和清晰的文档
4. **可扩展性**：支持自定义语法规则和语义分析

这种方法既保持了 JSmith 的核心优势，又降低了使用门槛，是代码生成技术的重要进步！🚀 