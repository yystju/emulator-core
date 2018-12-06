package com.tahariot.emulator.emulatorcore;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.tahariot.emulator.emulatorcore.api.*;
import com.tahariot.emulator.emulatorcore.business.transactions.AssemblyOnlinePlan;
import com.tahariot.emulator.emulatorcore.business.transactions.CoatingOfflinePlan;
import com.tahariot.emulator.emulatorcore.emulators.SimpleEmulator;
import com.tahariot.emulator.emulatorcore.evaluators.SimpleEvaluator;
import com.tahariot.emulator.emulatorcore.rasterizers.EvenPossibilityShuffler;
import com.tahariot.emulator.emulatorcore.rasterizers.VarianceConstraintedPossibilityShuffler;
import com.tahariot.emulator.emulatorcore.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootApplication
public class EmulatorCoreApplication implements ApplicationRunner {
    private static Logger logger = LoggerFactory.getLogger(EmulatorCoreApplication.class);

    @Autowired
    EmulatorFactory emulatorFactory;

	public static void main(String[] args) {
		SpringApplication.run(EmulatorCoreApplication.class, args);
	}

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.debug("[--Emulation--]");

        boolean noshuffle = false;
        double shuffleRatio = Double.MAX_VALUE;
        long coatTakt = 1;
        long assemblyTakt = 1;
        long avg = 2;
        long cap = 3;
        long windowWidth = -1;

        if(args.containsOption("noshuffle")) {
            noshuffle = "true".equals(args.getOptionValues("noshuffle").get(0));
        }

        if(!noshuffle && args.containsOption("shuffule.ratio")) {
            shuffleRatio = Double.parseDouble(args.getOptionValues("shuffule.ratio").get(0));
        }

        if(args.containsOption("coating.takt.moduler")) {
            coatTakt = Long.parseLong(args.getOptionValues("coating.takt.moduler").get(0));
        }

        if(args.containsOption("assembly.takt.moduler")) {
            assemblyTakt = Long.parseLong(args.getOptionValues("assembly.takt.moduler").get(0));
        }

        if(args.containsOption("avg")) {
            avg = Long.parseLong(args.getOptionValues("avg").get(0));
        }

        if(args.containsOption("cap")) {
            cap = Long.parseLong(args.getOptionValues("cap").get(0));
        }

        if(args.containsOption("window.width")) {
            windowWidth = Long.parseLong(args.getOptionValues("window.width").get(0));
        }

        logger.debug("PARAMETERS :: noshuffle({}), shuffleRatio({}), maxtakt({}), coatTakt({}), assemblyTakt({}), avg({}), cap({}), windowWidth({})", noshuffle, shuffleRatio, coatTakt, assemblyTakt, avg, cap, windowWidth);

        String category = "simple";

        final SimpleEvaluator evaluator = new SimpleEvaluator();

//        emulatingByFileData(args, noshuffle, shuffleRatio, coatTakt, assemblyTakt, avg, cap, windowWidth, category, evaluator);
        windowWidth = 10;
        cap = 200;
        avg = 0;
        emulatingByRange(1000, noshuffle, shuffleRatio, coatTakt, assemblyTakt, avg, cap, windowWidth, category, evaluator);
    }

    private void emulatingByFileData(ApplicationArguments args, boolean noshuffle, double shuffleRatio, long coatTakt, long assemblyTakt, long avg, long cap, long windowWidth, String category, SimpleEvaluator evaluator) throws IOException {
        long start = System.currentTimeMillis();

        if(args.getNonOptionArgs().size() > 0) {
            File folder = new File(args.getNonOptionArgs().get(0));

            if(folder.exists() && folder.isDirectory()) {
                File coatingPlanFile = new File(folder, "coating.csv");
                File assemblyPlanFile = new File(folder, "assembly.csv");

                if(coatingPlanFile.exists() && coatingPlanFile.isFile() && assemblyPlanFile.exists() && assemblyPlanFile.isFile()) {
                    CSVReader coatingPlanReader = new CSVReader(new FileReader(coatingPlanFile));

                    List<CoatingOfflinePlan> coatingOfflinePlan = coatingPlanReader.readAll()
                            .stream()
                            .filter(a -> a.length == 2)
                            .map(a -> new CoatingOfflinePlan(Long.parseLong(a[0].trim()), a[1].trim()))
                            .collect(Collectors.toList());

                    coatingPlanReader.close();

                    logger.debug("coatingOfflinePlan : {}", coatingOfflinePlan);

                    CSVReader assemblyPlanReader = new CSVReader(new FileReader(assemblyPlanFile));

                    List<AssemblyOnlinePlan> assemblyOnlinePlan = assemblyPlanReader.readAll()
                            .stream()
                            .filter(a -> a.length == 3)
                            .map(a -> new AssemblyOnlinePlan(Long.parseLong(a[0]), a[1].trim(), a[2].trim()))
                            .collect(Collectors.toList());

                    assemblyPlanReader.close();

                    logger.debug("assemblyOnlinePlan : {}", assemblyOnlinePlan);

                    int N = Math.max(assemblyOnlinePlan.size(), coatingOfflinePlan.size());

                    logger.info("gcd : {}", CommonUtil.lcm(coatTakt, assemblyTakt));

                    long range = N * CommonUtil.lcm(coatTakt, assemblyTakt);

                    long maxtakt = (long)(((double)range) * 1.1);

                    logger.info("maxtakt : {}", maxtakt);

                    List<CoatingOfflinePlan> pbsQueue = new ArrayList<>();                              //PBS queue
                    List<AssemblyOnlinePlan> assemblyOnlineActual = new ArrayList<>();                  //总装上线实绩

                    long finalCoatTakt = coatTakt;
                    long finalAssemblyTakt = assemblyTakt;

                    AtomicInteger coatingIndex = new AtomicInteger();

                    List<Long> coatingSpinList = new ArrayList<>();
                    List<Long> assemblySpinList = new ArrayList<>();

                    Kernel kernel = (Kernel<CoatingOfflinePlan, CoatingOfflinePlan>) (takt, data, result, context) -> {
                        logger.debug("takt : {}", takt);

                        CoatingOfflinePlan ret = null;

                        if(takt % finalCoatTakt == 0) {
                            CoatingOfflinePlan datium = (data.size() > coatingIndex.get()) ? data.get(coatingIndex.get()) : null;

                            logger.debug("datium : {}, coatingIndex : {}", datium, coatingIndex.toString());

                            if (datium != null && pbsQueue.size() <= cap) {
                                result.info(takt, ret, String.format("PBS QUEUE BEFORE : %s", pbsQueue.toString()));
                                pbsQueue.add(datium);
                                coatingIndex.incrementAndGet();
                                result.info(takt, ret, String.format("PBS QUEUE IN : %s", datium.toString()));
                                result.info(takt, ret, String.format("PBS QUEUE AFTER : %s", pbsQueue.toString()));

                                ret = datium;

                                logger.debug("[QUEUE IN] ({}) : {}", pbsQueue.size(), datium.toString());
                            } else {
                                logger.debug("[QUEUE IN SPIN] ({}) takt : {}", pbsQueue.size(), takt);
                                coatingSpinList.add(takt);
                            }
                        }

                        if(takt % finalAssemblyTakt == 0) {
                            if (pbsQueue.size() > avg) {
                                AssemblyOnlinePlan choosenPlan = null;

                                List<AssemblyOnlinePlan> plans = assemblyOnlinePlan
                                        .stream()
                                        .filter(p -> "0".equals(p.getStatus()))
                                        .sorted((o1, o2) -> (o1.getSeq() < o2.getSeq() ? -1 : (o1.getSeq() > o2.getSeq() ? 1 : 0)))
                                        .collect(Collectors.toList());

                                logger.debug("plans : {}", plans.size());

                                for (int i = 0; i < plans.size(); ++i) {
                                    AssemblyOnlinePlan plan = plans.get(i);

                                    if (pbsQueue.stream().filter(p -> p.getVIN().equals(plan.getVIN())).count() > 0) {
                                        logger.debug("pbsQueue {} contains {}", pbsQueue, plan.getVIN());
                                        choosenPlan = plan;
                                        break;
                                    } else {
                                        choosenPlan = plans
                                                .stream()
                                                .filter(p -> p.getVehicleCode().equals(plan.getVehicleCode()) && pbsQueue.stream().filter(c -> c.getVIN().equals(p.getVIN())).count() > 0)
                                                .findFirst().orElse(null);

                                        logger.debug("pbsQueue {} does not contain {}, choose the same VC code ({}) in pbsQueue. choosenPlan : {}.", pbsQueue, plan.getVIN(), plan.getVehicleCode(), choosenPlan);

                                        if (choosenPlan != null) {
                                            break;
                                        }
                                    }
                                }

                                final AssemblyOnlinePlan finalChoosenPlan = choosenPlan;

                                logger.debug("finalChoosenPlan : {}", finalChoosenPlan);

                                if (finalChoosenPlan != null) {
                                    CoatingOfflinePlan cplan = pbsQueue.stream().filter(p -> p.getVIN().equals(finalChoosenPlan.getVIN())).findFirst().orElse(null);

                                    if (cplan != null) {
                                        pbsQueue.remove(cplan);
                                        result.info(takt, ret, String.format("PBS QUEUE OUT : %s", cplan.toString()));
                                    }

                                    finalChoosenPlan.setStatus("1");
                                    assemblyOnlineActual.add(finalChoosenPlan);

                                    logger.debug("[QUEUE OUT] ({}) : {}", pbsQueue.size(), finalChoosenPlan.toString());
                                } else {
                                    assemblySpinList.add(takt);
                                    logger.debug("[QUEUE OUT SPIN] ({}) takt : {}", pbsQueue.size(), takt);
                                }
                            } else {
                                assemblySpinList.add(takt);
                                logger.debug("[QUEUE OUT SPIN] ({}) takt : {}", pbsQueue.size(), takt);
                            }
                        }

                        return ret;
                    };

                    logger.debug("kernel : {}", kernel);

                    Rasterizer<CoatingOfflinePlan> rasterizer = null;

                    if(noshuffle) {
                        logger.debug("CHOSE NO SHUFFLE");
                        rasterizer = (variables, context) -> {
                            ArrayList<CoatingOfflinePlan> ret = new ArrayList<>();
                            ret.addAll(coatingOfflinePlan);
                            return ret;
                        };
                    } else if (Math.abs(Double.MAX_VALUE - shuffleRatio) < 1e-5) {
                        logger.debug("CHOSE EvenPossibilityShuffler");
                        rasterizer = new EvenPossibilityShuffler<CoatingOfflinePlan>(windowWidth, (variables, context) -> {
                            ArrayList<CoatingOfflinePlan> ret = new ArrayList<>();
                            ret.addAll(coatingOfflinePlan);
                            return ret;
                        });
                    } else {
                        logger.debug("CHOSE VarianceConstraintedPossibilityShuffler");
                        rasterizer = new VarianceConstraintedPossibilityShuffler<CoatingOfflinePlan>(windowWidth, shuffleRatio, (variables, context) -> {
                            ArrayList<CoatingOfflinePlan> ret = new ArrayList<>();
                            ret.addAll(coatingOfflinePlan);
                            return ret;
                        });
                    }

                    Rasterizer<CoatingOfflinePlan> finalRasterizer = rasterizer;

                    logger.debug("finalRasterizer : {}", finalRasterizer);

                    Input input = new Input<CoatingOfflinePlan, CoatingOfflinePlan>() {
                        @Override
                        public Rasterizer<CoatingOfflinePlan> getRasterizer() {
                            return finalRasterizer;
                        }

                        @Override
                        public Rule<CoatingOfflinePlan> getOutputRule() {
                            return (value, context) -> (value != null);
                        }
                    };

                    logger.debug("input : {}", input);

                    Map<String, Object> variables = new HashMap<>();

                    variables.put(SimpleEmulator.VAR_EMULATOR_TAKT_MAX, maxtakt);

                    logger.debug("variables : {}", variables);

                    Emulator emulator = emulatorFactory.newInstance(category, kernel, variables);

                    logger.debug("emulator : {}", emulator);

                    Map<String, Object> context = new HashMap<>();

                    Result<CoatingOfflinePlan, CoatingOfflinePlan> result = emulator.emulate(input, context);

                    long end = System.currentTimeMillis();

                    logger.debug("INTERVAL : {}", end - start);

                    long evalStart = System.currentTimeMillis();

                    logger.info("================================================================================");
                    logger.info("=====                               REPORT                                 =====");
                    logger.info("--------------------------------------------------------------------------------");
                    logger.info("PARAMETERS :: noshuffle({}), shuffleRatio({}), maxtakt({}), coatTakt({}), assemblyTakt({}), avg({}), cap({}), windowWidth({})", noshuffle, shuffleRatio, maxtakt, coatTakt, assemblyTakt, avg, cap, windowWidth);

                    logger.info("  Length of pbsQueue residuals : {}", pbsQueue.size());
                    logger.info("  Length of coatingOfflinePlan : {}", coatingOfflinePlan.size());
                    logger.info("Length of coatingOfflineActual : {}", result.data().size());
                    logger.info("  Length of assemblyOnlinePlan : {}", assemblyOnlinePlan.size());
                    logger.info("Length of assemblyOnlineActual : {}", assemblyOnlineActual.size());

                    logger.debug("  pbsQueue residuals : {}", pbsQueue);
                    logger.debug("  coatingOfflinePlan : {}", coatingOfflinePlan);
                    logger.debug("coatingOfflineActual : {}", result.data());
                    logger.debug("  assemblyOnlinePlan : {}", assemblyOnlinePlan);
                    logger.debug("assemblyOnlineActual : {}", assemblyOnlineActual);

                    Comparator<CoatingOfflinePlan> coatingComparator = (e1, e2) -> {
                        String vc1 = e1 != null ? assemblyOnlinePlan.stream().filter(p -> p.getVIN().equals(e1.getVIN())).map(p -> p.getVehicleCode()).findFirst().get() : null;
                        String vc2 = e2 != null ? assemblyOnlinePlan.stream().filter(p -> p.getVIN().equals(e2.getVIN())).map(p -> p.getVehicleCode()).findFirst().get() : null;

                        return ((vc1 == null && vc2 == null) || vc1.equals(vc2)) ? 0 : 1;
                    };

                    Comparator<AssemblyOnlinePlan> assemblyComparator = (e1, e2) -> e1.getVehicleCode().equals(e2.getVehicleCode()) ? 0 : 1;

                    double[] coatingEvaluationResult = evaluator.evaluate(coatingOfflinePlan, result.data(), coatingComparator, (e1, e2) -> {
                        return e1.getSeq() > e2.getSeq() ? 1 : (e1.getSeq() < e2.getSeq() ? -1 : 0);
                    });

                    double[] assemblyEvaluationResult = evaluator.evaluate(assemblyOnlinePlan, assemblyOnlineActual, assemblyComparator, (e1, e2) -> {
                        return e1.getSeq() > e2.getSeq() ? 1 : (e1.getSeq() < e2.getSeq() ? -1 : 0);
                    });

                    logger.info("                   names : {}", evaluator.names());
                    logger.info(" coatingEvaluationResult : {}", coatingEvaluationResult);
                    logger.info("assemblyEvaluationResult : {}", assemblyEvaluationResult);
                    logger.info("              normalized : {}", CommonUtil.normalize(assemblyEvaluationResult, coatingEvaluationResult));

                    Map<Integer, Integer> coatingCount = CommonUtil.count(CommonUtil.offsetDiffi(coatingOfflinePlan, result.data(), coatingComparator, Math.min(coatingOfflinePlan.size(), result.data().size())));
                    Map<Integer, Integer> assemblyCount = CommonUtil.count(CommonUtil.offsetDiffi(assemblyOnlinePlan, assemblyOnlineActual, assemblyComparator, Math.min(assemblyOnlinePlan.size(), assemblyOnlineActual.size())));

                    CSVWriter csvWriter = new CSVWriter(new FileWriter("./coating_statistics.csv"));

                    coatingCount.forEach((value, count) -> {
                        csvWriter.writeNext(new String[]{Integer.toString(value), Integer.toString(count)});
                    });

                    csvWriter.close();

                    CSVWriter asvWriter = new CSVWriter(new FileWriter("./assembly_statistics.csv"));

                    assemblyCount.forEach((value, count) -> {
                        asvWriter.writeNext(new String[]{Integer.toString(value), Integer.toString(count)});
                    });

                    asvWriter.close();

                    for(History h : result.history().stream().filter(h -> h.level() == History.Level.INFOR).collect(Collectors.toList())) {
                        logger.debug("\t({}) {}", h.level(), h.toString());
                    }

                    long evalEnd = System.currentTimeMillis();

                    logger.info("EVALUATION INTERVAL : {}", evalEnd - evalStart);

                    logger.info("--------------------------------------------------------------------------------");

                    final CSVWriter awriter = new CSVWriter(new FileWriter("./actual_assembly.csv"));

                    assemblyOnlineActual.forEach(p -> {
                        awriter.writeNext(new String[]{Long.toString(p.getSeq()), p.getVIN().trim(), p.getVehicleCode().trim(), p.getStatus().trim()});
                    });

                    awriter.close();

                    CSVWriter cwriter = new CSVWriter(new FileWriter("./actual_coating.csv"));

                    result.data().forEach(p -> {
                        cwriter.writeNext(new String[]{Long.toString(p.getSeq()), p.getVIN().trim()});
                    });

                    cwriter.close();

                    CSVWriter coatingSpinFileWriter = new CSVWriter(new FileWriter("./coating_spin.csv"));

                    coatingSpinFileWriter.writeAll(coatingSpinList.stream().map(t -> new String[]{""+t}).collect(Collectors.toList()));

                    coatingSpinFileWriter.close();

                    CSVWriter assemblySpinFileWriter = new CSVWriter(new FileWriter("./assembly_spin.csv"));

                    assemblySpinFileWriter.writeAll(assemblySpinList.stream().map(t -> new String[]{""+t}).collect(Collectors.toList()));

                    assemblySpinFileWriter.close();
                }
            }
        }
    }

    private void emulatingByRange(long N, boolean noshuffle, double shuffleRatio, long coatTakt, long assemblyTakt, long avg, long cap, long windowWidth, String category, SimpleEvaluator evaluator) throws IOException {
	    long start = System.currentTimeMillis();

	    logger.info("gcd : {}", CommonUtil.lcm(coatTakt, assemblyTakt));

        long range = N * CommonUtil.lcm(coatTakt, assemblyTakt);

        long maxtakt = (long)(((double)range) * 1.1);

        logger.info("maxtakt : {}", maxtakt);

        List<CoatingOfflinePlan> coatingOfflinePlan = new ArrayList<>();

        for(long i = 0; i < N; ++i) {
            coatingOfflinePlan.add(new CoatingOfflinePlan(i, String.format("VIN%s", i)));
        }

        logger.debug("coatingOfflinePlan : {}", coatingOfflinePlan);

        List<AssemblyOnlinePlan> assemblyOnlinePlan = new ArrayList<>();
        for(long i = 0; i < N; ++i) {
            assemblyOnlinePlan.add(new AssemblyOnlinePlan(i, String.format("VIN%s", i), String.format("00%s", i % 19)));
        }

        logger.debug("assemblyOnlinePlan : {}", assemblyOnlinePlan);

        List<CoatingOfflinePlan> pbsQueue = new ArrayList<>();                              //PBS queue
        List<AssemblyOnlinePlan> assemblyOnlineActual = new ArrayList<>();                  //总装上线实绩

        long finalCoatTakt = coatTakt;
        long finalAssemblyTakt = assemblyTakt;

        AtomicInteger coatingIndex = new AtomicInteger();

        List<Long> coatingSpinList = new ArrayList<>();
        List<Long> assemblySpinList = new ArrayList<>();

        Kernel kernel = (Kernel<CoatingOfflinePlan, CoatingOfflinePlan>) (takt, data, result, context) -> {
            logger.debug("takt : {}", takt);

            CoatingOfflinePlan ret = null;

            if(takt % finalCoatTakt == 0) {
                CoatingOfflinePlan datium = (data.size() > coatingIndex.get()) ? data.get(coatingIndex.get()) : null;

                logger.debug("datium : {}, coatingIndex : {}", datium, coatingIndex.toString());

                if (datium != null && pbsQueue.size() <= cap) {
                    result.info(takt, ret, String.format("PBS QUEUE BEFORE : %s", pbsQueue.toString()));
                    pbsQueue.add(datium);
                    coatingIndex.incrementAndGet();
                    result.info(takt, ret, String.format("PBS QUEUE IN : %s", datium.toString()));
                    result.info(takt, ret, String.format("PBS QUEUE AFTER : %s", pbsQueue.toString()));

                    ret = datium;

                    logger.debug("[QUEUE IN] ({}) : {}", pbsQueue.size(), datium.toString());
                } else {
                    logger.debug("[QUEUE IN SPIN] ({}) takt : {}", pbsQueue.size(), takt);
                    coatingSpinList.add(takt);
                }
            }

            if(takt % finalAssemblyTakt == 0) {
                if (pbsQueue.size() > avg) {
                    AssemblyOnlinePlan choosenPlan = null;

                    List<AssemblyOnlinePlan> plans = assemblyOnlinePlan
                            .stream()
                            .filter(p -> "0".equals(p.getStatus()))
                            .sorted((o1, o2) -> (o1.getPriority() < o2.getPriority() ? -1 : (o1.getPriority() > o2.getPriority() ? 1 : 0)))
                            .collect(Collectors.toList());

                    logger.debug("plans : {}", plans.size());

                    for (int i = 0; i < plans.size(); ++i) {
                        AssemblyOnlinePlan plan = plans.get(i);

                        if (pbsQueue.stream().filter(p -> p.getVIN().equals(plan.getVIN())).count() > 0) {
                            logger.debug("pbsQueue {} contains {}", pbsQueue, plan.getVIN());
                            choosenPlan = plan;
                            break;
                        } else {
                            choosenPlan = plans
                                    .stream()
                                    .filter(p -> p.getVehicleCode().equals(plan.getVehicleCode()) && pbsQueue.stream().filter(c -> c.getVIN().equals(p.getVIN())).count() > 0)
                                    .findFirst().orElse(null);

                            logger.debug("pbsQueue {} does not contain {}, choose the same VC code ({}) in pbsQueue. choosenPlan : {}.", pbsQueue, plan.getVIN(), plan.getVehicleCode(), choosenPlan);

                            if (choosenPlan != null) {
                                long prio = choosenPlan.getPriority();
                                choosenPlan.setPriority(plan.getPriority());
                                plan.setPriority(prio);
                                break;
                            } else {
                                //TODO: Add Feature Code related logic here...
                                logger.debug("pbsQueue does not contain VC code ({}). Start to check feature code...", plan.getVehicleCode());
                            }
                        }
                    }

                    final AssemblyOnlinePlan finalChoosenPlan = choosenPlan;

                    logger.debug("finalChoosenPlan : {}", finalChoosenPlan);

                    if (finalChoosenPlan != null) {
                        CoatingOfflinePlan cplan = pbsQueue.stream().filter(p -> p.getVIN().equals(finalChoosenPlan.getVIN())).findFirst().orElse(null);

                        if (cplan != null) {
                            pbsQueue.remove(cplan);
                            result.info(takt, ret, String.format("PBS QUEUE OUT : %s", cplan.toString()));
                        }

                        finalChoosenPlan.setStatus("1");
                        assemblyOnlineActual.add(finalChoosenPlan);

                        logger.debug("[QUEUE OUT] ({}) : {}", pbsQueue.size(), finalChoosenPlan.toString());
                    } else {
                        assemblySpinList.add(takt);
                        logger.debug("[QUEUE OUT SPIN] ({}) takt : {}", pbsQueue.size(), takt);
                    }
                } else {
                    assemblySpinList.add(takt);
                    logger.debug("[QUEUE OUT SPIN] ({}) takt : {}", pbsQueue.size(), takt);
                }
            }

            return ret;
        };

        logger.debug("kernel : {}", kernel);

        Rasterizer<CoatingOfflinePlan> rasterizer = null;

        if(noshuffle) {
            logger.debug("CHOSE NO SHUFFLE");
            rasterizer = (variables, context) -> {
                ArrayList<CoatingOfflinePlan> ret = new ArrayList<>();
                ret.addAll(coatingOfflinePlan);
                return ret;
            };
        } else if (Math.abs(Double.MAX_VALUE - shuffleRatio) < 1e-5) {
            logger.debug("CHOSE EvenPossibilityShuffler");
            rasterizer = new EvenPossibilityShuffler<CoatingOfflinePlan>(windowWidth, (variables, context) -> {
                ArrayList<CoatingOfflinePlan> ret = new ArrayList<>();
                ret.addAll(coatingOfflinePlan);
                return ret;
            });
        } else {
            logger.debug("CHOSE VarianceConstraintedPossibilityShuffler");
            rasterizer = new VarianceConstraintedPossibilityShuffler<CoatingOfflinePlan>(windowWidth, shuffleRatio, (variables, context) -> {
                ArrayList<CoatingOfflinePlan> ret = new ArrayList<>();
                ret.addAll(coatingOfflinePlan);
                return ret;
            });
        }

        Rasterizer<CoatingOfflinePlan> finalRasterizer = rasterizer;

        logger.debug("finalRasterizer : {}", finalRasterizer);

        Input input = new Input<CoatingOfflinePlan, CoatingOfflinePlan>() {
            @Override
            public Rasterizer<CoatingOfflinePlan> getRasterizer() {
                return finalRasterizer;
            }

            @Override
            public Rule<CoatingOfflinePlan> getOutputRule() {
                return (value, context) -> (value != null);
            }
        };

        logger.debug("input : {}", input);

        Map<String, Object> variables = new HashMap<>();

        variables.put(SimpleEmulator.VAR_EMULATOR_TAKT_MAX, maxtakt);

        logger.debug("variables : {}", variables);

        Emulator emulator = emulatorFactory.newInstance(category, kernel, variables);

        logger.debug("emulator : {}", emulator);

        Map<String, Object> context = new HashMap<>();

        Result<CoatingOfflinePlan, CoatingOfflinePlan> result = emulator.emulate(input, context);

        long end = System.currentTimeMillis();

        logger.info("INTERVAL : {}", end - start);

        long evalStart = System.currentTimeMillis();

        logger.info("================================================================================");
        logger.info("=====                               REPORT                                 =====");
        logger.info("--------------------------------------------------------------------------------");
        logger.info("PARAMETERS :: noshuffle({}), shuffleRatio({}), maxtakt({}), coatTakt({}), assemblyTakt({}), avg({}), cap({}), windowWidth({})", noshuffle, shuffleRatio, maxtakt, coatTakt, assemblyTakt, avg, cap, windowWidth);

        logger.info("  Length of pbsQueue residuals : {}", pbsQueue.size());
        logger.info("  Length of coatingOfflinePlan : {}", coatingOfflinePlan.size());
        logger.info("Length of coatingOfflineActual : {}", result.data().size());
        logger.info("  Length of assemblyOnlinePlan : {}", assemblyOnlinePlan.size());
        logger.info("Length of assemblyOnlineActual : {}", assemblyOnlineActual.size());

        logger.debug("  pbsQueue residuals : {}", pbsQueue);
        logger.debug("  coatingOfflinePlan : {}", coatingOfflinePlan);
        logger.debug("coatingOfflineActual : {}", result.data());
        logger.debug("  assemblyOnlinePlan : {}", assemblyOnlinePlan);
        logger.debug("assemblyOnlineActual : {}", assemblyOnlineActual);

        Comparator<CoatingOfflinePlan> coatingComparator = (e1, e2) -> {
            String vc1 = e1 != null ? assemblyOnlinePlan.stream().filter(p -> p.getVIN().equals(e1.getVIN())).map(p -> p.getVehicleCode()).findFirst().get() : null;
            String vc2 = e2 != null ? assemblyOnlinePlan.stream().filter(p -> p.getVIN().equals(e2.getVIN())).map(p -> p.getVehicleCode()).findFirst().get() : null;

            return ((vc1 == null && vc2 == null) || vc1.equals(vc2)) ? 0 : 1;
        };

        Comparator<AssemblyOnlinePlan> assemblyComparator = (e1, e2) -> e1.getVehicleCode().equals(e2.getVehicleCode()) ? 0 : 1;

        double[] coatingEvaluationResult = evaluator.evaluate(coatingOfflinePlan, result.data(), coatingComparator, (e1, e2) -> {
            return e1.getSeq() > e2.getSeq() ? 1 : (e1.getSeq() < e2.getSeq() ? -1 : 0);
        });

        double[] assemblyEvaluationResult = evaluator.evaluate(assemblyOnlinePlan, assemblyOnlineActual, assemblyComparator, (e1, e2) -> {
            return e1.getSeq() > e2.getSeq() ? 1 : (e1.getSeq() < e2.getSeq() ? -1 : 0);
        });

        logger.info("                   names : {}", evaluator.names());
        logger.info(" coatingEvaluationResult : {}", coatingEvaluationResult);
        logger.info("assemblyEvaluationResult : {}", assemblyEvaluationResult);
        logger.info("              normalized : {}", CommonUtil.normalize(assemblyEvaluationResult, coatingEvaluationResult));

        Map<Integer, Integer> coatingCount = CommonUtil.count(CommonUtil.offsetDiffi(coatingOfflinePlan, result.data(), coatingComparator, Math.min(coatingOfflinePlan.size(), result.data().size())));
        Map<Integer, Integer> assemblyCount = CommonUtil.count(CommonUtil.offsetDiffi(assemblyOnlinePlan, assemblyOnlineActual, assemblyComparator, Math.min(assemblyOnlinePlan.size(), assemblyOnlineActual.size())));

        CSVWriter csvWriter = new CSVWriter(new FileWriter("./coating_statistics.csv"));

        coatingCount.forEach((value, count) -> {
            csvWriter.writeNext(new String[]{Integer.toString(value), Integer.toString(count)});
        });

        csvWriter.close();

        CSVWriter asvWriter = new CSVWriter(new FileWriter("./assembly_statistics.csv"));

        assemblyCount.forEach((value, count) -> {
            asvWriter.writeNext(new String[]{Integer.toString(value), Integer.toString(count)});
        });

        asvWriter.close();

        for(History h : result.history().stream().filter(h -> h.level() == History.Level.INFOR).collect(Collectors.toList())) {
            logger.debug("\t({}) {}", h.level(), h.toString());
        }

        long evalEnd = System.currentTimeMillis();

        logger.info("EVALUATION INTERVAL : {}", evalEnd - evalStart);

        logger.info("--------------------------------------------------------------------------------");

        final CSVWriter awriter = new CSVWriter(new FileWriter("./actual_assembly.csv"));

        assemblyOnlineActual.forEach(p -> {
            awriter.writeNext(new String[]{Long.toString(p.getSeq()), p.getVIN().trim(), p.getVehicleCode().trim(), p.getStatus().trim()});
        });

        awriter.close();

        CSVWriter cwriter = new CSVWriter(new FileWriter("./actual_coating.csv"));

        result.data().forEach(p -> {
            cwriter.writeNext(new String[]{Long.toString(p.getSeq()), p.getVIN().trim()});
        });

        cwriter.close();

        CSVWriter coatingSpinFileWriter = new CSVWriter(new FileWriter("./coating_spin.csv"));

        coatingSpinFileWriter.writeAll(coatingSpinList.stream().map(t -> new String[]{""+t}).collect(Collectors.toList()));

        coatingSpinFileWriter.close();

        CSVWriter assemblySpinFileWriter = new CSVWriter(new FileWriter("./assembly_spin.csv"));

        assemblySpinFileWriter.writeAll(assemblySpinList.stream().map(t -> new String[]{""+t}).collect(Collectors.toList()));

        assemblySpinFileWriter.close();
    }
}
