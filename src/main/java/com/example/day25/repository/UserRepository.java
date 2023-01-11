package com.example.day25.repository;

import com.example.day25.entity.User;
import com.example.day25.entity.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("select new com.example.day25.entity.UserDTO(u.id, u.name, u.email, u.phone, u.address, u.avatar) from User u")
    List<UserDTO> findAllUserDto();

    @Query("select new com.example.day25.entity.UserDTO(u.id, u.name, u.email, u.phone, u.address, u.avatar)" +
            "from User u " +
            "where upper(u.name) like upper(concat('%', ?1, '%'))")
    List<UserDTO> findUserDtoByNameContainingIgnoreCase(String name);

    List<User> findAll();

    List<User> findByNameContainingIgnoreCase(String name);

    Optional<User> findByEmail(String email);
}
