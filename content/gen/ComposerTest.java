package pagegen.content.gen;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;
import pagegen.entity.GeneratedPage;
import pagegen.entity.Keyword;
import pagegen.entity.PageLayout;
import pagegen.entity.PageText;
import pagegen.entity.Site;

public class ComposerTest extends TestCase {

	public void testCompose() {
		EntityManagerFactory emfMySQL = Persistence
				.createEntityManagerFactory("floep");

		EntityManager emMySQL = emfMySQL.createEntityManager();

		Composer composer = new Composer();
		composer.setEntityManager(emMySQL);

		emMySQL.getTransaction().begin();

		GeneratedPage homePage = new GeneratedPage(new Keyword("spinazi"),
				new PageText("homepage"), 0);

		GeneratedPage level1PageA = new GeneratedPage(new Keyword("spinazi"),
				new PageText("level1PageA"), 1);
		GeneratedPage level1PageB = new GeneratedPage(new Keyword("spinazi"),
				new PageText("level1PageB"), 1);

		GeneratedPage level2PageA = new GeneratedPage(new Keyword("spinazi"),
				new PageText("level2PageA"), 2);
		GeneratedPage level2PageB = new GeneratedPage(new Keyword("spinazi"),
				new PageText("level2PageB"), 2);
		

		List<GeneratedPage> links1 = new ArrayList<GeneratedPage>();
		links1.add(level1PageA);
		links1.add(level1PageB);

		List<GeneratedPage> links2 = new ArrayList<GeneratedPage>();

		links2.add(level2PageA);
		links2.add(level2PageB);

		homePage.setChildren(links1);
		level1PageA.setChildren(links2);

		emMySQL.persist(homePage);

		Site site = new Site(homePage, new PageLayout("<html> text </html>"));

		emMySQL.persist(site);

//		String style = emMySQL
//				.createQuery(
//						"select st.pageLayout.stringValue from Site as st join st.homePage.children as ch1 join ch1.children as ch2 join ch2.children as ch3 where st.homePage.id = 5 or ch1.id = 5 or ch2.id = 5 or ch3.id = 5")
//				.getSingleResult().toString();
//
		
		
		List<Integer>	ids = new ArrayList<Integer>();
		
		for(int i = 1; i < 4; i++)
		{
			ids.add(i);
		}
		
//		emMySQL.createQuery("update GeneratedPage as page set pageLayout = :pl where page.id in (:ids) ").
//		setParameter("pl", site.getDefaultPageLayout()).setParameter("ids", ids).executeUpdate();
		
		
		Keyword keyword = new Keyword("h788a");
		System.out.println("before " + keyword.getId());
		
		emMySQL.persist(keyword);
		System.out.println("after " + keyword.getId());	
		
		emMySQL.getTransaction().commit();
		
	
		
	//	System.out.println(style);

		System.out.println("kleer");
	}
}
