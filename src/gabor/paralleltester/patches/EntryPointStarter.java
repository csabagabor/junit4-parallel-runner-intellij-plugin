package gabor.paralleltester.patches;

public class EntryPointStarter {

    public static void main(String[] args) {
        try {
            ParallelStarter.start(args);
        } catch (Throwable e) {
            //catch every type or error(eg. NoClassDefFoundError) so that the exit code can be changed
            //and the next runner can be run
            System.exit(-5);
        }
    }
}
