package com.example.day25;

import com.example.day25.entity.User;
import com.example.day25.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class Day25ApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Rollback(value = false)
    void addUsers_test() {
        List<User> users = new ArrayList<>(List.of(
                new User(1, "Cong", "cong@gmail.com","0987654321", "Thanh Pho Ha Noi", "avatar1", "password1"),
                new User(2, "Hieu", "hieu@gmail.com","0912345678", "Thanh Pho Ho Chi Minh", "avatar2", "password2"),
                new User(3, "Dat", "dat@gmail.com","0949948170", "Thanh Pho Da Nang", "avatar3", "password3")
        ));
        userRepository.saveAll(users);
    }

}
