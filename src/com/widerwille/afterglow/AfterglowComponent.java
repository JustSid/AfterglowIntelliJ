package com.widerwille.afterglow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.ui.Gray;
import com.intellij.util.containers.HashMap;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class AfterglowComponent implements ApplicationComponent
{
	private AfterglowTheme.Tint activeTheme = null;

	public void initComponent()
	{
		AfterglowThemeManager.initialize();

		Color selectionColor = AfterglowThemeManager.getColor(AfterglowThemeManager.ColorType.SidebarSelection);
		Color backgroundColor = AfterglowThemeManager.getColor(AfterglowThemeManager.ColorType.SidebarBackground);
		Color textColor = AfterglowThemeManager.getColor(AfterglowThemeManager.ColorType.SidebarText);

		if(selectionColor == null)
			selectionColor = Gray._54;
		if(backgroundColor == null)
			backgroundColor = Gray._32;
		if(textColor == null)
			textColor = Gray._160;

		// This is a very evil hack
		// Basically the UIUtil class tries to figure out which colour to use for the selected
		// cell, and when the cell doesn't have the focus, it checks wether "Tree.textBackground"
		// is dark or not. If it's dark, it will return a hard coded color, but, if it's light
		// it will return a static field color... Which we can force replace. Yay for hacks

		/*UIManager.put("Tree.textBackground", new Color(255, 255, 255, 0));

		try
		{
			setFinalStatic(UIUtil.class, "UNFOCUSED_SELECTION_COLOR", selectionColor);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/

		// The above hack is GREAT! Except it breaks other trees, and I can't figure out how to fix them
		// so screw it... I'll go back to thee and hack you even more!


		// General colours
		UIManager.put("Tree.background", backgroundColor);
		UIManager.put("Tree.textBackground", backgroundColor);
		UIManager.put("Tree.selectionBackground", selectionColor);

		UIManager.put("Tree.foreground", textColor);

		// Icons
		try
		{
			Icon expanded = AfterglowThemeManager.getIconForName("EXPANDED");
			Icon collapsed = AfterglowThemeManager.getIconForName("COLLAPSED");

			if(expanded != null)
			{
				UIManager.put("Tree.expandedIcon", expanded);
				setFinalStatic(AllIcons.Mac.class, "Tree_white_down_arrow", expanded);
			}

			if(collapsed != null)
			{
				UIManager.put("Tree.collapsedIcon", collapsed);
				setFinalStatic(AllIcons.Mac.class, "Tree_white_right_arrow", collapsed);
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}


		AfterglowSettings settings = AfterglowSettings.getInstance();
		String theme = settings.theme;

		applyTheme(AfterglowThemeManager.getTint(theme));
	}

	public void disposeComponent()
	{
	}

	@NotNull
	public String getComponentName()
	{
		return "AfterglowComponent";
	}


	public void applyTheme(AfterglowTheme.Tint tint)
	{
		AfterglowThemeManager.applyTint(tint.getColor());
		fixIcons();

		activeTheme = tint;

		AfterglowSettings settings = AfterglowSettings.getInstance();
		settings.theme = tint.getIdentifier();
	}

	public AfterglowTheme.Tint getActiveTheme()
	{
		return activeTheme;
	}


	public void fixIcons()
	{
		// Override Icon Pack icons
		{
			HashMap<String, Icon> replacements = new HashMap<>();
			ArrayList<AfterglowTheme.Override> overrides = AfterglowThemeManager.getIconPackOverrides();

			if(overrides != null)
			{
				for(AfterglowTheme.Override override : overrides)
				{
					Icon icon = override.getIcon();

					try
					{
						for(String name : override.getOverrides())
							replacements.put(name, icon);
					}
					catch(NullPointerException e)
					{
						e.printStackTrace();
					}
				}

				fixIcons(AllIcons.class, replacements);
			}
		}
	}

	private void fixIcons(Class iconsClass, HashMap<String, Icon> replacements)
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

					if(byClass.getName().endsWith("$CachedImageIcon"))
					{
						Icon icon = findReplacement(ignored, replacements);
						if(icon != null)
						{
							try
							{
								setFinalStatic(subClass, icon);
								System.out.println("Added diversion of " + subClass.getName());
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}
				catch(IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}

		Class[] classes = iconsClass.getDeclaredClasses();

		for(Class cls : classes)
			fixIcons(cls, replacements);
	}

	private Icon findReplacement(Object object, HashMap<String, Icon> replacements)
	{
		try
		{
			Field pathField = object.getClass().getDeclaredField("myOriginalPath");

			pathField.setAccessible(true);

			Object path = pathField.get(object);

			if(path instanceof String)
			{
				pathField.setAccessible(false);
				return replacements.get(path);
			}

			pathField.setAccessible(false);
			return null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static void setFinalStatic(Class cls, String fieldName, Object newValue) throws Exception
	{
		Field[] fields = cls.getDeclaredFields();

		for(int i = 0; i < fields.length; i ++)
		{
			Field field = fields[i];

			if(field.getName().equals(fieldName))
			{
				setFinalStatic(field, newValue);
				return;
			}
		}
	}

	private static void setFinalStatic(Field field, Object newValue) throws Exception
	{
		field.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);

		modifiersField.setInt(field, field.getModifiers() | Modifier.FINAL);
		modifiersField.setAccessible(false);

		field.setAccessible(false);
	}
}
