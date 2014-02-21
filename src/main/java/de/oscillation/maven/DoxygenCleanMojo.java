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
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.WriterStreamConsumer;

/**
 * Goal which cleans the Doxygen output directory.
 */
@Mojo( name = "clean", defaultPhase = LifecyclePhase.CLEAN )
public class DoxygenCleanMojo extends AbstractDoxygenMojo
{
    private static final String DOXYGEN_OUTPUT_DIRECTORY_KEY = "OUTPUT_DIRECTORY";

    public void execute() throws MojoExecutionException {
        // Initialize the output base directory path with the working directory path
        String outputBasePath = getWorkingDirectory().getPath();

        // Prepare a list of all available output generators
        List<DoxygenOutputGenerator> outputGenerators = new ArrayList<DoxygenOutputGenerator>();
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

        // Set up command line
        Commandline cl = new Commandline();
        cl.setWorkingDirectory(getWorkingDirectory());
        cl.setExecutable("rm");
        cl.createArg().setValue("-rv");
        for (DoxygenOutputGenerator generator : outputGenerators) {
            if (generator.isActive()) {
                String path = outputBasePath + File.separator + generator.getOutputPath();
                cl.createArg().setFile(new File(path));
                getLog().info("Cleaning output directory " + path);
            }
        }

        // Set up stream consumers
        StringWriter stringWriter = new StringWriter();
        StreamConsumer systemOut = new WriterStreamConsumer(stringWriter);
        StreamConsumer systemErr = new WriterStreamConsumer(stringWriter);

        try {
            CommandLineUtils.executeCommandLine(cl, systemOut, systemErr);

            for (String line : stringWriter.toString().split("\n")) {
                getLog().debug(line);
            }
        }
        catch (CommandLineException e) {
            getLog().error("CommandLineException: " + e.getMessage());
        }
    }
}
