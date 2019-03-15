package com.prs.business.vendor;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface VendorRepository extends CrudRepository<Vendor, Integer>, PagingAndSortingRepository<Vendor, Integer>{

}
