<p align="center">
    <!--<img src="https://cdn.rawgit.com/pnavais/rezolver/master/logo.svg" height="50">-->
    <img src="logo.png"/>
</p>

<p align="center">
    <a href="https://travis-ci.org/pnavais/rezolver">
        <img src="https://img.shields.io/travis/pnavais/rezolver.svg"
             alt="Build Status"/>
    </a>
    <a href="https://coveralls.io/github/pnavais/rezolver?branch=master">
        <img src="https://img.shields.io/coveralls/pnavais/rezolver.svg"
             alt="Coverage"/>
    </a>
     <a href="LICENSE.txt">
       <img src="https://img.shields.io/github/license/pnavais/rezolver.svg"
            alt="License"/>
    </a>
</p>

<p align="center"><sup><strong>Simple resource locator for Java 8</strong></sup></p>

<p><i><b>Resolves the location of a given resource using a chain of loaders.</b></i></p>
<p>
Rezolver will try to do its best to resolve the correct URL of any
arbitrary resource specified using a string URL that can be either relative
or absolute containing optionally a full valid schema.
</p>

```Java
Rezolver.fetch("/home/pnavais/images/image.png");         // --> Resolve to file system
Rezolver.fetch("file:///home/pnavais/images/image.png");  // --> Same
Rezolver.fetch("classpath:META-INF/images/image.png");    // --> Resolve to classpath resource

// Retrieve the fetched resource
ResourceInfo resInfo = Rezolver.fetch("images/inner-image.png");  // --> Will resolve to META-INF/images/inner-image.png if path cannot be found 

resInfo.isResolved();    // --> True
resInfo.getSearchPath(); // --> images/inner-image.png
resInfo.getURL();        // --> file:///res/in/classpath/META-INF/images/inner-image.png

// Get URL of resource directly
URL resURL = Rezolver.lookup("image.png");
```

In order to retrieve the resolved URL of a given resource, Rezolver will use
a default chain of loaders performing the following steps :
<ol>
<li>Use the local loader to check that the specified resource location string refers to a file in the local
   file system or in the classpath.</li>
<li>Use the Fallback loader to check if the specified resource location string refers to a path relative
    to the current application runtime path.</li>
<li>Use a remote loader to check if the specified resource location string refers to a valid URL</li>
</ol>

<h2>Creating a custom chain of loaders</h2>

Use the ResourceBuilder to customize the loaders resolution chain :
```Java
// A custom chain looking first locally and in the classpath in case of failure (using META-INF as fallback folder)
Rezolver r = Rezolver.builder()
                     .add(new LocalLoader())
                     .add(FallbackLoader.of(new ClasspathLoader(), "META-INF")))
                     .build();
                     
r.resolve("images/inner-image.png").getURL(); // --> Will restrieve file:///res/in/classpath/META-INF/images/inner-image.png
```
---


<div><sup>Icons made by <a href="http://www.flaticon.com/authors/pixel-buddha" title="Pixel Buddha">Pixel Buddha</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></sup></div>

