package aforo.productrateplanservice.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.UUID;

@Service
public class IconStorageServiceImpl implements IconStorageService {

    private final Path rootDir;
    private final Path iconsDir;

    public IconStorageServiceImpl(@Value("${uploads.dir:uploads}") String uploadsDir) {
        this.rootDir = Path.of(uploadsDir);
        this.iconsDir = this.rootDir.resolve("icons");
        try {
            Files.createDirectories(this.iconsDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create uploads directory", e);
        }
    }

    @Override
    public String saveIcon(MultipartFile file, Long productId) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            ext = original.substring(dot);
        } else {
            // infer from content-type
            String ct = file.getContentType();
            if (ct != null) {
                if (ct.equalsIgnoreCase("image/png")) ext = ".png";
                else if (ct.equalsIgnoreCase("image/jpeg")) ext = ".jpg";
                else if (ct.equalsIgnoreCase("image/gif")) ext = ".gif";
            }
        }
        String filename = "product-" + productId + "-" + UUID.randomUUID() + "-" + Instant.now().toEpochMilli() + ext;
        Path target = iconsDir.resolve(filename);
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store icon", e);
        }
        // URL exposed via WebConfig resource handler
        return "/uploads/icons/" + filename;
    }

    @Override
    public void deleteByUrl(String url) {
        if (url == null) return;
        String prefix = "/uploads/";
        if (!url.startsWith(prefix)) return; // not managed by this storage
        String relative = url.substring(prefix.length());
        Path path = rootDir.resolve(relative.replace("/", java.io.File.separator));
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // log and ignore
        }
    }
}
