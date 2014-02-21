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

/**
 * Boolean values for the Doxygen configuration file.
 */
public enum DoxyfileBooleanValue {
    YES,
    NO;

    /**
     * @return the string that represents <code>true</code>
     */
    public static String getTrueString() {
        return YES.name();
    }

    /**
     * @return the string that represents <code>false</code>
     */
    public static String getFalseString() {
        return NO.name();
    }
}
