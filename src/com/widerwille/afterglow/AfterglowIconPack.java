package com.widerwille.afterglow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.containers.HashMap;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;

public class AfterglowIconPack implements ApplicationComponent
{
	private final HashMap<String, String> replacements = new HashMap<>();

	public AfterglowIconPack()
	{}

	public void initComponent()
	{
		replacements.put("/fileTypes/anyType.png", "/icons/file_type_default.png");
		replacements.put("/fileTypes/css.png", "/icons/file_type_css.png");
		replacements.put("/fileTypes/xslt.png", "/icons/file_type_source.png");
		replacements.put("/fileTypes/html.png", "/icons/file_type_html.png");
		replacements.put("/fileTypes/javaScript.png", "/icons/file_type_js.png");
		replacements.put("/fileTypes/text.png", "/icons/file_type_text.png");
	}

	public void disposeComponent()
	{}

	@NotNull
	public String getComponentName()
	{
		return "AfterglowIconPack";
	}



	public void fixIcons()
	{
		replacements.put("/modules/sourceRoot.png", AfterglowIcons.DIRECTORY_FILE);
		replacements.put("/modules/sourceFolder.png", AfterglowIcons.DIRECTORY_FILE);
		replacements.put("/modules/moduleGroup.png", AfterglowIcons.DIRECTORY_FILE);
		replacements.put("/nodes/TreeClosed.png", AfterglowIcons.DIRECTORY_FILE);

		fixIcons(AllIcons.class);
	}

	private void fixIcons(Class iconsClass)
	{
		Field[] fields = iconsClass.getDeclaredFields();

		for(int i = 0; i < fields.length; i ++)
		{
			Field subClass = fields[i];

			if(Modifier.isStatic(subClass.getModifiers()))
			{
				try
				{
					Object ignored = subClass.get(null);
					Class byClass = ignored.getClass();

					if(byClass.getName().endsWith("$ByClass"))
					{
						setFieldValue(ignored, "myCallerClass", AfterglowIconPack.class);
						setFieldValue(ignored, "myWasComputed", false);
						setFieldValue(ignored, "myIcon", null);
					}
					else if(byClass.getName().endsWith("$CachedImageIcon"))
					{
						patchUrlIfNeeded(ignored);
					}
				}
				catch(IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}

		Class[] classes = iconsClass.getDeclaredClasses();

		for(int i = 0; i < classes.length; i ++)
			fixIcons(classes[i]);
	}

	private void patchUrlIfNeeded(Object object)
	{
		try
		{
			Field urlField = object.getClass().getDeclaredField("myUrl");
			Field iconField = object.getClass().getDeclaredField("myRealIcon");

			urlField.setAccessible(true);
			iconField.setAccessible(true);

			Object url = urlField.get(object);

			if(url instanceof URL)
			{
				String path = ((URL)url).getPath();

				if(path == null)
					return;

				if(path.contains("!"))
					path = path.substring(path.lastIndexOf('!') + 1);

				String replacement = replacements.get(path);
				if(replacement != null)
				{
					URL newUrl = AfterglowIconPack.class.getResource(replacement);
					if(newUrl == null)
						newUrl = new URL("file://" + replacement);

					iconField.setAccessible(true);
					iconField.set(object, null);

					urlField.set(object, newUrl);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	private void setFieldValue(Object object, String fieldName, Object value)
	{
		try
		{
			Field field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(object, value);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
