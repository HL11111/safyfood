package com.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author mikasan
 * title
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "fabric")
public class HyperLedgerFabricProperties {

    String mspId;

    String certificatePath;

    String privateKeyPath;

    String tlsCertPath;

    String channel;



}
