package com.mindmirror.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/** Strongly-typed binding for the {@code mindmirror.*} configuration tree. */
@ConfigurationProperties(prefix = "mindmirror")
public class MindMirrorProperties {

    private Jwt jwt = new Jwt();
    private Ai ai = new Ai();
    private boolean seedDemo = true;
    private Cors cors = new Cors();

    public Jwt getJwt() { return jwt; }
    public void setJwt(Jwt jwt) { this.jwt = jwt; }
    public Ai getAi() { return ai; }
    public void setAi(Ai ai) { this.ai = ai; }
    public boolean isSeedDemo() { return seedDemo; }
    public void setSeedDemo(boolean seedDemo) { this.seedDemo = seedDemo; }
    public Cors getCors() { return cors; }
    public void setCors(Cors cors) { this.cors = cors; }

    public static class Jwt {
        private String secret;
        private long expirationMs = 86_400_000L;
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getExpirationMs() { return expirationMs; }
        public void setExpirationMs(long expirationMs) { this.expirationMs = expirationMs; }
    }

    public static class Ai {
        private String baseUrl = "http://localhost:8000";
        private int timeoutMs = 8000;
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public int getTimeoutMs() { return timeoutMs; }
        public void setTimeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; }
    }

    public static class Cors {
        private List<String> allowedOrigins = List.of("http://localhost:5173");
        public List<String> getAllowedOrigins() { return allowedOrigins; }
        public void setAllowedOrigins(List<String> allowedOrigins) { this.allowedOrigins = allowedOrigins; }
    }
}
