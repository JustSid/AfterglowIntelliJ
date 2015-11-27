package com.widerwille.afterglow;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import com.intellij.ide.projectView.impl.ProjectViewPane;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class AfterglowIDEComponent implements ProjectComponent
{
	private Project project;

	public AfterglowIDEComponent(Project project)
	{
		this.project = project;
	}

	@Override
	public void initComponent()
	{}

	@Override
	public void disposeComponent()
	{}

	@NotNull
	public String getComponentName()
	{
		return "AfterglowIDEComponent";
	}

	@Override
	public void projectOpened()
	{
		fixProjectViewPane();
	}

	private void fixProjectViewPane()
	{
		Collection<String> paneIDs = ProjectView.getInstance(project).getPaneIds();
		if(paneIDs.size() == 0)
		{
			java.util.Timer timer = new java.util.Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run()
				{
					fixProjectViewPane();
				}
			}, 100);

			return;
		}

		for(String id : paneIDs)
			fixPane(id, 10);
	}

	private void fixPane(String paneID, int left)
	{
		if(left == 0)
			return;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				__fixPane(paneID, left);
			}
		});
	}

	private void __fixPane(String paneID, int left)
	{
		AbstractProjectViewPane projectViewPane = ProjectView.getInstance(project).getProjectViewPaneById(paneID);

		if(projectViewPane != null)
		{
			JTree tree = projectViewPane.getTree();
			if(tree == null)
			{
				java.util.Timer timer = new java.util.Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run()
					{
						fixPane(paneID, left - 1);
					}
				}, 500);

				return;
			}

			final EditorColorsScheme globalScheme = EditorColorsManager.getInstance().getGlobalScheme();

			tree.setBackground(new Color(32, 32, 32));
			tree.setForeground(new Color(160, 160, 160));
		}
	}

	@Override
	public void projectClosed()
	{}
}
