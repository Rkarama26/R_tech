package com.r_tech.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.r_tech.ecommerce.exception.ProductException;
import com.r_tech.ecommerce.model.Product;
import com.r_tech.ecommerce.request.CreateProductRequest;
import com.r_tech.ecommerce.response.ApiResponse;
import com.r_tech.ecommerce.service.ProductService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin/products")
@Tag(name = "Admin Product API's")
public class AdminProductController {

	public AdminProductController(ProductService productService) {
		this.productService = productService;
	}

	private ProductService productService;

	@PostMapping("/create")
	public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest req) throws ProductException {

		Product createdProduct = productService.createProduct(req);

		return new ResponseEntity<Product>(createdProduct, HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/{productId}/delete")
	public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
		try {
			productService.deleteproduct(productId);
			ApiResponse response = new ApiResponse("Product deleted successfully", true);
			return ResponseEntity.ok(response);
		} catch (ProductException e) {
			ApiResponse errorResponse = new ApiResponse(e.getMessage(), false);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
		}
	}

	@GetMapping("/all")
	public ResponseEntity<List<Product>> findAllProduct() {

		List<Product> products = productService.findProducts();

		return new ResponseEntity<>(products, HttpStatus.OK);

	}

	@PutMapping("/{productId}/update")
	public ResponseEntity<Product> updateProduct(@RequestBody Product req, @PathVariable Long productId)
			throws ProductException {

		Product product = productService.updateProduct(productId, req);

		return new ResponseEntity<Product>(product, HttpStatus.CREATED);
	}
     @PostMapping("/creates")
	public ResponseEntity<ApiResponse> createMultipleProduct(@RequestBody CreateProductRequest[] req) {

		for (CreateProductRequest product : req) {
			productService.createProduct(product);
		}

		ApiResponse res = new ApiResponse();

		res.setMessage("product created successfully");
		res.setStatus(true);

		return new ResponseEntity<>(res, HttpStatus.CREATED);
	}

}
