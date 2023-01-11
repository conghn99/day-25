package com.example.day25.service;

import com.example.day25.exception.BadRequestException;
import com.example.day25.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class FileService {
    private Path rootPath = Paths.get("uploads");

    public FileService() {
        createFolder(rootPath.toString());
    }

    private void createFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public String uploadFile(int id, MultipartFile file) {
        // Tạo folder vs userID
        Path userPath = rootPath.resolve(String.valueOf(id));
        createFolder(userPath.toString());

        // Validate file
        validateFile(file);

        // Upload vào trong folder
        String fileId = String.valueOf(Instant.now().getEpochSecond());
        Path filePath = userPath.resolve(fileId);
        File targetFile = filePath.toFile();
        try (OutputStream os = new FileOutputStream(targetFile)) {
            os.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Lỗi trong quá trình up file");
        }
        return "/api/v1/users/" + id + "/files/" + fileId;
    }

    private void validateFile(MultipartFile file) {
        // Kiểm tra tên file
        String fileName = file.getOriginalFilename();
        if(fileName == null || fileName.isEmpty()) {
            throw new BadRequestException("File ko được để trống");
        }

        // Kiểm tra đuôi file
        String fileExtension = getFileExtension(fileName);
        if(!checkFileExtension(fileExtension)) {
            throw new BadRequestException("File ko đúng định dạng");
        }

        // Kiểm tra dung lượng file (<= 2MB)
        double fileSize = (double) (file.getSize() / 1048576);
        if (fileSize > 2) {
            throw new BadRequestException("File ko được vượt quá 2MB");
        }
    }

    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        return fileName.substring(lastIndexOf + 1);
    }

    private boolean checkFileExtension(String fileExtension) {
        List<String> extensions = new ArrayList<>(List.of("png", "jpg", "jpeg"));
        return extensions.contains(fileExtension);
    }

    public byte[] readFile(int id, String fileId) {
        Path userPath = rootPath.resolve(String.valueOf(id));
        Path filePath = userPath.resolve(fileId);
        if(!userPath.toFile().exists() || !filePath.toFile().exists()) {
            throw new NotFoundException("File ko tồn tại");
        }
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi trong quá trình đọc file");
        }
    }

    public List<String> getFile(int id) {
        Path userPath = rootPath.resolve(String.valueOf(id));
        if(!userPath.toFile().exists()) {
            return new ArrayList<>();
        }

        File userDir = userPath.toFile();
        File[] files = userDir.listFiles();

        return Arrays.stream(files)
                .map(File::getName)
                .sorted(Comparator.reverseOrder())
                .map(fileName -> "/api/v1/users/" + id + "/files/" + fileName)
                .toList();
    }

    public void deleteFile(int id, String fileId) {
        Path userPath = rootPath.resolve(String.valueOf(id));
        Path filePath = userPath.resolve(fileId);
        if(!userPath.toFile().exists() || !filePath.toFile().exists()) {
            throw new NotFoundException("File ko tồn tại");
        }

        if(!filePath.toFile().delete()) {
            throw new RuntimeException("Lỗi khi xóa file");
        }
    }
}
