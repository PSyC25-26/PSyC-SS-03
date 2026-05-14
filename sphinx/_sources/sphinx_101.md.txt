# Sphinx 101 — Complete Guide

## What Is Sphinx?

**Sphinx** is an open-source documentation generator originally created for the Python
language reference documentation. Today it is widely used across languages including
Java, C++, and JavaScript projects.

Sphinx takes plain text files written in **reStructuredText (.rst)** or
**Markdown (.md, via the MyST parser extension)** and generates static HTML websites,
PDF documents, ePub e-books, and man pages.

## What Is Sphinx Useful For?

| Use Case | Why Sphinx |
|----------|------------|
| **Project portals** | Aggregates API docs, guides, reports, and tutorials in one site |
| **API reference** | For Java projects, embeds Doxygen links |
| **Tutorials and how-tos** | Code blocks, admonition boxes, cross-references |
| **Search** | Built-in full-text client-side search (no server needed) |
| **Theming** | Dozens of themes give professional results instantly |

## How Is Sphinx Used?

### 1. Install Sphinx

```bash
pip install sphinx sphinx-rtd-theme myst-parser
```

### 2. Initialize a project

```bash
sphinx-quickstart docs-sphinx
```

### 3. Source File Structure

```
docs-sphinx/
+-- source/
|   +-- conf.py        <- Sphinx configuration
|   +-- index.rst      <- Master table of contents (toctree)
|   +-- overview.md    <- A page written in Markdown
|   +-- _static/
|   +-- _templates/
+-- Makefile
+-- requirements.txt
```

### 4. The toctree Directive

```rst
.. toctree::
   :maxdepth: 2
   :caption: Project Overview

   overview
   architecture
   getting_started
```

### 5. Build the Documentation

```bash
cd docs-sphinx
make html
```

Output goes to `_build/html/index.html`.

### 6. MyST Markdown Syntax

```markdown
# My Page

A paragraph with **bold** and *italic*.

    ```{note}
    This is a MyST admonition.
    ```

    ```{important}
    **[-> Open Doxygen](../doxygen/index.html)**
    ```
```

## How to Integrate with GitHub Actions

```yaml
- name: Install Sphinx
  run: pip install -r docs-sphinx/requirements.txt

- name: Build Sphinx HTML
  run: cd docs-sphinx && make html

- name: Deploy to GitHub Pages
  uses: peaceiris/actions-gh-pages@v4
  with:
    github_token: ${{ secrets.GITHUB_TOKEN }}
    publish_dir: ./docs-sphinx/_build/html
    destination_dir: sphinx
    keep_files: true
```

## Adding a New Documentation Page

1. Create `docs-sphinx/source/my_new_page.md`
2. Add `my_new_page` to the toctree in `index.rst`
3. Push to `main` — GitHub Actions rebuilds and redeploys automatically

## Useful Resources

| Resource | URL |
|----------|-----|
| Official Sphinx docs | https://www.sphinx-doc.org |
| MyST Markdown parser | https://myst-parser.readthedocs.io |
| Read the Docs theme | https://sphinx-rtd-theme.readthedocs.io |
| peaceiris/actions-gh-pages | https://github.com/peaceiris/actions-gh-pages |
