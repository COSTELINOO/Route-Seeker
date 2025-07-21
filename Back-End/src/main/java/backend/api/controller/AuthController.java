package backend.api.controller;

import backend.api.config.JwtUtil;
import backend.api.dto.UserDTO;
import backend.api.entity.User;
import backend.api.exceptions.MyExeption;
import backend.api.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserRepository usersRepository;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserRepository usersRepository, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.usersRepository = usersRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createToken(@RequestBody @Valid UserDTO authRequest) throws Exception {
        if (authRequest.getUsername() == null || authRequest.getPassword() == null) {
            throw new MyExeption.BadRequest("Utilizatorul si parola sunt obligatorii!");
        }
        Optional<User> user = usersRepository.findByUsername(authRequest.getUsername());
        if (user.isEmpty() || !user.get().getPassword().equals(authRequest.getPassword())) {
            throw new MyExeption.BadCredentials("Utilizatorul sau parola sunt invalide!");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok("{\"token\": \"" + token + "\"}");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserDTO authRequest) throws Exception {
        if (authRequest.getUsername() == null || authRequest.getPassword() == null) {
            throw new MyExeption.BadRequest("Utilizatorul si parola sunt obligatorii!");
        }
        if (usersRepository.findByUsername(authRequest.getUsername()).isPresent()) {
            throw new MyExeption.Conflict("Un utilizator cu acest username exista deja");
        }
        String token = jwtUtil.generateToken(authRequest.getUsername());
        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(authRequest.getPassword());
        usersRepository.save(user);
        return ResponseEntity.ok("{\"token\": \"" + token + "\"}");
    }
}