package com.lin.simplecron.web.rest;


import com.lin.simplecron.dto.FileDto;
import com.lin.simplecron.service.MinioService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE;

/**
 * com.lin.simplecron.web.rest
 *
 * @author quanlinlin
 * @date 2022/10/25 23:16
 * @since
 */
@Slf4j
@RestController
@RequestMapping(value = "/file")
public class MinioController {

    @Autowired
    private MinioService minioService;

    @GetMapping
    public ResponseEntity<Object> getFiles() {
        return ResponseEntity.ok(minioService.getListObjects());
    }

    @PostMapping(value = "/upload")
    @Timed(value = "main_page_request_duration", description = "Time taken to return main page", histogram = true)
    public ResponseEntity<Object> upload(@ModelAttribute FileDto request) {
        return ResponseEntity.ok().body(minioService.uploadFile(request));
    }

    @GetMapping(value = "/**")
    @Timed(value = "main_page_request_duration", description = "Time taken to return main page", histogram = true)
    public ResponseEntity<Object> getFile(HttpServletRequest request) throws IOException {
        String pattern = (String) request.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE);
        String filename = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(IOUtils.toByteArray(minioService.getObject(filename)));
    }


}
