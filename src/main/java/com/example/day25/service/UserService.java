package com.example.day25.service;

import com.example.day25.entity.User;
import com.example.day25.entity.UserDTO;
import com.example.day25.exception.BadRequestException;
import com.example.day25.exception.NotFoundException;
import com.example.day25.repository.UserRepository;
import com.example.day25.request.CreateUserRequest;
import com.example.day25.request.UpdateAvatarRequest;
import com.example.day25.request.UpdatePasswordRequest;
import com.example.day25.request.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FileService fileService;

    public List<UserDTO> getUserList() {
        return userRepository.findAllUserDto();
    }

    public List<UserDTO> getUserByName(String name) {
        return userRepository.findUserDtoByNameContainingIgnoreCase(name);
    }

    public UserDTO getUserById(int id) {
        return convertToDTO(userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Not found user with id " + id);
        }));
    }

    public UserDTO createUser(CreateUserRequest request) {
        if (request.getName().equals("") || request.getEmail().equals("") || request.getPhone().equals("") || request.getAddress().equals("Thành phố/Tỉnh") || request.getPassword().equals("")) {
            throw new BadRequestException("Ko được để trống các ô");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setAvatar(null);
        user.setPassword(request.getPassword());
        return convertToDTO(userRepository.save(user));
    }

    public UserDTO updateUser(int id, UpdateUserRequest request) {
        if (request.getName().equals("") || request.getPhone().equals("") || request.getAddress().equals("")) {
            throw new BadRequestException("Ko được để trống các trường");
        }
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Not found user with id " + id);
        });
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        return convertToDTO(userRepository.save(user));
    }

    public void deleteUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Not found user with id " + id);
        });
        userRepository.delete(user);
    }

    public void updateAvatar(Integer id, UpdateAvatarRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Not found user with id " + id);
        });
        user.setAvatar(request.getAvatar());
        userRepository.save(user);
    }

    public void updatePassword(Integer id, UpdatePasswordRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Not found user with id " + id);
        });
        if(user.getPassword().equals(request.getOldPassword())) {
            if(!request.getOldPassword().equals(request.getNewPassword())) {
                user.setPassword(request.getNewPassword());
                userRepository.save(user);
            } else {
                throw new BadRequestException("New password must not be the same with old password");
            }
        } else {
            throw new BadRequestException("Wrong password");
        }
    }

    public String forgotPassword(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Not found user with id " + id);
        });
        int leftLimit = 97;
        int rightLimit = 122;
        int len = 10;
        Random random = new Random();
        user.setPassword(random.ints(leftLimit, rightLimit + 1)
                .limit(len)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString());
        userRepository.save(user);
        return user.getPassword();
    }

    private UserDTO convertToDTO(User model) {
        UserDTO dto = new UserDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setEmail(model.getEmail());
        dto.setPhone(model.getPhone());
        dto.setAddress(model.getAddress());
        dto.setAvatar(model.getAvatar());
        return dto;
    }

    public String uploadFile(Integer id, MultipartFile file) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Not found user with id " + id);
        });
        return fileService.uploadFile(id, file);
    }

    public byte[] readFile(Integer id, String fileId) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Not found user with id " + id);
        });
        return fileService.readFile(id, fileId);
    }

    public List<String> getFiles(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Not found user with id " + id);
        });
        return fileService.getFile(id);
    }

    public void deleteFile(Integer id, String fileId) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Not found user with id " + id);
        });
        fileService.deleteFile(id, fileId);
    }
}
