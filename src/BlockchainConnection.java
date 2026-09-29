import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

public class BlockchainConnection {

    public static void main(String[] args) {

        Web3j web3j = Web3j.build(
                new HttpService("http://127.0.0.1:8545")
        );

        try {

            String clientVersion =
                    web3j.web3ClientVersion()
                         .send()
                         .getWeb3ClientVersion();

            System.out.println("Connected to Hardhat blockchain.");
            System.out.println("Client: " + clientVersion);

        } catch (Exception e) {

            System.out.println("Blockchain connection failed.");
            e.printStackTrace();

        } finally {

            web3j.shutdown();
        }
    }
}