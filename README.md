## eecs1510-2048
A text-based clone of 2048 (which is itself a clone of "3's"). Developed for EECS 1510 (Introduction to Object-Oriented Programming)

### Dependencies
You need Java 8. If you want to build outside of an IDE, you'll need Gradle. I'm using 2.3.

### Building
Run `gradle jar` from the project root. This will generate `eecs1510-2048.jar`. Launch it with

```text
java -jar eecs1510-2048.jar [options]
```

I'm working on fixing the build so that you can launch it right from gradle instead of having to build the jar every time,
but gradle seems to want to not start with fresh STDIN pipes, which throws off the scanner.
