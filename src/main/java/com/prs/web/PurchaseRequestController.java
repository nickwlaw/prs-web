package com.prs.web;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import com.prs.business.purchaserequest.PurchaseRequest;
import com.prs.business.purchaserequest.PurchaseRequestRepository;
import com.prs.business.user.User;
import com.prs.business.user.UserRepository;

@RestController
@RequestMapping("/purchase-requests")
public class PurchaseRequestController {

	private final String NEW = "New";
	private final String REVIEW = "Review";
	private final String APPROVED = "Approved";
	private final String REJECTED = "Rejected";
	private final String REOPENED = "Reopened";

	@Autowired
	private PurchaseRequestRepository purchaseRequestRepo;
	@Autowired
	private UserRepository userRepo;

	@GetMapping("/")
	public JsonResponse getAll() {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(purchaseRequestRepo.findAll());
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@GetMapping("")
	public JsonResponse getAllPaginated(@RequestParam int start, @RequestParam int limit) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(purchaseRequestRepo.findAll(PageRequest.of(start, limit)));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@GetMapping("list-review")
	public JsonResponse getForReview(@RequestBody User u) {
		JsonResponse jr = null;
		try {
			u = userRepo.findById(u.getId()).get();
			jr = JsonResponse.getInstance(purchaseRequestRepo.findByStatusAndUserNot(REVIEW, u));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@GetMapping("/{id}")
	public JsonResponse get(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			Optional<PurchaseRequest> pr = purchaseRequestRepo.findById(id);
			if (pr.isPresent())
				jr = JsonResponse.getInstance(pr);
			else
				jr = JsonResponse.getInstance("No purchase request found for ID " + id);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@PostMapping("/")
	public JsonResponse add(@RequestBody PurchaseRequest pr) {
		JsonResponse jr = null;
		try {
			if (purchaseRequestRepo.findById(pr.getId()).isPresent())
				jr = JsonResponse.getInstance("Purchase request already exists.");
			else
				jr = savePurchaseRequest(pr);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@PutMapping("/")
	public JsonResponse update(@RequestBody PurchaseRequest pr) {
		JsonResponse jr = null;
		try {
			if (purchaseRequestRepo.findById(pr.getId()).isPresent())
				jr = savePurchaseRequest(pr);
			else
				jr = JsonResponse.getInstance("Purchase request not found.");
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@DeleteMapping("/{id}")
	public JsonResponse delete(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			Optional<PurchaseRequest> purchaseRequest = purchaseRequestRepo.findById(id);
			if (purchaseRequest.isPresent()) {
				purchaseRequestRepo.deleteById(id);
				jr = JsonResponse.getInstance(purchaseRequest);
			} else
				jr = JsonResponse.getInstance("Delete failed. No purchase request for id: " + id);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@PutMapping("/submit-review")
	public JsonResponse submitForReview(@RequestBody PurchaseRequest pr) {
		pr = purchaseRequestRepo.findById(pr.getId()).get();
		if (pr.getTotal() <= 50.0)
			pr.setStatus(APPROVED);
		else
			pr.setStatus(REVIEW);
		pr.setSubmittedDate(LocalDateTime.now());
		return savePurchaseRequest(pr);
	}

	@PutMapping("/submit-new")
	public JsonResponse submitNew(@RequestBody PurchaseRequest pr) {
		pr.setStatus(NEW);
		pr.setSubmittedDate(LocalDateTime.now());
		return savePurchaseRequest(pr);
	}

	@PutMapping("/reject")
	public JsonResponse reject(@RequestBody PurchaseRequest pr) {
		pr = purchaseRequestRepo.findById(pr.getId()).get();
		pr.setStatus(REJECTED);
		return savePurchaseRequest(pr);
	}

	@PutMapping("/approve")
	public JsonResponse approve(@RequestBody PurchaseRequest pr) {
		pr = purchaseRequestRepo.findById(pr.getId()).get();
		pr.setStatus(APPROVED);
		return savePurchaseRequest(pr);
	}

	@PutMapping("/reopen")
	public JsonResponse reopen(@RequestBody PurchaseRequest pr) {
		pr = purchaseRequestRepo.findById(pr.getId()).get();
		pr.setStatus(REOPENED);
		return savePurchaseRequest(pr);
	}

	private JsonResponse savePurchaseRequest(PurchaseRequest pr) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(purchaseRequestRepo.save(pr));
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getMessage());
		}
		return jr;
	}
}
