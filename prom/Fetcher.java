package pagegen.prom;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pagegen.entity.Keyword;
import pagegen.entity.PageUrl;
import pagegen.entity.TargetType;
import pagegen.util.Http;
import pagegen.util.SearchEngine;
import pagegen.util.Enums.SearchEngineEnum;

@Stateless
public class Fetcher {
	private EntityManager entityManager;

	@PersistenceContext(unitName = "floep")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;

	}

	public void Fetch(Keyword keyword, SearchEngineEnum see,
			int numberOfSerpPages, List<TargetType> targetTypes) {

		for (TargetType targetType : targetTypes) {
			for (int serpIndex = 1; serpIndex < numberOfSerpPages; serpIndex++) {
				List<PageUrl> targetUrls = SearchEngine.getSerpUrls(targetType.getSearchString(see), see, serpIndex);
				
				for(PageUrl targetUrl : targetUrls)
				{
					Http.getHttp().getPageText(targetUrl);
					
				}
			}
		}
	}
}
