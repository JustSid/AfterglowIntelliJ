package com.widerwille.afterglow;


import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.HashMap;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AfterglowIconCache implements ApplicationComponent
{
	private HashMap<String, Icon> fileCache;

	@Override
	public void initComponent()
	{
		fileCache = new HashMap<>();
	}

	@Override
	public void disposeComponent()
	{
		fileCache = null;
	}

	@NonNls
	@NotNull
	public String getComponentName()
	{
		return "AfterglowIconCache";
	}

	@Nullable
	public void clearCache()
	{
		fileCache = new HashMap<>();
	}

	@Nullable
	private String getLookup(VirtualFile file, int flags)
	{
		String lookup;

		try
		{
			FileType type = file.getFileType();
			String typeName = type.getName();

			// Special handling for files that share the same type as others, but have a different icon
			if(typeName.equalsIgnoreCase("javascript") || typeName.equalsIgnoreCase("ecmascript 6"))
			{
				if(file.getName().equalsIgnoreCase("gruntfile.js"))
					return "gruntfile.js";
				if(file.getName().equalsIgnoreCase("gulpfile.js"))
					return "gulpfile.js";
			}
			else if(typeName.equalsIgnoreCase("json"))
			{
				if(file.getName().equalsIgnoreCase("package.json"))
					return "package.json";
				if(file.getName().equalsIgnoreCase("bower.json"))
					return "bower.json";
			}

			lookup = typeName + "." + file.getExtension();
		}
		catch(Exception e)
		{
			lookup = file.getPath();
		}

		return lookup;
	}

	@Nullable
	public Icon getIcon(VirtualFile file, int flags)
	{
		if(file.isDirectory())
			return AfterglowThemeManager.DIRECTORY;

		// Generate the lookup into the cache
		String lookup = getLookup(file, flags);

		if(lookup == null)
			return AfterglowThemeManager.getIcon(file);


		// Retrieve the actual icon
		Icon result = fileCache.get(lookup);

		if(result == null)
		{
			result = AfterglowThemeManager.getIcon(file);

			if(result != null)
				fileCache.put(lookup, result);
		}

		return result;
	}
}
