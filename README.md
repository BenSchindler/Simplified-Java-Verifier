# Simplified Java (s-Java) Verifier

An object-oriented verification tool implemented in Java to analyze and validate the syntax, structure, and scoping rules of Simplified Java (`.sjava`) source files without compiling to bytecode.

---

## Overview

The verifier reads a standalone `.sjava` source file and evaluates it against language rules, checking variable scopes, type compatibility, method signatures, return structures, and flow-control blocks (`if`/`while`).

### Exit Codes & Output
The program outputs a single digit to standard output (`System.out`):
* **`0`**: The code is legal and valid s-Java.
* **`1`**: The code violates s-Java syntax or scoping rules.
* **`2`**: An IO error occurred (e.g., file not found, invalid path, improper argument format).

*In the case of errors (`1` or `2`), an informative error message is printed to `System.err` detailing the reason for failure.*

---

## Prerequisites

* **Java Development Kit (JDK):** Version 11 or higher (configured with `java.util.regex` support).
* **Command line / Terminal**

---

## Compilation

Navigate to the project root directory (containing `src/`) and compile all Java source files:

```bash
javac -d out src/ex5/**/*.java
```

---

## Running the Program

Run the verifier by passing the path to a `.sjava` source file as a single command-line argument:

```bash
java -cp out ex5.main.Sjavac <path_to_sjava_file>
```

### Parameters
* `<path_to_sjava_file>`: **(Required)** Relative or absolute path to the `.sjava` file you wish to verify (e.g., `tests/test001.sjava`). Exactly one argument must be provided; any other number of arguments will trigger an error.

### Examples

**Verifying a valid file:**
```bash
java -cp out ex5.main.Sjavac tests/valid_code.sjava
```
*Output:*
```text
0
```

**Verifying an invalid syntax file:**
```bash
java -cp out ex5.main.Sjavac tests/illegal_syntax.sjava
```
*Output:*
```text
1
Error: Variable 'x' accessed before initialization at line 14.
```

**Invalid file path / IO error:**
```bash
java -cp out ex5.main.Sjavac nonexistent_file.sjava
```
*Output:*
```text
2
IO Error: The specified file could not be read or does not exist.
```

---

## Key Features & Language Support

* **Data Types:** Supports `int`, `double`, `String`, `boolean`, and `char`.
* **Modifiers:** `final` variables and parameter immutability.
* **Methods:** Non-overloaded `void` methods, parameter validation, and recursive calls.
* **Control Flow:** Nested `if` and `while` conditionals supporting logical operators (`&&`, `||`).
* **Text Analysis:** Built with Java regex (`Matcher` and `Pattern`) for parsing tokens, declarations, and statements.
