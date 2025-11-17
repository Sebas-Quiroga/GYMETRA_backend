package com.login.GYMETRA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private boolean success;   // nuevo campo
    private String token;
    private String type;       // Bearer
    private String message;    // mensaje de error o éxito
}
