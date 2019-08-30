package gabor.paralleltester.patches;

import gabor.paralleltester.Resources;

public class EntryPointStarter {

    public static void main(String[] args) {
        try {
            if (!Resources.checkJUnit4()) {
                System.exit(-8);
            }

            ParallelStarter.start(args);
        } catch (Throwable e) {
            //catch every type or error(eg. NoClassDefFoundError) so that the exit code can be changed
            //and the next runner can be run
            System.exit(-5);
        }
    }
}
