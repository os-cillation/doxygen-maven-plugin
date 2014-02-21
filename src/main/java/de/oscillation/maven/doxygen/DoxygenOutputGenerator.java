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

/**
 * An instance of this class represents a Doxygen output generator.
 */
public class DoxygenOutputGenerator
{
    private boolean active;
    private String name;
    private String outputPath;

    public DoxygenOutputGenerator(DoxygenOutputGeneratorName name) {
        super();
        this.name = name.name().toLowerCase();
        this.active = false;
        this.outputPath = this.name;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the outputPath
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param outputPath the outputPath to set
     */
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * @return the configuration key for the output path of this generator
     */
    public String getOutputPathConfigKey() {
        return name.toUpperCase() + "_OUTPUT";
    }

    /**
     * @return the configuration key for (de-)activation of this generator
     */
    public String getActivationConfigKey() {
        return "GENERATE_" + name.toUpperCase();
    }
}
