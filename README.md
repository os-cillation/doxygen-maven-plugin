## doxygen-maven-plugin

The doxygen-maven-plugin is a wrapper for Doxygen that provides two goals that can be used to automate API documentation through calls to Doxygen.

It relies on a Doxygen configuration file (&rArr; Doxyfile), whose location can be specified along with other parameters in the `pom.xml`.  This file can also be automatically generated on the fly.


<p>&nbsp;</p>
#### Goals

The goal `doxygen:generate` simply calls Doxygen according to the parameters given in the `pom.xml`, where one can specify a working directory, and a path to the Doxygen configuration file, as well as the path to the executable itself.

The `doxygen:clean` goal parses the configuration file and deletes the output directories of all activated output generators (&rArr; HTML, LATEX, MAN, ...).


<p>&nbsp;</p>
#### Parameters
* `workingDirectory`: the working directory for the Doxygen execution
   (default: `${basedir}`)
* `executable`: path to the Doxygen executable, absolute or relative to `workingDirectory` (default: `doxygen`)
* `doxyfile`: path to the Doxygen configuration file, absolute or relative to `workingDirectory` (default: `Doxyfile`)
* `autogen`: if set to `true`, the configuration file is automatically generated if not present (default: `false`)


<p>&nbsp;</p>
#### Usage

The following `pom.xml` excerpt shows how to integrate the plugin into the build lifecycle and lists all parameters with their default values:

``` xml
</project>
    [...]
    <build>
        [...]
        <plugins>
            [...]
            <plugin>
                <groupId>de.oscillation.maven</groupId>
                <artifactId>doxygen-maven-plugin</artifactId>
                <version>0.1.0</version>
                <configuration>
                    <workingDirectory>${basedir}</workingDirectory>
                    <executable>doxygen</executable>
                    <doxyfile>Doxyfile</doxyfile>
                    <autogen>false</autogen>
                </configuration>
                <executions>
                    <execution>
                        <id>clean</id>
                        <phase>clean</phase>
                        <goals>
                            <goal>clean</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>generate</id>
                        <phase>prepare-package</phase>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            [...]
        </plugins>
        [...]
    </build>
    [...]
</project>
```
