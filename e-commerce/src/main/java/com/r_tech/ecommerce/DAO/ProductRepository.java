package com.r_tech.ecommerce.DAO;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.r_tech.ecommerce.model.Product;



@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	
	
	@Query("SELECT p FROM Product p "
			+ "JOIN p.category c WHERE (:category IS NULL OR :category = '' OR c.name = :category OR c.parentCategory.name = :category) " +

		       "AND ((:minPrice IS NULL AND :maxPrice IS NULL) OR (p.discountedPrice BETWEEN :minPrice AND :maxPrice)) " +
		       "AND (:minDiscount IS NULL OR p.discountPercent >= :minDiscount) " +
		       "AND p.quantity > 0" +
		       "ORDER BY " +
		       "CASE WHEN :sort = 'price_low' THEN p.discountedPrice END ASC, " +
		       "CASE WHEN :sort = 'price_high' THEN p.discountedPrice END DESC")
		List<Product> filterProducts(@Param("category") String category,
		                             @Param("minPrice") Integer minPrice, 
		                             @Param("maxPrice") Integer maxPrice,
		                             @Param("minDiscount") Integer minDiscount,
		                             @Param("sort") String sort);

	 @Query("SELECT p From Product p Where LOWER(p.category.name)=:category")
		public List<Product> findByCategory(@Param("category") String category);
	}
