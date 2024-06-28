package volumen.security;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

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
		http.csrf((csrf) -> csrf // TODO: не хочу пока возиться, да и кому всё это надо
                .ignoringRequestMatchers("/**")
            ).authorizeHttpRequests(auth -> auth
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
}
