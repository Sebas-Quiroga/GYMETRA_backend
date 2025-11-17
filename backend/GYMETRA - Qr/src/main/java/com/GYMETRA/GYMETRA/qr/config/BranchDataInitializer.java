package com.GYMETRA.GYMETRA.qr.config;

import com.GYMETRA.GYMETRA.qr.entity.Branch;
import com.GYMETRA.GYMETRA.qr.repository.BranchRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BranchDataInitializer {
    @Bean
    public CommandLineRunner initBranches(BranchRepository branchRepository) {
        return args -> {
            if (branchRepository.count() == 0) {
                Branch b1 = new Branch();
                b1.setName("Sucursal Norte");
                b1.setAddress("Calle 1 #123");
                b1.setCity("Ciudad A");
                b1.setCapacity(100);

                Branch b2 = new Branch();
                b2.setName("Sucursal Centro");
                b2.setAddress("Avenida 45 #456");
                b2.setCity("Ciudad B");
                b2.setCapacity(150);

                Branch b3 = new Branch();
                b3.setName("Sucursal Sur");
                b3.setAddress("Carrera 9 #789");
                b3.setCity("Ciudad C");
                b3.setCapacity(120);

                branchRepository.save(b1);
                branchRepository.save(b2);
                branchRepository.save(b3);
            }
        };
    }
}
