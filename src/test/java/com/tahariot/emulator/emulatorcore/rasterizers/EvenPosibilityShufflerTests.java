package com.tahariot.emulator.emulatorcore.rasterizers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EvenPosibilityShufflerTests {
    private static Logger logger = LoggerFactory.getLogger(EvenPosibilityShufflerTests.class);

    @Test
    public void test() {
        Range<Integer> r = mock(Range.class);

        Map<String, Object> variables = new HashMap<>();
        Map<String, Object> context = new HashMap<>();

        List<Integer> data = new ArrayList<Integer>();

        data.addAll(Arrays.asList(1,3,5,7,9,2,4,5,8,10));

        when(r.rasterize(variables, context)).thenReturn(data);

        EvenPossibilityShuffler shuffler = new EvenPossibilityShuffler(r);

        long start = System.currentTimeMillis();

        logger.info("result : {}", shuffler.rasterize(variables, context));

        logger.info("INTERVAL : {}", System.currentTimeMillis() - start);
    }
}
