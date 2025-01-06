package org.matsim.tools;

import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.NetworkConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.MatsimServices;

public final class CreateEmissionsConfig {

    private static final String networkFile = "D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\studyNetworkDense.xml\\updated_munich_network.xml";

    private static final String detailedFleetWarmEmissionFactorsFile = "D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\EmissionModelling\\EFA_HOT_Concept_datahbefa2020.XLS";
    private static final String detailedFleetColdEmissionFactorsFile = "D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\EmissionModelling\\EFA_ColdStart_Concept_datahbefa2020.XLS";

    private static final String configFilePath = "D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\studyNetworkDense.xml\\config_emissions.xml";

    public static void main(String[] args) {

        Config config = new Config();
        config.addCoreModules();
        MatsimServices controler = new Controler(config);

        //vehicles
        config.vehicles().setVehiclesFile("D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\matsimPlans_5_percent.xml\\vehicles.xml");
        config.transit().setVehiclesFile("D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\matsimPlans_5_percent.xml\\publicBusVehicles.xml");

        // network
        NetworkConfigGroup ncg = controler.getConfig().network();
        ncg.setInputFile(networkFile);

        // define emission tool input files
        EmissionsConfigGroup ecg = new EmissionsConfigGroup();
        controler.getConfig().addModule(ecg);


        // Replace Boolean with enum for vehicle description source
        ecg.setHbefaRoadTypeSource(EmissionsConfigGroup.HbefaRoadTypeSource.fromLinkAttributes);
        ecg.setHbefaVehicleDescriptionSource(EmissionsConfigGroup.HbefaVehicleDescriptionSource.usingVehicleTypeId);
        ecg.setDetailedWarmEmissionFactorsFile(detailedFleetWarmEmissionFactorsFile);
        ecg.setDetailedColdEmissionFactorsFile(detailedFleetColdEmissionFactorsFile);
        ecg.setDetailedVsAverageLookupBehavior(EmissionsConfigGroup.DetailedVsAverageLookupBehavior.directlyTryAverageTable);
        ecg.setWritingEmissionsEvents(true);

        // write config
        ConfigWriter cw = new ConfigWriter(config);
        cw.write(configFilePath);
    }
}
