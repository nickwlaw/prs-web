package com.prs.web;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import com.prs.business.user.User;
import com.prs.business.user.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserRepository userRepo;
	
	@PostMapping("/authenticate")
	public JsonResponse authenticate(@RequestBody User u) {
		JsonResponse jr = null;
		try {
			if (userRepo.findByUserName(u.getUserName()).isPresent()) {
				Optional<User> user = userRepo.findByUserNameAndPassword(u.getUserName(), u.getPassword());
				if (user.isPresent())
					jr = JsonResponse.getInstance(user);
				else
					jr = JsonResponse.getInstance("Incorrect password for username " + u.getUserName());
			} else {
				jr = JsonResponse.getInstance("No user found with username " + u.getUserName());
			}
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}
	
	@GetMapping("/")
	public JsonResponse getAll() {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(userRepo.findAll());
		} catch (Exception e){
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}
	
	@GetMapping("")
	public JsonResponse getAllPaginated(@RequestParam int start, @RequestParam int limit) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(userRepo.findAll(PageRequest.of(start, limit)));
		} catch (Exception e){
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}
	
	@GetMapping("/{id}")
	public JsonResponse get(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			Optional<User> user = userRepo.findById(id);
			if (user.isPresent())
				jr = JsonResponse.getInstance(user);
			else 
				jr = JsonResponse.getInstance("No user found with ID " + id);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}
	
	@PostMapping("/")
	public JsonResponse add(@RequestBody User u) {
		JsonResponse jr = null;
		try {
			if (userRepo.findById(u.getId()).isPresent())
				jr = JsonResponse.getInstance("User already exists.");
			else
				jr = saveUser(u);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}
	
	@PutMapping("/")
	public JsonResponse update(@RequestBody User u) {
		JsonResponse jr = null;
		try {
			if (userRepo.findById(u.getId()).isPresent())
				jr = saveUser(u);
			else
				jr = JsonResponse.getInstance("User not found.");
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}
	
	@DeleteMapping("/{id}")
	public JsonResponse delete(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			Optional<User> user = userRepo.findById(id);
			if (user.isPresent()) {
				userRepo.deleteById(id);
				jr = JsonResponse.getInstance(user);
			} else
				jr = JsonResponse.getInstance("Delete failed. No user for id: " + id);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}
	
	private JsonResponse saveUser(User u) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(userRepo.save(u));
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getMessage());
		}
		return jr;
	}
}
