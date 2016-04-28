package pagegen.content.gen;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pagegen.entity.GeneratedPage;
import pagegen.entity.Keyword;
import pagegen.entity.PageLayout;
import pagegen.entity.PageText;
import pagegen.entity.Site;

@Stateless
public class SiteGen {

	private EntityManager entityManager;

	@PersistenceContext(unitName = "floep")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@SuppressWarnings("unchecked")
	public void generateSite(Keyword keyword, int numberOfSerpPages, int crawlDepth,
			PageText homePageText, PageLayout pageLayout) {

		GeneratedPage homePage = new GeneratedPage(keyword, homePageText, 0);
		Site site = new Site(homePage,pageLayout);
		
		entityManager.persist(site);
		
		new Fetcher().fetch(keyword, numberOfSerpPages, crawlDepth);
		new Composer().compose(keyword);
		new Linker().link(keyword, homePage);	
		
		
		List<Integer> generatedPageIds = entityManager.createQuery("select id from GeneratedPage where seedKeyword.stringValue = :kw")
		.setParameter("kw", keyword.getStringValue()).getResultList();
		
		entityManager.createQuery("update GeneratedPage as page set pageLayout = :pl where page.id in (:ids)")
		.setParameter("pl",site.getDefaultPageLayout()).setParameter("ids", generatedPageIds).executeUpdate();
		
		entityManager.createQuery("update GeneratedPage as page set site = :st where page.id in (:ids)")
		.setParameter("st",site).executeUpdate();
		
	}
}

