package net.evercode.lawfirm.common.utils;

import net.evercode.lawfirm.service.ConfigService;
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
    private ConfigService configService;

    public String saveFile(byte[] fileBytes, String subDir, String fileContentType) {
        String fileExt = getFileExtension(fileContentType);
        String filename = UUID.randomUUID() + "." + fileExt;
        String backendPath = configService.findByKey("backend.content.folder.path").getValue();
        try {
            Files.write(Paths.get(backendPath, subDir, "/", filename), fileBytes);
        } catch (IOException e) {
            log.error("Error while saving file!", e);
        }
        return filename;
    }

    public boolean deleteFile(String fileName, String subDir) {
        boolean result = false;
        String backendPath = configService.findByKey("backend.content.folder.path").getValue();
        try {
            result = Files.deleteIfExists(Paths.get(backendPath, subDir, "/", fileName));
        } catch (IOException e) {
            log.error("Error while deleting file! Filename: {}", fileName, e);
        }
        return result;
    }

    public String updateFile(byte[] fileBytes, String fileContentType, String oldFileName, String subDir) {
        String fileExt = getFileExtension(fileContentType);
        boolean deleteResult = deleteFile(oldFileName, subDir);
        if (deleteResult) {
            return saveFile(fileBytes, subDir, fileExt);
        }
        return oldFileName;
    }

    /*
     * Returns the file extension of a given file content type.
     * @param fileContentType: the file content type. Example: "image/jpeg"
     * @return the file extension
     */
    private String getFileExtension(String fileContentType) {
        String extension = "";
        int i = fileContentType.lastIndexOf('/');
        if (i > 0) {
            extension = fileContentType.substring(i + 1);
        }
        return extension;
    }
}
