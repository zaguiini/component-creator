package com.example;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.intellij.lang.ecmascript6.psi.ES6ExportDeclaration;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

public class ComponentCreator extends AnAction {
    static String ERROR_TITLE = "Failed to create Component";

    private String getPath(VirtualFile entry) {
        return entry.isDirectory() ? entry.getPath() : entry.getParent().getPath();
    }

    private String getComponentName(Project project) {
        return Messages.showInputDialog(project, "Input the name of component (e.g. MyComponent)",
                "Name of the Component", null);
    }

    private boolean isValidComponentName(String name) {
        // TODO: Have a better matcher
        return name != null && name.matches("^[A-Za-z]+$");
    }

    private void createFile(File file, String componentName, String template) throws IOException {
        MustacheFactory mustache = new DefaultMustacheFactory();
        HashMap<String, Object> scopes = new HashMap<>();
        scopes.put("componentName", componentName);

        Reader reader = new InputStreamReader(getClass().getResource("/templates/" + template).openStream());
        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        mustache.compile(reader, template).execute(writer, scopes);
        reader.close();
        writer.close();
    }

    private void addToFile(Project project, VirtualFile virtualFile, String componentName) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        FileDocumentManager.getInstance().saveDocument(PsiDocumentManager.getInstance(project).getDocument(psiFile));
        PsiElement[] children = psiFile.getChildren();

        int position = -1;

        for(int i = 0; i < children.length; i++) {
            PsiElement el = children[i];

            if (el instanceof ES6ExportDeclaration) {
                ES6ExportDeclaration export = (ES6ExportDeclaration) el;
                String existingExportName = export.getExportSpecifiers()[0].getDeclaredName();

                if(componentName.toLowerCase().compareTo(existingExportName.toLowerCase()) <= 0) {
                    position = i;
                    break;
                }
            }
        }
;
        int finalPosition = position;
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElement newExport = ExportDeclaration.create(project, componentName);

            if(finalPosition == -1) {
                psiFile.addAfter(newExport, children.length > 0 ? children[children.length - 1] : null);
            } else {
                psiFile.addBefore(newExport, children[finalPosition]);
            }
        });

        FileDocumentManager.getInstance().saveDocument(PsiDocumentManager.getInstance(project).getDocument(psiFile));
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        VirtualFile entry = event.getData(CommonDataKeys.VIRTUAL_FILE);

        if(entry == null) {
            return;
        }

        String path = getPath(entry);
        String componentName = getComponentName(project);

        if(!isValidComponentName(componentName)) {
            return;
        }

        Path componentFolder = Paths.get(path).resolve(Paths.get(componentName));
        File componentIndexFile = new File(componentFolder.resolve("index.ts").toString());
        String componentFilePath = componentFolder.resolve(componentName + ".tsx").toString();
        File componentFile = new File(componentFilePath);

        if(!componentIndexFile.getParentFile().mkdirs()) {
            Messages.showInfoMessage("Failed to create directory or it already exists", ComponentCreator.ERROR_TITLE);
            return;
        }

        try {
            componentIndexFile.createNewFile();
            createFile(componentIndexFile, componentName, "index");

            componentFile.createNewFile();
            createFile(componentFile, componentName, "component");
        } catch (IOException e) {
            e.printStackTrace();
            Messages.showInfoMessage("Failed to create files", ComponentCreator.ERROR_TITLE);
        }

        VirtualFile parentIndexTSFile = entry.isDirectory() ? entry.findFileByRelativePath("index.ts") : entry.findFileByRelativePath("../index.ts");
        VirtualFile parentIndexTSXFile = entry.isDirectory() ? entry.findFileByRelativePath("index.tsx") : entry.findFileByRelativePath("../index.tsx");

        if(parentIndexTSFile != null) {
            addToFile(project, parentIndexTSFile, componentName);
        } else if(parentIndexTSXFile != null) {
            addToFile(project, parentIndexTSXFile, componentName);
        } else {
            try {
                File file = new File(Paths.get(path).resolve("index.ts").toString());
                file.createNewFile();
                createFile(file, componentName, "parent-export");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileEditorManager.getInstance(project).openFile(LocalFileSystem.getInstance().refreshAndFindFileByIoFile(componentFile), true);
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        editor.getCaretModel().moveToVisualPosition(new VisualPosition(5, 11));
    }
}
