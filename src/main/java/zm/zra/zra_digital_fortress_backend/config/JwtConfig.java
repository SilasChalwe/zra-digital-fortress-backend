// package zm.zra.zra_digital_fortress_backend.config;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import zm.zra.zra_digital_fortress_backend.security.JwtTokenProvider;

// @Configuration
// public class JwtConfig {

//     @Value("${app.jwtSecret}")
//     private String jwtSecret;

//     @Value("${app.jwtExpirationMs}")
//     private int jwtExpirationMs;

//     @Bean
//     public JwtTokenProvider jwtTokenProvider() {
//         return new JwtTokenProvider(jwtSecret, jwtExpirationMs);
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//         return authConfig.getAuthenticationManager();
//     }
// }