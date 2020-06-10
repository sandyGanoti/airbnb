package org.di.airbnb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Override
	public void configure( AuthenticationManagerBuilder authenticationManagerBuilder )
			throws Exception {
		authenticationManagerBuilder.userDetailsService( userDetailsService )
				.passwordEncoder( passwordEncoder() );
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/* allow swagger and basic urls to be unauthorized */
	@Override
	public void configure( WebSecurity web ) throws Exception {
		web.ignoring()
				.antMatchers( "/airbnb/healthcheck", "/v2/api-docs", "/configuration/ui",
						"/swagger-resources/**", "/configuration/security", "/swagger-ui.html",
						"/webjars/**" );
	}

	@Override
	protected void configure( HttpSecurity http ) throws Exception {
		String crossOriginAllowedSites=" * ";

		http.cors()
				.and()
				.csrf()
				.disable()
				.authorizeRequests()
				.antMatchers( HttpMethod.POST, "/airbnb/user/signup", "/airbnb/user/login" )
				.permitAll()
				.anyRequest()
				.authenticated()
				.and()
				//				.addFilter(new JWTAuthenticationFilter(authenticationManager()))
				//				.addFilter(new JWTAuthorizationFilter(authenticationManager()))
				// this disables session creation on Spring Security
				.sessionManagement()
				.sessionCreationPolicy( SessionCreationPolicy.STATELESS ).and().headers()
				.frameOptions()
				.sameOrigin().addHeaderWriter((request,response)->{
			response.setHeader("Cache-Control","no-cache, no-store, max-age=0, must-revalidate, private");
			response.setHeader("Pragma","no-cache");
			response.setHeader("Access-Control-Allow-Origin",crossOriginAllowedSites);
		});

		http.addFilterBefore( authenticationJwtTokenFilter(),
				UsernamePasswordAuthenticationFilter.class );
	}
}