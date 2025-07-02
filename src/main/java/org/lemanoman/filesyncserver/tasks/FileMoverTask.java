package org.lemanoman.filesyncserver.tasks;

import org.lemanoman.filesyncserver.FileUtils;
import org.lemanoman.filesyncserver.dto.FileOperationDto;
import org.lemanoman.filesyncserver.interfaces.FileMoverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileMoverTask implements Runnable {
    private FileOperationDto fileOperation;
    private String id = "none";
    private FileMoverCallback callback;
    final private Logger logger = LoggerFactory.getLogger(FileMoverTask.class);
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
            logger.warn("Callback is null for id: {}, fileOperation: {}", id, fileOperation);
        }
    }

    @Override
    public void run() {
        callback.onStart(id, fileOperation);
        if (fileOperation == null) {
            logger.error("File operation is null");
            doCallback("File operation is null");
            return;
        }

        String sourcePath = fileOperation.sourcePath();
        String targetPath = fileOperation.targetPath();
        File targetFile = new File(targetPath);
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            logger.error("Source file does not exist: {}", sourcePath);
            doCallback("Source file does not exist");
            return;
        }
        final var operation = fileOperation.operation();
        if ("SKIPPED".equals(operation)) {
            logger.info("Operation skipped for source: {}, target: {}", sourcePath, targetPath);
            doCallback("Operation skipped");
            return;
        }
        if ("MKDIR".equals(operation) && sourceFile.isDirectory()) {
            if(targetFile.mkdirs()){
                logger.info("Directory created: {}", targetPath);
                doCallback("Directory created");
                return;
            }
            logger.error("Error creating directory: {}", targetPath);
            doCallback("Error creating directory");
            return;
        }
        if (sourceFile.isDirectory()) {
            logger.error("Source file is a directory: {}", sourcePath);
            doCallback("Source file is a directory");
            return;
        }

        if ("COPY".equals(operation)) {
            try {
                FileUtils.copyFileUsingChannel(sourceFile, targetFile);
                logger.info("File copied from {} to {}", sourcePath, targetPath);
                doCallback("File copied");
                return;
            } catch (IOException e) {
                logger.error("Error copying file from {} to {}: {}", sourcePath, targetPath, e.getMessage());
                doCallback("Error copying file");
                return;
            }
        }
        if ("DELETE".equals(operation)) {
            try {
                Files.deleteIfExists(sourceFile.toPath());
                logger.info("File deleted: {}", sourcePath);
                doCallback("File deleted");
                return;
            } catch (IOException e) {
                logger.error("Error deleting file {}: {}", sourcePath, e.getMessage());
                doCallback("Error deleting file");
            }
        }
        if ("OVERWRITE".equals(operation)) {
            try {
                Files.deleteIfExists(targetFile.toPath());
                FileUtils.copyFileUsingChannel(sourceFile, targetFile);
                logger.info("File overwritten from {} to {}", sourcePath, targetPath);
                doCallback("File overwritten");
            } catch (IOException e) {
                logger.error("Error overwriting file {}: {}", sourcePath, e.getMessage());
                doCallback("Error overwriting file");
            }
        }

    }
}
