import java.io.IOException;

public class ClientApplication {

    public static void main(String[] args) {
        try {
            new MyClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
