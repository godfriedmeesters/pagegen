package pagegen.content.gen;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pagegen.entity.GeneratedPage;
import pagegen.entity.Keyword;

@Stateless
public class Linker {

	private EntityManager entityManager;

	@PersistenceContext(unitName = "floep")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@SuppressWarnings("unchecked")
	public void link(Keyword keyword, GeneratedPage homePage) {
		
		entityManager.persist(homePage);
		
		int maxPageDepth = (Integer) entityManager
				.createQuery(
						"select max(page.level) from Keyword as keyword join keyword.generatedPages as page where "
								+ " keyword.stringValue = :keyw ")
				.setParameter("keyw", keyword.getStringValue())
				.getSingleResult();

		for (int currentGeneratedPageDepth = 0; currentGeneratedPageDepth < maxPageDepth; currentGeneratedPageDepth++) {
			List<Integer> generatedPageIds1 = entityManager
					.createQuery(
							"select page.id from Keyword as keyword join keyword.generatedPages as page where "
									+ " keyword.stringValue = :keyw and page.level = :lev ")
					.setParameter("keyw", keyword.getStringValue())
					.setParameter("lev", currentGeneratedPageDepth)
					.getResultList();
			

			List<Integer> generatedPageIds2 = entityManager
					.createQuery(
							"select page.id from Keyword as keyword join keyword.generatedPages as page where "
									+ " keyword.stringValue = :keyw and page.level = :lev ")
					.setParameter("keyw", keyword.getStringValue())
					.setParameter("lev", currentGeneratedPageDepth + 1)
					.getResultList();

			int currentGeneratedPageId1 = -1;
			int currentGeneratedPageId2 = -1;

			int generatedPageIds2Counter = 0;

			for (int generatedPageIds1Counter = 0; generatedPageIds1Counter < generatedPageIds1
					.size(); generatedPageIds1Counter++) {
				currentGeneratedPageId1 = generatedPageIds1
						.get(generatedPageIds1Counter);

				
				GeneratedPage generatedPage1 = (GeneratedPage) entityManager
						.createQuery(
								"from GeneratedPage where "
										+ " page.id = :pId").setParameter(
								"pId", currentGeneratedPageId1)
						.getSingleResult();

				int avgLinksPerPage = generatedPageIds2.size()
						/ generatedPageIds1.size();

//				int linksPerGeneratedPage = randomData.nextInt(
//						avgLinksPerPage - 2, avgLinksPerPage + 2);
				
				int linksPerGeneratedPage = avgLinksPerPage;
				
				List<GeneratedPage> generatedPages2 = new ArrayList<GeneratedPage>();

				for (int linksPerGeneratedPageCounter = 0; linksPerGeneratedPageCounter < linksPerGeneratedPage
						&& generatedPageIds2Counter < generatedPageIds2.size(); generatedPageIds2Counter++, linksPerGeneratedPageCounter++) {
					currentGeneratedPageId2 = generatedPageIds2
							.get(generatedPageIds2Counter);
					GeneratedPage generatedPage2 = (GeneratedPage) entityManager
							.createQuery(
									"select page from GeneratedPage where "
											+ " page.id = :pId").setParameter(
									"pId", currentGeneratedPageId2)
							.getSingleResult();
					
					generatedPage2.setParent(generatedPage1);

					generatedPages2.add(generatedPage2);
					
					
//					if(generatedPageIds1Counter == generatedPageIds1.size() - 1)
//						linksPerGeneratedPage += (generatedPageIds2.size() - generatedPageIds2Counter); 
						
				}

				generatedPage1.setChildren(generatedPages2);
			}
		}
		
	}
}
