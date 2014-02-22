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
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
    private static final String DOXYGEN_OUTPUT_DIRECTORY_KEY = "OUTPUT_DIRECTORY";

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

    /**
     * String writer for console output.
     */
    protected StringWriter stringWriter = new StringWriter();

    /**
     * Stream consumer for <code>stdout</code>.
     */
    protected StreamConsumer systemOut = new WriterStreamConsumer(stringWriter);

    /**
     * Stream consumer for <code>stderr</code>.
     */
    protected StreamConsumer systemErr = new WriterStreamConsumer(stringWriter);

    /**
     * The base path for Doxygen output files.
     */
    protected String outputBasePath = ".";

    /**
     * List of all available Doxygen output generators.
     */
    protected List<DoxygenOutputGenerator> outputGenerators = new ArrayList<DoxygenOutputGenerator>();

    /**
     * Checks if calling the Doxygen executable works.
     */
    protected void checkExecutable() {
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

    /**
     * Checks if the Doxygen configuration file exists.
     * If it does not exist and the <code>autogen</code> flag was set in <code>pom.xml</code>,
     * the file is automatically generated.
     * Otherwise the execution is aborted with an error message.
     */
    protected void ensureDoxyfile() {
        File doxyfile = new File(getWorkingDirectory() + File.separator + getDoxyfilePath());
        if (!doxyfile.exists()) {
            // Generate Doxyfile if the autogen flag is set
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
     * Parses the Doxygen configuration file and extracts information on output generators.
     * The information is stored in {@link #outputBasePath} and {@link #outputGenerators}.
     */
    protected void readOutputParametersFromDoxyfile() {
        // Prepare a list of all available output generators
        for (DoxygenOutputGeneratorName name : DoxygenOutputGeneratorName.values()) {
            outputGenerators.add(new DoxygenOutputGenerator(name));
        }

        // Read output generator information from the Doxyfile
        try {
            File doxyfile = new File(getWorkingDirectory() + File.separator + getDoxyfilePath());
            Scanner input = new Scanner(doxyfile);
            while (input.hasNext()) {
                String line = input.nextLine();

                // Extract output base directory
                if (line.indexOf(DOXYGEN_OUTPUT_DIRECTORY_KEY) > -1) {
                    String[] tokens = line.split("=");
                    if (tokens.length >= 2) {
                        outputBasePath = tokens[1].trim();
                    }
                    continue;
                }

                for (DoxygenOutputGenerator generator : outputGenerators) {
                    // Extract activation flag
                    if (line.indexOf(generator.getActivationConfigKey()) > -1) {
                        String[] tokens = line.split("=");
                        if (tokens.length >= 2
                        &&  tokens[1].indexOf(DoxyfileBooleanValue.getTrueString()) > -1) {
                            generator.setActive(true);
                        }
                        continue;
                    }

                    // Extract output path
                    if (line.indexOf(generator.getOutputPathConfigKey()) > -1) {
                        String[] tokens = line.split("=");
                        if (tokens.length >= 2) {
                            generator.setOutputPath(tokens[1].trim());
                        }
                        continue;
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            getLog().error("Doxyfile does not exist: " + e.getMessage());
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
