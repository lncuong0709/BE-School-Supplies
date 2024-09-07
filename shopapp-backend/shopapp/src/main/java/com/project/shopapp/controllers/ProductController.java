package com.project.shopapp.controllers;

import com.github.javafaker.Faker;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.IProductService;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;
    private final LocalizationUtils localizationUtils;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @Valid @ModelAttribute ProductDTO productDTO,
            BindingResult result,
            @RequestParam("thumbnail") MultipartFile thumbnail
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errorMessages);
            }

            if (thumbnail == null || thumbnail.isEmpty()) {
                return ResponseEntity.badRequest().body("Thumbnail is missing or empty");
            }

            if (thumbnail.getSize() == 0) {
                return ResponseEntity.badRequest().body("Thumbnail file is empty");
            }

            if (thumbnail.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body("File is too large! Maximum size is 10MB");
            }
            String contentType = thumbnail.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("File must be an image");
            }

            // Lưu file và lấy tên tệp
            String filename = storeFile(thumbnail);

            // Lấy Category từ cơ sở dữ liệu
          Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            // Tạo đối tượng Product
            Product newProduct = Product.builder()
                    .name(productDTO.getName())
                    .price(productDTO.getPrice())
                    .thumbnail(filename) // Lưu tên tệp thumbnail vào đối tượng Product
                    .description(productDTO.getDescription())
                    .category(existingCategory) // Đảm bảo existingCategory được xác định đúng
                    .build();

            // Lưu Product vào cơ sở dữ liệu
            newProduct = productRepository.save(newProduct);
            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpeg").toUri()));
                //return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    private String storeFile(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        // Đường dẫn đến thư mục mà bạn muốn lưu file
        java.nio.file.Path uploadDir = Paths.get("uploads");
        // Kiểm tra và tạo thư mục nếu nó không tồn tại
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // Đường dẫn đầy đủ đến file
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        // Sao chép file vào thư mục đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }




    @GetMapping("")
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> existingProducts = productService.getAllProducts();
            List<ProductResponse> productResponses = ProductResponse.fromProducts(existingProducts);
            return ResponseEntity.ok(productResponses);
        } catch (Exception e) {
            // Logging the exception might also be a good idea
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching products: " + e.getMessage());
        }
    }
    @GetMapping("/imageById")
    public ResponseEntity<?> getDistinctImageByCategory() {
        try {
            List<Product> existingProducts = productService.findDistinctImageByCategory();
            List<ProductResponse> productResponses = ProductResponse.fromProducts(existingProducts);
            return ResponseEntity.ok(productResponses);
        } catch (Exception e) {
            // Logging the exception might also be a good idea
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching products: " + e.getMessage());
        }
    }
    //http://localhost:8088/api/v1/products/6
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable("id") Long productId
    ) {
        try {
            Product existingProduct = productService.getProductById(productId);
            return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @PutMapping("/uploads/{id}")
    public ResponseEntity<?> updateUploadProductById(
            @PathVariable("id") Long productImageId,
            @ModelAttribute MultipartFile files) {

        return ResponseEntity.ok("Update upload product images successfully");
    }
    @GetMapping("/productByCategory/{id}")
    public ResponseEntity<?> getProductsByCategoryId(
            @PathVariable("id") Long productId
    ) {
        try {
            List<Product> existingProducts = productService.findProductsByCategoryId(productId);
            List<ProductResponse> productResponses = ProductResponse.fromProducts(existingProducts);
            return ResponseEntity.ok(productResponses);
        } catch (Exception e) {
            // Logging the exception might also be a good idea
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching products: " + e.getMessage());
        }

    }
    @GetMapping("/productByName/{name}")
    public ResponseEntity<?> getProductsByName(@PathVariable("name") String name) {
        try {
            List<Product> existingProducts = productService.getAllProductsByName(name);
            if (existingProducts == null || existingProducts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy sản phẩm có tên: " + name);
            }
            List<ProductResponse> productResponses = ProductResponse.fromProducts(existingProducts);
            return ResponseEntity.ok(productResponses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi lấy sản phẩm: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(String.format("Product with id = %d deleted successfully", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //@PostMapping("/generateFakeProducts")

    //update a product
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable long id,
            @RequestBody ProductDTO productDTO) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
