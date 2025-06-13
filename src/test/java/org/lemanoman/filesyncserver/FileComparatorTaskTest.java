package org.lemanoman.filesyncserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lemanoman.filesyncserver.dto.FileOperationDto;
import org.lemanoman.filesyncserver.interfaces.FileComparatorCallback;
import org.lemanoman.filesyncserver.tasks.FileComparatorTask;

import java.io.File;
import java.util.List;

public class FileComparatorTaskTest {
    File sourceDir;
    File targetDir;
    File realSourceDir;
    File realTargetDir;

    @BeforeEach
    void setUp() {
        sourceDir = new File("src\\test\\resources\\testeSource");
        realSourceDir = new File("src\\test\\resources\\realTesteSource");
        realTargetDir = new File("src\\test\\resources\\realTesteTarget");
        if(sourceDir.exists()) {
            sourceDir.delete();
        }
        sourceDir.mkdir();
        for(int i = 0; i < 10; i++) {
            File subfolder = new File(sourceDir, "folder" + i);
            subfolder.mkdir();
            for(int j = 0; j < 10; j++) {
                File file = new File(subfolder, "file" + j);
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        targetDir = new File("src\\test\\resources\\testeTarget");
        if(targetDir.exists()) {
            targetDir.delete();
        }
        targetDir.mkdir();
    }

    @AfterEach
    void tearDown() {
        if(sourceDir.exists()) {
            sourceDir.delete();
        }
        if(targetDir.exists()) {
            targetDir.delete();
        }
    }

    @Test
    void testDefault() {
        var callback = new FileComparatorCallback() {
            @Override
            public void onNextCompare(FileOperationDto fileOperation) {

            }

            @Override
            public void onFinish(List<FileOperationDto> fileOperations) {
                for (FileOperationDto fileOperation : fileOperations) {
                    System.out.println(fileOperation.sourcePath()+" -> " + fileOperation.targetPath() + " : " + fileOperation.operation());
                }
                Assertions.assertEquals(110, fileOperations.size());
            }
        };
        FileComparatorTask task = new FileComparatorTask(sourceDir.getAbsolutePath(), targetDir.getAbsolutePath(), callback);
        task.run();

    }


    @Test
    void testReal() {
        var callback = new FileComparatorCallback() {
            @Override
            public void onNextCompare(FileOperationDto fileOperation) {

            }

            @Override
            public void onFinish(List<FileOperationDto> fileOperations) {
                int overwrites = 0;
                int copies = 0;
                for (FileOperationDto fileOperation : fileOperations) {
                    if("COPY".equals(fileOperation.operation())) {
                        copies++;
                    }
                    if("OVERWRITE".equals(fileOperation.operation())) {
                        overwrites++;
                    }
                    System.out.println(fileOperation.sourcePath()+" -> " + fileOperation.targetPath() + " : " + fileOperation.operation());
                }
                Assertions.assertEquals(1, overwrites);
                Assertions.assertEquals(2, copies);

            }
        };
        FileComparatorTask task = new FileComparatorTask(realSourceDir.getAbsolutePath(), realTargetDir.getAbsolutePath(), callback);
        task.run();

    }

}