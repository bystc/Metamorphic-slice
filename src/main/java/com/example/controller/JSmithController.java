package com.example.controller;

import com.example.service.JSmithService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/jsmith")
public class JSmithController {

    @Autowired
    private JSmithService jsmithService;

    @GetMapping
    public String index() {
        return "jsmith";
    }

    @PostMapping("/generate")
    @ResponseBody
    public Map<String, Object> generateFiles(@RequestParam int numFiles, @RequestParam String type) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("Generating {} files of type: {}", numFiles, type);
            
            List<String> generatedFiles;
            switch (type) {
                case "random":
                    generatedFiles = jsmithService.generateRandomJavaCode(numFiles);
                    break;
                case "complex":
                case "simple":
                case "method":
                case "expression":
                    generatedFiles = jsmithService.generateTypedJavaCode(numFiles, type);
                    break;
                default:
                    generatedFiles = jsmithService.generateRandomJavaCode(numFiles);
                    break;
            }
            
            result.put("success", true);
            result.put("files", generatedFiles);
            result.put("count", generatedFiles.size());
            result.put("directory", jsmithService.getGeneratedDirectory());
            result.put("type", type);
            
            log.info("Successfully generated {} files of type {}", generatedFiles.size(), type);
            
        } catch (Exception e) {
            log.error("Error generating files", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    @PostMapping("/generate-random")
    @ResponseBody
    public Map<String, Object> generateRandomFiles(@RequestParam int numFiles) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("Generating {} random JSmith files", numFiles);
            List<String> generatedFiles = jsmithService.generateRandomJavaCode(numFiles);
            
            result.put("success", true);
            result.put("files", generatedFiles);
            result.put("count", generatedFiles.size());
            result.put("directory", jsmithService.getGeneratedDirectory());
            result.put("type", "random");
            
            log.info("Successfully generated {} random files", generatedFiles.size());
            
        } catch (Exception e) {
            log.error("Error generating random files", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    @PostMapping("/generate-complex")
    @ResponseBody
    public Map<String, Object> generateComplexFiles(@RequestParam int numFiles) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("Generating {} complex JSmith files", numFiles);
            List<String> generatedFiles = jsmithService.generateTypedJavaCode(numFiles, "complex");
            
            result.put("success", true);
            result.put("files", generatedFiles);
            result.put("count", generatedFiles.size());
            result.put("directory", jsmithService.getGeneratedDirectory());
            result.put("type", "complex");
            
            log.info("Successfully generated {} complex files", generatedFiles.size());
            
        } catch (Exception e) {
            log.error("Error generating complex files", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    @PostMapping("/cleanup")
    @ResponseBody
    public Map<String, Object> cleanupFiles() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            jsmithService.cleanupGeneratedFiles();
            result.put("success", true);
            result.put("message", "JSmith files cleaned up successfully");
            log.info("JSmith files cleaned up successfully");
            
        } catch (Exception e) {
            log.error("Error cleaning up files", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/list")
    @ResponseBody
    public Map<String, Object> listFiles() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<String> files = jsmithService.listGeneratedFiles();
            result.put("success", true);
            result.put("files", files);
            result.put("count", files.size());
            result.put("directory", jsmithService.getGeneratedDirectory());
            
        } catch (Exception e) {
            log.error("Error listing files", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/info")
    @ResponseBody
    public Map<String, Object> getInfo() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("success", true);
            result.put("directory", jsmithService.getGeneratedDirectory());
            result.put("supportedTypes", new String[]{"random", "complex", "simple", "method", "expression"});
            result.put("description", "JSmith is a Java code generator that creates random, syntactically correct Java programs");
            
        } catch (Exception e) {
            log.error("Error getting JSmith info", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
} 