package usm.edolomanji.budgetplanner.auth;

import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import usm.edolomanji.budgetplanner.api.AuthApi;
import usm.edolomanji.budgetplanner.model.LoginRequest;
import usm.edolomanji.budgetplanner.model.LoginResponse;
import usm.edolomanji.budgetplanner.model.RegisterRequest;
import usm.edolomanji.budgetplanner.security.JwtService;
import usm.edolomanji.budgetplanner.user.UserEntity;
import usm.edolomanji.budgetplanner.user.UserRepository;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<LoginResponse> authLoginPost(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username,
                password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<String> roles = authentication.getAuthorities().stream().map(a -> a.getAuthority()).toList();

        String token = jwtService.generateToken(username, roles);

        LoginResponse response = new LoginResponse();
        response.setToken(token);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> authRegisterPost(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        List<String> roles = registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty() ? List.of(
                "ROLE_USER") : registerRequest.getRoles();

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Set.copyOf(roles));
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
