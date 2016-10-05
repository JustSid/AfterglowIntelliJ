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
import java.util.*;

public class AfterglowTheme
{
	private static final Logger LOG = Logger.getInstance(AfterglowTheme.class);

	private URL url;
	private String name;
	private String author;
	private JsonObject options;
	private HashMap<String, ThemeIcon> iconTable = new HashMap<>();
	private HashMap<String, Tint> tintTable = new HashMap<>();
	private ArrayList<Tint> tintList = new ArrayList<>();
	private ArrayList<Override> overrides = new ArrayList<>();
	private boolean defaultTheme;

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

			defaultTheme = (AfterglowTheme.class.getResource("/theme/theme.json") == fileURL);
			url = fileURL;
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

	@NotNull
	public String getName()
	{
		return name;
	}
	@NotNull
	public String getAuthor()
	{
		return author;
	}
	@NotNull
	public URL getUrl()
	{
		return url;
	}

	public boolean isDefaultTheme()
	{
		return defaultTheme;
	}

	@Nullable
	public ArrayList<Override> getIconPackOverrides()
	{
		ArrayList<Override> result = new ArrayList<>();

		for(Override override : overrides)
		{
			if(override.getOverrides() != null)
				result.add(override);
		}

		return (result.size() > 0) ? result : null;
	}

	@Nullable
	public Icon getIconForName(@NotNull String name)
	{
		ThemeIcon icon = iconTable.get(name);
		if(icon != null)
			return icon.getIcon();

		return null;
	}

	@Nullable
	public Icon getIcon(VirtualFile virtualFile)
	{
		ArrayList<Override> matchList = new ArrayList<>();
		File file = new File(virtualFile);

		for(Override override : overrides)
		{
			if(override.MatchesFile(file))
				matchList.add(override);
		}

		if(matchList.size() == 0)
			return null;

		Override best = matchList.get(0);
		for(int i = 1; i < matchList.size(); i++)
		{
			Override other = matchList.get(i);
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

	public Tint getTint(@NotNull String identifier)
	{
		return tintTable.get(identifier);
	}

	public ArrayList<Tint> getTints()
	{
		return tintList;
	}

	public void applyTint(Color color)
	{
		for(ThemeIcon icon : iconTable.values())
			icon.applyTint(color);
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

		// Options
		name = options.getAsJsonPrimitive("name").getAsString();
		author = options.getAsJsonPrimitive("author").getAsString();

		try
		{
			JsonArray array = options.getAsJsonArray("tints");
			if(array != null)
			{
				for(int i = 0; i < array.size(); i ++)
				{
					JsonObject object = array.get(i).getAsJsonObject();

					try
					{
						Tint tint = new Tint(object);

						tintTable.put(tint.getIdentifier(), tint);
						tintList.add(tint);
					}
					catch(Exception e)
					{}
				}
			}
		}
		catch(Exception e)
		{}

		// Load all icons
		for(int i = 0; i < icons.size(); i++)
		{
			JsonObject object = icons.get(i).getAsJsonObject();

			try
			{
				ThemeIcon icon = new ThemeIcon(object);
				iconTable.put(icon.getIdentifier(), icon);
			}
			catch(Exception e)
			{
				LOG.error("Couldn't load icon");
				e.printStackTrace();
			}
		}

		// Load the overrides
		for(int i = 0; i < overrides.size(); i++)
		{
			JsonObject object = overrides.get(i).getAsJsonObject();

			String iconName = object.getAsJsonPrimitive("icon").getAsString();
			ThemeIcon icon = iconTable.get(iconName);

			if(icon != null)
			{
				Override override = new Override(object, icon);
				this.overrides.add(override);
			}
			else
				LOG.error("Couldn't resolve icon " + iconName);
		}
	}

	private class File
	{
		private String extension;
		private String name;
		private String type;

		private File(VirtualFile file)
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

	public class ThemeIcon
	{
		private Icon original;
		private Icon postProcessed = null; // Tinted, if tintable icon
		private boolean tintable = false;
		private String identifier;

		private ThemeIcon(@NotNull JsonObject object)
		{
			String name = object.getAsJsonPrimitive("name").getAsString();
			String file = object.getAsJsonPrimitive("icon").getAsString();

			if(name == null || file == null)
				throw new IllegalArgumentException("Invalid arguments, icon needs a name and a file");

			identifier = name;
			original = IconLoader.getIcon(file);

			JsonPrimitive tintable = object.getAsJsonPrimitive("tint");
			if(tintable != null && tintable.isBoolean())
				this.tintable = tintable.getAsBoolean();
		}

		private void applyTint(Color color)
		{
			if(tintable)
				postProcessed = new AfterglowTintedIcon(original, color);
		}

		public String getIdentifier()
		{
			return identifier;
		}
		public Icon getIcon()
		{
			return (postProcessed != null) ? postProcessed : original;
		}
	}

	public class Tint
	{
		private String name;
		private String identifier;
		private Color color;

		private Tint(@NotNull JsonObject tint)
		{
			JsonElement nameElement = tint.getAsJsonPrimitive("name");
			if(nameElement != null)
				name = nameElement.getAsString();

			JsonElement identifierElement = tint.getAsJsonPrimitive("identifier");
			if(identifierElement != null)
				identifier = identifierElement.getAsString();

			color = getColor(tint.getAsJsonArray("color"));
		}

		private Color getColor(@NotNull JsonArray values)
		{
			if(values == null || values.size() != 3)
				return null;

			int r = values.get(0).getAsInt();
			int g = values.get(1).getAsInt();
			int b = values.get(2).getAsInt();

			return new Color(r, g, b);
		}

		public String getName()
		{
			return name;
		}
		public String getIdentifier()
		{
			return identifier;
		}
		public Color getColor()
		{
			return color;
		}
	}

	public class Override
	{
		private ThemeIcon icon = null;
		private String name = null;
		private Set<String> extensions = new HashSet<>();
		private Set<String> types = new HashSet<>();
		private Set<String> overrides = new HashSet<>();
		private Set<String> defaults = new HashSet<>();

		public Override(@NotNull JsonObject override, @NotNull ThemeIcon icon)
		{
			this.icon = icon;

			JsonElement nameElement = override.getAsJsonPrimitive("name");
			if(nameElement != null)
				name = nameElement.getAsString().toLowerCase();

			JsonArray extensions = override.getAsJsonArray("extensions");
			JsonArray types = override.getAsJsonArray("types");
			JsonArray overrides = override.getAsJsonArray("overrides");
			JsonArray defaults = override.getAsJsonArray("defaults");

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

			if(overrides != null)
			{
				for(int i = 0; i < overrides.size(); i++)
					this.overrides.add(overrides.get(i).getAsString());
			}

			if(defaults != null)
			{
				for(int i = 0; i < defaults.size(); i++)
					this.defaults.add(defaults.get(i).getAsString());
			}

			if(this.overrides.size() == 0)
				this.overrides = null;

			if(this.defaults.size() == 0)
				this.defaults = null;
		}

		@NotNull
		Icon getIcon()
		{
			return icon.getIcon();
		}

		@Nullable
		Set<String> getOverrides()
		{
			return overrides;
		}

		boolean MatchesFile(@NotNull File file)
		{
			if(name != null && name.equals(file.getName()))
				return true;
			if(types.contains(file.getType()))
				return true;
			if(extensions.contains(file.getExtension()))
				return true;

			return false;
		}
		boolean IsBetterMatch(@NotNull Override other, @NotNull File file)
		{
			if(name != null && name.equals(file.getName()))
				return true;
			if(other.name != null && other.name.equals(file.getName()))

			if(extensions.contains(file.getExtension()))
				return true;
			if(other.extensions.contains(file.getExtension()))
				return false;

			if(types.contains(file.getType()))
				return true;
			if(other.types.contains(file.getType()))
				return false;

			return true; // Only extensions are left now, so both are an equally good match
		}
	}
}
