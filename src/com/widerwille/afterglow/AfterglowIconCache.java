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
	public Icon getIcon(VirtualFile file, int flags)
	{
		// Generate the lookup into the cache
		String lookup;
		try
		{
			lookup = file.getExtension();
			if(lookup == null)
			{
				FileType type = file.getFileType();
				lookup = type.getName();
			}
		}
		catch(Exception e)
		{
			lookup = null;
		}

		if(lookup == null)
			return AfterglowIcons.getIcon(file);


		// Retrieve the actual icon
		Icon result = fileCache.get(lookup);

		if(result == null)
		{
			result = AfterglowIcons.getIcon(file);

			if(result != null)
				fileCache.put(lookup, result);
		}

		return result;
	}
}
