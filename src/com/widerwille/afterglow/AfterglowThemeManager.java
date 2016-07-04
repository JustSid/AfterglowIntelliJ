package com.widerwille.afterglow;

import com.google.common.collect.Lists;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class AfterglowThemeManager
{
	private static final Logger LOG = Logger.getInstance(AfterglowThemeManager.class);

	public static final Icon DIRECTORY_ORIGINAL = IconLoader.getIcon("/icons/folder.png");
	public static Icon DIRECTORY = DIRECTORY_ORIGINAL;

	public static final Icon ANY = IconLoader.getIcon("/file-icons/file_type_default.png");
	public static final Icon BINARY = IconLoader.getIcon("/file-icons/file_type_binary.png");
	public static final Icon TEXT = IconLoader.getIcon("/file-icons/file_type_text.png");

	public static String DIRECTORY_FILE = "/icons/folder.png";
	private static File tempIcon = null;

	private static ArrayList<AfterglowTheme> themes = new ArrayList<>();

	public static void initialize()
	{
		try
		{
			URL fileURL = AfterglowTheme.class.getResource("/theme/theme.json");
			AfterglowTheme theme = AfterglowTheme.CreateThemeWithURL(fileURL);

			themes.add(theme);
		}
		catch(Exception e)
		{
			LOG.error("Couldn't load theme.json", e);
		}
	}

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
	public static Icon getIcon(VirtualFile virtualFile)
	{
		if(virtualFile.isDirectory())
			return DIRECTORY;

		for(AfterglowTheme theme : Lists.reverse(themes))
		{
			Icon icon = theme.getIcon(virtualFile);
			if(icon != null)
				return icon;
		}

		FileType type = virtualFile.getFileType();

		switch(type.getName().toLowerCase())
		{
			case "plain_text":
				return TEXT;

			case "unknown":
			default:
				return type.isBinary() ? BINARY : ANY;
		}
	}

	public enum ColorType
	{
		Selection,
		Background,
		Text
	}

	@Nullable
	public static Color getColor(ColorType type)
	{
		String name = null;
		switch(type)
		{
			case Selection:
				name = "selection";
				break;
			case Background:
				name = "background";
				break;
			case Text:
				name = "text";
				break;
		}

		for(AfterglowTheme theme : Lists.reverse(themes))
		{
			Color color = theme.getColor(name);
			if(color != null)
				return color;
		}

		return null;
	}
}
