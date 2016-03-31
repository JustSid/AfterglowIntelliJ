package com.widerwille.afterglow;

import com.intellij.ide.FileIconProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AfterglowFileIconProvider implements FileIconProvider, DumbAware
{
	@Nullable
	public final Icon getIcon(@NotNull VirtualFile file, @Iconable.IconFlags int flags, @Nullable Project project)
	{
		AfterglowIconCache cache = ApplicationManager.getApplication().getComponent(AfterglowIconCache.class);
		return cache.getIcon(file, flags);
	}
}
