package com.lin.simplecron.web.rest;

import com.lin.simplecron.service.LoginTokenService;
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
    public ResponseEntity<String> currentLoginToken() {
        return ResponseEntity.ok(loginTokenService.getCurrentLoginToken());
    }

    @PostMapping("update/{newToken}")
    public ResponseEntity<String> updateLoginToken(@PathVariable String newToken) {
        return ResponseEntity.ok(loginTokenService.updateLoginToken(newToken));
    }
}
