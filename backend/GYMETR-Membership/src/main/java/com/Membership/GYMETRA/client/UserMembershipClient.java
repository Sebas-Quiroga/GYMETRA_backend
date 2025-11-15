package com.Membership.GYMETRA.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

// DTO simple para mapear la respuesta del login backend
class UserResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;

    // Getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

@Component
public class UserMembershipClient {

    private final RestTemplate restTemplate;

    public UserMembershipClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserMembershipResponse getUserMembershipById(Integer id) {
        String url = "http://3.15.181.40:8080/api/auth/users/" + id;
        UserResponse user = restTemplate.getForObject(url, UserResponse.class);

        if (user != null) {
            UserMembershipResponse response = new UserMembershipResponse();
            response.setId(id);

            UserMembershipResponse.UserDto userDto = new UserMembershipResponse.UserDto();
            userDto.setId(id);
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setEmail(user.getEmail());

            response.setUser(userDto);
            return response;
        }

        return null;
    }

    // Nuevo método para actualizar el estado
    public void updateMembershipStatus(Integer id, String status) {
        String url = "http://3.15.181.40/api/user-memberships/" + id + "/status";
        Map<String, String> request = Map.of("status", status);
        restTemplate.put(url, request);
    }
}
