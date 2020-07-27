package com.zaguiini.davinci;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.Properties;

public class ModuleGenerator extends GeneratorBase {
    @Override
    protected String getCommand(Properties attributes) {
        String base = "yarn davinci-code module %s";
        return String.format(base, attributes.getProperty("elementName"));
    }

    @Override
    protected boolean isValidName(String name) {
        return name != null && name.matches("^[a-z0-9]+[\\-\\_a-z0-9]*[a-z0-9]+$");
    }

    @Override
    protected boolean shouldEnableAction(VirtualFile path) {
        return path.getName().equals("modules");
    }

    @Override
    protected String resolveFileToOpen(Properties attributes) {
        return "index.ts";
    }
}
