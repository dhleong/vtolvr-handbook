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


[1]: https://store.steampowered.com/app/667970/VTOL_VR/
[2]: docs/
[3]: compiler/
[4]: https://pandoc.org
[5]: web/
