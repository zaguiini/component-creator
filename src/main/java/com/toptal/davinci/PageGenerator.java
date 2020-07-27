package com.toptal.davinci;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.Properties;

public class PageGenerator extends GeneratorBase {
    @Override
    protected String getCommand(Properties attributes) {
        String base = "yarn davinci-code page %s %s";
        return String.format(base, attributes.getProperty("elementName"), attributes.getProperty("moduleName"));
    }

    @Override
    protected boolean isValidName(String name) {
        return name != null && name.matches("^[A-Z][A-Za-z0-9]+$");
    }

    @Override
    protected boolean shouldEnableAction(VirtualFile path) {
        return path.getParent().getName().equals("modules");
    }

    @Override
    protected String resolveFileToOpen(Properties attributes) {
        return "pages/" + attributes.getProperty("elementName") + "/" + attributes.getProperty("elementName") + ".tsx";
    }
}
