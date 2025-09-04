# Building a KotlinJS website with ESM (ES Modules)

When the Kotlin compiler generates JavaScript code, it must somehow link the different files together. In the JavaScript ecosystem, there are many ways of importing files within each other.

- AMD, UMD and CommonJS are different ways to import files. CommonJS is the name of Node.js' `require()` function.
- ESM is the native `import` statement that is part of the JavaScript language itself.

By default, the Kotlin compiler uses UMD. However, UMD isn't natively supported by Vite, so Vite for Kotlin uses preprocessors to convert the UMD declarations to ESM, which is natively supported by Vite.

By instructing the Kotlin compiler to directly generate code using ESM imports, we remove the need for preprocessing the source files. This makes Vite even faster, especially during development auto-reload.

## Comparing the generated code

Because ESM is the native JavaScript format, the generated files are smaller. For example, compare the following two generated files for a simple hello world that prints a message to the console (comments and source maps have been removed):

=== "Default configuration"

	```javascript hl_lines="15-17"
	(function (factory) {
	  if (typeof define === 'function' && define.amd)
	    define(['exports', './kotlin-kotlin-stdlib.js'], factory);
	  else if (typeof exports === 'object')
	    factory(module.exports, require('./kotlin-kotlin-stdlib.js'));
	  else {
	    if (typeof globalThis['kotlin-kotlin-stdlib'] === 'undefined') {
	      throw new Error("Error loading module 'example-simple'. Its dependency 'kotlin-kotlin-stdlib' was not found. Please, check whether 'kotlin-kotlin-stdlib' is loaded prior to 'example-simple'.");
	    }
	    globalThis['example-simple'] = factory(typeof globalThis['example-simple'] === 'undefined' ? {} : globalThis['example-simple'], globalThis['kotlin-kotlin-stdlib']);
	  }
	}(function (_, kotlin_kotlin) {
	  'use strict';
	  var println = kotlin_kotlin.$_$.a;
	  function main() {
	    println('Hello world!');
	  }
	  function mainWrapper() {
	    main();
	  }
	  mainWrapper();
	  return _;
	}));
	```

=== "Using ES Modules"

	```javascript hl_lines="2-4"
	import { println2shhhgwwt4c61 as println } from './kotlin-kotlin-stdlib.mjs';
	function main() {
	  println('Hello world!');
	}
	function mainWrapper() {
	  main();
	}
	mainWrapper();
	```

On this example, the final bundle is 4.22 kB by default and 2.68 kB with ESM. This is because Vite's bundler, Rollup, is much better at detecting unused code when ESM imports are used.

## Configuration

!!! info ""
    If you haven't configured Vite for your project yet, start with the [initial setup](index.md).

The Vite for Kotlin plugin will automatically pick up the Kotlin's compiler configuration.

Start by instructing the Kotlin compiler to generate ESM modules:
```kotlin title="build.gradle.kts" hl_lines="7"
// …

kotlin {
	js {
		browser()
		binaries.executable()
		useEsModules()
	}
	
	// …
}
```

The extension for ESM is `.mjs` instead of `.js`, so we must update our `index.html`:
```html title="index.html"
<!-- … -->
<script src="example-simple.mjs" type="module"></script>
<!-- … -->
```
Note the `.mjs` where the file previously contained a `.js`.
