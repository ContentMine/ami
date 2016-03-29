package org.xmlcml.ami2.plugins.word;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.cmine.files.ResultElement;
import org.xmlcml.cmine.files.ResultsElement;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.html.HtmlBody;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.html.HtmlP;
import org.xmlcml.html.HtmlSpan;
import org.xmlcml.html.HtmlStyle;
import org.xmlcml.xml.XMLUtil;

/** contains results for bag of words
 * 
 * @author pm286
 *
 */
public class WordResultsElement extends ResultsElement {

	
	private static final Logger LOG = Logger
			.getLogger(WordResultsElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private Set<String> wordSet;

	public WordResultsElement(String title) {
		super(title);
	}

	public WordResultsElement(ResultsElement resultsElement) {
		if (resultsElement == null) {
			throw new RuntimeException("Null ResultsElement");
		}
		XMLUtil.copyAttributesFromTo(resultsElement, this);
		for (ResultElement resultElement : resultsElement) {
			WordResultElement wordResultElement = new WordResultElement(resultElement);
			this.appendChild(wordResultElement);
		}
	}

	private void ensureSet() {
		getOrCreateResultElementList();
		wordSet = new HashSet<String>();
		for (ResultElement resultElement : resultElementList) {
			String word = ((WordResultElement) resultElement).getWord();
			wordSet.add(word);
		}
	}

	public boolean contains(String word) {
		ensureSet();
		return wordSet.contains(word);
	}

	public IntArray getCountArray() {
		getOrCreateResultElementList();
		IntArray countArray = new IntArray();
		for (ResultElement resultElement : resultElementList) {
			WordResultElement wordResultElement = (WordResultElement) resultElement;
			countArray.addElement(wordResultElement.getCount());
		}
		return countArray;
	}

	IntArray createOrderedFontSizeArray() {
		IntArray fontSizeIntArray = null;
		IntArray countArray = getCountArray();
		try {
			IntRange countRange = countArray.getRange();
			RealRange realCountRange = new RealRange(countRange);
			RealRange fontRange = new RealRange(WordArgProcessor.MIN_FONT, WordArgProcessor.MAX_FONT);
			double countToFont = realCountRange.getScaleTo(fontRange);
			RealArray fontSizeArray = new RealArray(countArray);
			fontSizeArray = fontSizeArray.multiplyBy(countToFont);
			fontSizeArray = fontSizeArray.addScalar(WordArgProcessor.MIN_FONT);
			fontSizeIntArray = fontSizeArray.createIntArray();
		} catch (ArrayIndexOutOfBoundsException e) {
			// return null
		}
		return fontSizeIntArray;
	}

	public void writeResultsElementAsHTML(File outputFile, AMIArgProcessor wordArgProcessor) {
		IntArray fontSizeIntArray = createOrderedFontSizeArray();
		if (fontSizeIntArray != null) {
			Set<Integer> fontSizeSet = fontSizeIntArray.createIntegerSet();
			HtmlElement html = createHtmlElement(wordArgProcessor, fontSizeIntArray, fontSizeSet);
			try {
				outputFile.getParentFile().mkdirs();
				XMLUtil.debug(html, new FileOutputStream(outputFile), 1);
			} catch (IOException e) {
				throw new RuntimeException("Cannot write file "+outputFile, e);
			}
		}
	}

	HtmlElement createHtmlElement(AMIArgProcessor wordArgProcessor, IntArray fontSizeIntArray, Set<Integer> fontSizeSet) {
		HtmlElement html = new HtmlHtml();
		HtmlStyle style = new HtmlStyle();
		html.appendChild(style);
		style.addCss("* { font-family : helvetica;}");
		for (Integer fontSize : fontSizeSet) {
			String cssStyle = ".font"+fontSize+" { font-size : "+fontSize+"; }";
			style.addCss(cssStyle);
		}
		HtmlBody body = new HtmlBody();
		html.appendChild(body);
		HtmlP p = new HtmlP();
		body.appendChild(p);
		addWordsWithFontSizesInSpans(fontSizeIntArray, p);
		return html;
	}

	void addWordsWithFontSizesInSpans(IntArray fontSizeIntArray, HtmlP p) {
		int i = 0;
		for (ResultElement resultElement : this) {
			WordResultElement wordResultElement = (WordResultElement) resultElement;
			String word = wordResultElement.getWord();
			int count = wordResultElement.getCount();
			int fontSize = fontSizeIntArray.elementAt(i);
			HtmlSpan span = new HtmlSpan();
			span.setClassAttribute("font"+fontSize);
			span.appendChild(word+" ");
			p.appendChild(span);
			i++;
		}
	}


}
