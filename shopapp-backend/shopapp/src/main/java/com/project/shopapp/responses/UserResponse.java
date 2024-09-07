package com.project.shopapp.responses;

import com.project.shopapp.models.Category;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.UserRepository;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String password;
    private String phoneNumber;
    public static UserResponse fromUser(User user) {
        UserResponse userResponse = UserResponse.builder()
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .build();
        return userResponse;
    }
    public static List<UserResponse> fromUsers(List<User> users) {
        return users.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }
}
