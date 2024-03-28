package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.service.ApigeeService;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;

import net.minidev.json.JSONObject;

@RestController
@RequestMapping("/secretManagerApigee")
public class SecretManagerApigeeController {

	@Autowired
	ApigeeService postService;

	RestTemplate restTemplate;
	private static final String BASE_URL = "https://130.211.39.21.nip.io";
	@Autowired
	private SecretManagerTemplate secretManagerTemplate;

	@PostMapping("/create")
	public ResponseEntity<Object> triggerProxyCreate(@RequestParam String key, @RequestBody JSONObject requestBody) {
		try {
			String secretKey = secretManagerTemplate.getSecretString(key);
			if (secretKey != null) {
				String url = BASE_URL + "/muruga82767640postgrescrudcreate";

				ResponseEntity<String> responseEntity = execute(url, HttpMethod.POST, requestBody, secretKey);

				String responseBody = responseEntity.getBody();
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("No Key");
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
		}
	}

	@GetMapping("/get")
	public ResponseEntity<Object> triggerProxyGet(@RequestParam String key) {
		try {
			String secretKey = secretManagerTemplate.getSecretString(key);
			if (secretKey != null) {
				String url = BASE_URL + "/muruga82767640postgrescrudget";

				ResponseEntity<String> responseEntity = execute(url, HttpMethod.GET, null, secretKey);
				String responseBody = responseEntity.getBody();
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("No Key");
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
		}
	}

	@PutMapping("/update")
	public ResponseEntity<Object> triggerProxyPut(@RequestParam String key, @RequestBody JSONObject requestBody) {
		try {
			String secretKey = secretManagerTemplate.getSecretString(key);
			if (secretKey != null) {
				String url = BASE_URL + "/muruga82767640postgrescrudupdate";

				ResponseEntity<String> responseEntity = execute(url, HttpMethod.PUT, requestBody, secretKey);
				String responseBody = responseEntity.getBody();
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("No Key");
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Object> triggerProxyDelete(@RequestParam String key, @RequestBody JSONObject body) {
		try {
			String secretKey = secretManagerTemplate.getSecretString(key);
			if (secretKey != null) {
				String url = BASE_URL + "/muruga82767640postgrescruddelete";

				ResponseEntity<String> responseEntity = execute(url, HttpMethod.DELETE, body, secretKey);
				String responseBody = responseEntity.getBody();
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("No Key");
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
		}
	}

	private HttpHeaders createHeaders(String secretKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("x.apikey", secretKey);
		return headers;
	}

	private ResponseEntity<String> execute(String url, HttpMethod httpMethod, JSONObject reqBody, String secretKey) {
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> requestEntity;
		if (reqBody != null) {
			requestEntity = new HttpEntity<>(reqBody.toJSONString(), createHeaders(secretKey));
		} else {
			requestEntity = new HttpEntity<>(createHeaders(secretKey));
		}
		return restTemplate.exchange(url, httpMethod, requestEntity, String.class);
	}
}
