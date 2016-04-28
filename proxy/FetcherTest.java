package pagegen.proxy;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import pagegen.entity.PageText;
import pagegen.entity.PageUrl;
import pagegen.entity.Proxy;
import pagegen.entity.ProxySource;
import pagegen.util.Http;
import pagegen.util.Scraper;

public class FetcherTest extends TestCase {

	public void testFetchProxies() {
		Logger logger  = Logger.getLogger("floep.proxy.fetcher");
		ProxySource proxySource = new ProxySource(new PageUrl(
				"http://www.proxyforest.com/e-proxy.htm?pages=0"));

		PageText pt = Http.getHttp().getPageText(proxySource.getPageUrl());

		List<Proxy> proxies = Scraper.scrapeForProxies(pt);
		
		
		logger.info("Scraped following proxies from " + proxySource.getPageUrl() + ":");
		for(Proxy proxy :  proxies)
		{
				logger.info(proxy.getIp() + ":" + proxy.getPort());
		}


		for (Proxy proxy : proxies) {
			logger.info("Testing proxy connectivity for " + proxy.getIp() + ":" + proxy.getPort());
			boolean isProxyConnectivityOk = Http.getHttp().isProxyConnectivityOk(
					proxy,new PageUrl("http://www.google.be"),
					"<title>Google</title>",  1);

			if (isProxyConnectivityOk) {
				boolean isProxyAnonimityOk = Http.testProxyAnonymity();

				if (isProxyConnectivityOk && isProxyAnonimityOk) {
					proxy.setProxySource(proxySource);
					proxy.setLastTimeChecked(new Date());

					/* em.persist(proxy) */;
				}
			}

		}
	}

}
