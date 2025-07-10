package com.pheanith.dev.restaurant.service.util;



import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface PageUtil {
	int DEFAULT_PAGE_LIMIT=2;
	int DEFAULT_PAGE_NUMBER=1;
	String PAGE_LIMIT="limit";
	String PAGE_NUMBER= "page";
	
	static Pageable getPageable(int pageNumber, int pageSize) {
		if(pageNumber < DEFAULT_PAGE_NUMBER) {
			pageNumber = DEFAULT_PAGE_NUMBER;
		}
		if(pageSize < DEFAULT_PAGE_LIMIT) {
			pageSize = DEFAULT_PAGE_LIMIT;
		}
		Pageable pageable = PageRequest.of(pageNumber- 1, pageSize);
		
		return pageable;
	}
}
