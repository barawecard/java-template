package server;

import edu.spbu.server.Client;
import org.junit.Test;



public class ClientTest {
    @Test
    public void mulDD() {
        Client cl1=new Client("ab-w.net/",80,"faq_site_document.php");
        cl1.GETrequest();
    }
}