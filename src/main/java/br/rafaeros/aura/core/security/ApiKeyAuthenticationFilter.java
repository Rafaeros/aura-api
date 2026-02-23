package br.rafaeros.aura.core.security;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.rafaeros.aura.modules.auth.model.ApiKey;
import br.rafaeros.aura.modules.auth.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";
    private final ApiKeyRepository apiKeyRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String extractedApiKey = request.getHeader(API_KEY_HEADER);

        if (extractedApiKey == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<ApiKey> apiKeyOpt = apiKeyRepository.findByKeyAndIsActiveTrue(extractedApiKey);

        if (apiKeyOpt.isPresent()) {
            ApiKey apiKey = apiKeyOpt.get();
            List<GrantedAuthority> authorities = AuthorityUtils
                    .commaSeparatedStringToAuthorityList(apiKey.getAuthorities());

            ApiKeyPrincipal principal = new ApiKeyPrincipal(
                apiKey.getId(),
                apiKey.getCompany().getId(),
                apiKey.getDescription()
            );
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}