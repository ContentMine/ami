package org.xmlcml.ami2.plugins.gene;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIPluginOption;
import org.xmlcml.cproject.util.CellRenderer;

public class GenePluginOption extends AMIPluginOption {

	private static final Logger LOG = Logger.getLogger(GenePluginOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "gene";

	public GenePluginOption() {
		super(TAG);
	}

	public GenePluginOption(List<String> options, List<String> flags) {
		super(TAG, options, flags);
	}

	protected void run() {
		String cmd = "--project "+projectDir+" -i scholarly.html --g.gene --g.type "+optionString;
		new GeneArgProcessor(cmd).runAndOutput();
	}
	
	@Override
	public CellRenderer getNewCellRenderer() {
		CellRenderer cellRenderer = super.getNewCellRenderer();
		cellRenderer.setHref0(AMIPluginOption.WIKIPEDIA_HREF0);
		cellRenderer.setHref1(AMIPluginOption.WIKIPEDIA_HREF1);
		return cellRenderer;
	}

}
