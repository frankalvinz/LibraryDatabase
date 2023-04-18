package ca.chibueze_ekwomadu.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

/**
 * This is the Web Security configuration class
 * @author chibueze frank ekwomadu
 * November 30, 2022
 */
@SuppressWarnings("deprecation")
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	private LoggingAccessDeniedHandler accessDeniedHandler;
	private BCryptPasswordEncoder encoder;
	private DataSource dataSource;
	
	/**
	 * Overloaded constructor of this class
	 * @param accessDeniedHandler is initialized here
	 * @param encoder is initialized here. It is annotated with @Lazy to avoid errors
	 * @param dataSource is initialized here
	 */
	public SecurityConfig(LoggingAccessDeniedHandler accessDeniedHandler,
			@Lazy BCryptPasswordEncoder encoder,
			DataSource dataSource) {
		this.accessDeniedHandler = accessDeniedHandler;
		this.encoder = encoder;
		this.dataSource = dataSource;
	}
	
	/**
	 * BCryptPasswordEncoder bean method managed by Spring Container
	 * @return new BCryptPasswordEncoder()
	 */
	@Bean
	public BCryptPasswordEncoder createPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * JdbcUserDetailsManager bean method managed by Spring Container
	 * @return jdbcUserDetailsManager
	 * @throws Exception
	 */
	@Bean
	public JdbcUserDetailsManager jdbcUserDetailsManager() throws Exception {	
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
		jdbcUserDetailsManager.setDataSource(dataSource); ///Link it with dataSource
		return jdbcUserDetailsManager;
	}
	
	/**
	 * This is a configuration method. Here, user roles, path restrictions, login and
	 * accessDeniedHandler are created/configured.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests()
			.antMatchers("/user/**").hasAnyRole("USER", "ADMIN") //only USER or ADMIN roles has access to /user
			.antMatchers("/admin/**").hasRole("ADMIN") //only ADMIN role has access to /admin
			.antMatchers("/h2-console/**").permitAll() //full access is permitted to h2Console (no restrictions)
			.antMatchers("/", "/**").permitAll() //full access is permitted to root (no restrictions)
			.and() //allows chain configuration calls of functions()
			.formLogin().loginPage("/login") //maps login to a custom login.html
			.defaultSuccessUrl("/user")
			.and() //add extra layer of security
			.logout().invalidateHttpSession(true)
			.clearAuthentication(true)
			.and()
			.exceptionHandling() //use my handler
			.accessDeniedHandler(accessDeniedHandler); //maps access denied to my handler, "accessDeniedHandler"
		//This is only used for the h2 database because I am accessing 
		//the console from the same server (LocalHost)
		http.csrf().disable();
		http.headers().frameOptions().disable();
	}
	
	/**
	 * This is a configuration method. Here, users are created and roles assigned to them.
	 * The passwords are encrypted using the BCryptEncoder.
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		
		auth.jdbcAuthentication()
		.dataSource(dataSource) //maps dataSource with my handler, "dataSource"
		.withDefaultSchema() //adds default schema of all the USERS & AUROTHORITIES info to h2Database
		.passwordEncoder(encoder) //maps passwordEncoder with my handler, "encoder"
		.withUser("bugs").password(encoder.encode("bunny")).roles("USER")
		.and()
		.withUser("daffy").password(encoder.encode("duck")).roles("USER", "ADMIN");
	}
}
