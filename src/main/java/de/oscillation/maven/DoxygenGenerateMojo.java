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

import java.io.StringWriter;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.WriterStreamConsumer;

/**
 * Goal which runs Doxygen in the project directory.
 */
@Mojo( name = "generate", defaultPhase = LifecyclePhase.PREPARE_PACKAGE )
public class DoxygenGenerateMojo extends AbstractDoxygenMojo
{
    public void execute() throws MojoExecutionException {
        // Set up command line
        Commandline cl = new Commandline();
        cl.setWorkingDirectory(getWorkingDirectory());
        cl.setExecutable(getExecutablePath());
        cl.createArg().setValue(getDoxyfilePath());

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
