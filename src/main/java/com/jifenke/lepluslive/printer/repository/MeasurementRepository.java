package com.jifenke.lepluslive.printer.repository;

import com.jifenke.lepluslive.printer.domain.entities.MeasurementUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by root on 16-12-30.
 */
public interface MeasurementRepository  extends JpaRepository<MeasurementUrl, Long> {
    @Query(value = "SELECT * FROM measurement_url WHERE name =?1", nativeQuery = true)
    MeasurementUrl findUrlByName(String name);

}
