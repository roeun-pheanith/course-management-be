package com.pheanith.dev.restaurant.dto;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Data;

@Data
public class PageDTO {
	private List<?> list;
	private PaginationDTO paginationDTO;
	public PageDTO(Page<?> page) {
		this.list = page.getContent();
		this.paginationDTO = PaginationDTO.builder()
					.pageSize(page.getPageable().getPageSize())
					.pageNumber(page.getPageable().getPageNumber() + 1)
					.totalPages(page.getTotalPages())
					.totalElements(page.getTotalElements())
					.numberOfElements(page.getNumberOfElements())
					.first(page.isFirst())
					.last(page.isLast())
					.empty(page.isEmpty())
					.build();
	}
}
