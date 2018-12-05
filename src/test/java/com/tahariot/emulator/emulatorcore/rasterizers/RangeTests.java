package com.tahariot.emulator.emulatorcore.rasterizers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@RunWith(JUnit4.class)
public class RangeTests {
    private static Logger logger = LoggerFactory.getLogger(RangeTests.class);

    @Test
    public void test() {
        Map<String, Object> variables = new HashMap<>();

        variables.put("var1", 50);

        Map<String, Object> context = new HashMap<>();


        Range<Integer> r0 = new Range<Integer>("var1", 0, 100, orig -> (orig + 1), Comparator.naturalOrder());

        List<Integer> result = r0.rasterize(variables, context);

        logger.info("result : {}", result);

        Range<Date> r1 = new Range<Date>("var2", new Date(System.currentTimeMillis() - 86400000), new Date(System.currentTimeMillis() + 86400000), orig -> (new Date(orig.getTime() + 4 * 60 * 60 * 1000)), Comparator.naturalOrder());

        List<Date> result1 = r1.rasterize(variables, context);

        logger.info("result1 : {}", result1);
    }
}
