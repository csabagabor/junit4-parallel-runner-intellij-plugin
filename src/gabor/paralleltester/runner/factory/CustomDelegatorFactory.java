package gabor.paralleltester.runner.factory;

import gabor.paralleltester.runner.CustomDelegatorRunner;
import gabor.paralleltester.runner.runners.CustomRunner1;
import gabor.paralleltester.runner.runners.CustomRunner2;

import java.util.HashMap;
import java.util.Map;

public abstract class CustomDelegatorFactory {
    protected static int RUNNER_ID = 1;

    protected static Map<Integer, CustomDelegatorRunner> runners = populateRunners();

    protected static Map<Integer, CustomDelegatorRunner> populateRunners() {
        Map<Integer, CustomDelegatorRunner> runners = new HashMap<>();

        runners.put(1, new CustomRunner1());
        runners.put(2, new CustomRunner2());

        return runners;
    }

    public static CustomDelegatorRunner getRunner() {
        return runners.get(RUNNER_ID);
    }
}
