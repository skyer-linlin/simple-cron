package com.lin.simplecron.web.rest;

import com.lin.simplecron.service.LoginTokenService;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * com.lin.simplecron.web.rest
 *
 * @author quanlinlin
 * @date 2022/10/19 19:32
 * @since
 */
@RestController
@RequestMapping("/jiemo/login_token/")
public class LoginController {

    @Autowired
    private LoginTokenService loginTokenService;

    @GetMapping("current")
    @Timed(value = "main_page_request_duration", description = "Time taken to return main page", histogram = true)
    public ResponseEntity<String> currentLoginToken() {
        return ResponseEntity.ok(loginTokenService.getCurrentLoginToken());
    }

    @PostMapping("update/{newToken}")
    @Timed(value = "main_page_request_duration", description = "Time taken to return main page", histogram = true)
    public ResponseEntity<String> updateLoginToken(@PathVariable String newToken) {
        return ResponseEntity.ok(loginTokenService.updateLoginToken(newToken));
    }
}
