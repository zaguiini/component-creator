package com.zaguiini.davinci;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

public class Helpers {
    static String ERROR_TITLE = "Failed to create element";

    static VirtualFile getPath(VirtualFile entry) {
        return entry.isDirectory() ? entry : entry.getParent();
    }

    static String getElementName() {
        return Messages.showInputDialog((Project) null, "Enter the desired name",
                "Name of the new item", null);
    }

    static boolean hasExistingFolder(VirtualFile path, String componentName) {
        return path.findFileByRelativePath(componentName) != null;
    }
}
