package pagegen.content.gen;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;
import pagegen.entity.Keyword;
import pagegen.entity.PageUrl;
import pagegen.entity.ProxySource;


public class FetcherTest extends TestCase {

	@SuppressWarnings("unchecked")
	public void testGenerateTree() throws InterruptedException,
			ExecutionException, IOException {
		
		EntityManagerFactory emfMySQL = Persistence.createEntityManagerFactory("floep");

        EntityManager emMySQL = emfMySQL.createEntityManager();
		
		Fetcher fetcher = new Fetcher();
		fetcher.setEntityManager(emMySQL);

		emMySQL.getTransaction().begin();
	
		
		fetcher.fetch(new Keyword("car rentals"), 1, 1);
		
		
		emMySQL.getTransaction().commit();
		
			
		System.out.println("kleer");		
		
	}
}
