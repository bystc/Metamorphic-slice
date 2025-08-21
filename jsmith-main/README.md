# jsmith - Grammar-Driven Java Code Generator

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.volodya-lombrozo/jsmith/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.volodya-lombrozo/jsmith)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/volodya-lombrozo/jsmith/blob/main/LICENSE.txt)
[![Hits-of-Code](https://hitsofcode.com/github/volodya-lombrozo/jsmith?branch=main&label=Hits-of-Code)](https://hitsofcode.com/github/volodya-lombrozo/jsmith/view?branch=main&label=Hits-of-Code)
[![codecov](https://codecov.io/gh/volodya-lombrozo/jsmith/branch/main/graph/badge.svg)](https://codecov.io/gh/volodya-lombrozo/jsmith)

jsmith is a sophisticated Java code generator that uses ANTLR grammar files to produce **syntactically and semantically correct** Java programs. The project is largely inspired by [csmith](https://github.com/csmith-project/csmith), a tool for the C language.

## 🚀 Key Features

- **Grammar-Driven Generation**: Uses ANTLR4 grammar files to define code structure
- **Semantic Analysis**: Advanced semantic validation with variable scoping and type checking
- **Error Recovery**: Intelligent retry mechanisms with convergence strategies
- **Dependency Chain Generation**: Creates complex variable dependency chains for testing
- **SDG Compatible**: Generated code works seamlessly with program slicing tools

The primary purpose of the library is to provide classes for generating random Java programs to test Java compilers, static analyzers, or program analysis tools.

## 🏗️ Architecture

jsmith employs a sophisticated multi-layer architecture to guarantee syntax correctness:

```
ANTLR Grammar Files → Grammar Parser → Semantic Analyzer → Code Generator → Error Retry System
```

### How It Ensures Correctness

1. **Lexical Layer**: ANTLR ensures tokens follow lexical rules
2. **Syntactic Layer**: Grammar rules ensure structural correctness
3. **Semantic Layer**: Semantic analyzer validates variables, types, and scopes
4. **Generation Layer**: Retry mechanisms handle semantic conflicts

### Semantic Annotation System

jsmith uses special comments in grammar files to add semantic constraints:

```antlr
localVariableDeclarationStatement
    : /* $jsmith-type */ localVariableType SPACE
      /* $jsmith-var-decl */ /* $jsmith-unique */ /* $jsmith-var-init */ Identifier
      SPACE '=' SPACE expression ';'
    ;
```

**Semantic Annotations**:
- `/* $jsmith-var-decl */`: Variable declaration
- `/* $jsmith-var-init */`: Variable initialization
- `/* $jsmith-var-use */`: Variable usage
- `/* $jsmith-type */`: Type information
- `/* $jsmith-unique */`: Unique identifier generation
- `/* $jsmith-predicate(type) */`: Type predicates

## Installation

The library is available on Maven Central. To add it to your project, add the
following snippet to your `pom.xml`:

```xml

<dependency>
  <groupId>com.github.volodya-lombrozo</groupId>
  <artifactId>jsmith</artifactId>
  <version>0.1.3</version>
</dependency>
```

## 🎯 Usage

### Basic Code Generation

The library provides a set of classes for generating random Java programs. To generate a random class, you can use the `new RandomJavaClass().src()` command:

```java
public class BasicExample {
    public static void main(String... args) {
        RandomJavaClass clazz = new RandomJavaClass();
        String code = clazz.src();
        System.out.println(code);
    }
}
```

### Advanced Generation with Parameters

You can also pass the `seed` parameter to the `RandomJavaClass` constructor to generate the same class every time:

```java
public class ReproducibleExample {
    public static void main(String... args) {
        RandomJavaClass clazz = new RandomJavaClass(12345L);
        String code = clazz.src();
        System.out.println(code);
    }
}
```

### Batch Generation

Generate multiple files using the BatchGenerator:

```bash
# Generate 5 random Java files
mvn exec:java -Dexec.mainClass="com.github.lombrozo.jsmith.BatchGenerator" -Dexec.args="5"
```

### Grammar-Based Generation

Use the low-level API for grammar-based generation:

```java
import com.github.lombrozo.jsmith.RandomScript;
import com.github.lombrozo.jsmith.Params;

// Create generator with custom parameters
Params params = new Params()
    .withSeed(12345)
    .withMaxDepth(10)
    .withConvergenceFactor(0.8);

RandomScript generator = new RandomScript(params);
String javaCode = generator.generate("compilationUnit").output();
System.out.println(javaCode);
```

**Note**: A single `RandomJavaClass` instance can generate only one random class and the invocation of `src()` method will return the same class every time:

```java
public class SameExample {
    public static void main(String... args) {
        RandomJavaClass clazz = new RandomJavaClass();
        assert clazz.src().equals(clazz.src()); // true
    }
}
```

## 🧪 Advanced Features

### Dependency Chain Generation

jsmith can generate complex variable dependency chains for testing program slicing tools:

```java
// Example generated dependency chain
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

### Template-Based Generation

Use predefined templates for specific dependency patterns:

```java
TemplateDependencyChainGenerator generator = new TemplateDependencyChainGenerator();
String code = generator.generateFromTemplate("INIT -> ADD -> MUL -> OUTPUT");
```

### Integration with Program Slicing Tools

Generated code is compatible with SDG (System Dependence Graph) tools:

```bash
# Generate code
mvn exec:java -Dexec.mainClass="com.github.lombrozo.jsmith.BatchGenerator" -Dexec.args="1"

# Perform program slicing
java -jar sdg-cli.jar -c "GeneratedClass.java#15:variable"
```

### Error Recovery and Validation

jsmith uses intelligent retry mechanisms:

- **Default 10 retry attempts** per grammar rule
- **Convergence strategies** to avoid infinite recursion
- **Semantic validation** for variable scopes and types
- **Type checking** to ensure compatibility

## 📊 Generated Code Examples

### Simple Class with Method Calls

```java
public class Example {
    public static void main(String[] args) {
        helperMethod();
        long x = 42;
        System.out.println(x);
    }

    private static void helperMethod() {
        // Generated method body
    }
}
```

### Complex Dependency Chain

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

## 🔍 Internals

### Grammar Processing Pipeline

1. **ANTLR Grammar Parsing**: Parses `.g4` files and builds abstract syntax trees
2. **Rule Registration**: Registers grammar rules in `Unparser` and `Unlexer`
3. **Semantic Analysis**: Processes semantic annotations and validates constraints
4. **Code Generation**: Generates Java code following grammar rules
5. **Error Recovery**: Handles failures with retry mechanisms

### Scope Management

```java
// Variable lifecycle in semantic analyzer
context.scope().declare("variable", "long");  // Declaration
context.scope().init("variable");             // Initialization
context.scope().initialized("long");          // Usage validation
```

### Convergence Strategy

jsmith uses convergence algorithms to:
- **Prevent infinite recursion** in grammar rules
- **Ensure termination** of the generation process
- **Maintain diversity** in generated code
- **Balance rule selection** through dynamic weights

### Multi-Attempt Generation

```java
// Retry mechanism for handling semantic conflicts
SeveralAttempts attempts = new SeveralAttempts(10, "ruleName", generator);
Node result = attempts.choose(); // Retry until success or max attempts
```

If you’re interested in understanding the internal mechanics of the library, you can explore the [Under the Hood](docs/under_the_hood.md) guide, which provides an in-depth explanation of the core design and generation logic. Additionally, the process of [Semantic-Aware Generation](docs/semantic_aware_generation.md) explains how the library generates programs with semantic awareness. For more detailed technical documentation, visit the [docs](docs) directory.

## 🤝 Contributing

We welcome contributions! Here's how you can help:

### Development Setup

```bash
# Clone and setup
git clone https://github.com/volodya-lombrozo/jsmith.git
cd jsmith

# Run tests
mvn test

# Generate ANTLR grammar files
mvn antlr4:antlr4

# Build with quality checks
mvn clean install -Pqulice
```

### Contributing Guidelines

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass (`mvn test`)
6. Run quality checks (`mvn clean install -Pqulice`)
7. Commit your changes (`git commit -m 'Add amazing feature'`)
8. Push to the branch (`git push origin feature/amazing-feature`)
9. Open a Pull Request

### Areas for Contribution

- **Grammar Rules**: Extend Java grammar coverage
- **Semantic Annotations**: Add new semantic constraints
- **Code Generators**: Implement new generation strategies
- **Testing**: Add test cases and improve coverage
- **Documentation**: Improve docs and examples

### Requirements

- [Maven 3.3+](https://maven.apache.org)
- Java 8+ installed
- Understanding of ANTLR4 grammar files (for grammar contributions)

---

**jsmith** - Generating syntactically and semantically correct Java code through grammar-driven generation with advanced semantic validation.