package com.Membership.GYMETRA.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class UserMembershipClient {

    private final RestTemplate restTemplate;

    public UserMembershipClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserMembershipResponse getUserMembershipById(Integer id) {
        String url = "http://localhost:8081/api/user-memberships/" + id;
        return restTemplate.getForObject(url, UserMembershipResponse.class);
    }

    // Nuevo método para actualizar el estado
    public void updateMembershipStatus(Integer id, String status) {
        String url = "http://localhost:8081/api/user-memberships/" + id + "/status";
        Map<String, String> request = Map.of("status", status);
        restTemplate.put(url, request);
    }
}
