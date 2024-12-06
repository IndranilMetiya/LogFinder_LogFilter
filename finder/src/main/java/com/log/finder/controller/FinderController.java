package com.log.finder.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
public class FinderController {

	@GetMapping
	public ResponseEntity<?> getLogs(@RequestParam String logPath, @RequestParam String logLevel) {
		File logFile = new File(logPath);

		// Check if the log file exists
		if (!logFile.exists()) {
			return ResponseEntity.badRequest().body("Log file not found at: " + logPath);
		}

		// Split log levels into a list
		List<String> levels = Arrays.asList(logLevel.split(","));

		// Validate log levels
		List<String> validLevels = Arrays.asList("INFO", "ERROR", "DEBUG", "WARN", "TRACE");
		if (!levels.stream().allMatch(validLevels::contains)) {
			return ResponseEntity.badRequest().body("Invalid log level(s) provided.");
		}

		// Read and filter logs
		try (Stream<String> stream = Files.lines(logFile.toPath())) {
			List<String> filteredLogs = stream.filter(line -> levels.stream().anyMatch(line::contains))
					.collect(Collectors.toList());

			return ResponseEntity.ok(filteredLogs);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error reading log file: " + e.getMessage());
		}
	}
}
