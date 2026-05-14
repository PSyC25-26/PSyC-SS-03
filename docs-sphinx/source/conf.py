# Configuration file for the Sphinx documentation builder.
# JailQ - docs-sphinx/source/conf.py

import os
import sys

# -- Project information -----------------------------------------------------
project   = 'JailQ'
copyright = '2025, PSyC25-26 | Universidad de Deusto'
author    = 'Equipo 3'
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
html_logo        = None
html_favicon     = None



myst_enable_extensions = [
    "colon_fence",
    "deflist",
    "tasklist",
]
