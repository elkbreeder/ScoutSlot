package picview;

public class Vars {

    private static final String REMOTE_USER = "pi";
    private static final String LOCAL_USER = "jzbor";
    private static final String REMOTE_HOST = "raspberrypi";
    private static final String FILE_KNOWN_HOSTS = "/home/" + LOCAL_USER + "/.ssh/known_hosts";
    public static final String LOCAL_DIR = "./test/out/";
    private static final String REMOTE_DIR = "/home/"+REMOTE_USER+"/ScoutSlot/PythonSrc/images/";
    private static Vars ourInstance = new Vars();

    public static Vars getInstance() {
        return ourInstance;
    }

    private Vars() {
    }

    public static String getRemoteUser() {
        return REMOTE_USER;
    }

    public static String getLocalUser() {
        return LOCAL_USER;
    }

    public static String getRemoteHost() {
        return REMOTE_HOST;
    }

    public static String getFileKnownHosts() {
        return FILE_KNOWN_HOSTS;
    }

    public static String getLocalDir() {
        return LOCAL_DIR;
    }

    public static String getRemoteDir() {
        return REMOTE_DIR;
    }
}
