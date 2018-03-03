package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;


public class AuthorRankwithQuery {

	HashMap<String,Double>TopPapers=new HashMap<String,Double>();
	HashMap<String,List<String>>authors=new HashMap<String,List<String>>();
	HashMap<String,Double>ap=new HashMap<String,Double>();
	HashMap <String,String>v =new HashMap<String,String>();
	Map<String,List<String>>edge=new LinkedHashMap<>();
	DirectedSparseGraph<String, String>dsg=new DirectedSparseGraph<>();
	HashMap<String,Double>res=new HashMap<String,Double>();

public void BM25Search(String query) throws Exception
{
	String indexpath="author_index";
	Analyzer an=new StandardAnalyzer();
	IndexReader r= DirectoryReader.open(FSDirectory.open(new File(indexpath)));
	IndexSearcher s=new IndexSearcher(r);
	s.setSimilarity(new BM25Similarity());
	QueryParser p=new QueryParser("content",an);
	Query q=p.parse(QueryParser.escape(query));
	TopDocs topDocs = s.search(q, 300);
	ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	for(int k=0;k<scoreDocs.length;k++)
	{
		Document d=s.doc(scoreDocs[k].doc);
		System.out.println(d.get("paperid")+"\t"+(k+1)+"\t"+scoreDocs[k].score);
		TopPapers.put(d.get("paperid"), (double)(scoreDocs[k].score));	
		String authorid=d.get("authorid");
		String paperid=d.get("paperid");
		put(authors,authorid,paperid);
			
	}
	System.out.println(authors);
	
}



public static void put(Map<String, List<String>> a, String key, String value)
{
	if (a.get(key) == null) 
	{
        List<String> list = new ArrayList<>();
        list.add(value);
        a.put(key, list);
    } 
 else {
        a.get(key).add(value);
    }
}
	
	
public void calPrior()
{
	double tot_score=0;
	for (Map.Entry<String, List<String>> a2 : authors.entrySet()) 
	{
	    String key = a2.getKey();
	    List<String> value = a2.getValue();
	    tot_score=0;
	    for(String paperval : value)
	    {
	        
	        double score=TopPapers.get(paperval);
	        tot_score=tot_score+score;     
	        
	    }
	    ap.put(key, tot_score);
	}
}

public void normPrior()
{
	double sum=0;
	for(double f:ap.values())
	{
		sum=sum+f;
	}
	//Normalize
	for(Map.Entry<String,Double> author1:ap.entrySet())
	{
		String k=author1.getKey();
		double val=author1.getValue();
		double final_val=(double)(val/sum);
		ap.put(k,final_val);
	}
}

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
				put1(edge,k,e);
				Line=s.nextLine();
				
			}
		}
		
		//System.out.println(edge);
		//System.out.println(v);
	}
	//System.out.println(edge);
	//System.out.println(v);
			
}

public static void put1(Map<String, List<String>> edge, String key, String value)
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
	Transformer<String, Double> apt = MapTransformer.getInstance(ap);
	PageRankWithPriors<String, String>pg=new PageRankWithPriors<String,String>(dsg, apt,(1-0.85));
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

public void checkAuthors()
{
	for(Map.Entry<String, String>vertices:v.entrySet())
	{
		if(ap.containsKey(vertices.getValue())==false)
		{
			ap.put(vertices.getValue(),new Double(0.0));
		}
	}
}

	
	public static void main(String[] args) throws Exception 
	{
		// TODO Auto-generated method stub
		AuthorRankwithQuery a1=new AuthorRankwithQuery();
		System.out.println("Enter the Query:");
		Scanner s=new Scanner(System.in);
		String query=s.nextLine();
		a1.BM25Search(query);
		a1.calPrior();
		a1.normPrior();
		a1.parseFile();
		a1.createGraph();
		a1.checkAuthors();
		a1.implementPagerank();
	}

}
