package com.zaguiini;

import com.intellij.lang.javascript.frameworks.react.JSXPropTypesUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

public class Helpers {
    static String ERROR_TITLE = "Failed to create Component";

    static VirtualFile getPath(VirtualFile entry) {
        return entry.isDirectory() ? entry : entry.getParent();
    }

    static String getComponentName() {
        return Messages.showInputDialog((Project) null, "Enter the desired name",
                "Name of the new item", null);
    }

    static boolean isValidComponentName(String name) {
        // TODO: Have a better matcher
        return name != null && JSXPropTypesUtil.isPossibleReactClassName(name);
    }

    static boolean hasExistingFolder(VirtualFile path, String componentName) {
        return path.findFileByRelativePath(componentName) != null;
    }
}
