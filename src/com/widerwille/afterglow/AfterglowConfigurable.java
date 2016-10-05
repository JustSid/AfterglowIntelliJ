package com.widerwille.afterglow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.Gray;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AfterglowConfigurable implements Configurable
{
	private JComponent component = null;
	private ComboBox selectionBox;
	private ArrayList<AfterglowTheme.Tint> tints = null;

	@Nullable
	@Override
	public JComponent createComponent()
	{
		if(component == null)
		{
			AfterglowComponent afterglow = ApplicationManager.getApplication().getComponent(AfterglowComponent.class);

			tints = AfterglowThemeManager.getTints();

			ArrayList<String> options = new ArrayList<>();

			for(AfterglowTheme.Tint tint : tints)
				options.add(tint.getName());


			final JPanel comboPanel = new JPanel();

			JLabel label = new JLabel("Theme:");

			selectionBox= new ComboBox(options.toArray());
			selectionBox.setSelectedItem(afterglow.getActiveTheme().getName());

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
		AfterglowComponent afterglow = ApplicationManager.getApplication().getComponent(AfterglowComponent.class);
		return !(selectionBox.getSelectedItem().equals(afterglow.getActiveTheme().getName()));
	}

	@Override
	public void reset()
	{
		AfterglowComponent afterglow = ApplicationManager.getApplication().getComponent(AfterglowComponent.class);
		selectionBox.setSelectedItem(afterglow.getActiveTheme().getName());
	}

	@Override
	public void apply() throws ConfigurationException
	{
		AfterglowComponent afterglow = ApplicationManager.getApplication().getComponent(AfterglowComponent.class);

		int index = selectionBox.getSelectedIndex();
		afterglow.applyTheme(tints.get(index));
	}
}
