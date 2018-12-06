package com.tahariot.emulator.emulatorcore;

import com.tahariot.emulator.emulatorcore.api.*;
import com.tahariot.emulator.emulatorcore.business.transactions.AssemblyOnlinePlan;
import com.tahariot.emulator.emulatorcore.business.transactions.CoatingOfflinePlan;
import com.tahariot.emulator.emulatorcore.emulators.SimpleEmulator;
import com.tahariot.emulator.emulatorcore.rasterizers.EvenPossibilityShuffler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
//@TestPropertySource(properties = {"emulator.json.input=./test/resources/abcd.json"})
public class EmulatorComplexTests {
    private static Logger logger = LoggerFactory.getLogger(EmulatorComplexTests.class);

    @Autowired
    EmulatorFactory emulatorFactory;

//    @Value("${emulator.json.input}")
//    File inputFile;

    @Test
	public void test() throws Exception {
        logger.info("[--Emulation--]");

        long start = System.currentTimeMillis();

        String category = "simple";

        final long MAXTAKT = 100;

        final int PBSAVG = 2;
        final int PBSCAP = 3;

        List<CoatingOfflinePlan> coatingOfflinePlan = new ArrayList<>(); //涂装下线计划

        for(int i = 0; i < 5; ++i) {
            coatingOfflinePlan.add(new CoatingOfflinePlan(i, String.format("VIN_%d", i)));
        }

        List<AssemblyOnlinePlan> assemblyOnlinePlan =  new ArrayList<>();           //总装上线计划

        for(int i = 0; i < 3; ++i) {
            assemblyOnlinePlan.add(new AssemblyOnlinePlan(i, String.format("VIN_%d", i), "00"));
        }

        List<CoatingOfflinePlan> pbsQueue = new ArrayList<>();                     //PBS queue
        List<AssemblyOnlinePlan> assemblyOnlineActual = new ArrayList<>();         //总装上线实绩

        AtomicLong coatingIndex = new AtomicLong();

        Kernel kernel = (Kernel<CoatingOfflinePlan, CoatingOfflinePlan>) (takt, data, result, context) -> {
            CoatingOfflinePlan ret = null;

            CoatingOfflinePlan datium = coatingIndex.get() < data.size() ? data.get((int)coatingIndex.get()) : null;

            if(datium != null&& pbsQueue.size() <= PBSCAP) {
                result.info(takt, ret, String.format("PBS QUEUE BEFORE : %s", pbsQueue.toString()));
                pbsQueue.add(datium);
                coatingIndex.incrementAndGet();
                result.info(takt, ret, String.format("PBS QUEUE IN : %d", ret));
                result.info(takt, ret, String.format("PBS QUEUE AFTER : %s", pbsQueue.toString()));
                
                ret = datium;
            }

            if(pbsQueue.size() > PBSAVG) {
                CoatingOfflinePlan i = null;

                for(int j = 0; j < pbsQueue.size(); ++j) {
                    final int fj = j;
                    if(assemblyOnlinePlan.stream().filter(p -> p.getVIN().equals(pbsQueue.get(fj).getVIN())).count() > 0) {
                        i = pbsQueue.stream().min((o1, o2) -> (o1.getSeq() < o2.getSeq() ? -1 : (o1.getSeq() > o2.getSeq() ? 1 : 0))).get();
                        pbsQueue.remove(i);
                        result.info(takt, ret, String.format("PBS QUEUE OUT : %s", i.toString()));
                        break;
                    }
                }

                final CoatingOfflinePlan fi = i;

                AssemblyOnlinePlan aplan = assemblyOnlinePlan.stream().filter(p -> p.getVIN().equals(fi.getVIN())).findFirst().orElse(null);

                if(aplan != null) {
                    aplan.setStatus("1");
                    assemblyOnlineActual.add(aplan);
                }
            }

            return ret;
        };

        logger.info("kernel : {}", kernel);

        Input input = new Input<CoatingOfflinePlan, CoatingOfflinePlan>() {
            @Override
            public Rasterizer<CoatingOfflinePlan> getRasterizer() {
                return new EvenPossibilityShuffler<CoatingOfflinePlan>((variables, context) -> {
                    ArrayList<CoatingOfflinePlan> ret = new ArrayList<>();
                    ret.addAll(coatingOfflinePlan);
                    return ret;
                });
            }

            @Override
            public Rule<CoatingOfflinePlan> getOutputRule() {
                return (value, context) -> (value != null);
            }
        };

        logger.info("input : {}", input);

        Map<String, Object> variables = new HashMap<>();

        variables.put(SimpleEmulator.VAR_EMULATOR_TAKT_MAX, MAXTAKT);

        logger.info("variables : {}", variables);

        Emulator emulator = emulatorFactory.newInstance(category, kernel, variables);

        logger.info("emulator : {}", emulator);

        Map<String, Object> context = new HashMap<>();

        Result<Integer, Integer> result = emulator.emulate(input, context);

        logger.info("coatingOfflinePlan : {}", coatingOfflinePlan);
        logger.info("assemblyOnlinePlan : {}", assemblyOnlinePlan);
        logger.info("pbsQueue residuals : {}", pbsQueue);
        logger.info("coatingOfflineActual : {}", result.data());
        logger.info("assemblyOnlineActual : {}", assemblyOnlineActual);

        for(History h : result.history().stream().filter(h -> h.level() == History.Level.INFOR).collect(Collectors.toList())) {
            logger.info("\t({}) {}", h.level(), h.toString());
        }

        long end = System.currentTimeMillis();

        logger.info("INTERVAL : {}", end - start);
	}

}
