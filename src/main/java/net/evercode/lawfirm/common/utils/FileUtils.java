package net.evercode.lawfirm.common.utils;

import net.evercode.lawfirm.service.ConfigsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUtils {

    private final Logger log = LoggerFactory.getLogger(FileUtils.class);

    @Autowired
    private ConfigsService configsService;

    public String saveFile(byte[] fileBytes, String subDir, String fileExt) {
        String filename = UUID.randomUUID() + "." + fileExt;
        String backendPath = configsService.findTopByKey("backend.content.folder.path").getValue();
        try {
            Files.write(Paths.get(backendPath, subDir, "/", filename), fileBytes);
        } catch (IOException e) {
            log.error("Error while saving file!", e);
        }
        return filename;
    }

    public boolean deleteFile(String fileName, String subDir) {
        boolean result = false;
        String backendPath = configsService.findTopByKey("backend.content.folder.path").getValue();
        try {
            result = Files.deleteIfExists(Paths.get(backendPath, subDir, "/", fileName));
        } catch (IOException e) {
            log.error("Error while deleting file! Filename: {}", fileName, e);
        }
        return result;
    }

    public String updateFile(byte[] fileBytes, String oldFileName, String subDir, String fileExt) {
        boolean deleteResult = deleteFile(oldFileName, subDir);
        if (deleteResult) {
            return saveFile(fileBytes, subDir, fileExt);
        }
        return oldFileName;
    }
}
