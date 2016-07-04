package com.widerwille.afterglow;

import com.google.gson.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AfterglowTheme
{
	private static final Logger LOG = Logger.getInstance(AfterglowTheme.class);

	private String name;
	private String author;
	private JsonObject options;
	private ArrayList<AfterglowOverride> overrides = new ArrayList<>();

	private AfterglowTheme(@NotNull URL fileURL) throws Exception
	{
		InputStream stream = fileURL.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		String line;
		StringBuilder content = new StringBuilder();

		while((line = reader.readLine()) != null)
			content.append(line);

		try
		{
			ParseTheme(content.toString());
		}
		catch(Exception e)
		{
			reader.close();

			LOG.error("Couldn't parse theme, error: " + e.getLocalizedMessage());
			throw e;
		}
		finally
		{
			reader.close();
		}
	}

	static public AfterglowTheme CreateThemeWithURL(@NotNull URL fileURL) throws Exception
	{
		return new AfterglowTheme(fileURL);
	}

	@Nullable
	public Icon getIcon(VirtualFile virtualFile)
	{
		ArrayList<AfterglowOverride> matchList = new ArrayList<>();
		AfterglowFile file = new AfterglowFile(virtualFile);

		for(AfterglowOverride override : overrides)
		{
			if(override.MatchesFile(file))
				matchList.add(override);
		}

		if(matchList.size() == 0)
			return null;

		AfterglowOverride best = matchList.get(0);
		for(int i = 1; i < matchList.size(); i++)
		{
			AfterglowOverride other = matchList.get(i);
			if(other.IsBetterMatch(best, file))
				best = other;
		}

		return best.getIcon();
	}

	public Color getColor(@NotNull String name)
	{
		JsonObject colors = options.getAsJsonObject("colors");
		if(colors == null)
			return null;

		JsonArray values = colors.getAsJsonArray(name);
		if(values == null || values.size() != 3)
			return null;

		int r = values.get(0).getAsInt();
		int g = values.get(1).getAsInt();
		int b = values.get(2).getAsInt();

		return new Color(r, g, b);
	}


	private void ParseTheme(@NotNull String content)
	{
		JsonElement root = new JsonParser().parse(content);
		JsonObject rootObject = root.getAsJsonObject();

		options = rootObject.getAsJsonObject("options");
		JsonArray icons = rootObject.getAsJsonArray("icons");
		JsonArray overrides = rootObject.getAsJsonArray("overrides");

		if(options == null || icons == null || overrides == null)
			throw new InternalError("options, icons and overrides mustn't be null");

		// Load all icons
		HashMap<String, Icon> iconTable = new HashMap<>();

		for(int i = 0; i < icons.size(); i++)
		{
			JsonObject object = icons.get(i).getAsJsonObject();

			String name = object.getAsJsonPrimitive("name").getAsString();
			String file = object.getAsJsonPrimitive("icon").getAsString();

			try
			{
				Icon icon = IconLoader.getIcon(file);
				iconTable.put(name, icon);
			}
			catch(Exception e)
			{
				LOG.error("Couldn't load icon " + file);
			}
		}

		// Load the overrides
		for(int i = 0; i < overrides.size(); i++)
		{
			JsonObject object = overrides.get(i).getAsJsonObject();

			String iconName = object.getAsJsonPrimitive("icon").getAsString();
			Icon icon = iconTable.get(iconName);

			if(icon != null)
			{
				AfterglowOverride override = new AfterglowOverride(object, icon);
				this.overrides.add(override);
			}
			else
				LOG.error("Couldn't resolve icon " + iconName);
		}
	}

	private class AfterglowFile
	{
		private String extension;
		private String name;
		private String type;

		private AfterglowFile(VirtualFile file)
		{
			type = file.getFileType().getName().toLowerCase();
			name = file.getName().toLowerCase();
			extension = file.getExtension();

			if(extension != null)
				extension = extension.toLowerCase();
			else
				extension = "";
		}

		String getExtension()
		{
			return extension;
		}
		String getName()
		{
			return name;
		}
		String getType()
		{
			return type;
		}
	}

	private class AfterglowOverride
	{
		private Icon icon = null;
		private String name = null;
		private Set<String> extensions = new HashSet<>();
		private Set<String> types = new HashSet<>();

		public AfterglowOverride(@NotNull JsonObject override, @NotNull Icon icon)
		{
			this.icon = icon;

			JsonElement nameElement = override.getAsJsonPrimitive("name");
			if(nameElement != null)
				name = nameElement.getAsString().toLowerCase();

			JsonArray extensions = override.getAsJsonArray("extensions");
			JsonArray types = override.getAsJsonArray("types");

			if(extensions != null)
			{
				for(int i = 0; i < extensions.size(); i++)
					this.extensions.add(extensions.get(i).getAsString().toLowerCase());
			}

			if(types != null)
			{
				for(int i = 0; i < types.size(); i++)
					this.types.add(types.get(i).getAsString().toLowerCase());
			}
		}

		@NotNull
		Icon getIcon()
		{
			return icon;
		}

		boolean MatchesFile(@NotNull AfterglowFile file)
		{
			if(name != null && name.equals(file.getName()))
				return true;
			if(types.contains(file.getType()))
				return true;
			if(extensions.contains(file.getExtension()))
				return true;

			return false;
		}
		boolean IsBetterMatch(@NotNull AfterglowOverride other, @NotNull AfterglowFile file)
		{
			if(name != null && name.equals(file.getName()))
				return true;
			if(other.name != null && other.name.equals(file.getName()))
				return false;

			if(types.contains(file.getType()))
				return true;
			if(other.types.contains(file.getType()))
				return false;

			return true; // Only extensions are left now, so both are an equally good match
		}
	}
}
