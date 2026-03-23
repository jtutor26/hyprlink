package com.basecamp.HyprLink.controller;

import com.basecamp.HyprLink.entity.SocialLink;
import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.repository.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.Principal;
import java.util.List;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import java.io.IOException;

@Controller
public class DashboardController {

    private static final String STATIC_BG_DIR = "src/main/resources/static/images/background-templates";
    private static final String PROJECT_BG_DIR = "src/main/resources/background templates";

    private final UserRepository userRepository;

    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Principal principal, Model model) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        // Add a blank link slot at the end so the user always has room to add a new one
        if (user != null) {
            user.getSocialLinks().add(new SocialLink());
        }


        addDashboardModelData(model, user);
        return "dashboard";
    }


    @PostMapping("/dashboard/save")
    public String saveProfile(@ModelAttribute("user") User updatedData, Principal principal,
                              @RequestParam(value = "profilePictureFile", required = false) MultipartFile profilePictureFile,
                              @RequestParam(value = "backgroundFile", required = false) MultipartFile backgroundFile) throws IOException {

        User existingUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
            String filename = saveUploadFile(profilePictureFile, principal.getName() + "_");
            existingUser.setProfilePicture("/images/uploads/" + filename);
        } else if (updatedData.getProfilePicture() != null && !updatedData.getProfilePicture().isEmpty()) {
            existingUser.setProfilePicture(updatedData.getProfilePicture());
        }

        if (backgroundFile != null && !backgroundFile.isEmpty()) {
            String filename = saveUploadFile(backgroundFile, principal.getName() + "_bg_");
            existingUser.setBackgroundImage("/images/" + filename);
        } else if (updatedData.getBackgroundImage() != null && !updatedData.getBackgroundImage().isEmpty()) {
            existingUser.setBackgroundImage(updatedData.getBackgroundImage());
        }

        // Update basic fields
        existingUser.setName(updatedData.getName());
        existingUser.setAge(updatedData.getAge());
        existingUser.setPronouns(updatedData.getPronouns());
        existingUser.setBio(updatedData.getBio());
        existingUser.setTheme(updatedData.getTheme());
        existingUser.setLinkStyle(updatedData.getLinkStyle());
        existingUser.setTextAlign(updatedData.getTextAlign());
        existingUser.setButtonColor(updatedData.getButtonColor());
        existingUser.setFontFamily(updatedData.getFontFamily());

        // Keep only links that have both a title and a URL
        if (updatedData.getSocialLinks() != null) {
            List<SocialLink> validLinks = new ArrayList<>();
            for (SocialLink link : updatedData.getSocialLinks()) {
                if (link.getTitle() != null && !link.getTitle().trim().isEmpty()
                        && link.getUrl() != null && !link.getUrl().trim().isEmpty()) {
                    validLinks.add(link);
                }
            }

            existingUser.getSocialLinks().clear();
            existingUser.getSocialLinks().addAll(validLinks);
        }

        userRepository.save(existingUser);
        return "redirect:/dashboard?success";
    }

    @PostMapping("/dashboard/clear-background")
    public String clearBackground(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        user.setBackgroundImage(null);
        userRepository.save(user);
        return "redirect:/dashboard?success";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleUploadTooLarge() {
        return "redirect:/dashboard?uploadTooLarge";
    }

    @GetMapping("/images/background-templates/{filename:.+}")
    public ResponseEntity<Resource> backgroundTemplate(@PathVariable String filename) throws MalformedURLException {
        String safeFilename = Paths.get(filename).getFileName().toString();
        Path path = resolveBackgroundPath(safeFilename);
        if (path == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(path.toUri());
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    private List<String> loadBackgrounds() {
        Set<String> backgrounds = new LinkedHashSet<>();
        addBackgroundsFromDirectory(backgrounds, PROJECT_BG_DIR);
        addBackgroundsFromDirectory(backgrounds, STATIC_BG_DIR);
        List<String> sorted = new ArrayList<>(backgrounds);
        sorted.sort((a, b) -> {
            try {
                int numA = Integer.parseInt(a.replaceAll("\\.[^.]+$", ""));
                int numB = Integer.parseInt(b.replaceAll("\\.[^.]+$", ""));
                return Integer.compare(numA, numB);
            } catch (NumberFormatException e) {
                return a.compareTo(b);
            }
        });
        return sorted;
    }

    private void addBackgroundsFromDirectory(Set<String> backgrounds, String directoryPath) {
        File folder = new File(directoryPath);
        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isFile() && hasSupportedImageExtension(file.getName())) {
                backgrounds.add(file.getName());
            }
        }
    }

    private Path resolveBackgroundPath(String filename) {
        Path projectPath = Paths.get(PROJECT_BG_DIR, filename);
        if (projectPath.toFile().isFile()) {
            return projectPath;
        }

        Path staticPath = Paths.get(STATIC_BG_DIR, filename);
        if (staticPath.toFile().isFile()) {
            return staticPath;
        }

        return null;
    }

    private boolean hasSupportedImageExtension(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp");
    }

    private void addDashboardModelData(Model model, User user) {
        model.addAttribute("user", user);
        model.addAttribute("themes", java.util.Arrays.asList("default", "dark"));
        model.addAttribute("linkStyles", java.util.Arrays.asList("pill", "box", "underline"));
        model.addAttribute("textAlignments", java.util.Arrays.asList("center", "left"));
        model.addAttribute("buttonColors", java.util.Arrays.asList("blue", "green", "red", "purple", "orange"));
        model.addAttribute("fontFamilies", java.util.Arrays.asList("System", "Georgia", "Courier", "Arial"));
        model.addAttribute("backgrounds", loadBackgrounds());
    }

    private String saveUploadFile(MultipartFile file, String filenamePrefix) throws IOException {
        String uploadDir = "src/main/resources/static/images/";

        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        String originalName = file.getOriginalFilename() == null ? "image" : Paths.get(file.getOriginalFilename()).getFileName().toString();
        String filename = filenamePrefix + System.currentTimeMillis() + "_" + originalName;
        String filepath = uploadDir + filename;

        file.transferTo(new File(filepath));
        return filename;
    }
}