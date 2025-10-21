# Sonar Cryptography Plugin - Development Guidelines

## Project Overview

This is a multi-module Maven project that implements a SonarQube plugin for detecting cryptographic assets in source code and generating Cryptographic Bill of Materials (CBOM). The project supports Java (JCA, BouncyCastle) and Python (pyca/cryptography) cryptographic libraries.

## Build Configuration

### Prerequisites
- Java 17 (source and target compatibility)
- Maven 3.x
- Docker (optional, for running with SonarQube)

### Project Structure
Multi-module Maven project with the following modules:
- `mapper` - Data model mapping
- `java` - Java language support
- `python` - Python language support
- `engine` - Core detection engine
- `output` - CBOM output generation
- `common` - Common utilities
- `enricher` - Data enrichment
- `sonar-cryptography-plugin` - Main SonarQube plugin
- `rules` - Detection rules definitions

### Building the Project

Build the entire project:
```bash
mvn clean package
```

The plugin JAR file will be created in `sonar-cryptography-plugin/target/` and copied to `.SonarQube/plugins/` directory.

### Running with SonarQube

Start SonarQube with Docker Compose (includes PostgreSQL database):
```bash
docker-compose up
```

## Code Style and Formatting

### Spotless (Google Java Format)

This project uses **Spotless Maven Plugin** with **Google Java Format (AOSP style)**.

Check formatting and license headers:
```bash
mvn spotless:check
```

Apply formatting and license headers:
```bash
mvn spotless:apply
```

**Note**: Spotless runs automatically during the `package` phase.

### License Headers

All Java files must include the Apache 2.0 license header:
```java
/*
 * Sonar Cryptography Plugin
 * Copyright (C) $YEAR PQCA
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```

### Checkstyle Rules

The project uses Maven Checkstyle Plugin with inline configuration. Key rules enforced:

- **Import management**: No illegal imports, no redundant imports, no unused imports
- **Package naming**: Lowercase with dots (e.g., `com.ibm.plugin`)
- **Lambda parameters**: camelCase format (e.g., `value`, `detectionStore`)
- **Boolean complexity**: Maximum of 5 boolean operators per expression
- **Utility classes**: Must have private constructors
- **Code quality**: Final classes, missing @Override annotations, fall-through switches, inner assignments, unused local variables

Check for violations:
```bash
mvn checkstyle:check
```

**Note**: Checkstyle runs automatically during the `validate` phase.

## Testing

### Testing Framework

- **JUnit 5 (Jupiter)** - Testing framework (version 5.13.0)
- **AssertJ** - Fluent assertions library (version 3.27.3)
- **SonarQube Test Fixtures** - For plugin testing
- **Maven Surefire Plugin** - Test execution (version 3.5.3)

### Running Tests

Run all tests in the project:
```bash
mvn test
```

Run tests for a specific module:
```bash
mvn test -pl java
```

Run a specific test class:
```bash
mvn test -Dtest=SimpleGuidelineTest
```

Run a specific test method:
```bash
mvn test -Dtest=SimpleGuidelineTest#testBasicAssertion
```

### Test Structure

#### Simple Unit Tests

For basic unit tests, use JUnit 5 and AssertJ:

```java
package com.ibm.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SimpleTest {

    @Test
    void testExample() {
        String actual = "value";
        assertThat(actual).isEqualTo("value");
        
        java.util.List<String> items = java.util.List.of("AES", "RSA");
        assertThat(items).hasSize(2);
        assertThat(items).contains("AES");
    }
}
```

#### Detection Rule Tests

Detection rule tests extend `TestBase` and use SonarQube's `CheckVerifier`:

**Test class structure:**
```java
package com.ibm.plugin.rules.detection.ssl;

import static org.assertj.core.api.Assertions.assertThat;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.mapper.model.INode;
import com.ibm.plugin.TestBase;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.Tree;

class MyDetectionRuleTest extends TestBase {

    protected MyDetectionRuleTest() {
        super(SSLDetectionRules.rules());
    }

    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/rules/detection/ssl/MyTestFile.java")
                .withChecks(this)
                .verifyIssues();
    }

    @Override
    public void asserts(
            int findingId,
            @Nonnull DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> detectionStore,
            @Nonnull List<INode> nodes) {
        // Verify detection store values
        assertThat(detectionStore.getDetectionValues()).hasSize(1);
        
        // Verify translated nodes
        assertThat(nodes).hasSize(1);
        assertThat(nodes.get(0).asString()).isEqualTo("expected");
    }
}
```

**Key points for detection rule tests:**
- Extend `TestBase` (located in `java/src/test/java/com/ibm/plugin/TestBase.java`)
- Use `CheckVerifier` to scan test files
- Test files are placed in `src/test/files/` directory (not `src/test/java/`)
- Implement the `asserts` method to verify:
  - Detection store values (raw detected values)
  - Translated nodes (mapped to the data model)
- Tests are invoked through the `update` method callback

### Adding New Tests

1. Create test class in appropriate module's `src/test/java/` directory
2. For detection rules, create corresponding test file in `src/test/files/`
3. Extend `TestBase` if testing detection rules, or just use JUnit 5 for simple tests
4. Use AssertJ for assertions
5. Run the test to verify it passes
6. Ensure code follows formatting and style guidelines

## Key Dependencies

- **SonarQube Plugin API**: 12.0.0.2960
- **SonarQube Java Plugin**: 8.15.0.39343
- **SonarQube Python Plugin**: 5.4.0.22255
- **JUnit 5**: 5.13.0
- **AssertJ**: 3.27.3
- **Gson**: 2.13.1
- **Apache Commons Lang3**: 3.17.0
- **Google Java Format**: 1.27.0

## Development Workflow

1. **Before making changes**: Ensure your code is up to date
   ```bash
   git pull
   mvn clean install
   ```

2. **While developing**: 
   - Write tests first (TDD approach recommended)
   - Follow the code style guidelines
   - Use descriptive variable and method names

3. **Before committing**:
   ```bash
   mvn spotless:apply        # Format code
   mvn checkstyle:check      # Check style violations
   mvn test                  # Run all tests
   ```

4. **Building for deployment**:
   ```bash
   mvn clean package
   ```

## Module-Specific Notes

### Java Module
- Contains detection rules for Java cryptographic APIs (JCA, BouncyCastle)
- Test files in `java/src/test/files/` are Java files that will be analyzed
- Uses SonarQube's Java AST for code analysis

### Output Module
- Generates CycloneDX CBOM JSON files
- Contains model classes for protocol, algorithm, and cryptographic asset representation

### Mapper Module
- Defines the core data model (`INode`, `Algorithm`, `Version`, etc.)
- Translation layer between detected values and CBOM format

### Engine Module
- Core detection engine with `DetectionStore` and `Finding` classes
- Detection rule definitions and matching logic

## Troubleshooting

### Build Issues

If build fails with formatting errors:
```bash
mvn spotless:apply
mvn clean package
```

If tests fail unexpectedly:
```bash
mvn clean test -X  # Run with debug output
```

### Code Style Issues

View detailed Checkstyle report:
```bash
mvn checkstyle:check
# Check target/checkstyle-result.xml for details
```

## Additional Resources

- [Official SonarQube Plugin Development Guide](https://docs.sonarqube.org/latest/extend/developing-plugin/)
- [Extending Language Support](./docs/LANGUAGE_SUPPORT.md)
- [Writing Detection Rules](./docs/DETECTION_RULE_STRUCTURE.md)
- [Troubleshooting Guide](./docs/TROUBLESHOOTING.md)
- [Spotless Maven Plugin Documentation](https://github.com/diffplug/spotless/blob/main/plugin-maven/README.md)

---

*Last updated: 2025-10-21*
