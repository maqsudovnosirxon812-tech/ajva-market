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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            String uploadDir = "./profile-photos/";

            Files.write(Paths.get(uploadDir + fileName), file.getBytes());
            user.setProfileImage("/profile-photos/" + fileName);
            userService.saveUser(user);
        }
        return "redirect:/profile";
    }

}
