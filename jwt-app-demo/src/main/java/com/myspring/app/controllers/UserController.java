package com.myspring.app.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myspring.app.configs.jwt.JwtTokenProvider;
import com.myspring.app.entities.ResponseDTO;
import com.myspring.app.entities.User;
import com.myspring.app.services.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserServiceImpl userService;


    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);


        if (header != null && header.startsWith("Bearer_")) {
            try {
                String refresh_token = header.substring("Bearer_".length());
                Verification verifier = JWT
                        .require(Algorithm.HMAC256("secret".getBytes()));
                DecodedJWT decodedJWT = verifier.build().verify(refresh_token);
                String username = decodedJWT.getSubject();

                User user = userService.findByUsername(username);

                String access_token = JwtTokenProvider.createToken(user.getUsername(), user.getRoles().stream().map(
                        role -> new SimpleGrantedAuthority(role.getName())
                ).collect(Collectors.toList()), 10 * 60 * 1000);

                Map<String, String> tokens = new HashMap<>();

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);

                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception exception) {
                response.setHeader("error", exception.getMessage());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error", exception.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }


        } else {
            throw new RuntimeException("error");
        }

    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> getAllUsers(){

        log.info("GET : /api/users/all");

        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .statusCode(HttpStatus.OK.value())
                        .status(HttpStatus.OK)
                        .dateTime(LocalDateTime.now())
                        .message("users successfully fetched")
                        .data(Map.of("Users", userService.getAll()))
                        .build()
        );

    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getOneUser(@PathVariable("id") Long id) {

        User user = userService.getOneById(id);

        if (user == null) {

            log.error("GET: /api/users/{} not found", id);

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .status(HttpStatus.NOT_FOUND)
                            .dateTime(LocalDateTime.now())
                            .message(String.format("user with id: %s not found", id))
                            .data(Map.of("users", null))
                            .build()
            );
        }

        else {
            log.info("GET: /api/users/{}", id);


            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .statusCode(HttpStatus.OK.value())
                            .status(HttpStatus.OK)
                            .dateTime(LocalDateTime.now())
                            .message(String.format("user with id: %s has successfully fetched", id))
                            .data(Map.of("User", user))
                            .build()
            );

        }


    }

    @PostMapping
    public ResponseEntity<ResponseDTO> save(@RequestBody User user){

        boolean userBool = userService.save(user);

        if(!userBool){
            log.info("GET: /api/users/{} error", user);

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .statusCode(HttpStatus.ALREADY_REPORTED.value())
                            .status(HttpStatus.ALREADY_REPORTED)
                            .message(String.format("user with email %s has already exists", user.getEmail()))
                            .dateTime(LocalDateTime.now())
                            .data(Map.of("Users", userService.getAll()))
                            .build()
            );
        }

        else {

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .statusCode(HttpStatus.OK.value())
                            .status(HttpStatus.OK)
                            .message(String.format("user with username %s has successfully saved", user.getUsername()))
                            .dateTime(LocalDateTime.now())
                            .data(Map.of("User", user))
                            .build()
            );

        }

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable("id") Long id){
        boolean userBool = userService.deleteById(id);

        if(!userBool){

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .status(HttpStatus.NOT_FOUND)
                            .message(String.format("user with id %s not found", id))
                            .dateTime(LocalDateTime.now())
                            .build()
            );

        }
        else {

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .statusCode(HttpStatus.OK.value())
                            .status(HttpStatus.OK)
                            .message(String.format("user with id: %s has successfully deleted", id))
                            .dateTime(LocalDateTime.now())
                            .data(Map.of("Users", userService.getAll()))
                            .build()
            );

        }

    }


    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateUser(@PathVariable("id") Long id,
                                                  @RequestBody User user){
        if(!userService.update(id, user)){
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .status(HttpStatus.NOT_FOUND)
                            .message(String.format("user with id: %s not found", id))
                            .dateTime(LocalDateTime.now())
                            .build()
            );
        }

        else {
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .statusCode(HttpStatus.OK.value())
                            .status(HttpStatus.OK)
                            .message(String.format("user with id: %s has successfully updated", id))
                            .dateTime(LocalDateTime.now())
                            .data(Map.of("Users", userService.getAll()))
                            .build()
            );
        }

    }



}
