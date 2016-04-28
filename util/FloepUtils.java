package pagegen.util;

import java.net.MalformedURLException;

import org.apache.commons.lang.StringUtils;

import pagegen.entity.PageText;
import pagegen.entity.PageUrl;

public class FloepUtils {

	public static String convertHtmlToAscii(String sourceText) {
		
		sourceText = sourceText.replaceAll("(&nbsp;)|(&#160;)", "\u00A0");
		sourceText = sourceText.replaceAll("(&ccedil;)|(&#231;)", "ç");
		sourceText = sourceText.replaceAll("(&egrave;)|(&#232;)", "è");
		sourceText = sourceText.replaceAll("(&eacute;)|(&#233;)", "é");
		sourceText = sourceText.replaceAll("(&ecirc;)|(&#234;)", "ë");
		sourceText = sourceText.replaceAll("(&euml;)|(&#235;)", "ë");
		sourceText = sourceText.replaceAll("(&szlig;)|(&#223;)", "ß");
		sourceText = sourceText.replaceAll("(&quot;)|(&#34;)", "\"");
		sourceText = sourceText.replaceAll("(&copy;)|(&#169;)", "©");
		sourceText = sourceText.replaceAll("(&lt;)|(&#60;)", "<");
		sourceText = sourceText.replaceAll("(&gt;)|(&#62;)", ">");
		sourceText = sourceText.replaceAll("(&amp;)|(&#38;)", "&");
		sourceText = sourceText.replaceAll("(&apos;)|(&#39;)", "'");
	
		return sourceText;
	}
	
	public static String convertAsciiToXml(String sourceText)
	{
		sourceText = sourceText.replaceAll("'","&apos;");
		sourceText = sourceText.replaceAll("\"", "&quot;");
//		sourceText = sourceText.replaceAll("<", "&lt;");
//		sourceText = sourceText.replaceAll(">", "&gt;");
		sourceText = sourceText.replaceAll("&", "&amp;");
		
		return sourceText;
	}
	
	public static boolean matchesIgnoreCase(String str, String regex)
	{
		return str.toLowerCase().matches(regex.toLowerCase());
	}
	
	public static boolean matchesIgnoreCase(PageText pageText, String[] regex)
	{
		return matchesIgnoreCase(pageText.toString(), regex);
	}
	
	public static boolean matchesIgnoreCase(String s, String[] regex)
	{
		for(String oneRegex : regex)
		{
			if(s.toLowerCase().matches(oneRegex))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean matches(PageText pageText, String[] regex)
	{
		for(String oneRegex : regex)
		{
			if(pageText.toString().toLowerCase().matches(oneRegex))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static String extractHost(PageUrl pageUrl)
	{
		String[] parts = pageUrl.toString().split("\\.");
		String host = "";
	
		for(int i = parts.length - 1;i > 0; i--)
		{
			if(i == parts.length - 2)
				host = parts[i] ;
		}
		 
		return host;
		
	}
	
	public static boolean containsIgnoreCase(PageText pageText, String searchStr)
	{
		if(StringUtils.containsIgnoreCase(pageText.toString(), searchStr))
			return true;
		else
			return false;
	}
	
}
