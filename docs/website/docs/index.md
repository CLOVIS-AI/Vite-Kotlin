---
template: home.html
---

# Welcome!

[ViteJS](https://vite.dev/) is a modern web bundler for development environments, paired with the powerful [Rollup](https://rollupjs.org/) for small final bundle.

During development, Vite is much faster than Webpack and uses much less memory during auto-reloading, improving developer experience.
Production builds by Rollup generate faster than Webpack's production build and are competitive when it comes to the bundle size.

Some of the projects we tested were up to 30% smaller with Rollup than with Webpack or the Google Closure Compiler's standard mode.

## For Kotlin users

Kotlin can be transpiled to JavaScript as part of Kotlin Multiplatform. We support Kotlin users seamlessly with almost no added configuration for working projects with a configured JS target.

[Learn more about the configuration and usage](guides/index.md).

## For everyone else

If you work with a web codebase embedded in a Gradle project, you may still benefit from using Vite.
The base plugin contains the logic for the configuration and the tasks, but they are not instantiated.

You can use this plugin to easily configure Vite in your own projects with your own logic or custom tooling.

[Learn more about the configuration and usage](https://vite-kotlin.opensavvy.dev/api-docs/vite-base).

## Troubleshooting

**Failed build on Windows with the message:** "A required privilege is not held by the client".

:   You may need to enable Developer Mode in the settings. This plugin requires the creation of symbolic links to avoid duplicating the `node_modules` files. See [gradle#9077](https://github.com/gradle/gradle/issues/9077) for more information.
