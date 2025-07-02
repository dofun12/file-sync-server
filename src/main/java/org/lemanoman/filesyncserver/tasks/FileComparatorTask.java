package org.lemanoman.filesyncserver.tasks;

import org.lemanoman.filesyncserver.FileUtils;
import org.lemanoman.filesyncserver.HashUtil;
import org.lemanoman.filesyncserver.dto.FileOperationDto;
import org.lemanoman.filesyncserver.interfaces.FileComparatorCallback;
import org.slf4j.Logger;

import java.io.File;
import java.util.*;

public class FileComparatorTask implements Runnable{
    Logger logger = org.slf4j.LoggerFactory.getLogger(FileComparatorTask.class);
    final String sourcePath;
    final String targetPath;
    final FileComparatorCallback callback;
    Long id = null;

    public FileComparatorTask(Long id, String sourcePath, String targetPath, FileComparatorCallback callback) {
        this.id = id;
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.callback = callback;
    }

    public FileComparatorTask(String sourcePath, String targetPath, FileComparatorCallback callback) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.callback = callback;
    }

    @Override
    public void run() {
        if(callback == null) {
            return;
        }

        if(sourcePath == null || targetPath == null) {
            callback.onStart(id, 0, 0.0);
            callback.onFinish(null);
            return;
        }
        File source = new File(sourcePath);
        List<FileOperationDto> fileOperations = new ArrayList<>();

        File target = new File(targetPath);
        List<String> potencialFiles = new ArrayList<>();
        var allFiles = FileUtils.listFiles(source);
        var allDirs = FileUtils.listAllSubDir(source);
        allFiles.addAll(allDirs);
        Double sumTotal = 0.0;
        for(File file : allFiles) {
            if(file.isDirectory()) {
                sumTotal+=10d; // 10 bytes for directory
                continue;
            }
            sumTotal += file.length();
        }
        callback.onStart(id, allFiles.size(), sumTotal);
        allFiles.sort(Comparator.comparing(File::getAbsolutePath));
        for(File sourceFile : allFiles) {
            String sourcePath = source.getAbsolutePath();
            sourcePath = sourcePath.replaceAll("\\\\", "\\\\\\\\");
            String newFilename = sourceFile.getAbsolutePath();
            newFilename = newFilename.replaceAll(sourcePath, "@");
            potencialFiles.add(newFilename);
            File targetFile = new File(newFilename.replaceAll("@", target.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\")));
            if(!targetFile.exists()) {
                if(sourceFile.isDirectory()) {
                    FileOperationDto fileOperation = new FileOperationDto(sourceFile.getAbsolutePath(), targetFile.getAbsolutePath(), 10L,"MKDIR");
                    fileOperations.add(fileOperation);
                    logger.info("Directory created: {}", targetFile.getAbsolutePath());
                    callback.onNextCompare(fileOperation);
                    continue;
                }
                FileOperationDto fileOperation = new FileOperationDto(sourceFile.getAbsolutePath(), targetFile.getAbsolutePath(), sourceFile.length(),"COPY");
                fileOperations.add(fileOperation);
                logger.info("File copied: {}", targetFile.getAbsolutePath());
                callback.onNextCompare(fileOperation);
                continue;
            }
            if(targetFile.isDirectory()) {
                logger.warn("Target file is a directory, skipping: {}", targetFile.getAbsolutePath());
                continue;
            }
            final var resultSource = HashUtil.getPartialMD5(sourceFile);
            if(resultSource==null || resultSource.hash()==null) {
                logger.warn("Hash of source is on error: {}", targetFile.getAbsolutePath());
                FileOperationDto fileOperation = new FileOperationDto(sourceFile.getAbsolutePath(), targetFile.getAbsolutePath(), sourceFile.length(), "ERROR");
                fileOperations.add(fileOperation);
                callback.onNextCompare(fileOperation);
                continue;
            }
            final String hashSource = HashUtil.getPartialMD5(sourceFile).hash();
            final var resultTarget = HashUtil.getPartialMD5(targetFile);
            if(resultTarget==null|| resultTarget.hash()==null) {
                logger.warn("Hash of target is on error {}", targetFile.getAbsolutePath());
                FileOperationDto fileOperation = new FileOperationDto(sourceFile.getAbsolutePath(), targetFile.getAbsolutePath(), sourceFile.length(), "ERROR");
                fileOperations.add(fileOperation);
                callback.onNextCompare(fileOperation);
                continue;
            }
            final String targetSource = resultTarget.hash();
            if(!hashSource.equals(targetSource)) {
                FileOperationDto fileOperation = new FileOperationDto(sourceFile.getAbsolutePath(), targetFile.getAbsolutePath(), sourceFile.length(), "OVERWRITE");
                fileOperations.add(fileOperation);
                logger.info("File overwritten: {}", targetFile.getAbsolutePath());
                callback.onNextCompare(fileOperation);
            }
            FileOperationDto fileOperation = new FileOperationDto(sourceFile.getAbsolutePath(), targetFile.getAbsolutePath(), sourceFile.length(), "SKIPPED");
            fileOperations.add(fileOperation);
            logger.info("File skipped: {}", targetFile.getAbsolutePath());
            callback.onNextCompare(fileOperation);

        }
        logger.info("Files processed: {}", potencialFiles.size());
        callback.onFinish(fileOperations);

    }
}
