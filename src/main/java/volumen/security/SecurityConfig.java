package volumen.security;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import volumen.data.UsersRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	UserDetailsService userDetailsService(UsersRepository userRepo) {
		return username -> {
			var user = userRepo.findByUsername(username);
			if (user != null) {
				var details = User.builder().username(username).password(user.getPassword());
				String roles[] = user.getRolesArray();
				if (roles != null)
					details.authorities(roles);
				return details.build();
			}
			throw new UsernameNotFoundException("User '" + username + "' not found");
		};
	}
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		String [] editorRoles = {"TEACHER", "ADMIN"};
		String [] allRoles = {"TEACHER", "ADMIN", "USER"};
		http
		.logout((logout)->logout.invalidateHttpSession(true).logoutSuccessUrl("/").deleteCookies("JSESSIONID"))
		.rememberMe((rem) -> rem.key("remember-me").rememberMeCookieName(AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY)
				.tokenRepository(persistentTokenRepository()).tokenValiditySeconds(86400))
		.csrf((csrf) -> csrf // TODO: пока упростим
                .ignoringRequestMatchers("/**")
            ).authorizeHttpRequests(auth -> auth
            	.requestMatchers("/userpage/**").authenticated()
				.requestMatchers("/category/add/**").hasAnyAuthority(editorRoles)
				.requestMatchers("/lecture/add/**").hasAnyAuthority(editorRoles)
				.requestMatchers("/course/add/**").hasAnyAuthority(editorRoles)
				.requestMatchers("/test/add/**").hasAnyAuthority(editorRoles)
				.requestMatchers("/category/edit/**").hasAnyAuthority(editorRoles)
				.requestMatchers("/lecture/edit/**").hasAnyAuthority(editorRoles)
				.requestMatchers("/course/edit/**").hasAnyAuthority(editorRoles)
				.requestMatchers("/test/edit/**").hasAnyAuthority(editorRoles)
				.requestMatchers("/category/delete/**").hasAnyAuthority(editorRoles)
				.requestMatchers("/lecture/delete/**").hasAnyAuthority(editorRoles)
				.requestMatchers("/course/delete/**").hasAnyAuthority(editorRoles)
				.requestMatchers("/test/delete/**").hasAnyAuthority(editorRoles)
				.requestMatchers("/", "/**").permitAll()
				.anyRequest().authenticated()
	            ).formLogin(formLogin -> formLogin
	                    .loginPage("/login")
	                    .defaultSuccessUrl("/", true)
	                    .permitAll()
	            ).headers(headers -> headers.frameOptions(FrameOptionsConfig::disable)
				);
		return http.build();
	}
	
	@Autowired
    DataSource dataSource;
	
	@Bean
    PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepositoryImpl = new JdbcTokenRepositoryImpl();
        tokenRepositoryImpl.setDataSource(dataSource);
        return tokenRepositoryImpl;
    }
}
