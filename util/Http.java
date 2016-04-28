package pagegen.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import pagegen.entity.Page;
import pagegen.entity.PageText;
import pagegen.entity.PageUrl;
import pagegen.entity.Proxy;
import pagegen.model.Form;

class HttpGetCallable implements Callable<PageText> {
	private PageUrl pageUrl;
	private HttpClient httpClient;

	public HttpGetCallable(PageUrl pageUrl, HttpClient httpClient) {
		this.pageUrl = pageUrl;
		this.httpClient = httpClient;
	}

	public PageText call() {
		PageText pageText = Http.httpGet(httpClient, pageUrl);
		return pageText;
	}
}

class HttpPostCallable implements Callable<Page> {
	private PageUrl pageUrl;
	private HttpClient httpClient;
	private List<NameValuePair> nameValuePairs;

	public HttpPostCallable(HttpClient client, PageUrl pageUrl,
			List<NameValuePair> nameValuePairs) {
		this.httpClient = client;
		this.pageUrl = pageUrl;
		this.nameValuePairs = nameValuePairs;
	}

	public Page call() {
		Page page = Http.httpPost(httpClient, pageUrl, nameValuePairs);
		return page;
	}
}

/**
 * Utility class to retrieve page text(s) from given url(s) via the internet.
 * 
 * @author gmeester
 * 
 */
public class Http {

	private static Http ref;
	private static Logger logger = Logger.getLogger("floep.util.Http");

	private ExecutorService executor;
	public HttpClient httpClient;

	@SuppressWarnings("deprecation")
	public Http(int threadPoolSize) {
		this.executor = Executors.newFixedThreadPool(threadPoolSize);
		this.httpClient = new HttpClient();
		//httpClient.setStrictMode(true);
	}

	public static synchronized Http getHttp() {
		if (ref == null) {
			ref = new Http(10);
		}
		return ref;
	}

	public PageText getPageText(PageUrl pageUrl) {

		PageText pageText = new PageText();

		FutureTask<PageText> future = new FutureTask<PageText>(
				new HttpGetCallable(pageUrl, httpClient));

		executor.execute(future);

		try {
			pageText = future.get();
		} catch (InterruptedException e) {
			logger.error(e + ", cause = " + e.getCause());
		} catch (ExecutionException e) {
			logger.error(e + ", cause = " + e.getCause());
		}

		return pageText;
	}
	
	public Page getPage(PageUrl pageUrl)
	{
		Page page = new Page(pageUrl, getPageText(pageUrl));
		return page;
	}
	
	public Page postPageText(PageUrl pageUrl,
			List<NameValuePair> nameValuePairs) {
		Page page = null;

		FutureTask<Page> future = new FutureTask<Page>(
				new HttpPostCallable(httpClient, pageUrl, nameValuePairs));

		executor.execute(future);

		try {
			page = future.get();
		} catch (InterruptedException e) {
			logger.error(e + ", cause = " + e.getCause());
		} catch (ExecutionException e) {
			logger.error(e + ", cause = " + e.getCause());
		}

		return page;
	}

	@SuppressWarnings("deprecation")
	protected static Page httpPost(HttpClient client, PageUrl pageUrl,
			List<NameValuePair> postData) {

		logger.info("Preparing POST for url " + pageUrl);

		PageText pageText = null;
		PostMethod method = null;

		try {

			method = new PostMethod(pageUrl.getStringValue());
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(1, false));

			NameValuePair[] data = new NameValuePair[postData.size()];

			for (int i = 0; i < data.length; i++) {
				data[i] = postData.get(i);
			}

			method.setRequestBody(data);

			method.setFollowRedirects(false);

			method.setRequestHeader(new Header("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0;Windows NT 5.1)"));

			method.setRequestHeader(new Header("accept", "*/*"));
			method.setRequestHeader(new Header("accept-language", "en-us"));
			method.setRequestHeader(new Header("connection", "Keep-Alive"));
			method.setRequestHeader(new Header("cache-control", "no-cache"));

			HostConfiguration hostConfig = client.getHostConfiguration();

			hostConfig.setProxy("10.255.170.65", 8080);

			// Authenticate using NTLM
			client.getState()
					.setProxyCredentials(
							null,
							"10.255.170.65",
							new NTCredentials("godfried.meesters", "pas314159",
									"", ""));

			int statusCode = client.executeMethod(method);

			StringBuffer responseBody = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(method
					.getResponseBodyAsStream()));
			String readLine;
			while (((readLine = br.readLine()) != null)) {
				responseBody.append(readLine);
			}

			pageText = new PageText(FloepUtils.convertHtmlToAscii(responseBody
					.toString().trim()));

			if (statusCode >= 300 && statusCode < 399) {
				Header location = method.getResponseHeader("Location");
				String redirect = location.getValue();
				pageUrl = new PageUrl(redirect, pageUrl);
				logger.info("Did post to " + pageUrl.getStringValue()
						+ " and now redirecting to "
						+ pageUrl.getStringValue());
				pageText = getHttp().getPageText(pageUrl);
			} else if (statusCode != HttpStatus.SC_OK) {
				logger.error(pageUrl.getStringValue() + " - Method failed: "
						+ method.getStatusLine()
						+ "\n"
						+ pageText.getStringValue());
				
				pageText.setStringValue("");
			} else {
				logger.info("Did POST to " + pageUrl.getStringValue());
			}

		} catch (HttpException e) {
			logger.error(pageUrl.getStringValue()
					+ " - Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			logger.error(pageUrl.getStringValue()
					+ " - Fatal transport error: " + e.getMessage());
		} catch (Exception e) {
			logger.error(pageUrl.getStringValue() + " - " + e.getMessage());
		} finally {
			method.releaseConnection();
		}

		return new Page(pageUrl,pageText);
	}

	/**
	 * @param pageUrl
	 *            The url to connect to.
	 * @return The whole page corresponing to the {@link PageUrl}.
	 */
	@SuppressWarnings("deprecation")
	protected static PageText httpGet(HttpClient client, PageUrl pageUrl) {
		logger.info("Preparing GET for url " + pageUrl);
		
		PageText pageText = new PageText();
		GetMethod method = null;
		try {
			method = new GetMethod(pageUrl.getStringValue());
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(1, false));

			method.setFollowRedirects(true);

			method.setRequestHeader(new Header("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0;Windows NT 5.1)"));

			method.setRequestHeader(new Header("accept", "*/*"));
			method.setRequestHeader(new Header("accept-language", "en-us"));
			method.setRequestHeader(new Header("connection", "Keep-Alive"));
			method.setRequestHeader(new Header("cache-control", "no-cache"));
			
			HostConfiguration hostConfig = client.getHostConfiguration();

			hostConfig.setProxy("10.255.170.65", 8080);

			// Authenticate using NTLM
			client.getState()
					.setProxyCredentials(
							null,
							"10.255.170.65",
							new NTCredentials("godfried.meesters", "floepfloep",
									"", ""));

			int statusCode = client.executeMethod(method);

			StringBuffer responseBody = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(method
					.getResponseBodyAsStream()));
			String readLine;
			while (((readLine = br.readLine()) != null)) {
				responseBody.append(readLine);
			}

			pageText = new PageText(FloepUtils.convertHtmlToAscii(responseBody
					.toString().trim()));

			if (statusCode >= 300 && statusCode < 399) {
				// Header location = method.getResponseHeader("Location");
				// String redirect = location.getValue();
				// PageUrl newPageUrl = new PageUrl(redirect,pageUrl);
				// Logger.getLogger("floep.util.Http").info(
				// "Redirecting to " + newPageUrl.getStringValue());
				// pageText = httpGet(client, newPageUrl);
			} else if (statusCode != HttpStatus.SC_OK) {
				logger.error(pageUrl.getStringValue() + " - Method failed: "
						+ method.getStatusLine()
						+ "\n"
						+ pageText.getStringValue());
						
				pageText.setStringValue("");
			} else {
				logger.info("GOT " + pageUrl.getStringValue());
			}

		} catch (HttpException e) {
			logger.error(pageUrl.getStringValue()
					+ " - Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			logger.error(pageUrl.getStringValue()
					+ " - Fatal transport error: " + e.getMessage());
		} catch (Exception e) {
			logger.error(pageUrl.getStringValue() + " - " + e.getMessage());
		} finally {
			method.releaseConnection();
		}

		return pageText;
	}

	public boolean isProxyConnectivityOk(Proxy proxy, PageUrl pageUrlToTest,
			String expectedStringInPage, int maxTimeOutInSecs) {
		HttpClient localClient = new HttpClient();
		localClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(maxTimeOutInSecs * 1000);

		GetMethod method = null;

		try {

			method = new GetMethod(pageUrlToTest.getStringValue());
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(1, false));

			method.setFollowRedirects(true);

			method
					.setRequestHeader(new Header(
							"User-Agent",
							"Mozilla/5.0 (Windows; U; Windows NT 5.1; en_US; rv:1.8.1.4)Gecko/20070515 Firefox/2.0.0.4"));

			HostConfiguration hostConfig = httpClient.getHostConfiguration();

			hostConfig.setProxy(proxy.getIp(), proxy.getPort());

			int statusCode = httpClient.executeMethod(method);

			StringBuffer responseBody = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(method
					.getResponseBodyAsStream()));
			String readLine;
			while (((readLine = br.readLine()) != null)) {
				responseBody.append(readLine);
			}

			if (statusCode == HttpStatus.SC_OK) {
				logger
						.info("Sucessfully got "
								+ pageUrlToTest.getStringValue());

				String pageText = FloepUtils.convertHtmlToAscii(responseBody.toString());

				if (StringUtils.containsIgnoreCase(pageText,
						expectedStringInPage)) {
					logger.info("Expected string is in returned page "
							+ pageUrlToTest.getStringValue());

					return true;
				} else
					logger.info("Expected string is NOT in returned page "
							+ pageUrlToTest.getStringValue());

			}

		} catch (HttpException e) {
			logger.error(pageUrlToTest.getStringValue()
					+ " - Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			logger.error(pageUrlToTest.getStringValue()
					+ " - Fatal transport error: " + e.getMessage());
		} catch (Exception e) {
			logger.error(pageUrlToTest.getStringValue() + " - "
					+ e.getMessage());
		} finally {
			method.releaseConnection();
		}

		return false;
	}

	public static boolean testProxyAnonymity() {
		return true;
	}
	
	
	
	
	public Page postForm(Form form)
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.addAll(form.getInputFields());
		nameValuePairs.addAll(form.getHiddenFields());
		Page page = null;
		
		for (NameValuePair submitButton : form.getSubmitButtons()) {
			List<NameValuePair> nameValuePairsToSubmit = new ArrayList<NameValuePair>();
			
			if (!submitButton.getName().equals("")) {
				nameValuePairsToSubmit.add(submitButton);
			}

			logger.info("Current submit button name is '"
					+ submitButton.getName() + "'");

			logger.info("Submitting following nameValuePairs:");
			for (NameValuePair nvp : nameValuePairsToSubmit) {
				logger.info(" - " + nvp.getName() + ":'"
						+ nvp.getValue() + "'");
			}

			
			page = Http.getHttp().postPageText(form.getActionUrl(),
					nameValuePairsToSubmit);
		}
		
		return page;
	}
	
}
