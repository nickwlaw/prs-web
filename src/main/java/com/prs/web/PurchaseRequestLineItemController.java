package com.prs.web;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import com.prs.business.purchaserequest.PurchaseRequest;
import com.prs.business.purchaserequest.PurchaseRequestLineItem;
import com.prs.business.purchaserequest.PurchaseRequestLineItemRepository;
import com.prs.business.purchaserequest.PurchaseRequestRepository;

@RestController
@RequestMapping("/purchase-request-line-items")
public class PurchaseRequestLineItemController {

	@Autowired
	private PurchaseRequestLineItemRepository prliRepo;
	@Autowired
	private PurchaseRequestRepository prRepo;
	@Autowired
	private EntityManager em;

	@GetMapping("/")
	public JsonResponse getAll() {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(prliRepo.findAll());
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@GetMapping("")
	public JsonResponse getAllPaginated(@RequestParam int start, @RequestParam int limit) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(prliRepo.findAll(PageRequest.of(start, limit)));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@GetMapping("/{id}")
	public JsonResponse get(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			Optional<PurchaseRequestLineItem> prli = prliRepo.findById(id);
			if (prli.isPresent())
				jr = JsonResponse.getInstance(prli);
			else
				jr = JsonResponse.getInstance("No purchase request line item found for ID " + id);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@PostMapping("/")
	public JsonResponse add(@RequestBody PurchaseRequestLineItem prli) {
		JsonResponse jr = null;
		try {
			if (prliRepo.findById(prli.getId()).isPresent())
				jr = JsonResponse.getInstance("Purchase request line item already exists.");
			else {
				/* The savePrli method is being called twice, once to save it before recalculating the total,
				   and once to set the JsonResponse with fully qualified Entities for postman testing */
				savePrli(prli);
				recalculateTotal(prli.getPurchaseRequest());
				jr = savePrli(prli);
			}
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@PutMapping("/")
	public JsonResponse update(@RequestBody PurchaseRequestLineItem prli) {
		JsonResponse jr = null;
		try {
			if (prliRepo.findById(prli.getId()).isPresent()) {
				jr = savePrli(prli);
				recalculateTotal(prli.getPurchaseRequest());
			} else
				jr = JsonResponse.getInstance("Purchase request line item not found.");
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@DeleteMapping("/{id}")
	public JsonResponse delete(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			Optional<PurchaseRequestLineItem> prli = prliRepo.findById(id);
			if (prli.isPresent()) {
				prliRepo.deleteById(id);
				recalculateTotal(prli.get().getPurchaseRequest());
				jr = JsonResponse.getInstance(prli);
			} else
				jr = JsonResponse.getInstance("Delete failed. No purchase request line item for id: " + id);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	private void recalculateTotal(PurchaseRequest pr) {
		em.clear();
		pr = prRepo.findById(pr.getId()).get();
		List<PurchaseRequestLineItem> prlis = prliRepo.findByPurchaseRequest(pr);
		double total = 0.0;
		for (PurchaseRequestLineItem prli : prlis)
			total += prli.getProduct().getPrice() * prli.getQuantity();
		pr.setTotal(total);
		prRepo.save(pr);
	}

	private JsonResponse savePrli(PurchaseRequestLineItem prli) {
		JsonResponse jr = null;
		try {
			prli = prliRepo.save(prli);
			jr = JsonResponse.getInstance(prli);
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive);
		}
		return jr;
	}
}
