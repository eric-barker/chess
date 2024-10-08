# My notes

# Java Fundamentals (9/9/2024)

## James Gosling

- Father of Java

## Java

- Portable
- Object Oriented
- Garbage collection
- Mature
- Rich Libraries
- Large community

### Architecture

- Binary | C, Rust, Go
- Bytecode | Java, WebAssembly -- More portable but needs an interpreter on the host machine. An intermediate layer
  binary, interpreted on the target device.
- Interpreted | JavaScript, Python -- Source code interpreter executes on the target device.

### Types

- Java is strongly typed
- chars are 16-bit in Java

### In Java Everything is a Class

- Wrap all code in a class. There is always a class-wrapper.

#### Example

Here is an in-class example:

```java
public class Person {
    private String name;

    public Person(String name) {
        this.name = name;
    }

    public void sleep() {
        System.out.printf("%s is sleeping", name);
    }
}
```

Class Notes are actually in the following repo.
https://github.com/softwareconstruction240/softwareconstruction.git
