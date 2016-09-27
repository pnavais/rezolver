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


import com.github.pnavais.rezolver.loader.impl.ContextAwareLoader;
import com.github.pnavais.rezolver.loader.impl.FallbackLoader;
import com.github.pnavais.rezolver.loader.IResourceLoader;
import com.github.pnavais.rezolver.loader.impl.ClasspathLoader;
import com.github.pnavais.rezolver.loader.impl.LocalLoader;
import com.github.pnavais.rezolver.loader.impl.RemoteLoader;

import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;

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
public class Rezolver<R>
{
    /** Holds any arbitrary data obtained during resolution */
    private Context<R> context;

    /** The chain of loaders */
    private LoadersChain<R> loadersChain;

    /** The default loaders chain */
    public static LoadersChain<URL> DEFAULT_CHAIN = LoadersChain.from(new LocalLoader(), new ClasspathLoader(), new RemoteLoader());

    /**
     * This class uses a builder pattern,
     * we keep the constructor private to avoid instantiation
     * from client code.
     */
    private Rezolver() {
        this(new LoadersChain<>());
    }

    /**
     * This class uses a builder pattern,
     * we keep the constructor private to avoid instantiation
     * from client code.
     */
    private Rezolver(LoadersChain<R> loadersChain) {
        requireNonNull(loadersChain);
        this.loadersChain = loadersChain;
        this.context = new Context<>();
    }

    /**
     * Lazy-instantiated singleton holder for the default instance
     */
    private static class RezolverHolder {
        private static Rezolver<URL> instance = new Rezolver<>(DEFAULT_CHAIN);
    }

    /**
     * Resolves the resource using the default
     * rezolver instance. This method is not
     * thread-safe with regards to the context.
     *
     * @param resourcePath the path to the resource
     * @return the resolved URL
     */
    public R lookup(String resourcePath) {
        this.context = lookup(resourcePath, this.context);
        return (this.context != null) ? this.context.getItem() : null;
    }

    /**
     * Resolves the resource using the default
     * rezolver instance. This method is not
     * thread-safe with regards to the context.
     *
     * @param resourcePath the path to the resource
     * @return the resolved URL
     */
    public Context<R> lookupCtx(String resourcePath) {
        return lookup(resourcePath, new Context<>());
    }

    /**
     * Resolves the resource using the default
     * rezolver instance. This method is not
     * thread-safe with regards to the context.
     *
     * @param resourcePath the path to the resource
     * @return the resolved context
     */
    public Context<R> lookup(String resourcePath, Context<R> context) {
        Optional.ofNullable(this.loadersChain).ifPresent(loaders -> {
            loaders.getLoadersChain().stream().filter(l -> {
                // Use the context  as input
                if (l instanceof ContextAwareLoader) {
                    ((ContextAwareLoader<R>) l).setContext(context);
                }

                // Resolve the resource with the current chain's loader
                R item = l.resolve(resourcePath);

                // Update the output context
                if (l instanceof ContextAwareLoader) {
                    this.context = ((ContextAwareLoader<R>) l).getContext();
                }

                return ((item != null) || (this.context.isResolved()));
            }).findFirst();
        });

        return this.context;
    }

    /**
     * Builder
     */
    public static class RezolverBuilder<R> {

        /** The rezolver instance */
        private Rezolver<R> instance = new Rezolver<>();

        /**
         * Starts the loader chain with the given
         * loader.
         *
         * @param loader the loader to add
         * @return the builder
         */
        public RezolverBuilder<R> withLoader(IResourceLoader<R> loader) {
            requireNonNull(loader);
            instance.loadersChain = new LoadersChain<>();
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
        public RezolverBuilder<R> withLoader(FallbackLoader<R> loader, String fallbackPath) {
            requireNonNull(loader);
            loader.setFallbackLocation(fallbackPath);
            return withLoader(loader);
        }

        /**
         * Adds a new loader to the chain
         *
         * @param loader the loader to add
         * @return the builder
         */
        public RezolverBuilder<R> andLoader(IResourceLoader<R> loader) {
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
        public RezolverBuilder<R> andLoader(FallbackLoader<R> loader, String fallbackPath) {
            requireNonNull(loader);
            loader.setFallbackLocation(fallbackPath);
            return andLoader(loader);
        }

        /**
         * Creates a new Rezolver builder using a functional interface
         *
         * @return the rezolver builder
         */
        public RezolverBuilder<R> andLoader(BiFunction<String, Context<R>, R> function) {
            return andLoader(new ContextAwareLoader<R>() {
                @Override
                public R lookup(String location) {
                    return Optional.ofNullable(function).map(f -> f.apply(location, context)).orElse(null);
                }
            });
        }

        /**
         * Sets the loader chain
         *
         * @param loaders the chain of loaders
         * @return the builder
         */
        public RezolverBuilder<R> withLoaders(Collection<IResourceLoader<R>> loaders) {
            requireNonNull(loaders);
            instance.loadersChain = new LoadersChain<>(loaders);
            return this;
        }

        /**
         * Sets the loader chain
         *
         * @param loaders the chain of loaders
         * @return the builder
         */
        public RezolverBuilder<R> withLoaders(LoadersChain<R> loaders) {
            requireNonNull(loaders);
            instance.loadersChain = loaders;
            return this;
        }

        /**
         * Retrieves the built instance
         *
         * @return the instance
         */
        public Rezolver<R> build() { return instance; }

    }

    /**
     * Creates a new Rezolver builder with the given chain
     * of loaders.
     *
     * @return the rezolver builder
     */
    public static <T> RezolverBuilder<T> withLoaders(LoadersChain<T> chain) {
        return new RezolverBuilder<T>().withLoaders(chain);
    }

    /**
     * Creates a new Rezolver builder.
     * @return the rezolver builder
     */
    public static <T> RezolverBuilder<T> withLoader(IResourceLoader<T> loader) {
        return new RezolverBuilder<T>().withLoader(loader);
    }

    /**
     * Creates a new Rezolver builder using a functional interface
     *
     * @return the rezolver builder
     */
    public static <T> RezolverBuilder<T> withLoader(BiFunction<String, Context<T>, T> function) {
        return new RezolverBuilder<T>().withLoader(new ContextAwareLoader<T>() {
            @Override
            public T lookup(String location) {
                 return Optional.ofNullable(function).map(f -> f.apply(location, context)).orElse(null);
            }
        });
    }

    /**
     * Create a default @{@link Rezolver} instance to obtain
     * the URL for a given location.
     *
     * @return a default Rezolver instance
     */
    public static RezolverBuilder<URL> withDefaults() {
        return withLoaders(DEFAULT_CHAIN);
    }

    /**
     * Creates a new Rezolver builder.
     * @return the rezolver builder
     */
    public static <T> RezolverBuilder<T> newBuilder() {
        return new RezolverBuilder<>();
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
    public static URL resolve(String resourcePath) {
        return RezolverHolder.instance.lookup(resourcePath);
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
    public static Context<URL> resolveCtx(String resourcePath) {
        return RezolverHolder.instance.lookup(resourcePath, new Context<>());
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
     * @param context the loaders's context
     * @return the resolved URL or null if not resolved
     */
    public static Context resolve(String resourcePath, Context<URL> context) {
        return RezolverHolder.instance.lookup(resourcePath, context);
    }

    /**
     * Retrieves this @{@link Rezolver} resolution
     * context.
     *
     * @return the resolution context
     */
    public Context<R> getContext() {
        return context;
    }
}
