package com.toptal.davinci;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Function;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;
import org.jetbrains.plugins.terminal.TerminalView;

public abstract class GeneratorBase extends AnAction {
    protected abstract String getCommand(Properties attributes);
    protected abstract boolean isValidName(String desiredName);
    protected abstract boolean shouldEnableAction(VirtualFile path);
    protected abstract String resolveFileToOpen(Properties attributes);

    private void runCommand(Project project, String elementName, String command, Function onSessionClose) throws IOException {
        TerminalView terminal = TerminalView.getInstance(project);

        ShellTerminalWidget shell = terminal.createLocalShellWidget(project.getBasePath(), String.format("Davinci - %s", elementName));
        shell.executeCommand(command + "; exit");

        shell.addListener(onSessionClose::apply);
    }

    private void openFile(Project project, VirtualFile fileToOpen) {
        FileEditorManager.getInstance(project).openFile(fileToOpen, true);
    }

    @Override
    public void update(AnActionEvent event) {
        event.getPresentation().setVisible(false);
        event.getPresentation().setEnabled(false);
        VirtualFile path = event.getData(CommonDataKeys.VIRTUAL_FILE);

        if(this.shouldEnableAction(path)) {
            event.getPresentation().setVisible(true);
            event.getPresentation().setEnabled(true);
        }
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        VirtualFile path = event.getData(CommonDataKeys.VIRTUAL_FILE);

        if(path == null || !this.shouldEnableAction(path)) {
            return;
        }

        String elementName = Helpers.getElementName();

        if(!this.isValidName(elementName)) {
            return;
        }

        if(Helpers.hasExistingFolder(path, elementName)) {
            Messages.showInfoMessage("Directory already exists", Helpers.ERROR_TITLE);
            return;
        }

        Properties attributes = new Properties();
        attributes.put("moduleName", path.getName());
        attributes.put("elementName", elementName);

        try {
            this.runCommand(project, elementName, this.getCommand(attributes), (terminal) -> {
                path.refresh(true, true, () -> {
                    this.openFile(project, path.findFileByRelativePath(this.resolveFileToOpen(attributes)));
                });

                return null;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
