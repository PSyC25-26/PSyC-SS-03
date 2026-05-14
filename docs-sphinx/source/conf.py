# Configuration file for the Sphinx documentation builder.
# JailQ - docs-sphinx/source/conf.py

import os
import sys

# -- Project information -----------------------------------------------------
project   = 'JailQ'
copyright = '2025, PSyC25-26 – Universidad de Deusto'
author    = 'Equipo PSyC-SS-03'
release   = '0.0.2'

# -- General configuration ---------------------------------------------------
extensions = [
    'sphinx.ext.autodoc',
    'sphinx.ext.viewcode',
    'sphinx.ext.napoleon',
    'myst_parser',
]

templates_path   = ['_templates']
exclude_patterns = ['_build', 'Thumbs.db', '.DS_Store']

source_suffix = {
    '.rst': 'restructuredtext',
    '.md':  'markdown',
}

# -- Options for HTML output -------------------------------------------------
html_theme       = 'furo'
html_static_path = ['_static']
html_title       = 'JailQ Documentation'

# -- MyST options ------------------------------------------------------------
myst_enable_extensions = [
    "colon_fence",
    "deflist",
    "tasklist",
]

# Treat all links as external URLs so relative links to Doxygen/site
# are rendered as plain <a href> tags and work correctly on GitHub Pages
myst_all_links_external = True