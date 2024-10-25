
YAMLib dependency
```xml
<dependency>
    <groupId>io.github.pigaut.yamlib</groupId>
    <artifactId>YAMLib</artifactId>
    <version>1.0</version>
</dependency>
```
---
SpigotYAMLib dependency
```xml
<dependency>
    <groupId>io.github.pigaut.yamlib</groupId>
    <artifactId>SpigotYAMLib</artifactId>
    <version>1.0</version>
</dependency>
```
---
Shade and relocate
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.6.0</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
        <configuration>
            <relocations>
                <relocation>
                    <pattern>io.github.pigaut.yamlib</pattern>
                    <shadedPattern>YOUR.PACKAGE.yamlib</shadedPattern>
                </relocation>
            </relocations>
    </configuration>
</plugin>
```


