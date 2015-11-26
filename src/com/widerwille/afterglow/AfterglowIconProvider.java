package com.widerwille.afterglow;

import com.intellij.ide.IconProvider;
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

			if(vFile != null && vFile.isDirectory())
				return AfterglowIcons.DIRECTORY;

			return AfterglowIcons.getIcon(vFile, flags, null);
		}

		return AfterglowIcons.DIRECTORY;
	}
}
