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

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Goal which cleans the Doxygen output directory.
 */
@Mojo(name = "clean", defaultPhase = LifecyclePhase.CLEAN)
public class DoxygenCleanMojo extends AbstractDoxygenMojo
{
    /* (non-Javadoc)
     * @see de.oscillation.maven.doxygen.AbstractDoxygenMojo#execute()
     */
    @Override
    public void performTasks() {
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
