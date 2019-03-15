package com.prs.web;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import com.prs.business.product.Product;
import com.prs.business.product.ProductRepository;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductRepository productRepo;

	@GetMapping("/")
	public JsonResponse getAll() {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(productRepo.findAll());
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@GetMapping("")
	public JsonResponse getAllPaginated(@RequestParam int start, @RequestParam int limit) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(productRepo.findAll(PageRequest.of(start, limit)));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@GetMapping("/{id}")
	public JsonResponse get(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			Optional<Product> product = productRepo.findById(id);
			if (product.isPresent())
				jr = JsonResponse.getInstance(product);
			else
				jr = JsonResponse.getInstance("No product found for ID " + id);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@PostMapping("/")
	public JsonResponse add(@RequestBody Product p) {
		JsonResponse jr = null;
		try {
			if (productRepo.findById(p.getId()).isPresent())
				jr = JsonResponse.getInstance("Product already exists.");
			else
				jr = saveProduct(p);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@PutMapping("/")
	public JsonResponse update(@RequestBody Product p) {
		JsonResponse jr = null;
		try {
			if (productRepo.findById(p.getId()).isPresent())
				jr = saveProduct(p);
			else
				jr = JsonResponse.getInstance("Product not found.");
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@DeleteMapping("/{id}")
	public JsonResponse delete(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			Optional<Product> prod = productRepo.findById(id);
			if (prod.isPresent()) {
				productRepo.deleteById(id);
				jr = JsonResponse.getInstance(prod);
			} else
				jr = JsonResponse.getInstance("Delete failed. No product for id: " + id);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	private JsonResponse saveProduct(Product p) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(productRepo.save(p));
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getMessage());
		}
		return jr;
	}
}