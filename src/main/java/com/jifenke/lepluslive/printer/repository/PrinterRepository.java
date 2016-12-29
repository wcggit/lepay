package com.jifenke.lepluslive.printer.repository;

import com.jifenke.lepluslive.printer.domain.entities.Printer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by root on 16-12-22.
 */
public interface PrinterRepository extends JpaRepository<Printer, Long> {
    Page findAll(Specification<Printer> whereClause, Pageable pageRequest);
}
