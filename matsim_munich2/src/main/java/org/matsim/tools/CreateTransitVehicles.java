package org.matsim.tools;

import org.matsim.api.core.v01.Id;
import org.matsim.vehicles.*;

public class CreateTransitVehicles {

    public static void main(String[] args) {

        Vehicles vehicles = VehicleUtils.createVehiclesContainer();

        // Define the vehicle type for public buses in Munich
        VehicleType publicBusType = VehicleUtils.createVehicleType(Id.create("PublicBus", VehicleType.class));
        publicBusType.setNetworkMode("bus");

        // Set vehicle capacity
        VehicleCapacity capacity = publicBusType.getCapacity();
        capacity.setSeats(30);
        capacity.setStandingRoom(30);

        vehicles.addVehicleType(publicBusType);

        // Set HBEFA attributes for the vehicle type
        EngineInformation engineInformation = publicBusType.getEngineInformation();
        VehicleUtils.setHbefaVehicleCategory(engineInformation, "urban bus");
        VehicleUtils.setHbefaSizeClass(engineInformation, "average");
        VehicleUtils.setHbefaTechnology(engineInformation, "average");

        // Add proportions for different fuel types
        VehicleUtils.setHbefaEmissionsConcept(engineInformation, "average");

        for (int i = 8 * 3600; i < 18 * 3600; i += 5 * 60) {
            Id<Vehicle> vehicleId = Id.createVehicleId("departure_" + i);
            Vehicle vehicle = VehicleUtils.createVehicle(vehicleId, publicBusType);
            vehicles.addVehicle(vehicle);
        }

        // Add proportions for emission standards
        String[][] emissionStandards = {
                {"Euro 1", "0.07"},
                {"Euro 2", "1.15"},
                {"Euro 3", "4.87"},
                {"Euro 4", "3.20"},
                {"Euro 5", "8.89"},
                {"Euro 6", "64.17"},
                {"others", "17.66"}
        };

        String[][] fuelTypes = {
                {"petrol", "0.06"},
                {"diesel", "88.02"},
                {"hybrid", "7.80"},
                {"electric", "2.51"}
        };

        // Assign emissions and fuel standards proportionally (for demo purposes, exact logic may vary based on modeling needs)
        for (String[] fuel : fuelTypes) {
            for (String[] emission : emissionStandards) {
                System.out.println("Fuel Type: " + fuel[0] + ", Proportion: " + fuel[1] + ", Emission Standard: " + emission[0] + ", Proportion: " + emission[1]);
                // Logic to create vehicle entries combining fuel types and emission standards can go here
            }
        }

        // Write vehicles to XML
        new MatsimVehicleWriter(vehicles).writeFile("D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\matsimPlans_5_percent.xml\\publicBusVehicles.xml");
    }
}
