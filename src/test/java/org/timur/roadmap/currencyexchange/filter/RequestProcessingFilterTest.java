package org.timur.roadmap.currencyexchange.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RequestProcessingFilterTest {

    @Mock
    private ServletRequest request;
    @Mock
    private ServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RequestProcessingFilter requestProcessingFilter;

    @Test
    void shouldSetEncodingAndContentType() throws IOException, ServletException {

        requestProcessingFilter.doFilter(request, response, filterChain);

        verify(request).setCharacterEncoding(StandardCharsets.UTF_8);
        verify(response).setCharacterEncoding(StandardCharsets.UTF_8);
        verify(response).setContentType("application/json");
        verify(filterChain).doFilter(request, response);
    }
}