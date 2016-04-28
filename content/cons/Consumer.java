package pagegen.content.cons;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pagegen.entity.GeneratedPage;
import pagegen.entity.PageLayout;

@Stateless
public class Consumer {
	private EntityManager entityManager;

	@PersistenceContext(unitName = "floep")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@SuppressWarnings("unchecked")
	public String consumePage(int pageId) {
		GeneratedPage generatedPage = (GeneratedPage) entityManager
				.createQuery(
						"from GeneratedPage as page where page.id = " + ":id")

				.setParameter("id", pageId).getSingleResult();

		PageLayout layout = generatedPage.getPageLayout();

		String fullPageText = layout.getStringValue().replace("#PAGETEXT",
				generatedPage.getPageText().getStringValue());

		List<Integer> childIds = entityManager
				.createQuery(
						"select child.id from GeneratedPage as child where parent.id = pid")
				.setParameter("pid", generatedPage.getId()).getResultList();

		for (int linkCounter = 1, childIdsIndex = 0; linkCounter < layout
				.getNumberOfLinks()
				&& childIdsIndex < childIds.size(); childIdsIndex++, linkCounter++) {

			fullPageText = fullPageText.replace("#LINK" + linkCounter, "/"
					+ childIds.get(childIdsIndex) + ".html");

		}

		return fullPageText;
	}
}
