package io.github.watertao.xyao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.CountDownLatch;

@EnableScheduling
@EnableAsync
@SpringBootApplication
public class XyaoBrainApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(XyaoBrainApplication.class, args);
		new CountDownLatch(1).await();
	}

}
