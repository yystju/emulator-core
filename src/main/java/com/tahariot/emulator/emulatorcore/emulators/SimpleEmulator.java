package com.tahariot.emulator.emulatorcore.emulators;

import com.tahariot.emulator.emulatorcore.api.*;
import com.tahariot.emulator.emulatorcore.vo.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class SimpleEmulator<Inbound, Outbound> implements Emulator<Inbound, Outbound> {
    private static Logger logger = LoggerFactory.getLogger(SimpleEmulator.class);

    public static final String VAR_EMULATOR_TAKT_MAX = "var.emulator.takt.max";
    private Map<String, Object> variables;
    private Kernel<Inbound, Outbound> kernel;
    private long maxTakt = 0;

    public SimpleEmulator(Kernel<Inbound, Outbound> kernel, Map<String, Object> variables) {
        this.kernel = kernel;
        this.variables = variables;

        if(variables.containsKey(VAR_EMULATOR_TAKT_MAX)) {
            maxTakt = (long)variables.get(VAR_EMULATOR_TAKT_MAX);
        }
    }

    @Override
    public Result<Inbound, Outbound> emulate(Input<Inbound, Outbound> input, Map<String, Object> context) {
        SimpleResult<Inbound, Outbound> result = new SimpleResult<>();

        Rasterizer<Inbound> rasterizer = input.getRasterizer();

        Rule<Outbound> rule = input.getOutputRule();

        List<Inbound> data = rasterizer.rasterize(variables, context);

        logger.debug("maxTakt : {}", maxTakt);

        for(long takt = 0; takt < maxTakt; ++takt) {
            Outbound o = kernel.action(takt, data, result, context);

            logger.debug("takt : {}, o : {}", takt, o);

            if(rule.valid(o, context)) {
                result.addDatium(takt, o);

                result.info(takt, o, "STEP OVER");
            } else {
                result.debug(takt, o, "STEP");
            }
        }

        return result;
    }
}
