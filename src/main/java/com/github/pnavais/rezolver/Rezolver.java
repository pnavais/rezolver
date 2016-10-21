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

package com.github.pnavais.rezolver;


import com.github.pnavais.rezolver.loader.IResourceLoader;
import com.github.pnavais.rezolver.loader.impl.ClasspathLoader;
import com.github.pnavais.rezolver.loader.impl.FallbackLoader;
import com.github.pnavais.rezolver.loader.impl.LocalLoader;
import com.github.pnavais.rezolver.loader.impl.RemoteLoader;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * <b> Rezolver.</b>
 *
 * <p><i>Tries to resolve the location of a given resource using a chain
 * of loaders.</i></p>
 * <p>
 * Rezolver will try to do it's best to resolve the correct URL of any
 * arbitrary resource specified using a string URL that can be either relative
 * or absolute containing optionally a full valid schema.
 * </p>
 * Examples of possible URLs :<code><ul>
 *  <li>"/home/pnavais/myfile.nfo"</li>
 *  <li>"file:///C:/Users/pnavais/test/image.png"</li>
 *  <li>"classpath:/META-INF/resource.xml"</li>
 *  <li>"https://github.com/pnavais/rezolver/"</li>
 *  </ul>
 *  </code>
 *
 *  In order to retrieve the resolved URL of a given resource, Rezolver will use
 *  a default chain of loaders performing the following steps :
 *  <ol>
 *  <li>Use the local loader to check that the specified resource location string refers to a file in the local
 *      file system or in the classpath.</li>
 *  <li>Use a remote loader to check if the specified resource location string refers to a valid URL</li>
 *  <li>Use the Application loader to check if the specified resource location string refers to a path relative
 *      to the current application runtime path.</li>
 *  </ol>
 *
 */
public class Rezolver
{
    /** The chain of loaders */
    private LoadersChain loadersChain;

    /** The default loaders chain */
    public static LoadersChain DEFAULT_CHAIN = LoadersChain.from(Arrays.asList(new LocalLoader(),
                                                                               FallbackLoader.of(new ClasspathLoader(), "META-INF"),
                                                                               new RemoteLoader()));

    /**
     * This class uses a builder pattern,
     * we keep the constructor private to avoid instantiation
     * from client code.
     */
    private Rezolver() {
        this(new LoadersChain());
    }

    /**
     * This class uses a builder pattern,
     * we keep the constructor private to avoid instantiation
     * from client code.
     */
    private Rezolver(LoadersChain loadersChain) {
        requireNonNull(loadersChain);
        this.loadersChain = loadersChain;

    }

    /**
     * Lazy-instantiated singleton holder for the default instance
     */
    private static class RezolverHolder {
        private static Rezolver instance = new Rezolver(DEFAULT_CHAIN);
    }

    /**
     * Resolves the resource using the default
     * rezolver instance. This method is not
     * thread-safe with regards to the context.
     *
     * @param resourcePath the path to the resource
     * @return the resolved URL
     */
    public ResourceInfo resolve(String resourcePath) {
        return loadersChain.process(resourcePath);
    }

    /**
     * Builder
     */
    public static class RezolverBuilder {

        /** The rezolver instance */
        private Rezolver instance = new Rezolver();

        /**
         * Setup the defaults for the rezolver
         * builder. (e.g. assign the default
         * resource loader chain)
         *
         * @return the rezolver builder instance
         */
        public RezolverBuilder withDefaults() {
            instance.loadersChain = DEFAULT_CHAIN;
            return this;
        }

        /**
         * Adds the given loader at the end of the chain
         *
         * @param loader the loader to add
         * @return the rezolver builder instance
         */
        public RezolverBuilder add(IResourceLoader loader) {
            requireNonNull(loader);
            instance.loadersChain.add(loader);
            return this;
        }

        /**
         * Adds the given loader with fallback path
         * at the end of the chain
         *
         * @param loader the loader
         * @param fallbackPath the fallback path
         * @return the rezolver builder instance
         */
        public RezolverBuilder add(IResourceLoader loader, String fallbackPath) {
            requireNonNull(loader);
            requireNonNull(fallbackPath);
            instance.loadersChain.add(FallbackLoader.of(loader, fallbackPath));
            return this;
        }

        /**
         * Adds the collection of loaders to the current builder
         * chain.
         * @param loaders the collection of loaders
         * @return the rezolver builder instance
         */
        public RezolverBuilder add(Collection<IResourceLoader> loaders) {
            requireNonNull(loaders);
            loaders.forEach(r -> instance.loadersChain.add(r));
            return this;
        }

        /**
         * Retrieves the built Rezolver instance
         *
         * @return the instance
         */
        public Rezolver build() { return instance; }

    }

    /**
     * Creates a new rezolver builder
     * with an empty chain of resource loaders.
     *
     * @return the newly created rezolver builder
     */
    public static RezolverBuilder builder() {
        RezolverBuilder builder = new RezolverBuilder();
        builder.instance.loadersChain.clear();
        return builder;
    }

    /**
     * Retrieve the URL for a given resourcePath
     * using the resolver chain.
     *
     * Examples of possible resource paths :<code><ul>
     *  <li>"/home/pnavais/myfile.nfo"</li>
     *  <li>"file:///C:/Users/pnavais/test/image.png"</li>
     *  <li>"classpath:/META-INF/resource.xml"</li>
     *  <li>"https://github.com/pnavais/rezolver/"</li>
     *  </ul>
     *  </code>
     *
     * @param resourcePath the path to a resource
     * @return the resolved URL or null if not resolved
     */
    public static URL lookup(String resourcePath) {
        return fetch(resourcePath).getURL();
    }

    /**
     * Retrieve the Resource Information for a given resourcePath
     * using the resolver chain.
     *
     * @param resourcePath the path to a resource
     * @return the resolved URL or null if not resolved
     */
    public static ResourceInfo fetch(String resourcePath) {
        return RezolverHolder.instance.resolve(resourcePath);
    }

}
