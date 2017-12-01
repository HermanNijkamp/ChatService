import javax.xml.crypto.Data;

public class DataProvider {

    private static DataProvider instance = null;
    private final int SERVER_PORT = 1337;
    private final String SERVER_ADDRESS = "localhost";

    private DataProvider() {
    }

    public DataProvider getInstance() {
        if (instance == null) {
            instance = new DataProvider();
        }
        return instance;
    }
}
