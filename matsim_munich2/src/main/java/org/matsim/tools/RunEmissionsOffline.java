package org.matsim.tools;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.emissions.EmissionModule;
import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Injector;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.EventWriterXML;
import org.matsim.core.scenario.ScenarioUtils;


public final class RunEmissionsOffline{

    private static final String configFile = "D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\studyNetworkDense.xml\\config_emissions.xml";
    private final static String outputDirectory = "D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\outputExpressBus";

    private static final String eventsFile =  "D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\outputExpressBus\\output_events.xml.gz";
    // (remove dependency of one test/execution path from other. kai/ihab, nov'18)

    private static final String emissionEventOutputFileName = "D:\\TUM TS\\SEM 3\\Project_Seminar\\MATSim\\Data\\outputExpressBus\\emission.events.offline.xml.gz";
    private Config config;

    // =======================================================================================================

    public static void main (String[] args){
        RunEmissionsOffline emissionToolOfflineExampleV2 = new RunEmissionsOffline();
        emissionToolOfflineExampleV2.run();
    }

    public void prepareConfig() {
        config = ConfigUtils.loadConfig(configFile, new EmissionsConfigGroup());
    }

    public Config prepareConfig(String configFile) {
        config = ConfigUtils.loadConfig(configFile, new EmissionsConfigGroup());
        return config;
    }

    public void run() {
        Config config = ConfigUtils.loadConfig(configFile, new EmissionsConfigGroup());
        Scenario scenario = ScenarioUtils.loadScenario(config);
        EventsManager eventsManager = EventsUtils.createEventsManager();

        AbstractModule module = new AbstractModule(){
            @Override
            public void install(){
                bind( Scenario.class ).toInstance( scenario );
                bind( EventsManager.class ).toInstance( eventsManager );
                bind( EmissionModule.class ) ;
            }
        };;

        com.google.inject.Injector injector = Injector.createInjector(config, module );

        EmissionModule emissionModule = injector.getInstance(EmissionModule.class);

        EventWriterXML emissionEventWriter = new EventWriterXML( outputDirectory + emissionEventOutputFileName );
        emissionModule.getEmissionEventsManager().addHandler(emissionEventWriter);

        MatsimEventsReader matsimEventsReader = new MatsimEventsReader(eventsManager);
        matsimEventsReader.readFile(eventsFile);

        emissionEventWriter.closeFile();
    }
}