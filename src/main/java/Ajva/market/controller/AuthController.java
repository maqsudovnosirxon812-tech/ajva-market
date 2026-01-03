package Ajva.market.controller;

import Ajva.market.entity.User;
import Ajva.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute("user") User user,
                                 @RequestParam("profileImageFile") MultipartFile profileImageFile,
                                 Model model) throws IOException {

        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Bunday foydalanuvchi allaqachon mavjud!");
            return "register";
        }

        // Parolni shifrlash
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");

        // Profil rasmni saqlash
        if (!profileImageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + profileImageFile.getOriginalFilename();
            String uploadDir = "/Users/macbookpro/Desktop/uploads/profile/";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) uploadPath.mkdirs();
            profileImageFile.transferTo(new File(uploadDir + fileName));
            user.setProfileImage("/profile/" + fileName);
        } else {
            // Default image
            user.setProfileImage("/images/default-profile.png");
        }


        userService.saveUser(user);
        return "redirect:/login";
    }

}
