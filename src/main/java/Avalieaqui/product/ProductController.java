package Avalieaqui.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import Avalieaqui.StorageService;
import Avalieaqui.auth.JwtUtil;
import Avalieaqui.review.Review;
import Avalieaqui.review.ReviewRepository;
import Avalieaqui.user.User;
import Avalieaqui.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ReviewRepository reviewRepository;

    @PostMapping
    public ResponseEntity<?> addProduct(
            @RequestPart("image") MultipartFile imageFile,
            @RequestParam("name") String name,
            @RequestParam("categoryId") String categoryId,
            @RequestParam("description") String description,
            @RequestParam("specification") String specification,
            @RequestHeader("Authorization") String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Header de autorização incorreto.");
        }

        String token = authorizationHeader.substring(7);
        String userEmail = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByEmail(userEmail);

        if (user == null || !jwtUtil.validateToken(token, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("token error", "Token inválido"));
        }

        if (!user.getAdm()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("permission error",
                            "Acesso negado. Apenas administradores podem adicionar produtos."));
        }

        try {
            String imageUrl = storageService.uploadFile(imageFile);
            Product product = new Product();
            product.setName(name);
            product.setCategoryId(categoryId);
            product.setDescription(description);
            product.setSpecification(specification);
            product.setImageLink(imageUrl);
            Product savedProduct = productRepository.save(product);
            return ResponseEntity.ok(savedProduct);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("upload error", "Erro ao fazer upload da imagem"));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeProduct(@PathVariable String productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            productRepository.delete(productOptional.get());
            return ResponseEntity.ok(Map.of("message","Produto removido com sucesso."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","Produto não encontrado."));
        }
    }

    @GetMapping
    public List<Product> getProducts() {
        List<Product> products = productRepository.findAll();
        return products;
    }

    @GetMapping("/id/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable String productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setViews(product.getViews() + 1);
            productRepository.save(product);

            List<Review> reviews = reviewRepository.findByProductId(product.getId());
            if (!reviews.isEmpty()) {
                double average = reviews.stream()
                        .mapToInt(Review::getStars)
                        .average()
                        .orElse(0.0);
                product.setAverage(average);
            }

            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado.");
        }
    }

    @GetMapping("/by-view/{qnt}")
    public List<Product> getProductsSortedByViews(@PathVariable int qnt) {
        List<Product> products = productRepository.findAll();
        products.sort((p1, p2) -> Integer.compare(p2.getViews(), p1.getViews()));
        products = products.stream().limit(qnt).collect(Collectors.toList());

        for (Product product : products) {
            List<Review> reviews = reviewRepository.findByProductId(product.getId());
            if (!reviews.isEmpty()) {
                double average = reviews.stream().mapToInt(Review::getStars).average().orElse(0.0);
                product.setAverage(average);
            }
        }

        return products;
    }

    @GetMapping("/by-view/{categoryId}/{qnt}")
    public ResponseEntity<List<Product>> getProductsByCategorySortedByViews(@PathVariable String categoryId,
            @PathVariable int qnt) {
        List<Product> products = productRepository.findByCategoryIdOrderByViewsDesc(categoryId);
        products = products.stream().limit(qnt).collect(Collectors.toList());

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        for (Product product : products) {
            List<Review> reviews = reviewRepository.findByProductId(product.getId());
            if (!reviews.isEmpty()) {
                double average = reviews.stream().mapToInt(Review::getStars).average().orElse(0.0);
                product.setAverage(average);
            }
        }

        return ResponseEntity.ok(products);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<?> handleDuplicateKeyException(DuplicateKeyException e) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Um produto com esse id já existe.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/search/{term}")
    public ResponseEntity<List<Product>> searchProductsByTerm(@PathVariable String term) {
        String sanitizedTerm = term.trim().replaceAll("\\s+", ".*");

        List<Product> products = productRepository.findByNameContainingIgnoreCase(".*" + sanitizedTerm + ".*");
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> editProduct(@PathVariable String productId,
            @RequestPart("image") MultipartFile imageFile,
            @RequestParam("name") String name,
            @RequestParam("categoryId") String categoryId,
            @RequestParam("description") String description,
            @RequestParam("specification") String specification,
            @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Header de autorização incorreto.");
        }

        String token = authorizationHeader.substring(7);
        String userEmail = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByEmail(userEmail);

        if (user == null || !jwtUtil.validateToken(token, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("token error", "Token inválido"));
        }

        if (!user.getAdm()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("permission error", "Acesso negado. Apenas administradores podem editar produtos."));
        }

        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado.");
        }

        try {
            String imageUrl = storageService.uploadFile(imageFile);
            Product product = productOptional.get();
            product.setName(name);
            product.setCategoryId(categoryId);
            product.setDescription(description);
            product.setSpecification(specification);
            product.setImageLink(imageUrl);
            productRepository.save(product);
            return ResponseEntity.ok().body(Map.of("message", "Produto atualizado com sucesso."));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao fazer upload da imagem");
        }
    }

}
