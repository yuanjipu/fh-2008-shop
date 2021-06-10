package com.fh.shop.api;

//import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fh.shop.api.member.mapper")
//@EnableSwagger2Doc
public class ShopMemberApp {

    public static void main(String[] args) {
        SpringApplication.run(ShopMemberApp.class,args);
    }
}
