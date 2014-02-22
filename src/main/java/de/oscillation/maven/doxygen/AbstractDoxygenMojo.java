package de.oscillation.maven.doxygen;

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
import java.io.StringWriter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.WriterStreamConsumer;

/**
 * Abstract Mojo for Doxygen Mojos.
 */
public abstract class AbstractDoxygenMojo extends AbstractMojo
{
    /**
     * Path to the Doxyfile relative to the working directory.
     */
    @Parameter( property="doxygen.doxyfilePath", defaultValue="Doxyfile", required=true )
    private String doxyfilePath;

    /**
     * Flag that triggers automatic generation of Doxygen config if it is missing.
     */
    @Parameter( property="doxygen.autogen", defaultValue="false", required=true )
    private boolean autogen;

    /**
     * Path of the Doxygen executable.
     */
    @Parameter( property="doxygen.executablePath", defaultValue="doxygen", required=true )
    private String executablePath;

    /**
     * Working directory for Doxygen.
     */
    @Parameter( property="doxygen.workingDirectory", defaultValue="${basedir}", required=true )
    private File workingDirectory;

    protected void checkExecutable() {
        // Set up stream consumers
        StringWriter stringWriter = new StringWriter();
        StreamConsumer systemOut = new WriterStreamConsumer(stringWriter);
        StreamConsumer systemErr = new WriterStreamConsumer(stringWriter);

        // Check the executable
        try {
            Commandline cl = new Commandline();
            cl.setWorkingDirectory(getWorkingDirectory());
            cl.setExecutable(getExecutablePath());
            cl.createArg().setValue("--version");

            int exitCode = CommandLineUtils.executeCommandLine(cl, systemOut, systemErr);
            if (exitCode != 0) {
                getLog().error(getExecutablePath() + ": command not found");
                return;
            }
        }
        catch (CommandLineException e) {
            getLog().error("CommandLineException: " + e.getMessage());
            return;
        }
    }

    protected void ensureDoxyfile() {
        // Set up stream consumers
        StringWriter stringWriter = new StringWriter();
        StreamConsumer systemOut = new WriterStreamConsumer(stringWriter);
        StreamConsumer systemErr = new WriterStreamConsumer(stringWriter);

        // Generate Doxyfile if it is missing and the autogen flag is set
        File doxyfile = new File(getWorkingDirectory() + File.separator + getDoxyfilePath());
        if (!doxyfile.exists()) {
            if (this.shouldAutogen()) {
                Commandline cl = new Commandline();
                cl.setWorkingDirectory(getWorkingDirectory());
                cl.setExecutable(getExecutablePath());
                cl.createArg().setValue("-g");
                cl.createArg().setValue(getDoxyfilePath());
                try {
                    getLog().info("Generating " + getDoxyfilePath());
                    CommandLineUtils.executeCommandLine(cl, systemOut, systemErr);

                    // Log debug output
                    for (String line : stringWriter.toString().split("\n")) {
                        getLog().debug(line);
                    }
                }
                catch (CommandLineException e) {
                    getLog().error("CommandLineException: " + e.getMessage());
                    return;
                }
            }
            else {
                getLog().error("configuration file " + getDoxyfilePath() + " not found!");
                return;
            }
        }
    }

    /**
     * @return <code>true</code> if the autogen flag is set
     */
    public boolean shouldAutogen() {
        return autogen;
    }

    /**
     * @return the doxyfile path
     */
    public String getDoxyfilePath() {
        return doxyfilePath;
    }

    /**
     * @return the executable path
     */
    public String getExecutablePath() {
        return executablePath;
    }

    /**
     * @return the workingDirectory
     */
    public File getWorkingDirectory() {
        return workingDirectory;
    }
}
