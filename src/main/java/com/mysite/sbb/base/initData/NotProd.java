package com.mysite.sbb.base.initData;


import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Configuration
@Profile({"local", "test"})
public class NotProd {
    @Bean
    CommandLineRunner initData(
            UserService userService
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {

                userService.create("admin","admin@test.com", "1234");

                SiteUser user1 = userService.create("user1", "user1@test.com", "1234");
                SiteUser user2 = userService.create("user2", "user2@test.com", "1234");

                SiteUser user3ByKakao = userService.whenSocialLogin("KAKAO", "KAKAO__3005003556");

            }

        };
    }
}