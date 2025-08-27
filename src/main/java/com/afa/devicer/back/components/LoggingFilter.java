package com.afa.devicer.back.components;

import com.afa.core.components.FilterOrderConstants;
import com.afa.devicer.back.controllers.internal.ControllerConstants;
import com.afa.core.utils.MaskHelper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(FilterOrderConstants.LOGGING_FILTER)
@SuppressWarnings({"PMD.LawOfDemeter"})
public class LoggingFilter implements Filter {
    private static final int MAX_PAYLOAD_SIZE = 1024 * 3;
    private static final int RND_SEED = 899_999_998;
    private static final int RND_SHIFT = 100_000_001;
    private static final String MULTIPART = "multipart/form-data";
    private static final SecureRandom RND = new SecureRandom();

    @Override
    public void doFilter(
            final ServletRequest request,
            final ServletResponse response,
            final FilterChain chain)
            throws IOException, ServletException {
        // Get rid of logging some unnecessary urls & content types
        final String requestUri = ((HttpServletRequest) request).getRequestURI();
        final String requestContentType = request.getContentType();
        if (requestUri.startsWith(ControllerConstants.ACTUATOR) ||
                requestUri.startsWith(ControllerConstants.API_DOCS) ||
                requestUri.startsWith(ControllerConstants.SWAGGER) ||
                requestUri.startsWith(ControllerConstants.FILES) ||
                (requestContentType != null && requestContentType.toLowerCase().contains(MULTIPART))) {
            chain.doFilter(request, response);
        } else {
            final int marker = RND.nextInt(RND_SEED) + RND_SHIFT;

            // Log request info
            final CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest((HttpServletRequest) request);
            log.info(logRequest(cachedRequest, marker));

            // Get wrappers
            final ContentCachingResponseWrapper cachedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

            // Process request
            final long startTime = Instant.now().toEpochMilli();
            chain.doFilter(cachedRequest, cachedResponse);

            // Log response info
            log.info(logResponse(cachedResponse, marker, startTime));

            // Copy response body, absolutely necessarily!
            cachedResponse.copyBodyToResponse();
        }
    }

    private String logRequest(final CachedBodyHttpServletRequest request, final int marker) {
        final StringBuilder sb = new StringBuilder();

        // Common info
        sb.append(String.format("Request  {'correlationMarker':%d, 'method':'%s', 'uri':'%s'",
                marker, request.getMethod(), request.getRequestURI()));

        // Query params
        final String params = request.getQueryString();
        if (params != null) {
            sb.append(String.format(", 'params':'[%s]'", params));
        }

        // Query body
        final String payload = request
                .getReader()
                .lines()
                .collect(Collectors.joining(System.lineSeparator()))
                .replace(System.lineSeparator(), "");
        sb.append(maskPayload(payload));

        return sb.append('}').toString().replace('\'', '"');
    }

    private String logResponse(final ContentCachingResponseWrapper response, final int marker, final long startTime) {
        final StringBuilder sb = new StringBuilder();

        // Common info
        sb.append(String.format("Response {'correlationMarker':%d, 'executeTime':%d, 'status':%d",
                marker, Instant.now().toEpochMilli() - startTime, response.getStatus()));

        // Response body
        final String payload = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        sb.append(maskPayload(payload));

        return sb.append('}').toString().replace('\'', '"');
    }

    private String maskPayload(final String payload) {
        if (StringUtils.isEmpty(payload)) {
            return "";
        }

        final String payloadMasked = MaskHelper.maskJson(payload);
        if (StringUtils.isEmpty(payloadMasked)) {
            return "";
        }
        return String.format(", 'payload':%s",
                payloadMasked.length() < MAX_PAYLOAD_SIZE
                        ? payloadMasked
                        : payloadMasked.substring(0, MAX_PAYLOAD_SIZE) + "...");
    }

    // Inner classes, see https://www.baeldung.com/spring-reading-httpservletrequest-multiple-times
    private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
        private final byte[] cachedBody;


        public CachedBodyHttpServletRequest(final HttpServletRequest request) throws IOException {
            super(request);
            try (InputStream requestInputStream = request.getInputStream()) {
                this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
            }
        }

        @Override
        public ServletInputStream getInputStream() {
            return new CachedBodyServletInputStream(this.cachedBody);
        }

        @Override
        public BufferedReader getReader() {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
            return new BufferedReader(new InputStreamReader(byteArrayInputStream));
        }

        private static class CachedBodyServletInputStream extends ServletInputStream {
            private final InputStream cachedBodyInputStream;


            public CachedBodyServletInputStream(final byte[] cachedBody) {
                super();
                this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
            }

            @Override
            public int read() throws IOException {
                return cachedBodyInputStream.read();
            }

            @SneakyThrows
            @Override
            public boolean isFinished() {
                return cachedBodyInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(final ReadListener listener) {
                // ignored
            }
        }
    }
}
