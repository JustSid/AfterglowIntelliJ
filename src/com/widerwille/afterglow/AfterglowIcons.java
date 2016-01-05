package com.widerwille.afterglow;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class AfterglowIcons
{
	public static final Icon DIRECTORY_ORIGINAL = IconLoader.getIcon("/icons/folder.png");
	public static Icon DIRECTORY = DIRECTORY_ORIGINAL;
	public static final Icon ANY = IconLoader.getIcon("/file-icons/file_type_default.png");
	public static final Icon CFILE = IconLoader.getIcon("/file-icons/file_type_c.png");
	public static final Icon CPPFILE = IconLoader.getIcon("/file-icons/file_type_c++.png");
	public static final Icon CSHARP = IconLoader.getIcon("/file-icons/file_type_c#.png");
	public static final Icon MFILE = IconLoader.getIcon("/file-icons/file_type_objectivec.png");
	public static final Icon HEADER = IconLoader.getIcon("/file-icons/file_type_header.png");
	public static final Icon HASKELL = IconLoader.getIcon("/file-icons/file_type_haskell.png");
	public static final Icon RUBY = IconLoader.getIcon("/file-icons/file_type_ruby.png");
	public static final Icon PYTHON = IconLoader.getIcon("/file-icons/file_type_python.png");
	public static final Icon JAVASCRIPT = IconLoader.getIcon("/file-icons/file_type_js.png");
	public static final Icon COFFEE = IconLoader.getIcon("/file-icons/file_type_coffescript.png");
	public static final Icon JAVA = IconLoader.getIcon("/file-icons/file_type_java.png");
	public static final Icon PHP = IconLoader.getIcon("/file-icons/file_type_php.png");
	public static final Icon SHELL = IconLoader.getIcon("/file-icons/file_type_source.png");
	public static final Icon MARKDOWN = IconLoader.getIcon("/file-icons/file_type_markdown.png");
	public static final Icon GIT = IconLoader.getIcon("/file-icons/file_type_git.png");
	public static final Icon FONT = IconLoader.getIcon("/file-icons/file_type_font.png");
	public static final Icon HTML = IconLoader.getIcon("/file-icons/file_type_html.png");
	public static final Icon CSS = IconLoader.getIcon("/file-icons/file_type_css.png");
	public static final Icon JADE = IconLoader.getIcon("/file-icons/file_type_jade.png");
	public static final Icon XML = IconLoader.getIcon("/file-icons/file_type_markup.png");
	public static final Icon JSON = IconLoader.getIcon("/file-icons/file_type_settings.png");
	public static final Icon YAML = IconLoader.getIcon("/file-icons/file_type_yaml.png");
	public static final Icon GRUNT = IconLoader.getIcon("/file-icons/file_type_gruntfile.png");
	public static final Icon GULP = IconLoader.getIcon("/file-icons/file_type_gulpfile.png");
	public static final Icon BOWER = IconLoader.getIcon("/file-icons/file_type_bower.png");
	public static final Icon NPM = IconLoader.getIcon("/file-icons/file_type_npm.png");
	public static final Icon BINARY = IconLoader.getIcon("/file-icons/file_type_binary.png");
	public static final Icon TEXT = IconLoader.getIcon("/file-icons/file_type_text.png");
	public static final Icon IMAGE = IconLoader.getIcon("/file-icons/file_type_image.png");
	public static final Icon SQL = IconLoader.getIcon("/file-icons/file_type_sql.png");

	public static String DIRECTORY_FILE = "/icons/folder.png";
	private static File tempIcon = null;

	public static void applyDirectoryTint(Color color)
	{
		AfterglowTintedIcon tintedIcon = new AfterglowTintedIcon(DIRECTORY_ORIGINAL, color);
		DIRECTORY = tintedIcon;

		try
		{
			if(tempIcon != null)
			{
				tempIcon.delete();
				tempIcon = null;
			}

			String suffix = tintedIcon.isRetina() ? "@2x.png" : ".png";

			tempIcon = File.createTempFile("AfterglowTintedDirectory", suffix);
			ImageIO.write(tintedIcon.getImage(), "png", tempIcon);

			String path = tempIcon.getParent();
			String name = tempIcon.getName();

			DIRECTORY_FILE = path + "/" + name.substring(0, name.length() - suffix.length()) + ".png";
		}
		catch(IOException e)
		{
			DIRECTORY_FILE = "/icons/folder.png";
			tempIcon = null;
		}
	}

	public static void cleanUp()
	{
		if(tempIcon != null)
		{
			tempIcon.delete();
			tempIcon = null;
		}
	}



	@Nullable
	public static Icon getIconForExtension(@NotNull String extension)
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
	public static Icon getIcon(VirtualFile file, int flags, @Nullable Project project)
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
				if(file.getName().equalsIgnoreCase("bower.json"))
					return BOWER;

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
				if(file.getName().equalsIgnoreCase("gulpfile.js"))
					return GULP;

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

			case "bash":
				return SHELL;

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
