# VTOLVR Handbook

*Everything you need to know and more*

## What?

VTOLVR Handbook is an unofficial, open source guide to [VTOLVR][1]. It is
designed to be consumable in multiple formats, including the raw markdown
documents, a compiled PDF, and a dynamic webpage.

## How?

The core docs are written in markdown format in the [docs][2] directory.
From those docs we generate the PDF using a [custom compiler][3] written in
Clojure that utilizes [pandoc][4]. The same compiler generates the files that
are consumed by the [web application][5].

### Building on macOS

There are a few dependencies that we can't automatically install:

```
brew install clojure pandoc
brew cask install mactex
```

### Building general

In the `compiler` directory run:

```
clj -A:build
```

to compile the PDF and the data used by the web app.

[1]: https://store.steampowered.com/app/667970/VTOL_VR/
[2]: docs/
[3]: compiler/
[4]: https://pandoc.org
[5]: web/
