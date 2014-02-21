package de.oscillation.maven;

/*
 * Copyright 2014 Sebastian Neuser
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Abstract Mojo for Doxygen Mojos.
 */
public abstract class AbstractDoxygenMojo extends AbstractMojo {
    /**
     * Path to the Doxyfile relative to the working directory.
     */
    @Parameter( property="doxygen.doxyfile", defaultValue="Doxyfile", required=true )
    private String doxyfile;

    /**
     * Path of the Doxygen executable.
     */
    @Parameter( property="doxygen.executable", defaultValue="doxygen", required=true )
    private String executable;

    /**
     * Working directory for Doxygen.
     */
    @Parameter( property="doxygen.workingDirectory", defaultValue="${basedir}", required=true )
    private File workingDirectory;

    /**
     * @return the doxyfile
     */
    public String getDoxyfile() {
        return doxyfile;
    }

    /**
     * @return the executable
     */
    public String getExecutable() {
        return executable;
    }

    /**
     * @return the workingDirectory
     */
    public File getWorkingDirectory() {
        return workingDirectory;
    }
}