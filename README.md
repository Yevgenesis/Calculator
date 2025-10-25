#  Java Calculator

A simple desktop calculator with GUI built using Java Swing.

## Features

- Basic arithmetic operations (+, -, *, /)
- Decimal number support
- Complex expression evaluation
- Error handling

## Tech Stack

- **Java SE** - Core language
- **Swing** - GUI framework (JFrame, JTextField, JButton)
- **exp4j** - Mathematical expression parser and evaluator
- **MVC Pattern** - Clean architecture with separated logic and UI

## Requirements

- JDK 8+
- exp4j library (0.4.8)

## Installation

### Maven
```xml
<dependency>
    <groupId>net.objecthunter</groupId>
    <artifactId>exp4j</artifactId>
    <version>0.4.8</version>
</dependency>
```

Run:
```bash
    mvn clean compile
    mvn exec:java -Dexec.mainClass="Main"
```

### Manual
```bash
    # Download exp4j jar
    wget https://repo1.maven.org/maven2/net/objecthunter/exp4j/0.4.8/exp4j-0.4.8.jar
    
    # Compile
    javac -cp exp4j-0.4.8.jar *.java
    
    # Run
    java -cp .:exp4j-0.4.8.jar Main
```

## Project Structure

```
calculator/
├── Main.java              # Entry point
├── Calculator.java        # Business logic
└── CalculatorView.java    # GUI
```
