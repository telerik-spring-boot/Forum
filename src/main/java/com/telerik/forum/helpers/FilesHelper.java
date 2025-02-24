package com.telerik.forum.helpers;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FilesHelper {

    public static String uploadDirectory;

    public FilesHelper(Environment env) {
        uploadDirectory = env.getProperty("uploads.location", "uploads");
    }

    public static void deleteUserPhoto(String fileName, String uploadDir) {
        Path filePath = Paths.get(uploadDir).resolve(fileName);

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    public static boolean checkIfPhotoExists(int userId) {
        String imagePath = uploadDirectory + "/picture" + userId + ".jpg";

        return Files.exists(Paths.get(imagePath));
    }
}
