package com.widerwille.afterglow;

import com.intellij.ide.IconProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AfterglowIconProvider extends IconProvider implements DumbAware
{
	@Nullable
	public final Icon getIcon(@NotNull PsiElement element, int flags)
	{
		PsiFile containingFile = element.getContainingFile();

		if(containingFile != null)
		{
			VirtualFile vFile = containingFile.getVirtualFile();
			if(vFile == null)
				return AfterglowIcons.DIRECTORY;

			AfterglowIconCache cache = ApplicationManager.getApplication().getComponent(AfterglowIconCache.class);
			return cache.getIcon(vFile, flags);
		}

		return null;
	}
}
