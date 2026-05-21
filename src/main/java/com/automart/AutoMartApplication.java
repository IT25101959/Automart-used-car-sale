package com.automart;

import com.automart.model.*;
import com.automart.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AutoMartApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoMartApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(UserRepository userRepository, VehicleRepository vehicleRepository, SparePartRepository sparePartRepository, ReviewRepository reviewRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                // Add Admin
                Admin admin = new Admin("System Admin", "admin@automart.com", "admin123", "0112345678", "Super");
                userRepository.save(admin);
                
                // Add Customer
                Customer customer = new Customer("John Doe", "john@email.com", "password", "0771234567", "Colombo");
                userRepository.save(customer);

                // Add Vehicles
                Car car1 = new Car("Toyota", "Prius", 2018, 7500000, 45000, "Hybrid", "Automatic", "https://images.unsplash.com/photo-1621007947382-bb3c3994e3fd?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", 4);
                car1.setOwner(customer);
                vehicleRepository.save(car1);

                SUV suv1 = new SUV("Honda", "Vezel", 2016, 8200000, 60000, "Hybrid", "Automatic", "https://images.unsplash.com/photo-1563720223185-11003d516935?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", true);
                suv1.setOwner(customer);
                vehicleRepository.save(suv1);
                
                Truck truck1 = new Truck("Toyota", "Hilux", 2020, 15000000, 20000, "Diesel", "Manual", "https://images.unsplash.com/photo-1559416523-140ddc3d238c?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", 1000);
                truck1.setOwner(customer);
                vehicleRepository.save(truck1);

                // Add Reviews
                PublicReview review1 = new PublicReview("Great car, runs smooth!", 5, car1, customer);
                reviewRepository.save(review1);

                // Add Spare Parts
                EnginePart part1 = new EnginePart("Spark Plugs (Set of 4)", "NGK", 8500, "High performance iridium spark plugs.", "https://images.unsplash.com/photo-1625047509168-a7026f36de04?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", 25, "Toyota Prius, Honda Vezel", "12 Months");
                BodyPart part2 = new BodyPart("Front Bumper", "OEM", 45000, "Original front bumper for Toyota Corolla.", "https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", 8, "Toyota Corolla 2018-2022", "6 Months", "White");
                TyrePart part3 = new TyrePart("All-Season Radial Tyre", "Michelin", 28000, "Premium grip and comfort all-season radial tyre.", "https://images.unsplash.com/photo-1578844251758-2f71da64c96f?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", 16, "Universal 15-inch rims", "24 Months");
                ToolPart part4 = new ToolPart("Mechanic Wrench Toolkit", "Stanley", 14500, "Complete 45-piece chrome vanadium socket wrench kit.", "https://images.unsplash.com/photo-1611078489935-0cb964de46d6?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", 12, "All Vehicles", "Lifetime");
                sparePartRepository.save(part1);
                sparePartRepository.save(part2);
                sparePartRepository.save(part3);
                sparePartRepository.save(part4);
            }
        };
    }
}
