package org.lemanoman;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    //Scan files recursively

    public static String bytesToHumanReadable(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp-1) + "i";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    public static List<File> listFiles(File someFile) {
        List<File> files = new ArrayList<>();
        if(someFile==null){
            return files;
        }

        if(!someFile.isDirectory()){
            files.add(someFile);
            return files;
        }
        final var list = someFile.listFiles();
        if(list==null){
            return files;
        }
        for(File f:list){
            if(f.isDirectory()){
                files.addAll(listFiles(f));
            }else{
                files.add(f);
            }
        }
        return files;
    }

    public interface OnFileFound{
        public void onFileFound(File file);
    }
    public static void scanFiles(File someFile, OnFileFound onFileFound) {
        if(someFile==null){
            return;
        }

        if(!someFile.isDirectory()){
            onFileFound.onFileFound(someFile);
            return;
        }
        final var list = someFile.listFiles();
        if(list==null){
            return;
        }
        for(File f:list){
            if(f.isDirectory()){
                scanFiles(f,onFileFound);
            }else{
                onFileFound.onFileFound(f);
            }
        }
    }
}

