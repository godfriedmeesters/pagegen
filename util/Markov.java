package pagegen.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;

public class Markov {
	public static String markov(String source)
	{
		String[] splitted = source.split("\\s");
		List<String> allWords = new ArrayList<String>();

		for (String word : splitted) {
			if (!word.equals("")) {
				allWords.add(word);
			}
		}

		List<String> allWordsFixed = new ArrayList<String>();

		for (int allWordsIndex = 0; allWordsIndex < allWords.size(); allWordsIndex++) {
			String composedWord = "";

			if (allWords.get(allWordsIndex).contains("(")) {
				composedWord += " " + allWords.get(allWordsIndex);
				allWordsIndex++;
				while (allWordsIndex < allWords.size()
						&& (!allWords.get(allWordsIndex).contains(")"))) {
					composedWord += " " + allWords.get(allWordsIndex);
					allWordsIndex++;
				}

				if (allWordsIndex < allWords.size()) {
					composedWord += " " + allWords.get(allWordsIndex);
					allWordsFixed.add(composedWord.trim());
				}

			} else if (allWords.get(allWordsIndex).contains("\"")) {
				composedWord += " " + allWords.get(allWordsIndex);
				allWordsIndex++;
				while (allWordsIndex < allWords.size()
						&& (!allWords.get(allWordsIndex).contains("\""))) {
					composedWord += " " + allWords.get(allWordsIndex);
					allWordsIndex++;
				}

				if (allWordsIndex < allWords.size()) {
					composedWord += " " + allWords.get(allWordsIndex);
					allWordsFixed.add(composedWord.trim());
				}
			} else {
				allWordsFixed.add(allWords.get(allWordsIndex));
			}
		}

		Set<String> wordSet = new LinkedHashSet<String>();

		wordSet.addAll(allWordsFixed);

		List<String> uniqueWords = new ArrayList<String>(wordSet);

		HashMap<String, HashMap<String, Double>> nextWordsCount = new LinkedHashMap<String, HashMap<String, Double>>();

		for (String word : uniqueWords) {
			int totalIncidenceCount = 0;
			HashMap<String, Double> nextWordCount = new LinkedHashMap<String, Double>();
			for (int wordIndex = 0; wordIndex < allWordsFixed.size(); wordIndex++) {

				if (allWordsFixed.get(wordIndex).equals(word)) {
					if (wordIndex + 1 < allWordsFixed.size() - 1) {
						String nextWord = allWordsFixed.get(wordIndex + 1);

						if (nextWordCount.containsKey(nextWord)) {
							Double incidenceCount = nextWordCount.get(nextWord);
							nextWordCount.put(nextWord, incidenceCount + 1);

						} else {
							Double incidenceCount = 1.0;
							nextWordCount.put(nextWord, incidenceCount);
						}

						totalIncidenceCount++;
					}
				}
			}

			for (String nextWord : nextWordCount.keySet()) {
				Double incidenceCount = nextWordCount.get(nextWord);
				nextWordCount.put(nextWord, incidenceCount
						/ totalIncidenceCount);
			}

			nextWordsCount.put(word, nextWordCount);
		}

		String currentWord = uniqueWords.get(0);
		String text = "";
		String sentence = currentWord;
		RandomData randomData = new RandomDataImpl();

		while (text.length() < source.length()) {
			double x = randomData.nextUniform(0, 1);
			double probSum = 0;

			for (String nextWord : nextWordsCount.get(currentWord).keySet()) {
				double prob = nextWordsCount.get(currentWord).get(nextWord);
			
				if (x >= probSum && x <= probSum + prob) {
					sentence += " " + nextWord;
					
					if (nextWord.contains(".")) {
						if (StringUtils.countMatches(sentence, ",") <= 2 && sentence.length() > 50
								&& sentence.length() <250) {
							text += sentence.trim();
						}
						sentence = "";
					}

					currentWord = nextWord;
					break;
				}
				probSum += prob;
			}
		}

		return text;
	}
}
