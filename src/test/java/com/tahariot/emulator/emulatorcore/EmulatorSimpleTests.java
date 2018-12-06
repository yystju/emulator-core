package com.tahariot.emulator.emulatorcore;

import com.tahariot.emulator.emulatorcore.api.*;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
//@TestPropertySource(properties = {"emulator.json.input=./test/resources/abcd.json"})
public class EmulatorSimpleTests {
    private static Logger logger = LoggerFactory.getLogger(EmulatorSimpleTests.class);

    @Autowired
    EmulatorFactory emulatorFactory;

//    @Value("${emulator.json.input}")
//    File inputFile;

    @Test
	public void contextLoads() throws Exception {
        logger.info("[--Emulation--]");

        long start = System.currentTimeMillis();

        String category = "simple";

        final long MAXTAKT = 100;

        final int PBSAVG = 2;
        final int PBSCAP = 3;

        List<Integer> coatingOfflinePlan = Arrays.asList(1,2,3,4,5);    //涂装下线计划
        List<Integer> assemblyOnlinePlan = Arrays.asList(1,2,3);        //总装上线计划

        List<Integer> pbsQueue = new ArrayList<>();                     //PBS queue
        List<Integer> assemblyOnlineActual = new ArrayList<>();         //总装上线实绩
        AtomicInteger coatingIndex = new AtomicInteger();

        Kernel kernel = (Kernel<Integer, Integer>) (takt, data, result, context) -> {
            int ret = 0;

            Integer datium = coatingIndex.get() < data.size() ? data.get(coatingIndex.get()) : null;

            if(datium != null&& pbsQueue.size() <= PBSCAP) {
                result.info(datium, ret, String.format("PBS QUEUE BEFORE : %s", pbsQueue.toString()));
                pbsQueue.add(datium);
                coatingIndex.incrementAndGet();
                result.info(datium, ret, String.format("PBS QUEUE IN : %d", ret));
                result.info(datium, ret, String.format("PBS QUEUE AFTER : %s", pbsQueue.toString()));

                ret = datium;
            }

            if(pbsQueue.size() > PBSAVG) {
                Integer i = 0;

                for(int j = 0; j < pbsQueue.size(); ++j) {
                    if(assemblyOnlinePlan.contains(pbsQueue.get(j))) {
                        i = pbsQueue.remove(j);
                        result.info(datium, ret, String.format("PBS QUEUE OUT : %d", i));
                        break;
                    }
                }

                assemblyOnlineActual.add(i);
            }

            return ret;
        };

        logger.info("kernel : {}", kernel);

        Input input = new Input<Integer, Integer>() {
            @Override
            public Rasterizer<Integer> getRasterizer() {
                return new EvenPossibilityShuffler<Integer>((variables, context) -> {
                    ArrayList<Integer> ret = new ArrayList<>();
                    ret.addAll(coatingOfflinePlan);
                    return ret;
                });
            }

            @Override
            public Rule<Integer> getOutputRule() {
                return (value, context) -> (value != null && 0 != value);
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
