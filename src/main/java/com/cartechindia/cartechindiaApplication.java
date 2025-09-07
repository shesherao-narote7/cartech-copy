package com.cartechindia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class cartechindiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(cartechindiaApplication.class, args);

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));

        // ANSI escape codes
        String GREEN = "\u001B[32m";
        String RESET = "\u001B[0m";

        System.out.println("\n\n");
        System.out.println(GREEN + "PORT : localhost8080" + RESET);
        System.out.println(GREEN + "documentation : http://localhost:8080/cartech/swagger-ui" + RESET);

        System.out.println(GREEN + "  *****    *******  *******       *****   *******    *****    ******   *******" + RESET);
        System.out.println(GREEN + " *     *   *      *    *         *           *      *     *   *     *     *   " + RESET);
        System.out.println(GREEN + "*       *  *      *    *         *           *     *       *  *     *     *   " + RESET);
        System.out.println(GREEN + "*       *  *******     *          *****      *     *       *  ******      *   " + RESET);
        System.out.println(GREEN + "*********  *           *               *     *     *********  *   *       *   " + RESET);
        System.out.println(GREEN + "*       *  *           *               *     *     *       *  *    *      *   " + RESET);
        System.out.println(GREEN + "*       *  *        *******       *****      *     *       *  *     *     *   " + RESET);
	}

}
