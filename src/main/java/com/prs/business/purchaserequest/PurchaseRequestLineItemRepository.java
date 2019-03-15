package com.prs.business.purchaserequest;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PurchaseRequestLineItemRepository extends CrudRepository<PurchaseRequestLineItem, Integer>, PagingAndSortingRepository<PurchaseRequestLineItem, Integer> {

	List<PurchaseRequestLineItem> findByPurchaseRequest(PurchaseRequest pr);
}
