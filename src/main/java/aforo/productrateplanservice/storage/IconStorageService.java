package aforo.productrateplanservice.storage;

import org.springframework.web.multipart.MultipartFile;

public interface IconStorageService {
    String saveIcon(MultipartFile file, Long productId);
    void deleteByUrl(String url);
}
