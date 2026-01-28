package Ajva.market.controller;

import Ajva.market.entity.User;
import Ajva.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/updateImage")
    public String updateProfileImage(@RequestParam("profileImageFile") MultipartFile file,
                                     Principal principal) throws IOException {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);

        if (!file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            String srcPathStr = "src/main/resources/static/images/profile/";
            Path srcPath = Paths.get(srcPathStr);

            String targetPathStr = "target/classes/static/images/profile/";
            Path targetPath = Paths.get(targetPathStr);

            if (!Files.exists(srcPath)) Files.createDirectories(srcPath);
            if (!Files.exists(targetPath)) Files.createDirectories(targetPath);

            byte[] bytes = file.getBytes();
            Files.write(srcPath.resolve(fileName), bytes);
            Files.write(targetPath.resolve(fileName), bytes);

            user.setProfileImage("/images/profile/" + fileName);
            userService.saveUser(user);
        }
        return "redirect:/profile";
    }

}
