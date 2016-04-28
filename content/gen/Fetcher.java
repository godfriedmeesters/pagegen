package pagegen.content.gen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import pagegen.entity.ContentGenPage;
import pagegen.entity.Keyword;
import pagegen.entity.PagePageAssociation;
import pagegen.entity.PageUrl;
import pagegen.util.Http;
import pagegen.util.Scraper;
import pagegen.util.SearchEngine;
import pagegen.util.Enums.SearchEngineEnum;

class FetcherCallable implements Callable<Long> {
	private ContentGenPage levelOnePage;

	private EntityManager entityManager;

	private int fetchDepth;

	public FetcherCallable(ContentGenPage levelOnePageWithUrl, int fetchDepth,
			EntityManager entityManager) {
		this.levelOnePage = levelOnePageWithUrl;
		this.fetchDepth = fetchDepth;
		this.entityManager = entityManager;
	}

	public Long call() {
		long startTime = System.currentTimeMillis();
		Logger logger = Logger.getLogger("floep.content.Fetcher");

		levelOnePage.setPageText(Http.getHttp().getPageText(
				levelOnePage.getPageUrl()));

		logger.info("Set pageText for level " + levelOnePage.getLevel()
				+ " page with url " + levelOnePage.getPageUrl());

		entityManager.persist(levelOnePage);

		List<PagePageAssociation> parentChilds = new ArrayList<PagePageAssociation>();

		if (fetchDepth > 0) {

			Queue<Integer> queue = new LinkedList<Integer>();

			List<Integer> currentChildIds = new ArrayList<Integer>();
			for (ContentGenPage emptyChild : Scraper
					.scrapeForEmptyChildPages(levelOnePage)) {
				entityManager.persist(emptyChild);
				parentChilds.add(new PagePageAssociation(levelOnePage.getId(),
						emptyChild.getId()));
				currentChildIds.add(emptyChild.getId());
			}

			queue.add(levelOnePage.getId());

			while (!queue.isEmpty()) {
				int currentPageId = queue.remove();

				ContentGenPage currentParent = entityManager.find(
						ContentGenPage.class, currentPageId);
				// current page now has 1. pageText 2. sentences 3. keywords and
				// 4. empty
				// children

				if (currentParent.getLevel() < this.fetchDepth) {

					List<Integer> nextChildIds = new ArrayList<Integer>();
					
					for (int currentChildId : currentChildIds) {
						ContentGenPage currentChild = entityManager.find(
								ContentGenPage.class, currentChildId);
						currentChild.setPageText(Http.getHttp().getPageText(
								currentChild.getPageUrl()));
						logger.info("Set pageText for level " + currentChild.getLevel()
								+ " page with url " + currentChild.getPageUrl());

						
						if (currentParent.getLevel() < this.fetchDepth - 1) {
							for (ContentGenPage nextChild : Scraper
									.scrapeForEmptyChildPages(currentChild)) {
								entityManager.persist(nextChild);
								parentChilds.add(new PagePageAssociation(
										currentChildId, nextChild.getId()));
								nextChildIds.add(nextChild.getId());
								queue.add(currentChildId);
							}
						}
						
					}
					
					currentChildIds = nextChildIds;

				}
			}

		}

		return System.currentTimeMillis() - startTime;
	}
}

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

	public void fetch(Keyword keyword, int serpPages, int crawlDepth) {
		Logger logger = Logger.getLogger("floep.content.Fetcher");

		logger.info("Crawl started for keyword " + keyword + " using "
				+ serpPages + " serp page(s) and crawling to a depth of "
				+ crawlDepth + " inclusive.");

		ExecutorService executor = Executors.newFixedThreadPool(10);

		logger.info("Constructed thread pool for 10 CrawlerCallables");

		for (int serpCounter = 1; serpCounter <= serpPages; serpCounter++) {

			List<FetcherCallable> callableList = new ArrayList<FetcherCallable>();

			List<PageUrl> levelZeroPageUrls = SearchEngine.getSerpUrls(keyword
					.getStringValue(), SearchEngineEnum.GOOGLE, serpCounter);

			for (PageUrl pageUrl : levelZeroPageUrls) {
				FetcherCallable fetcherCallable = new FetcherCallable(
						new ContentGenPage(pageUrl, keyword), crawlDepth,
						entityManager);
				callableList.add(fetcherCallable);
			}

			logger.info("Crawling started for pages :");

			for (PageUrl levelZeroPageUrl : levelZeroPageUrls) {
				logger.info("-" + levelZeroPageUrl);
			}

			try {
				executor.invokeAll(callableList);

			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
}
