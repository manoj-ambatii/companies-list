package com.example.companies_list.repository;

import com.example.companies_list.model.ApplicationStatus;
import com.example.companies_list.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByName(String name);

    boolean existsByName(String name);

    List<Company> findByStatus(ApplicationStatus status);

    long countByStatus(ApplicationStatus status);

    @Query("SELECT c FROM Company c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:keyword IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.cleanName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Company> searchCompanies(@Param("status") ApplicationStatus status, @Param("keyword") String keyword);

    List<Company> findAllByOrderByStatusAscIdAsc();
}
