/*
 * Copyright 2016 Pablo Navais
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.pnavais.rezolver.loader;

import com.github.pnavais.rezolver.Context;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * Common interface for all loader implementations.
 */
public interface ResourceLoader {

    /**
     * Resolves the given resource location path to obtain
     * a valid URL or null when the loader cannot resolve it.
     *
     * Examples of valid URLs :
     * <code>
     *     <ul>
     *     <li>"/home/pnavais/myfile.nfo"</li>
     *     <li>"file:///C:/Users/pnavais/test/image.png"</li>
     *     <li>"classpath:/META-INF/resource.xml"</li>
     *     <li>"https://github.com/pnavais/rezolver/"</li>
     *     </ul>
     *  </code>
     *
     * @param path the resource location path
     * @param context the resolution context
     * @return the output context
     */
    Context resolve(String path, Context context);

    /**
     * Retrieves the path where the application class
     * was loaded.
     *
     * @return the running path
     */
    static URL getRunningPathURL() {
        Class<?> clazz = ResourceLoader.class;
        if (Thread.currentThread().getStackTrace().length>2) {
            String className = Thread.currentThread().getStackTrace()[3].getClassName();
            if (className != null) {
                try {
                    clazz = Class.forName(className);
                } catch (Exception ignored) { }
            }
        }

        return getClassRunningPath(clazz);
    }

    /**
     * Retrieves the running path in string format or
     * null if not able to compute it correctly.
     *
     * Due to UNC file path issues the recommended way
     * for conversions is URL -> URI -> Path.
     *
     * @return the running path or null if not found
     */
    static String getRunningPath() {
        String path;
        try {
            path = Paths.get(getRunningPathURL().toURI()).toString();
        } catch (URISyntaxException e) {
            path = null;
        }
        return path;
    }

    /**
     * Retrieves the path where the application class
     * was loaded.
     *
     * @return the running path
     */
    static URL getClassRunningPath(Class<?> clazz) {
        URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
        // Check if the code source is inside a file (JAR)
        try {
            File f = new File(location.toURI());
            if (f.isFile()) {
                location = f.getParentFile().getCanonicalFile().toURI().toURL();
            }
        } catch (Exception ignored) {
        }

        return location;
    }


}
