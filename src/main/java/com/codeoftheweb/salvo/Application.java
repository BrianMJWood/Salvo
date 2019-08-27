package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository,
                                      GamePlayerRepository gamePlayerRepository,
                                      ShipRepository shipRepository,
                                      SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
        return (args) -> {

            Player p1 = new Player("j.bauer@ctu.gov", passwordEncoder().encode("123"));
            Player p2 = new Player("jim.bean@gmail.com", passwordEncoder().encode("123"));
            Player p3 = new Player("doug.jones@gmail.com", passwordEncoder().encode("123"));

            Score score1 = new Score(1.0, "Sept 24 2018");
            Score score2 = new Score(0.0, "Oct 1 2016");
            Score score3 = new Score(0.0, "Oct 31 2016");
            Score score4 = new Score(0.5, "Oct 5 2016");
            Score score5 = new Score(1.0, "Oct 25 2016");
            Score score6 = new Score(1.0, "Oct 15 2016");
            Score score7 = new Score(1.0, "Oct 5 2011");


            p1.addScore(score1);
            p2.addScore(score2);
            p1.addScore(score3);
            p1.addScore(score4);

            p3.addScore(score5);
            p3.addScore(score6);
            p3.addScore(score7);



            Game g1 = new Game();
            Game g2 = new Game();
            Game g3 = new Game();



            GamePlayer gp1 = new GamePlayer(p1, g1);
            GamePlayer gp2 = new GamePlayer(p2, g1);
            GamePlayer gp3 = new GamePlayer(p1, g2);
            GamePlayer gp4 = new GamePlayer(p3, g3);


            g1.addScore(score1);
            g1.addScore(score2);



            List s1FrigateLocation = new ArrayList();
            List s2SubmarineLocation = new ArrayList();
            List s3DestroyerLocation = new ArrayList();

            List s4FrigateLocation = new ArrayList();
            List s5SubmarineLocation = new ArrayList();
            List s6DestroyerLocation = new ArrayList();

            s1FrigateLocation.addAll(Arrays.asList("H1", "H2", "H3"));
            s2SubmarineLocation.addAll(Arrays.asList("A1", "A2"));
            s3DestroyerLocation.addAll(Arrays.asList("D1", "D2", "D3", "D4"));

            s4FrigateLocation.addAll(Arrays.asList("E1", "E2", "E3"));
            s5SubmarineLocation.addAll(Arrays.asList("G1", "G2"));
            s6DestroyerLocation.addAll(Arrays.asList("A1", "B1", "C1", "D1"));



            Ship s1 = new Ship("Frigate", s1FrigateLocation);
            Ship s2 = new Ship("Submarine", s2SubmarineLocation);
            Ship s3 = new Ship("Destroyer", s3DestroyerLocation);

            Ship s4 = new Ship("Frigate", s4FrigateLocation);
            Ship s5 = new Ship("Submarine", s5SubmarineLocation);
            Ship s6 = new Ship("Destroyer", s6DestroyerLocation);

            s1.setLocations(s1FrigateLocation);
            s2.setLocations(s2SubmarineLocation);
            s3.setLocations(s3DestroyerLocation);

            gp1.addShip(s1);
            gp1.addShip(s2);
            gp1.addShip(s3);

            gp2.addShip(s4);
            gp2.addShip(s5);
            gp2.addShip(s6);

            List sal1Locations = new ArrayList();
            List sal2Locations = new ArrayList();
            List sal3Locations = new ArrayList();
            List sal4Locations = new ArrayList();

            sal1Locations.addAll(Arrays.asList("D3", "G5"));
            sal2Locations.addAll(Arrays.asList("J2", "B7"));
            sal3Locations.addAll(Arrays.asList("C7", "E1"));
            sal4Locations.addAll(Arrays.asList("H7", "A8"));

            Salvo sal1 = new Salvo(1, sal1Locations);
            Salvo sal2 = new Salvo(1, sal2Locations);
            Salvo sal3 = new Salvo(2, sal3Locations);
            Salvo sal4 = new Salvo(2, sal4Locations);

            gp1.addSalvo(sal1);
            gp2.addSalvo(sal2);
            gp1.addSalvo(sal3);
            gp2.addSalvo(sal4);

            playerRepository.save(p1);
            playerRepository.save(p2);
            playerRepository.save(p3);

            playerRepository.save(new Player("Chloe", "123"));
            playerRepository.save(new Player("Kim", "123"));
            playerRepository.save(new Player("David", "123"));
            playerRepository.save(new Player("Michelle", "123"));



            gameRepository.save(g1);
            gameRepository.save(g2);
            gameRepository.save(g3);

            gamePlayerRepository.save(gp1);
            gamePlayerRepository.save(gp2);
            gamePlayerRepository.save(gp3);
            gamePlayerRepository.save(gp4);

            shipRepository.save(s1);
            shipRepository.save(s2);
            shipRepository.save(s3);

            shipRepository.save(s4);
            shipRepository.save(s5);
            shipRepository.save(s6);


            salvoRepository.save(sal1);
            salvoRepository.save(sal2);
            salvoRepository.save(sal3);
            salvoRepository.save(sal4);

            scoreRepository.save(score1);
            scoreRepository.save(score2);
            scoreRepository.save(score3);
            scoreRepository.save(score4);
            scoreRepository.save(score5);
            scoreRepository.save(score6);
            scoreRepository.save(score7);

        };
    }

}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputName-> {
            Player player = playerRepository.findByUsername(inputName);
            if (player != null) {
                return new User(player.getUsername(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }
        });
    }
}
@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/games").permitAll()
                .antMatchers("/api/players").permitAll()
                .antMatchers("/web/games.html").permitAll()
                .antMatchers("/web/scripts/games.js").permitAll()
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                .antMatchers("/api/game_view/**").hasAuthority("USER")
                .and()
                .formLogin()
                .usernameParameter("name")
                .passwordParameter("pwd")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");

        // turn off checking for CSRF tokens
        http.csrf().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
        http.headers().frameOptions().disable();
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
}}