package neo.spider.demo.E2E.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import neo.spider.demo.E2E.entity.Customer;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{
	
    Optional<Customer> findByMobileNumber(String mobileNumber);
}
