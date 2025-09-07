package com.cartechindia.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityRulesProperties {

    private List<Rule> rules;

    @Setter
    @Getter
    public static class Rule {
        private String pattern;
        private String roles;
    }
}
