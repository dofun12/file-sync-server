package org.lemanoman.filesyncserver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lemanoman.filesyncserver.dto.FileOperationDto;
import org.lemanoman.filesyncserver.tasks.FileMoverTask;

import java.io.File;
import java.io.IOException;

public class FileUtilsTest {

    @Test
    void testSpeed() {
        File out = new File("out");
        if(out.exists()) {
            out.delete();
        }
        out.mkdir();
        long start = System.currentTimeMillis();
        File sourceFile = new File("D:\\Nova pasta\\Giantess 2 1080p.mp4");
        File outFileA = new File(out, "testA.mp4");
        File outFileB = new File(out, "testB.mp4");
        try {
            FileUtils.copyFileUsingChannel(sourceFile, outFileA);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();

        System.out.println(" File Using Channel Time taken: " + (end - start) + "ms");
        start = System.currentTimeMillis();
        try {
            FileUtils.copyFileUsingStream(sourceFile, outFileB);
        } catch (IOException e) {
            e.printStackTrace();
        }
        end = System.currentTimeMillis();
        System.out.println(" File Using Stream Time taken: " + (end - start) + "ms");
        outFileA.delete();
        outFileB.delete();
    }

    @Test
    void testOperations(){
        File sourceDir = new File("src\\test\\resources\\realTesteSource");
        File targetDir = new File("src\\test\\resources\\realTesteTarget2");

        if(targetDir.exists()) {
            targetDir.delete();
        }
        targetDir.mkdir();

        File sourceFileA = new File(sourceDir, "imagemA.png");
        File sourceFileB = new File(sourceDir, "imagemA.png");

        File targetFileA = new File(targetDir, "imagemA.png");
        File targetFileB = new File(targetDir, "imagemB.png");

        FileOperationDto fileOperation = new FileOperationDto(sourceFileA.getAbsolutePath(), targetFileA.getAbsolutePath(), "COPY");
        FileMoverTask fileMoverTask = new FileMoverTask(fileOperation);
        fileMoverTask.run();
        Assertions.assertTrue(targetFileA.exists());
        Assertions.assertTrue(FileUtils.isFileEquals(sourceFileA, targetFileA));

        fileOperation = new FileOperationDto(sourceFileB.getAbsolutePath(), targetFileB.getAbsolutePath(), "COPY");
        fileMoverTask = new FileMoverTask(fileOperation);
        fileMoverTask.run();
        Assertions.assertTrue(targetFileB.exists());
        Assertions.assertTrue(FileUtils.isFileEquals(sourceFileB, targetFileB));

        File sourceFileC = new File(sourceDir, "imagemB.png");
        fileOperation = new FileOperationDto(sourceFileC.getAbsolutePath(), targetFileB.getAbsolutePath(), "OVERWRITE");
        fileMoverTask = new FileMoverTask(fileOperation);
        fileMoverTask.run();
        Assertions.assertTrue(targetFileB.exists());
        Assertions.assertTrue(FileUtils.isFileEquals(sourceFileC, targetFileB));

        File sourceFolderA = new File(sourceDir, "diretorioA");
        File targetFolderA = new File(targetDir, "diretorioA");

        fileOperation = new FileOperationDto(sourceFolderA.getAbsolutePath(), targetFolderA.getAbsolutePath(), "MKDIR");
        fileMoverTask = new FileMoverTask(fileOperation);
        fileMoverTask.run();
        Assertions.assertTrue(targetFolderA.exists());


    }

}
