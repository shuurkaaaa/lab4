package com.example.casdoor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "casdoor")
@Getter
@Setter
public class OpenIdConnectProperties {
    private String endpoint;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String organizationName;
    private String applicationName;
    private String certificate;
}
