package com.prs.web;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import com.prs.business.vendor.Vendor;
import com.prs.business.vendor.VendorRepository;

@RestController
@RequestMapping("/vendors")
public class VendorController {

	@Autowired
	private VendorRepository vendorRepo;

	@GetMapping("/")
	public JsonResponse getAll() {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(vendorRepo.findAll());
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@GetMapping("")
	public JsonResponse getAllPaginated(@RequestParam int start, @RequestParam int limit) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(vendorRepo.findAll(PageRequest.of(start, limit)));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@GetMapping("/{id}")
	public JsonResponse get(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			Optional<Vendor> vendor = vendorRepo.findById(id);
			if (vendor.isPresent())
				jr = JsonResponse.getInstance(vendor);
			else
				jr = JsonResponse.getInstance("No vendor found for ID " + id);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@PostMapping("/")
	public JsonResponse add(@RequestBody Vendor v) {
		JsonResponse jr = null;
		try {
			if (vendorRepo.findById(v.getId()).isPresent())
				jr = JsonResponse.getInstance("Vendor already exists");
			else
				jr = saveVendor(v);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@PutMapping("/")
	public JsonResponse update(@RequestBody Vendor v) {
		JsonResponse jr = null;
		try {
			if (vendorRepo.findById(v.getId()).isPresent())
				jr = saveVendor(v);
			else
				jr = JsonResponse.getInstance("Vendor not found.");
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@DeleteMapping("/{id}")
	public JsonResponse delete(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			Optional<Vendor> vendor = vendorRepo.findById(id);
			if (vendor.isPresent()) {
				vendorRepo.deleteById(id);
				jr = JsonResponse.getInstance(vendor);
			} else
				jr = JsonResponse.getInstance("Delete failed. No vendor for id: " + id);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	private JsonResponse saveVendor(Vendor v) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(vendorRepo.save(v));
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getMessage());
		}
		return jr;
	}
}