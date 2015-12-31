package com.widerwille.afterglow;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class AfterglowIcons
{
	public static Icon DIRECTORY = IconLoader.getIcon("/icons/folder.png");
	public static final Icon ANY = IconLoader.getIcon("/icons/file_type_default.png");
	public static final Icon CFILE = IconLoader.getIcon("/icons/file_type_c.png");
	public static final Icon CPPFILE = IconLoader.getIcon("/icons/file_type_c++.png");
	public static final Icon CSHARP = IconLoader.getIcon("/icons/file_type_c#.png");
	public static final Icon MFILE = IconLoader.getIcon("/icons/file_type_objectivec.png");
	public static final Icon HEADER = IconLoader.getIcon("/icons/file_type_header.png");
	public static final Icon HASKELL = IconLoader.getIcon("/icons/file_type_haskell.png");
	public static final Icon RUBY = IconLoader.getIcon("/icons/file_type_ruby.png");
	public static final Icon PYTHON = IconLoader.getIcon("/icons/file_type_python.png");
	public static final Icon JAVASCRIPT = IconLoader.getIcon("/icons/file_type_js.png");
	public static final Icon COFFEE = IconLoader.getIcon("/icons/file_type_coffescript.png");
	public static final Icon JAVA = IconLoader.getIcon("/icons/file_type_java.png");
	public static final Icon PHP = IconLoader.getIcon("/icons/file_type_php.png");
	public static final Icon SHELL = IconLoader.getIcon("/icons/file_type_source.png");
	public static final Icon MARKDOWN = IconLoader.getIcon("/icons/file_type_markdown.png");
	public static final Icon GIT = IconLoader.getIcon("/icons/file_type_git.png");
	public static final Icon FONT = IconLoader.getIcon("/icons/file_type_font.png");
	public static final Icon HTML = IconLoader.getIcon("/icons/file_type_html.png");
	public static final Icon CSS = IconLoader.getIcon("/icons/file_type_css.png");
	public static final Icon JADE = IconLoader.getIcon("/icons/file_type_jade.png");
	public static final Icon XML = IconLoader.getIcon("/icons/file_type_markup.png");
	public static final Icon JSON = IconLoader.getIcon("/icons/file_type_settings.png");
	public static final Icon YAML = IconLoader.getIcon("/icons/file_type_yaml.png");
	public static final Icon GRUNT = IconLoader.getIcon("/icons/file_type_gruntfile.png");
	public static final Icon NPM = IconLoader.getIcon("/icons/file_type_npm.png");
	public static final Icon BINARY = IconLoader.getIcon("/icons/file_type_binary.png");
	public static final Icon TEXT = IconLoader.getIcon("/icons/file_type_text.png");
	public static final Icon IMAGE = IconLoader.getIcon("/icons/file_type_image.png");
	public static final Icon SQL = IconLoader.getIcon("/icons/file_type_sql.png");

	public static final void PrepareIcons()
	{
		DIRECTORY = new AfterglowTintedIcon(DIRECTORY, new Color(80, 110, 132));

		UIManager.put("Tree.selectionBackground", new Color(54, 54, 54));
	}

	@Nullable
	public static final Icon getIconForExtension(@NotNull String extension)
	{
		switch(extension)
		{
			case "md":
			case "markdown":
				return MARKDOWN;

			case "gitignore":
			case "gitmodules":
				return GIT;

			case "npmignore":
				return NPM;

			case "sh":
				return SHELL;

			case "py":
				return PYTHON;
			case "rb":
				return RUBY;
			case "js":
				return JAVASCRIPT;
			case "ttf":
			case "otf":
			case "ttc":
				return FONT;
		}

		return null;
	}

	@Nullable
	public static final Icon getIcon(VirtualFile file, int flags, @Nullable Project project)
	{
		if(file.isDirectory())
			return DIRECTORY;

		FileType type = file.getFileType();
		String typeString = type.getName().toLowerCase();
		String extension = file.getExtension();

		if(extension != null)
			extension = extension.toLowerCase();
		else
			extension = "";

		switch(typeString)
		{
			case "objectivec":
			{
				switch(extension)
				{
					case "h":
					case "hpp":
					case "pch":
						return HEADER;
					case "cpp":
						return CPPFILE;
					case "c":
						return CFILE;
					case "mm":
					case "m":
						return MFILE;
				}

				return CFILE;
			}
			case "c#":
				return CSHARP;
			case "c++":
				return CPPFILE;

			case "json":
				if(file.getName().equalsIgnoreCase("package.json"))
					return NPM;

				return JSON;
			case "xml":
			case "plist":
				return XML;

			case "html":
			case "xhtml":
				return HTML;
			case "css":
				return CSS;
			case "yaml":
				return YAML;
			case "jade":
				return JADE;

			case "javascript":
			case "ecmascript 6":

				if(file.getName().equalsIgnoreCase("gruntfile.js"))
					return GRUNT;

				return JAVASCRIPT;
			case "coffeescript":
			case "literal coffeescript":
				return COFFEE;
			case "ruby":
			case "podfile":
				return RUBY;
			case "python":
				return PYTHON;
			case "php":
				return PHP;
			case "haskell":
				return HASKELL;
			case "java":
				return JAVA;

			case "images":
				return IMAGE;
			case "sql":
				return SQL;

			case "cmakelists.txt":
				return null; // Use the default CMake icon

			case "strings":
				return TEXT;

			case "git file":
				return GIT;

			case "npm file":
				return NPM;

			case "markdown":
				return MARKDOWN;

			case "plain_text":
			{
				Icon icon = getIconForExtension(extension);
				if(icon != null)
					return icon;

				return TEXT;
			}

			case "unknown":
				Icon icon = getIconForExtension(extension);
				if(icon != null)
					return icon;

			default:
				return type.isBinary() ? BINARY : ANY;
		}
	}
}
