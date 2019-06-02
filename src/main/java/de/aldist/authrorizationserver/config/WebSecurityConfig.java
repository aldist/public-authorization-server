package de.aldist.authrorizationserver.config;

import de.aldist.authrorizationserver.service.CustomAppUsersDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final CustomAppUsersDetailsService customAppUsersDetailsService;

  @Autowired
  public WebSecurityConfig(CustomAppUsersDetailsService customAppUsersDetailsService) {
    this.customAppUsersDetailsService = customAppUsersDetailsService;
  }

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  // TODO: check if nothing is missing
  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        //.and().formLogin().permitAll()
        .and()
          .authorizeRequests()//.antMatchers("/login").permitAll()
          .anyRequest().authenticated()
        //.and().exceptionHandling().accessDeniedHandler(accessDeniedHandler())
        .and()
          .csrf().disable();
  }

  @Override
  @Autowired
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(this.customAppUsersDetailsService)
        .passwordEncoder(this.passwordEncoder());
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
