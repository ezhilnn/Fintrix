package com.fintrix.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "fintrix.oauth2")
public class FintrixOAuth2Properties {

    private List<String> authorizedRedirectUris;

    public List<String> getAuthorizedRedirectUris() {
        return authorizedRedirectUris;
    }

    public void setAuthorizedRedirectUris(List<String> authorizedRedirectUris) {
        this.authorizedRedirectUris = authorizedRedirectUris;
    }
}