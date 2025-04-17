package org.lemanoman.filesyncserver.controller;

import org.lemanoman.filesyncserver.dto.RequestPathKeyDto;
import org.lemanoman.filesyncserver.model.LocationModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DirectoryController {
    private String toBase64(String path) {
        return Base64.getEncoder().encodeToString(path.getBytes());
    }

    private String fromBase64(String base64Path) {
        return new String(Base64.getDecoder().decode(base64Path));
    }

    @GetMapping("/browse")
    public String browseDirectory(@RequestParam(value = "path", required = false) String base64Path, Model model) {
        model.addAttribute("hasParent", false);
        List<Map<String, String>> result = new ArrayList<>();
        try {
            if(base64Path == null || base64Path.isEmpty()) {
                base64Path = System.getProperty("user.dir");
                base64Path = toBase64(base64Path);
            }
            // Decode the base64 path
            String decodedPath = fromBase64(base64Path);
            model.addAttribute("selectedPath", decodedPath);
            model.addAttribute("selectedPathKey", base64Path);



            // Get the directory
            File directory = new File(decodedPath);
            if(directory.getParentFile() != null && directory.getParentFile().exists()) {
                model.addAttribute("hasParent",true);
                model.addAttribute("parentPathKey", toBase64(directory.getParentFile().getAbsolutePath()));
            }
            model.addAttribute("selectedFolder", directory.getName());
            if (directory.exists() && directory.isDirectory()) {
                // List files and directories
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        Map<String, String> fileInfo = new HashMap<>();
                        fileInfo.put("name", file.getName());
                        fileInfo.put("path", file.getAbsolutePath());
                        fileInfo.put("isdir", String.valueOf(file.isDirectory()));
                        fileInfo.put("pathkey", Base64.getEncoder().encodeToString(file.getAbsolutePath().getBytes()));
                        result.add(fileInfo);
                    }
                }
            } else {
                throw new IllegalArgumentException("Invalid directory path");
            }
            model.addAttribute("files", result);
            return "browse";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

    }
}