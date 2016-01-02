package com.widerwille.afterglow;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class AfterglowTheme implements ApplicationComponent
{
	public enum Theme
	{
		Default,
		Blue,
		Magenta,
		Orange,
		Green
	}

	private static final Icon ExpandedIcon = IconLoader.getIcon("/icons/folder-open.png");
	private static final Icon CollapsedIcon = IconLoader.getIcon("/icons/folder-closed.png");
	private static Theme activeTheme;

	private static final String THEME_KEY = "com.widerwille.Afterglow.theme";


	public void initComponent()
	{
		Color selectionColor = new Color(54, 54, 54);
		Color backgroundColor = new Color(32, 32, 32);
		Color textColor = new Color(160, 160, 160);

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
			UIManager.put("Tree.collapsedIcon", CollapsedIcon);
			UIManager.put("Tree.expandedIcon", ExpandedIcon);

			setFinalStatic(AllIcons.Mac.class, "Tree_white_right_arrow", CollapsedIcon);
			setFinalStatic(AllIcons.Mac.class, "Tree_white_down_arrow", ExpandedIcon);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}


		PropertiesComponent component = PropertiesComponent.getInstance();
		String theme = component.getValue(THEME_KEY, "Default");
		applyTheme(getThemeForString(theme));
	}

	public void disposeComponent()
	{}

	@NotNull
	public String getComponentName()
	{
		return "AfterglowTheme";
	}

	public static void applyTheme(Theme theme)
	{
		Color directoryColor;

		switch(theme)
		{
			case Blue:
				directoryColor = new Color(108, 153, 187);
				break;
			case Magenta:
				directoryColor = new Color(128, 67, 93);
				break;
			case Orange:
				directoryColor = new Color(229, 181, 103);
				break;
			case Green:
				directoryColor = new Color(124, 144, 68);
				break;

			case Default:
			default:
				directoryColor = Color.WHITE;
				break;
		}

		AfterglowIcons.applyDirectoryTint(directoryColor);
		activeTheme = theme;

		PropertiesComponent component = PropertiesComponent.getInstance();
		component.setValue(THEME_KEY, getStringForTheme(activeTheme));
	}

	public static Theme getActiveTheme()
	{
		return activeTheme;
	}



	public static String getStringForTheme(Theme theme)
	{
		switch(theme)
		{
			case Blue:
				return "Blue";
			case Magenta:
				return "Magenta";
			case Orange:
				return "Orange";
			case Green:
				return "Green";

			case Default:
			default:
				return "Default";
		}
	}
	public static Theme getThemeForString(String string)
	{
		if(string.equals("Blue"))
			return Theme.Blue;
		else if(string.equals("Magenta"))
			return Theme.Magenta;
		else if(string.equals("Orange"))
			return Theme.Orange;
		else if(string.equals("Green"))
			return Theme.Green;

		return Theme.Default;
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
	}
}
