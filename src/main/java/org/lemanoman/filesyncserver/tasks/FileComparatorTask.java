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
                    callback.onNextCompare(fileOperation);
                    continue;
                }
                FileOperationDto fileOperation = new FileOperationDto(sourceFile.getAbsolutePath(), targetFile.getAbsolutePath(), sourceFile.length(),"COPY");
                fileOperations.add(fileOperation);
                callback.onNextCompare(fileOperation);
                continue;
            }
            if(targetFile.isDirectory()) {
                continue;
            }
            final String hashSource = HashUtil.getPartialMD5(sourceFile).hash();
            final String targetSource = HashUtil.getPartialMD5(targetFile).hash();
            if(!hashSource.equals(targetSource)) {
                FileOperationDto fileOperation = new FileOperationDto(sourceFile.getAbsolutePath(), targetFile.getAbsolutePath(), sourceFile.length(), "OVERWRITE");
                fileOperations.add(fileOperation);
                callback.onNextCompare(fileOperation);
            }
            FileOperationDto fileOperation = new FileOperationDto(sourceFile.getAbsolutePath(), targetFile.getAbsolutePath(), sourceFile.length(), "SKIP");
            fileOperations.add(fileOperation);
            callback.onNextCompare(fileOperation);

        }
        callback.onFinish(fileOperations);

    }
}
