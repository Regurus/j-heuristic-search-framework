package core; /**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.FileNotFoundException;
import java.io.InputStream;
import core.algorithms.*;
import core.domains.FifteenPuzzle;
import org.junit.Test;

public class TestAllBasics {
		
	@Test
	public void testAstarBinHeap() throws FileNotFoundException {
		SearchDomain domain = createFifteenPuzzle("12");
		SearchAlgorithm algo = new WAStar();
		testSearchAlgorithm(domain, algo, 65271, 32470, 45);
	}	

	@Test
	public void testRBFS() throws FileNotFoundException {
		SearchDomain domain = createFifteenPuzzle("12");
		SearchAlgorithm algo = new RBFS();
		testSearchAlgorithm(domain, algo, 301098, 148421, 45);
	}
	@Test
	public void testDPS() throws FileNotFoundException {
		SearchDomain domain = createFifteenPuzzle("12");
		SearchAlgorithm algo = new DP("name",true,true,false);
		testSearchAlgorithm(domain, algo, 301098, 148421, 45);
	}
	
	@Test
	public void testIDAstar() throws FileNotFoundException {
		SearchDomain domain = createFifteenPuzzle("12");
		SearchAlgorithm algo = new IDAstar();
		testSearchAlgorithm(domain, algo, 546343, 269708, 45);
	}

	@Test
	public void testIDDPS() throws FileNotFoundException {
		SearchDomain domain = createFifteenPuzzle("12");
		SearchAlgorithm algo = new IDDPS();
		testSearchAlgorithm(domain, algo, 546343, 269708, 45);
	}

	@Test
	public void testEES() throws FileNotFoundException {
		SearchDomain domain = createFifteenPuzzle("12");
		SearchAlgorithm algo = new EES(2);
		testSearchAlgorithm(domain, algo, 5131, 2506, 55);
	}	
	
	@Test
	public void testWRBFS() throws FileNotFoundException {
		SearchDomain domain = createFifteenPuzzle("12");
		SearchAlgorithm algo = new WRBFS();
		testSearchAlgorithm(domain, algo, 301098, 148421, 45);
	}	
	
	public SearchDomain createFifteenPuzzle(String instance) throws FileNotFoundException {
		InputStream is = getClass().getClassLoader().getResourceAsStream("tileFormatTest.pzl");
		FifteenPuzzle puzzle = new FifteenPuzzle(is);
		return puzzle;
	}
	public static void testSearchAlgorithm(SearchDomain domain, SearchAlgorithm algo, long generated, long expanded, double cost) {
		SearchResult result = algo.search(domain);
		Solution sol = result.getSolutions().get(0);
		showSolution(result,0);
		/*Assert.assertTrue(result.getWallTimeMillis() > 1);
		Assert.assertTrue(result.getWallTimeMillis() < 200);
		Assert.assertTrue(result.getCpuTimeMillis() > 1);
		Assert.assertTrue(result.getCpuTimeMillis() < 200);
		Assert.assertTrue(result.getGenerated() == generated);
		Assert.assertTrue(result.getExpanded() == expanded);
		Assert.assertTrue(sol.getCost() == cost);
		Assert.assertTrue(sol.getLength() == cost+1);*/
	}
	public static void showSolution(SearchResult searchResult,int solutionIndex){
		Solution solution = searchResult.getSolutions().get(solutionIndex);
		/*for(State state: solution.getStates()){
			System.out.println(state.convertToString());
		}*/
		System.out.println("Cost: "+solution.getCost());
		System.out.println("Time: "+(searchResult).getCpuTimeMillis()/1000+"s");
		System.out.println("Expanded: "+(searchResult).getExpanded());
		System.out.println("Generated: "+(searchResult).getGenerated());
	}
	public static void main(String[] args) throws FileNotFoundException {
		TestAllBasics test = new TestAllBasics();
		test.testIDAstar();
		test.testIDDPS();
		test.testEES();
	}

}
