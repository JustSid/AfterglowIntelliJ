package com.widerwille.afterglow;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.packageDependencies.ui.PackageDependenciesNode;
import com.intellij.ui.ColoredTreeCellRenderer;

import javax.swing.*;


public class AfterglowProjectViewNodeDecorator implements ProjectViewNodeDecorator
{
	@Override
	public void decorate(ProjectViewNode node, PresentationData data)
	{
		AfterglowIconCache cache = ApplicationManager.getApplication().getComponent(AfterglowIconCache.class);
		Icon icon = cache.getIcon(node.getVirtualFile(), 0);

		if(icon != null)
			data.setIcon(icon);
	}

	@Override
	public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer cellRenderer)
	{}
}
