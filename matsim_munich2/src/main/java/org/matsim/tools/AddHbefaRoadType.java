package org.matsim.tools;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

public class AddHbefaRoadType {

    public static void main(String[] args) {
        // Path to the input MATSim network file
        String inputNetworkFile = "D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\studyNetworkDense.xml\\studyNetworkDense.xml";
        // Path to the output MATSim network file
        String outputNetworkFile = "D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\studyNetworkDense.xml\\updated_munich_network.xml";

        // Read the network
        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile("D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\studyNetworkDense.xml\\studyNetworkDense.xml");

        // Add the hbefa_road_type attribute to each link
        for (Link link : network.getLinks().values()) {
            // Determine HBEFA road type based on the link's attributes
            String hbefaRoadType = getHbefaRoadType(link);
            // Add the hbefa_road_type attribute to the link
            link.getAttributes().putAttribute("hbefa_road_type", hbefaRoadType);
        }

        // Write the updated network to a new file
        new NetworkWriter(network).write("D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\studyNetworkDense.xml\\updated_munich_network.xml");
        System.out.println("Updated network saved to: " + outputNetworkFile);
    }

    /**
     * Determines the HBEFA road type for a given link.
     * Modify this function to suit your specific classification logic.
     */
    private static String getHbefaRoadType(Link link) {
        String roadType = "UNKNOWN"; // Default value
        double speed = link.getFreespeed() * 3.6; // Convert m/s to km/h
        double length = link.getLength(); // Length in meters

        // Example classification logic
        if (length < 1000) { // Short links -> local roads
            if (speed <= 50) {
                roadType = "URB/Local/50";
            } else if (speed <= 70) {
                roadType = "URB/Local/70";
            } else {
                roadType = "RUR/Local/90";
            }
        } else if (speed > 70) { // Higher speeds -> main roads or motorways
            if (speed <= 100) {
                roadType = "URB/Main/100"; // Urban main road
            } else if (speed <= 120) {
                roadType = "RUR/Main/120"; // Rural main road
            } else {
                roadType = "RUR/Motorway/130"; // Rural motorway
            }
        } else { // Default to urban main road
            if (speed <= 50) {
                roadType = "URB/Main/50";
            } else if (speed <= 80) {
                roadType = "RUR/Main/80"; // Rural main road
            }
        }

        return roadType;
    }
}
