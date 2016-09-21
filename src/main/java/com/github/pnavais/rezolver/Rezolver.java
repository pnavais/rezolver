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


import com.github.pnavais.rezolver.loader.ClasspathLoader;
import com.github.pnavais.rezolver.loader.FileLoader;
import com.github.pnavais.rezolver.loader.IResourceLoader;
import com.github.pnavais.rezolver.loader.RemoteLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

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
    /** Holds any arbitrary data obtained during resolution */
    private Context context;

    /** The chain of loaders */
    private LoadersChain loadersChain;

    /** The default loaders chain */
    private static LoadersChain defaultChain = new LoadersChain(new ArrayList<>(Arrays.asList(new FileLoader(), new ClasspathLoader(), new RemoteLoader())));

    /**
     * This class uses a builder pattern,
     * we keep the constructor private to avoid instantiation
     * from client code.
     */
    private Rezolver() {
        this.loadersChain = defaultChain;
        this.context = new Context();
    }

    /**
     * Lazy-instantiated singleton holder for the default instance
     */
    private static class RezolverHolder {
        private static Rezolver instance = new Rezolver();
    }

    /**
     * Resolves the resource using the default
     * rezolver instance. This method is not
     * thread-safe with regards to the context.
     *
     * @param resourcePath the path to the resource
     * @return the resolved URL
     */
    public static URL resolve(String resourcePath) {
        return RezolverHolder.instance.lookup(resourcePath);
    }

    /**
     * Resolves the resource using the default
     * rezolver instance. This method is not
     * thread-safe with regards to the context.
     *
     * @param resourcePath the path to the resource
     * @return the resolved context
     */
    public static Context resolveCtx(String resourcePath) {
        return RezolverHolder.instance.lookupCtx(resourcePath);
    }

    /**
     * Builder
     */
    public static class RezolverBuilder {

        /** The rezolver instance */
        private Rezolver instance = new Rezolver();

        /**
         * Starts the loader chain with the given
         * loader.
         *
         * @param loader the loader to add
         * @return the builder
         */
        public RezolverBuilder withLoader(IResourceLoader loader) {
            requireNonNull(loader);
            instance.loadersChain = new LoadersChain();
            instance.loadersChain.add(loader);
            return this;
        }

        /**
         * Starts the loader chain with the given
         * loader using for it the given fallback path
         *
         * @param loader the loader to add
         * @param fallbackPath the fallback path for the loader
         * @return the builder
         */
        public RezolverBuilder withLoader(IResourceLoader loader, String fallbackPath) {
            requireNonNull(loader);
            loader.setFallbackPath(fallbackPath);
            return withLoader(loader);
        }

        /**
         * Adds a new loader to the chain
         *
         * @param loader the loader to add
         * @return the builder
         */
        public RezolverBuilder andLoader(IResourceLoader loader) {
            requireNonNull(loader);
            instance.loadersChain.add(loader);
            return this;
        }

        /**
         * Adds a new loader to the chain with the given
         * fallback path.
         *
         * @param loader the loader to add
         * @param fallbackPath the fallback path
         * @return the builder
         */
        public RezolverBuilder andLoader(IResourceLoader loader, String fallbackPath) {
            requireNonNull(loader);
            loader.setFallbackPath(fallbackPath);
            return andLoader(loader);
        }

        /**
         * Sets the loader chain
         *
         * @param loaders the chain of loaders
         * @return the builder
         */
        public RezolverBuilder withLoaders(Collection<IResourceLoader> loaders) {
            requireNonNull(loaders);
            instance.loadersChain = new LoadersChain(loaders);
            return this;
        }

        /**
         * Retrieves the built instance
         *
         * @return the instance
         */
        public Rezolver build() { return instance; }

    }

    /**
     * Creates a new Rezolver builder.
     * @return the rezolver builder
     */
    public static RezolverBuilder newBuilder() {
        return new RezolverBuilder();
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
    public URL lookup(String resourcePath) {
        Context c = lookupCtx(resourcePath);
        return (c != null) ? c.getResURL() : null;
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
    public Context lookupCtx(String resourcePath) {
        Optional.ofNullable(this.loadersChain).ifPresent(loaders -> {
            loaders.getLoadersChain().stream().filter(l -> {
                context = l.resolve(resourcePath, context);
                return ((context != null) && context.isResolved());
            }).findFirst();
        });

        return context;
    }

    /**
     * Retrieves the context
     *
     * @return the context
     */
    public Context getContext() {
        return context;
    }
}
