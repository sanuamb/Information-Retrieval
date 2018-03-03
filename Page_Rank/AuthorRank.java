package com;

import java.io.File;
import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;


public class AuthorRank {

	HashMap <String,String>v =new HashMap<String,String>();
	Map<String,List<String>>edge=new LinkedHashMap<>();
	DirectedSparseGraph<String, String>dsg=new DirectedSparseGraph<>();
	HashMap<String,Double>res=new HashMap<String,Double>();
	
	public void parseFile() throws Exception
	{
		String Line;
		File f=new File("author.txt");
		System.out.println(f.getPath());
		Scanner s=new Scanner(new FileInputStream(f));
		
		
		while(s.hasNextLine())
		{
			Line=s.nextLine();
			//System.out.println(Line);
			if(Line.contains("*Vertices"))
			{
				String [] s_line=Line.split("\\s+");
				String tot_vertice=s_line[1];
				System.out.println(tot_vertice);
				Line=s.nextLine();
				while(Line.contains("*Edges")==false)
				{
					String[] s1_line=Line.split("\\s+");
					String k=(s1_line[0]);
					String label=s1_line[1];
					label=label.substring(1,label.length()-1);
					v.put(k, label);
					Line=s.nextLine();
					//System.out.println(k +" "+label);
				}
			}
			if(Line.contains("*Edges"))
			{
				String[] e_line=Line.split("\\s+");
				String tot_edges=e_line[1];
				//System.out.println(tot_edges);
				Line=s.nextLine();
				while(s.hasNextLine())
				{
					String[] e1_line=Line.split("\\s+");
					String k=(e1_line[0]);
					String e=e1_line[1];
					put(edge,k,e);
					Line=s.nextLine();
					
				}
			}
			
			//System.out.println(edge);
			//System.out.println(v);
		}
		//System.out.println(edge);
		//System.out.println(v);
				
	}
	
	
	 public static void put(Map<String, List<String>> edge, String key, String value)
	 {
		 if (edge.get(key) == null) {
	            List<String> list = new ArrayList<>();
	            list.add(value);
	            edge.put(key, list);
	        } 
		 else {
	            edge.get(key).add(value);
	        }
	 }
	 
	public void createGraph()
	{
		//Adding vertices 
		for(Map.Entry<String, String>vertices:v.entrySet())
		{
			String vertex=vertices.getKey();
			String lab=vertices.getValue();
			dsg.addVertex(lab);
		}
		
		//Adding edges
		int edge_count=0;
		for (Map.Entry<String, List<String>> e1 : edge.entrySet()) 
		{
		    String key = e1.getKey();
		    List<String> value = e1.getValue();
		    
		    for(String aString : value)
		    {
		        edge_count++;
		        String node1_label=v.get(key);
		        String node2_label=v.get(aString);
		        dsg.addEdge(Integer.toString(edge_count),node1_label, node2_label,EdgeType.DIRECTED);
		        
		    }
		}
		
		
		
	}
	@SuppressWarnings("unchecked")
	public void implementPagerank()
	{
		PageRank<String, String>pg=new PageRank<>(dsg, (1-0.85));
		pg.evaluate();
		
		//Checking the vertex score
		for(String v1:dsg.getVertices())
		{
			res.put(v1, pg.getVertexScore(v1));
		}
		
		Set<Map.Entry<String, Double>>a1=res.entrySet();
		Object []p1=a1.toArray();
		Arrays.sort(p1,new Comparator()
				{
			public int compare(Object o1, Object o2) {
		        return ((Map.Entry<String, Double>) o2).getValue()
		                   .compareTo(((Map.Entry<String, Double>) o1).getValue());}
				});
		
		
		
		
		
		//printing the top 10 authors
//		Iterator it=res.entrySet().iterator();
//		int top=1;
//		while(it.hasNext())
//		{
//			Map.Entry pair = (Map.Entry)it.next();
//	        System.out.println("Author"+" "+ pair.getKey() +" "+"Score:" + pair.getValue());
//	        top=top+1;
//	        if(top>10)
//	        {
//	        		break;
//	        }
		//}
		int top=1;
		for(Object e:p1)
		{
			if(top<=10)
			{
				System.out.println("Author"+"  "+((Map.Entry<String, Double>) e).getKey() + "  "+"Score:"
			            + ((Map.Entry<String,Double>) e).getValue());
			}
			top++;
		}
		
	}
	 
	public static void main(String[] args) throws Exception 
	{
		AuthorRank a=new AuthorRank();
		a.parseFile();
		a.createGraph();
		a.implementPagerank();
		
		

	}

}
