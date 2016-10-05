package com.widerwille.afterglow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ListIterator;

public class AfterglowThemeManager
{
	private static final Logger LOG = Logger.getInstance(AfterglowThemeManager.class);

	public static Icon DIRECTORY_ORIGINAL = null;
	public static Icon DIRECTORY = DIRECTORY_ORIGINAL;

	public static Icon ANY = null;
	public static Icon BINARY = null;
	public static Icon TEXT = null;

	public static String DIRECTORY_FILE = null;
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

		// Get the generic icons
		DIRECTORY_ORIGINAL = getIconForName("FOLDER");
		ANY = getIconForName("ANY");
		BINARY = getIconForName("BINARY");
		TEXT = getIconForName("TEXT");
	}

	public static void addTheme(AfterglowTheme theme)
	{
		themes.add(theme);

		AfterglowIconCache cache = ApplicationManager.getApplication().getComponent(AfterglowIconCache.class);
		cache.clearCache();
	}

	public static void applyDirectoryTint(Color color)
	{
		if(DIRECTORY_ORIGINAL == null)
			return;

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
			DIRECTORY_FILE = null;
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
	public static ArrayList<AfterglowTheme.Override> getIconPackOverrides()
	{
		ArrayList<AfterglowTheme.Override> result = new ArrayList<>();

		ListIterator<AfterglowTheme> iterator = themes.listIterator(themes.size());

		while(iterator.hasPrevious())
		{
			AfterglowTheme theme = iterator.previous();

			ArrayList<AfterglowTheme.Override> overrides = theme.getIconPackOverrides();
			if(overrides != null)
				result.addAll(overrides);
		}

		return (result.size() > 0) ? result : null;
	}

	@NotNull
	public static ArrayList<AfterglowTheme.Tint> getTints()
	{
		ArrayList<AfterglowTheme.Tint> result = new ArrayList<>();

		ListIterator<AfterglowTheme> iterator = themes.listIterator(themes.size());

		while(iterator.hasPrevious())
		{
			AfterglowTheme theme = iterator.previous();

			ArrayList<AfterglowTheme.Tint> tints = theme.getTints();
			if(tints != null)
				result.addAll(tints);
		}

		return result;
	}

	@NotNull
	public static AfterglowTheme.Tint getTint(@NotNull String identifier)
	{
		ListIterator<AfterglowTheme> iterator = themes.listIterator(themes.size());

		while(iterator.hasPrevious())
		{
			AfterglowTheme theme = iterator.previous();

			ArrayList<AfterglowTheme.Tint> tints = theme.getTints();
			for(AfterglowTheme.Tint tint : tints)
			{
				if(tint.getIdentifier().equals(identifier))
					return tint;
			}
		}

		// Fallback to the default themes default tint
		return themes.get(0).getTints().get(0);
	}

	@Nullable
	public static Icon getIconForName(@NotNull String name)
	{
		ListIterator<AfterglowTheme> iterator = themes.listIterator(themes.size());

		while(iterator.hasPrevious())
		{
			AfterglowTheme theme = iterator.previous();

			Icon icon = theme.getIconForName(name);
			if(icon != null)
				return icon;
		}

		return null;
	}

	@Nullable
	public static Icon getIcon(VirtualFile virtualFile)
	{
		if(virtualFile.isDirectory())
			return DIRECTORY;

		ListIterator<AfterglowTheme> iterator = themes.listIterator(themes.size());

		while(iterator.hasPrevious())
		{
			AfterglowTheme theme = iterator.previous();

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
		SidebarSelection,
		SidebarBackground,
		SidebarText
	}

	@Nullable
	public static Color getColor(ColorType type)
	{
		String name = null;
		switch(type)
		{
			case SidebarSelection:
				name = "sidebar.selection";
				break;
			case SidebarBackground:
				name = "sidebar.background";
				break;
			case SidebarText:
				name = "sidebar.text";
				break;
		}

		ListIterator<AfterglowTheme> iterator = themes.listIterator(themes.size());

		while(iterator.hasPrevious())
		{
			AfterglowTheme theme = iterator.previous();

			Color color = theme.getColor(name);
			if(color != null)
				return color;
		}

		return null;
	}
}
