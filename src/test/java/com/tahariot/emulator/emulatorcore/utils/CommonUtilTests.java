package com.tahariot.emulator.emulatorcore.utils;

import com.tahariot.emulator.emulatorcore.business.ut.VehicleFeatureTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class CommonUtilTests {
    private static Logger logger = LoggerFactory.getLogger(VehicleFeatureTests.class);

    @Test
    public void test0() {
        logger.info("VARIANCE({1, -1, 0, 2, -2}) = {}", CommonUtil.variance(new double[]{1, -1, 0, 2, -2}));
    }

    @Test
    public void test() {
        List<Integer> v1 = Arrays.asList(1,2,3,4,5,6);
        List<Integer> v2 = Arrays.asList(4,6,3,2,5,1);

        logger.info("v1 : {}", v1);
        logger.info("v2 : {}", v2);
        logger.info("VARIANCE = {}", CommonUtil.variance(CommonUtil.offsetDiff(v1, v2, -1)));
    }
}
