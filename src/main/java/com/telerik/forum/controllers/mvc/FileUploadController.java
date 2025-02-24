package com.telerik.forum.controllers.mvc;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/uploads")
public class FileUploadController {

    private final String uploadDirectory;

    @Autowired
    public FileUploadController(Environment env) {
        this.uploadDirectory = env.getProperty("uploads.location", "uploads");
    }


    @PostMapping("/{userId}")
    public String handleFileUpload(@RequestParam("picture") MultipartFile picture, @PathVariable int userId, Model model, HttpServletRequest request) {
        try {
            File uploadDir = new File(uploadDirectory);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            Path filePath = Paths.get(uploadDirectory, "picture" + userId + ".jpg");
            picture.transferTo(filePath);

            return "redirect:" + request.getHeader("Referer");

        } catch (IOException e) {
            model.addAttribute("message", "File upload failed: " + e.getMessage());
            return request.getHeader("Referer");
        }
    }

    @ResponseBody
    @GetMapping("/{filename}")
    public Resource serveFile(@PathVariable String filename) {
        Path filePath = Paths.get(uploadDirectory).resolve(filename);
        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            return null;
        }
    }
}