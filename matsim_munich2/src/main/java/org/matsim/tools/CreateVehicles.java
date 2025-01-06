package org.matsim.tools;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.*;

import java.util.*;

public class CreateVehicles {

    public static void main(String[] args) {
        // Create Scenario and Read Population
        Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        new PopulationReader(scenario).readFile("D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\matsimPlans_5_percent.xml\\matsim_population_1_percent.xml");

        // Create Vehicles Container
        Vehicles vehicles = VehicleUtils.createVehiclesContainer();

        // Define HBEFA vehicle categories and probabilities
        Map<String, Map<String, Double>> hbefaMapping = Map.of(
                "pass. car", Map.of(
                        "PC-D-Euro-4", 0.17,
                        "PC-D-Euro-5", 0.21,
                        "PC-D-Euro-6", 0.48,
                        "PC-P-Euro-4", 0.07,
                        "PC-P-Euro-5", 0.07
                ),
                "HGV", Map.of(
                        "HDV-D-Euro-5", 0.25,
                        "HDV-D-Euro-6", 0.55,
                        "HDV-P-Euro-4", 0.20
                )
        );

        // Create Vehicle Types
        for (String category : hbefaMapping.keySet()) {
            Map<String, Double> typeProbabilities = hbefaMapping.get(category);

            for (String type : typeProbabilities.keySet()) {
                String vehicleTypeId = type;
                VehicleType vehicleType = VehicleUtils.createVehicleType(Id.create(vehicleTypeId, VehicleType.class));
                vehicleType.setNetworkMode("car"); // Assuming all use "car" network mode
                vehicleType.setMaximumVelocity(130.0 / 3.6); // Assuming max speed is 130 km/h
                vehicles.addVehicleType(vehicleType);

                // Set engine information
                EngineInformation engineInfo = vehicleType.getEngineInformation();
                String[] hbefaComponents = type.split("-");
                VehicleUtils.setHbefaVehicleCategory(engineInfo, category);
                VehicleUtils.setHbefaTechnology(engineInfo, hbefaComponents[1]); // E.g., "D" or "P"
                VehicleUtils.setHbefaSizeClass(engineInfo, "average");
                VehicleUtils.setHbefaEmissionsConcept(engineInfo, hbefaComponents[2]); // E.g., "Euro-4"
            }
        }

        // Randomly assign vehicles to population
        Random random = new Random();
        for (Person person : scenario.getPopulation().getPersons().values()) {
            String category = "pass. car"; // Example: Assign all as passenger cars, can adjust for other categories
            Map<String, Double> typeProbabilities = hbefaMapping.get(category);

            // Select vehicle type based on probabilities
            String assignedVehicleType = selectVehicleTypeByProbability(typeProbabilities, random.nextDouble());
            VehicleType vehicleType = vehicles.getVehicleTypes().get(Id.create(assignedVehicleType, VehicleType.class));
            Vehicle vehicle = VehicleUtils.createVehicle(Id.createVehicleId(person.getId()), vehicleType);
            vehicles.addVehicle(vehicle);
        }

        // Write the vehicles file
        new MatsimVehicleWriter(vehicles).writeFile("D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\matsimPlans_5_percent.xml\\vehicles.xml");
    }

    /**
     * Select a vehicle type based on probabilities.
     *
     * @param probabilities Map of vehicle types to probabilities
     * @param randomValue Random value between 0 and 1
     * @return Selected vehicle type ID
     */
    private static String selectVehicleTypeByProbability(Map<String, Double> probabilities, double randomValue) {
        double cumulative = 0.0;
        for (Map.Entry<String, Double> entry : probabilities.entrySet()) {
            cumulative += entry.getValue();
            if (randomValue <= cumulative) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("Failed to select vehicle type. Check probabilities.");
    }
}