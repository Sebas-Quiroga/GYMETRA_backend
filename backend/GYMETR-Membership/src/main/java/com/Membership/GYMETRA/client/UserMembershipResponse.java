package com.Membership.GYMETRA.client;

import lombok.Data;

@Data
public class UserMembershipResponse {
    private Integer id;
    private String status;
    private UserDto user;
    private MembershipDto membership;

    @Data
    public static class UserDto {
        private Integer id;
        private String firstName;
        private String lastName;
        private String email;
    }

    @Data
    public static class MembershipDto {
        private Integer id;
        private String name;
        private String description;
        private Double price;
    }
}
