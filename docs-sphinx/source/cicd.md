# CI/CD with GitHub Actions

The project ships with two GitHub Actions workflows:

| Workflow file | Trigger | What it does |
|---------------|---------|--------------|
| `doxygen-docs.yml` | Push to `main` | Build Doxygen docs + deploy to `gh-pages/doxygen/` |
| `sphinx-docs.yml` | Push to `main` | Build Sphinx docs + deploy to `gh-pages/sphinx/` |

---

## Workflow 1: `doxygen-docs.yml`

Generates Doxygen documentation and deploys it to the `gh-pages` branch.

```yaml
name: Doxygen Docs

on:
  push:
    branches: [main]
  workflow_dispatch:

jobs:
  doxygen:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Install Doxygen 1.13.2 and Graphviz
        run: |
          sudo apt-get update && sudo apt-get install -y graphviz
          wget -q https://github.com/doxygen/doxygen/releases/download/Release_1_13_2/doxygen-1.13.2.linux.bin.tar.gz
          tar -xzf doxygen-1.13.2.linux.bin.tar.gz
          sudo cp doxygen-1.13.2/bin/doxygen /usr/local/bin/doxygen

      - name: Generate Doxygen
        working-directory: JailQ
        run: |
          mkdir -p target/doxygen
          doxygen src/main/resources/Doxyfile

      - name: Deploy to gh-pages/doxygen/
        uses: peaceiris/actions-gh-pages@v4
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: ./JailQ/target/doxygen/html
          destination_dir: doxygen
          keep_files: true
```

---

## Workflow 2: `sphinx-docs.yml`

Builds Sphinx documentation and deploys it alongside Doxygen without overwriting it.

```yaml
name: Build and Deploy Sphinx Docs

on:
  push:
    branches: [main]
  workflow_dispatch:

jobs:
  sphinx:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-python@v5
        with:
          python-version: '3.12'

      - name: Install Sphinx dependencies
        run: pip install -r docs-sphinx/requirements.txt

      - name: Build Sphinx HTML
        run: cd docs-sphinx && make html

      - name: Deploy to gh-pages/sphinx/
        uses: peaceiris/actions-gh-pages@v4
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: ./docs-sphinx/_build/html
          destination_dir: sphinx
          keep_files: true
```

---

## GitHub Pages Setup

1. Go to **Settings -> Pages**
2. Set **Source** to `gh-pages` branch, root `/`
3. Save

> **Permissions:** Settings -> Actions -> General -> Workflow permissions -> Read and write permissions

---

## Final URL Structure

```
gh-pages/
+-- index.html       <- Landing page
+-- sphinx/          <- This Sphinx portal
|   +-- index.html
+-- doxygen/         <- Doxygen technical reference
    +-- index.html
```
