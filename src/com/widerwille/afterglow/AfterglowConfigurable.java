package com.widerwille.afterglow;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.Gray;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class AfterglowConfigurable implements Configurable
{
	private JComponent component = null;
	private ComboBox selectionBox;

	@Nullable
	@Override
	public JComponent createComponent()
	{
		if(component == null)
		{
			String[] options = {
					AfterglowTheme.getStringForTheme(AfterglowTheme.Theme.Default),
					AfterglowTheme.getStringForTheme(AfterglowTheme.Theme.Blue),
					AfterglowTheme.getStringForTheme(AfterglowTheme.Theme.Magenta),
					AfterglowTheme.getStringForTheme(AfterglowTheme.Theme.Orange),
					AfterglowTheme.getStringForTheme(AfterglowTheme.Theme.Green) };

			final JPanel comboPanel = new JPanel();

			JLabel label = new JLabel("Theme:");

			selectionBox= new ComboBox(options);
			selectionBox.setSelectedItem(AfterglowTheme.getStringForTheme(AfterglowTheme.getActiveTheme()));

			comboPanel.add(label);
			comboPanel.add(selectionBox);


			JLabel help = new JLabel("Changing may require reloading the project");
			help.setForeground(Gray._180);

			final JPanel container = new JPanel();
			container.setLayout(new BorderLayout());
			container.add(comboPanel, BorderLayout.LINE_START);
			container.add(help, BorderLayout.SOUTH);

			component = container;
		}

		return component;
	}

	@Override
	public void disposeUIResources()
	{
		component = null;
	}

	@Nls
	@Override
	public String getDisplayName()
	{
		return "Afterglow";
	}

	@Nullable
	@Override
	public String getHelpTopic()
	{
		return null;
	}

	@Override
	public boolean isModified()
	{
		return !(selectionBox.getSelectedItem().equals(AfterglowTheme.getStringForTheme(AfterglowTheme.getActiveTheme())));
	}

	@Override
	public void reset()
	{
		selectionBox.setSelectedItem(AfterglowTheme.getStringForTheme(AfterglowTheme.getActiveTheme()));
	}

	@Override
	public void apply() throws ConfigurationException
	{
		String theme = (String)selectionBox.getSelectedItem();
		AfterglowTheme.applyTheme(AfterglowTheme.getThemeForString(theme));
	}
}
