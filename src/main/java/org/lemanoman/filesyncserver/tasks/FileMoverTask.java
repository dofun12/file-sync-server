package org.lemanoman.filesyncserver.tasks;

import org.lemanoman.filesyncserver.FileUtils;
import org.lemanoman.filesyncserver.dto.FileOperationDto;
import org.lemanoman.filesyncserver.interfaces.FileMoverCallback;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileMoverTask implements Runnable {
    private FileOperationDto fileOperation;
    private String id = "none";
    private FileMoverCallback callback;

    public FileMoverTask(FileOperationDto fileOperation) {
        this.fileOperation = fileOperation;
    }

    public FileMoverTask(String id, FileOperationDto fileOperation, FileMoverCallback callback) {
        this.callback = callback;
        this.fileOperation = fileOperation;
        this.id = id;
    }

    void doCallback(String status) {
        if (callback != null) {
            callback.onFinish(id, fileOperation, status);
        } else {
            System.out.println(status);
            System.out.println("Callback is null");
        }
    }

    @Override
    public void run() {
        callback.onStart(id, fileOperation);
        if (fileOperation == null) {
            doCallback("File operation is null");
            return;
        }

        String sourcePath = fileOperation.sourcePath();
        String targetPath = fileOperation.targetPath();
        File targetFile = new File(targetPath);
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            doCallback("Source file does not exist");
            return;
        }
        final var operation = fileOperation.operation();
        if ("MKDIR".equals(operation) && sourceFile.isDirectory()) {
            if(targetFile.mkdirs()){
                doCallback("Directory created");
                return;
            }
            doCallback("Error creating directory");
            return;
        }
        if (sourceFile.isDirectory()) {
            doCallback("Source file is a directory");
            return;
        }

        if ("COPY".equals(operation)) {
            try {
                FileUtils.copyFileUsingChannel(sourceFile, targetFile);
                doCallback("File copied");
                return;
            } catch (IOException e) {
                e.printStackTrace();
                doCallback("Error copying file");
                return;
            }
        }
        if ("DELETE".equals(operation)) {
            try {
                Files.deleteIfExists(sourceFile.toPath());
                doCallback("File deleted");
                return;
            } catch (IOException e) {
                e.printStackTrace();
                doCallback("Error deleting file");
            }
        }
        if ("OVERWRITE".equals(operation)) {
            try {
                Files.deleteIfExists(targetFile.toPath());
                FileUtils.copyFileUsingChannel(sourceFile, targetFile);
                doCallback("File overwritten");
            } catch (IOException e) {
                e.printStackTrace();
                doCallback("Error overwriting file");
            }
        }

    }
}
