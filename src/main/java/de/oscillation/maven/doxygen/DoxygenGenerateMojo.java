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

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Execute Doxygen according to the parameters specified in pom.xml.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class DoxygenGenerateMojo extends AbstractDoxygenMojo
{
    /* (non-Javadoc)
     * @see de.oscillation.maven.doxygen.AbstractDoxygenMojo#execute()
     */
    @Override
    public void performTasks() {
        // Set up command line
        Commandline cl = new Commandline();
        cl.setWorkingDirectory(getWorkingDirectory());
        cl.setExecutable(getExecutablePath());
        cl.createArg().setValue(getDoxyfilePath());

        // Log which output generators are active
        for (DoxygenOutputGenerator generator : outputGenerators) {
            if (generator.isActive()) {
                getLog().info("Generating " + generator.getName() + " output");
            }
        }

        // Execute command line and log debug output
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
