package org.xmlcml.ami2.plugins.phylotree;

import org.junit.Assert;
import org.junit.Test;

import blogspot.software_and_algorithms.stern_library.string.DamerauLevenshteinAlgorithm;

public class StringsTest {

	@Test
	public void testLevenstein() {
		// no idea what are good values
		int deleteCost = 1;
		int insertCost = 1;
		int replaceCost = 1;
		int swapCost = 1;
		DamerauLevenshteinAlgorithm dl = new DamerauLevenshteinAlgorithm(deleteCost, insertCost, replaceCost, swapCost);
		// from rosettacode.org/wiki/Levenshtein_distance ; values work
		String source = "kitten";
		String target = "sitting";
		int distance = dl.execute(source, target);
		Assert.assertEquals("distance", 3, distance);
		distance = dl.execute("rosettacode", "raisethysword");
		Assert.assertEquals("rosettacode", 8, distance);
	}
	
}
