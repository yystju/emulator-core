package com.tahariot.emulator.emulatorcore.business.ut;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tahariot.emulator.emulatorcore.business.vo.VehicleFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(JUnit4.class)
public class VehicleFeatureTests {
    private static Logger logger = LoggerFactory.getLogger(VehicleFeatureTests.class);

    @Test
    public void test() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        VehicleFeature vf = new VehicleFeature();

        vf.setVehicleCode("1");
        vf.setFeatureCode("1");
        vf.setFeatureValue("100");

        logger.info("vc : {}", mapper.writeValueAsString(vf));
    }
}
