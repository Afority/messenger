package com.github.messenger.infrastructure.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@RequestMapping("/secured")
public class SecuredController {
    @GetMapping
    public ResponseEntity<String> getMe(Principal principal) {
        return ResponseEntity.ok(principal.getName());
    }
}
