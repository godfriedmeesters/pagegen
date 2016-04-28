package pagegen.content.gen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.log4j.Logger;

import pagegen.entity.GeneratedPage;
import pagegen.entity.Keyword;
import pagegen.entity.PageText;
import pagegen.util.Scraper;

@Stateless
public class Composer {
	private EntityManager entityManager;

	@PersistenceContext(unitName = "floep")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@SuppressWarnings("unchecked")
	public void compose(Keyword keyword) {
		int fileNumber = 0;
		Logger logger = Logger.getLogger("floep.content.Composer");
		int pagesCreated = 0;
		logger.info("Content creation started for keyword '"
				+ keyword.getStringValue() + "'");

		RandomData randomData = new RandomDataImpl();

		String paragraph = "";
		String text = "";
		String title = "";
		String header = "";
		String originalTitleSentence = "";
		boolean originalTitleSentenceUsedInText = false;
		boolean originalHeaderSentenceUsedInParagraph = false;
		String originalHeaderSentence = "";
		
		
		try {

			int maxPageDepth = (Integer) entityManager.createQuery(
					"select max(pageDepth) from ContentGenPage as page where "
							+ " page.seedKeyword.stringValue = :keyw ")
					.setParameter("keyw", keyword.getStringValue())
					.getSingleResult();

			for (int currentPageDepth = 0; currentPageDepth <= maxPageDepth; currentPageDepth++) {

				List<Integer> sentenceIds = entityManager
						.createQuery(
								"select sentence.id from ContentGenPage as page join page.sentences as sentence where "
										+ "page.seedKeyword.stringValue = :keyw and page.pageDepth = :pagedep")
						.setParameter("keyw", keyword.getStringValue())
						.setParameter("pagedep", currentPageDepth).getResultList();

				Collections.shuffle(sentenceIds);

				Stack<Integer> sentenceIdStack = new Stack<Integer>();
				sentenceIdStack.addAll(sentenceIds);

				Collections.shuffle(sentenceIdStack);

				while (!sentenceIdStack.isEmpty()) {
					if (text == "") {
						title = entityManager.createQuery(
								"select sentence.stringValue from Sentence as sentence where sentence.id"
										+ " = :sent ").setParameter("sent",
								sentenceIdStack.pop()).getSingleResult()
								.toString();

						originalTitleSentence = title;

						title = StringUtils.removeEnd(title, ".");

						title = Scraper.scrapeLeft(title, 100);

						text = "<title>" + title + "</title> ";

						logger.info("Creating new page with title '" + title
								+ "'");
						
						header = entityManager.createQuery(
								"select sentence.stringValue from Sentence as sentence where sentence.id"
										+ " = :sent ").setParameter("sent",
								sentenceIdStack.pop()).getSingleResult()
								.toString();

						originalHeaderSentence = header;

						header = StringUtils.removeEnd(header, ".");

						header = Scraper.scrapeLeft(header, 100);

						paragraph = "<h3>" + header + "</h3>";
					}

					if (randomData.nextInt(0, 25) == 0) {
						String sentence = entityManager.createQuery(
								"select sentence.stringValue from Sentence as sentence where sentence.id"
										+ " = :sent ").setParameter("sent",
								sentenceIdStack.pop()).getSingleResult()
								.toString();
						sentence = StringUtils.removeEnd(sentence, ".");
						sentence = StringUtils.removeEnd(sentence, "?");
						sentence = StringUtils.removeEnd(sentence, "!");
						sentence += ":";
						
						paragraph += sentence;

						int bulletListLenght = randomData.nextInt(2, 10);
						logger.info("Creating lenght " + bulletListLenght
								+ " bullet list for page with title " + title);
						paragraph += "<br/><ul>";

						for (int bulletListIndex = 0; bulletListIndex < bulletListLenght; bulletListIndex++) {
							if (originalTitleSentenceUsedInText == false
									&& randomData.nextInt(0, 10) == 0) {
								paragraph += "<li>" + originalTitleSentence
										+ "</li>";
								originalTitleSentenceUsedInText = true;
							} else {
								if (originalHeaderSentenceUsedInParagraph == false
										&& randomData.nextInt(0, 10) == 0) {
									paragraph += "<li>"
											+ originalHeaderSentence + "</li>";
									originalHeaderSentenceUsedInParagraph = true;
								} else {
									paragraph += "<li>"
											+ entityManager.createQuery(
													"select sentence.stringValue from Sentence as sentence where sentence.id"
															+ " = :sent ")
													.setParameter(
															"sent",
															sentenceIdStack
																	.pop())
													.getSingleResult()
													.toString() + "</li>";
								}
							}
						}

						paragraph += "</ul><br/>";

						System.out.println();
					} else {
						if (originalTitleSentenceUsedInText == false
								&& randomData.nextInt(0, 10) == 0) {
							paragraph += " " + originalTitleSentence;
							originalTitleSentenceUsedInText = true;
						} else {

							if (originalHeaderSentenceUsedInParagraph == false
									&& randomData.nextInt(0, 10) == 0) {
								paragraph += " " + originalHeaderSentence;

								originalHeaderSentenceUsedInParagraph = true;
							} else {
								paragraph += " "
										+ entityManager.createQuery(
												"select sentence.stringValue from Sentence as sentence where sentence.id"
														+ " = :sent ")
												.setParameter("sent",
														sentenceIdStack.pop())
												.getSingleResult().toString();
							}

						}

					}

					
					if (paragraph.length() > randomData.nextInt(350, 1000)) {
						text += paragraph;

						if (originalTitleSentenceUsedInText == false
								&& randomData.nextInt(0, 8) == 0) {
							header = originalTitleSentence;
							originalTitleSentenceUsedInText = true;
						} else {
							header = entityManager.createQuery(
									"select sentence.stringValue from Sentence as sentence where sentence.id"
											+ " = :sent ").setParameter("sent",
									sentenceIdStack.pop()).getSingleResult()
									.toString();
						}

						originalHeaderSentence = header;

						header = StringUtils.removeEnd(header, ".");
						header = Scraper.scrapeLeft(header, 100);

						paragraph = "<h3>" + header + "</h3>";

					}

					if (text.length() > randomData.nextInt(650, 2500)) {
						PageText pageText = new PageText(text);
						GeneratedPage generatedPage = new GeneratedPage(
								keyword, pageText, currentPageDepth);
						entityManager.persist(generatedPage);
						
						
						logger.info("Finished creation of page with title "
								+ title);
						pagesCreated++;
						text = "";
						originalTitleSentenceUsedInText = false;
						originalHeaderSentenceUsedInParagraph = false;

						
						////////////////
						
						try {
							File file = new File("c:\\html\\" + fileNumber++
									+ ".html");
							
							FileUtils.writeStringToFile(file, pageText
									.getStringValue());
						} catch (IOException e) {
							e.printStackTrace();
						}

						////////////////

					}
				}
			}
		} catch (EmptyStackException ese) {
			logger.info("Composing content for page with title '" + title
					+ "' stopped; no more sentences to use");

		} catch (Exception e) {
			logger.error(e);
		}

		logger.info("Content composition for keyword "
				+ keyword.getStringValue() + " ended with " + pagesCreated
				+ " pages created.");

	}
}
