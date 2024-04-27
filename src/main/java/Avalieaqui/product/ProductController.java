package Avalieaqui.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RestController;

import Avalieaqui.auth.JwtUtil;
import Avalieaqui.user.User;
import Avalieaqui.user.UserRepository;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody Product product,
            @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Header de autorização incorreto."));
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

        Product savedProduct = productRepository.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeProduct(@PathVariable String productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            productRepository.delete(productOptional.get());
            return ResponseEntity.ok("Produto removido com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado.");
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
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado.");
        }
    }

    @GetMapping("/by-view")
    public List<Product> getProductsSortedByViews() {
        List<Product> products = productRepository.findAll();
        products.sort((p1, p2) -> Integer.compare(p2.getViews(), p1.getViews()));

        return products;
    }

    @GetMapping("/by-view/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategorySortedByViews(@PathVariable String categoryId) {
        List<Product> products = productRepository.findByCategoryIdOrderByViewsDesc(categoryId);
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(products);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Ocorreu um erro ao processar a solicitação.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
    public ResponseEntity<?> editProduct(@PathVariable String productId, @RequestBody Product productDetails,
            @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Header incorreto."));
        }

        String token = authorizationHeader.substring(7);
        String userEmail = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByEmail(userEmail);

        if (user == null || !jwtUtil.validateToken(token, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("token error", "Token inválido"));
        }

        if (!user.getAdm()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("permission error", "Acesso negado. user precisa ser adm"));
        }

        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Produto não encontrado."));
        }

        Product product = productOptional.get();
        product.setName(productDetails.getName());
        product.setImageLink(productDetails.getImage());
        product.setCategoryId(productDetails.getCategoryId());

        productRepository.save(product);

        return ResponseEntity.ok().body(Map.of("message", "Produto atualizado com sucesso."));
    }

}
