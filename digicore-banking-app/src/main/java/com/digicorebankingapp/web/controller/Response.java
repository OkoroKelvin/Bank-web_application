package com.digicorebankingapp.web.controller;


import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
public class Response implements Serializable {
    private HttpStatus responseCode;
    private boolean success;
    private String message;

    public Response(HttpStatus responseCode, boolean success, String message) {
        this.responseCode = responseCode;
        this.success = success;
        this.message = message;
    }
}
