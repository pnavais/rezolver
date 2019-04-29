/* Copyright 2016 Pablo Navais
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

package com.github.pnavais.rezolver.loader.impl;

import com.github.pnavais.rezolver.loader.IFileSystemLoader;
import com.github.pnavais.rezolver.loader.IResourceLoader;
import com.github.pnavais.rezolver.loader.IUrlLoader;

import static java.util.Objects.requireNonNull;

/**
 * <b>AbstractLocationLoader</b>
 * <p>
 *  Common base for loaders using a restricted location
 * </p>
 */
public abstract class AbstractLocationLoader implements IResourceLoader  {

    /** The default path separator */
    public static final String DEFAULT_PATH_SEPARATOR = "/";

    /** The target loader */
    protected final IResourceLoader loader;

    /**
     * Creates a @{@link FallbackLoader} wrapping
     * a given resource loader.
     *
     * @param loader the resource loader to wrap
     */
    public AbstractLocationLoader(IResourceLoader loader) {
        requireNonNull(loader);
        this.loader = loader;
    }

    /**
     * Modify the current location applying the fallback path.
     * By default, the fallback will be appended to the location
     * using the path separator.
     *
     * @param location location to resolve
     * @return the location updated with fallback information
     */
    protected String applyRootPath(String rootPath, String location) {
        requireNonNull(rootPath);
        requireNonNull(location);

        String prefix = rootPath + getSeparator();
        String newLocation = location;

        // Rearrange the scheme in case of URL Loaders
        if (this.loader instanceof IUrlLoader) {
            newLocation = ((IUrlLoader) this.loader).stripScheme(location);
            prefix = ((IUrlLoader) this.loader).getUrlScheme() + ":" + prefix;
        }

        return prefix + newLocation;
    }

    /**
     * Retrieves the separator
     *
     * @return the separator
     */
    protected String getSeparator() {
        return ((this.loader instanceof IFileSystemLoader)
                ? ((IFileSystemLoader) this.loader).getPathSeparator()
                : DEFAULT_PATH_SEPARATOR);
    }
}
