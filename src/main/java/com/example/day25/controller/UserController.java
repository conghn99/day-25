package com.example.day25.controller;

import com.example.day25.entity.Pages;
import com.example.day25.entity.User;
import com.example.day25.entity.UserDTO;
import com.example.day25.request.CreateUserRequest;
import com.example.day25.request.UpdateAvatarRequest;
import com.example.day25.request.UpdatePasswordRequest;
import com.example.day25.request.UpdateUserRequest;
import com.example.day25.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class UserController {
    @Autowired
    private UserService userService;

    // 1. Lấy danh sách users (có phân trang - pagination)
    @GetMapping("users")
    public Pages geUserList(@RequestParam (required = false, defaultValue = "1") int page, @RequestParam (required = false, defaultValue = "10") int limit) {
        return null;
    }

    // 1.1 Lấy danh sách users
    @GetMapping("user")
    public List<UserDTO> geUserLists() {
        return userService.getUserList();
    }

    // 2. Tìm kiếm user theo tên
    @GetMapping("search")
    public List<UserDTO> getUserByName(@RequestParam String name) {
        return userService.getUserByName(name);
    }

    // 3. Lấy chi tiết user theo id
    @GetMapping("users/{id}")
    public UserDTO getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    // 4. Tạo mới user
    @PostMapping("users")
    public UserDTO postUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    // 5. Cập nhật thông tin user
    @PutMapping("users/{id}")
    public UserDTO updateUser(@PathVariable int id, @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    // 6. Xóa user
    @DeleteMapping("users/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }

    // 7. Thay đổi ảnh avatar
    @PutMapping("users/{id}/update-avatar")
    public void updateAvatar(@PathVariable int id, @RequestBody UpdateAvatarRequest request) {
        userService.updateAvatar(id, request);
    }

    // 8. Thay đổi mật khẩu
    @PutMapping("users/{id}/update-password")
    public void updatePassword(@PathVariable int id, @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(id, request);
    }

    // 9. Quên mật khẩu
    @PostMapping("users/{id}/forgot-password")
    public String newPassword(@PathVariable int id) {
        return userService.forgotPassword(id);
    }

    // Upload ảnh
    // c1 : lưu trực tiếp vào database
    // c2 : lưu ảnh vào 1 folder ở server -> lưu path image vào database
    // trong TH ko có database : lưu ảnh vào 1 folder ở server -> sử dụng userId, fileId để tìm kiếm ảnh

    // upload
    // 1, 2, 3 : folder tương ứng với userId
    // trong folder userId là các ảnh mà user đó upload

    // 10. Upload file
    @PostMapping("users/{id}/files")
    public ResponseEntity<?> uploadFile(@PathVariable int id, @ModelAttribute("file") MultipartFile file) {
        String filePath = userService.uploadFile(id, file);
        return ResponseEntity.ok(filePath);
    }

    // 11. Xem ảnh
    @GetMapping("users/{id}/files/{fileId}")
    public ResponseEntity<?> readFile(@PathVariable int id, @PathVariable String fileId) {
        byte[] bytes = userService.readFile(id, fileId);
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }

    // 12. Lấy danh sách ảnh
    @GetMapping("users/{id}/files")
    public ResponseEntity<?> getFile(@PathVariable int id) {
        List<String> files = userService.getFiles(id);
        return ResponseEntity.ok(files);
    }

    // 13. Xóa ảnh
    @DeleteMapping("users/{id}/files/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable int id, @PathVariable String fileId) {
        userService.deleteFile(id, fileId);
        return ResponseEntity.noContent().build();
    }
}
