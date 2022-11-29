package tn.essat.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired //Injecter la base de données
	@Qualifier("dataSource")
	DataSource data;

	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		PasswordEncoder crypte = cryptage();
		auth.jdbcAuthentication()
		.dataSource(data)
		.usersByUsernameQuery("select username as principal , password as credentials, active from user where username=?")
		.authoritiesByUsernameQuery("select user_username as principal, roles_role as role from user_roles where user_username=?")
		.passwordEncoder(crypte)
		.rolePrefix("ROLE_");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin();
		http.authorizeRequests().antMatchers("/add**/**").hasRole("ADMIN");
		http.authorizeRequests().anyRequest().authenticated();
		http.csrf();

	}

	@Bean
	public PasswordEncoder cryptage() {
		return new BCryptPasswordEncoder();
	}

}
