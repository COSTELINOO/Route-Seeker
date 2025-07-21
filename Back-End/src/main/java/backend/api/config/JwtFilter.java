package backend.api.config;

import backend.api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final UserService userDetailsService;

    private ObjectMapper objectMapper;

    public JwtFilter(JwtUtil jwtUtil, UserService userDetailsService, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (Boolean.TRUE.equals(jwtUtil.validateToken(jwt, userDetails))) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);

        } catch (SignatureException e) {
            logError(e);
            sendErrorResponse(response, "Semnătura token-ului JWT nu este validă. Token-ul ar putea fi alterat.", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (ExpiredJwtException e) {
            logError(e);
            sendErrorResponse(response, "Token-ul JWT a expirat.", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            logError(e);
            sendErrorResponse(response, "Token-ul JWT este invalid sau malformat.", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (JwtException e) {
            logError(e);
            sendErrorResponse(response, "Eroare de autentificare: " + e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            logError(e);
            sendErrorResponse(response, "Eroare internă de server în timpul procesării autentificării", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorDetails = Map.of(
                "error", statusCode == HttpServletResponse.SC_UNAUTHORIZED ? "Unauthorized" : "Internal Server Error",
                "message", message,
                "code", statusCode
        );

        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }

        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }

    private void logError(Exception e) throws Exception {
        System.err.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] " + e.toString());
       throw  new Exception(e);

    }
}