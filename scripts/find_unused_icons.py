#!/usr/bin/env python
#
# Script for listing unused icons in a plugin.

import os
import re
import fnmatch

ICON_USAGE_RE = re.compile(r"""IconLoader\.getIcon[ \t]*\([ \t]*(['"])/?((\\\1|.)*?)\1[ \t]*\)[ \t]*;""",
                           flags=re.MULTILINE)


def collect_file_recursively(path, pattern):
    """
    Method returns list of files in `path` that would match some `pattern`.
    :param path: path for exploring.
    :param pattern: pattern to match for.
    :return: list of matches.
    """
    matches = list()
    for root, folders, filenames in os.walk(path):
        for filename in fnmatch.filter(filenames, pattern):
            matches.append(os.path.join(root, filename))
    return matches


def collect_resource_icons(my_dir):
    """
    Method for searching existing icons in `resources` folder.
    :param my_dir: folder of current script.
    :return: set of found icons.
    """
    path = os.path.normpath(os.path.join(my_dir, os.path.pardir, 'resources'))

    resource_icons = set()
    for icon in collect_file_recursively(path, '*.png'):
        if icon.endswith('@2x.png'):
            continue
        resource_icons.add(os.path.relpath(icon, path))

    return resource_icons


def collect_java_files(my_dir):
    """
    Method searches `*.java` files of a plugin.
    :param my_dir: folder of current script.
    :return: list of found java sources.
    """
    path = os.path.normpath(os.path.join(my_dir, os.path.pardir, 'src'))
    return collect_file_recursively(path, '*.java')


def find_icons_in_java(filename):
    """
    Method parses java file and searching usage of any icon resource in it.
    :param filename: absolute path to java source file.
    :return: list of used icons.
    """
    icons = list()
    content = open(filename, 'rb').read()
    for match in ICON_USAGE_RE.findall(content):
        icons.append(match[1])
    return icons


def main(my_dir):
    java_files = collect_java_files(my_dir)

    # get icons, used in java source files
    icon_usages = set()
    for filename in java_files:
        icon_usages = icon_usages.union(find_icons_in_java(filename))

    # get existing icons
    icons_in_resources = collect_resource_icons(my_dir)
    # get missing files
    unused = sorted(icons_in_resources - icon_usages)

    if len(unused):
        print('Here is icons, that are not used:')
        for name in unused:
            print("'{}' is not used!".format(name))


if __name__ == '__main__':
    current_dir = os.path.dirname(os.path.realpath(__file__))
    main(current_dir)
