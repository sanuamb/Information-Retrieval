package com.search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

import javax.security.auth.callback.LanguageCallback;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.FSDirectory;

public class compareAlgorithms 
{	
	
public void compare() throws Exception
{	
	
	
	
	
	System.out.println("\n1.VectorSpace Model\n2.BM25\n3.Language Model with Dirichlet Smoothing\n4.Language Model with Jelinek Mercer Smoothing");
	Scanner scan=new Scanner(System.in);
	System.out.println("Enter the choice:");
	int choice=scan.nextInt();
	scan.close();
	switch(choice)
	{
		case 1: System.out.println("Vector Space Model");
		        searchvectorspace();
		        break;
		case 2: System.out.println("BM25");
				searchBM25();
				break;
		case 3:System.out.println("Language Model with Dirichlet Smoothing");
			   searchLMDS();
				break;
		case 4:System.out.println("Language Model with Jelinek Mercer Smoothing");
			   searchLMJMS();
			   break;
		
	}
	
	

}

public void searchvectorspace() throws ParseException, IOException
{
	String indexpath="index";
	
	System.out.println("Processing for ShortQuery of TREC topics");
	HashMap<Integer,String>ShortQuery=new HashMap<Integer,String>();
	searchTRECtopics tr=new searchTRECtopics();
	ShortQuery=tr.parseShortQuery(ShortQuery);
	File vs=new File("vectorspace_output_shortquery.txt");
	PrintWriter vsp=new PrintWriter(vs);
	vsp.println("Topic"+"  "+"Query0"+"  "+"DocNo"+"  "+"Rank"+"  "+"Score"+"  "+"Run");
	vsp.flush();
	for(int i=1;i<=50;i++)
	{
		
		Analyzer a=new StandardAnalyzer();
		IndexReader r= DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
		IndexSearcher s=new IndexSearcher(r);
		s.setSimilarity(new ClassicSimilarity());
		QueryParser p=new QueryParser("TEXT",a);
		String query=ShortQuery.get(i);
		Query q=p.parse(QueryParser.escape(query));
		TopDocs topDocs = s.search(q, 1000);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for(int k=0;k<scoreDocs.length;k++)
		{
			Document d=s.doc(scoreDocs[k].doc);
			System.out.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp.flush();
			
		}
	}
	vsp.close();
	
	System.out.println("Processing LongQuery TREC topics");
	HashMap<Integer,String>LongQuery=new HashMap<Integer,String>();
	searchTRECtopics tr1=new searchTRECtopics();
	LongQuery=tr1.parseLongQuery(LongQuery);
	File vs1=new File("vectorspace_output_Longquery.txt");
	PrintWriter vsp1=new PrintWriter(vs1);
	vsp1.println("Topic"+"  "+"Query0"+"  "+"DocNo"+"  "+"Rank"+"  "+"Score"+"  "+"Run");
	vsp1.flush();
	for(int i=1;i<=50;i++)
	{
		
		Analyzer a=new StandardAnalyzer();
		IndexReader r= DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
		IndexSearcher s=new IndexSearcher(r);
		s.setSimilarity(new ClassicSimilarity());
		QueryParser p=new QueryParser("TEXT",a);
		String query=LongQuery.get(i);
		Query q=p.parse(QueryParser.escape(query));
		TopDocs topDocs = s.search(q, 1000);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for(int k=0;k<scoreDocs.length;k++)
		{
			Document d=s.doc(scoreDocs[k].doc);
			System.out.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp1.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp1.flush();
			
		}
	}
	vsp1.close();
	
	
	

	
}

public void searchBM25() throws IOException, Exception
{
   String indexpath="index";
	
	System.out.println("Processing for ShortQuery of TREC topics");
	HashMap<Integer,String>ShortQuery=new HashMap<Integer,String>();
	searchTRECtopics tr=new searchTRECtopics();
	ShortQuery=tr.parseShortQuery(ShortQuery);
	File vs=new File("BM25_output_shortquery.txt");
	PrintWriter vsp=new PrintWriter(vs);
	vsp.println("Topic"+"  "+"Query0"+"  "+"DocNo"+"  "+"Rank"+"  "+"Score"+"  "+"Run");
	vsp.flush();
	for(int i=1;i<=50;i++)
	{
		
		Analyzer a=new StandardAnalyzer();
		IndexReader r= DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
		IndexSearcher s=new IndexSearcher(r);
		s.setSimilarity(new BM25Similarity());
		QueryParser p=new QueryParser("TEXT",a);
		String query=ShortQuery.get(i);
		Query q=p.parse(QueryParser.escape(query));
		TopDocs topDocs = s.search(q, 1000);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for(int k=0;k<scoreDocs.length;k++)
		{
			Document d=s.doc(scoreDocs[k].doc);
			System.out.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp.flush();
			
		}
	}
	vsp.close();
	
	System.out.println("Processing LongQuery TREC topics");
	HashMap<Integer,String>LongQuery=new HashMap<Integer,String>();
	searchTRECtopics tr1=new searchTRECtopics();
	LongQuery=tr1.parseLongQuery(LongQuery);
	File vs1=new File("BM25_output_Longquery.txt");
	PrintWriter vsp1=new PrintWriter(vs1);
	vsp1.println("Topic"+"  "+"Query0"+"  "+"DocNo"+"  "+"Rank"+"  "+"Score"+"  "+"Run");
	vsp1.flush();
	for(int i=1;i<=50;i++)
	{
		
		Analyzer a=new StandardAnalyzer();
		IndexReader r= DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
		IndexSearcher s=new IndexSearcher(r);
		s.setSimilarity(new BM25Similarity());
		QueryParser p=new QueryParser("TEXT",a);
		String query=LongQuery.get(i);
		Query q=p.parse(QueryParser.escape(query));
		TopDocs topDocs = s.search(q, 1000);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for(int k=0;k<scoreDocs.length;k++)
		{
			Document d=s.doc(scoreDocs[k].doc);
			System.out.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp1.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp1.flush();
			
		}
	}
	vsp1.close();
	
	
	

}

public void searchLMDS() throws Exception
{
	String indexpath="index";
	
	System.out.println("Processing for ShortQuery of TREC topics");
	HashMap<Integer,String>ShortQuery=new HashMap<Integer,String>();
	searchTRECtopics tr=new searchTRECtopics();
	ShortQuery=tr.parseShortQuery(ShortQuery);
	File vs=new File("LMDS_output_shortquery.txt");
	PrintWriter vsp=new PrintWriter(vs);
	vsp.println("Topic"+"  "+"Query0"+"  "+"DocNo"+"  "+"Rank"+"  "+"Score"+"  "+"Run");
	vsp.flush();
	for(int i=1;i<=50;i++)
	{
		
		Analyzer a=new StandardAnalyzer();
		IndexReader r= DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
		IndexSearcher s=new IndexSearcher(r);
		s.setSimilarity(new LMDirichletSimilarity());
		QueryParser p=new QueryParser("TEXT",a);
		String query=ShortQuery.get(i);
		Query q=p.parse(QueryParser.escape(query));
		TopDocs topDocs = s.search(q, 1000);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for(int k=0;k<scoreDocs.length;k++)
		{
			Document d=s.doc(scoreDocs[k].doc);
			System.out.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp.flush();
			
		}
	}
	vsp.close();
	
	System.out.println("Processing LongQuery TREC topics");
	HashMap<Integer,String>LongQuery=new HashMap<Integer,String>();
	searchTRECtopics tr1=new searchTRECtopics();
	LongQuery=tr1.parseLongQuery(LongQuery);
	File vs1=new File("LMDS_output_Longquery.txt");
	PrintWriter vsp1=new PrintWriter(vs1);
	vsp1.println("Topic"+"  "+"Query0"+"  "+"DocNo"+"  "+"Rank"+"  "+"Score"+"  "+"Run");
	vsp1.flush();
	for(int i=1;i<=50;i++)
	{
		
		Analyzer a=new StandardAnalyzer();
		IndexReader r= DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
		IndexSearcher s=new IndexSearcher(r);
		s.setSimilarity(new LMDirichletSimilarity());
		QueryParser p=new QueryParser("TEXT",a);
		String query=LongQuery.get(i);
		Query q=p.parse(QueryParser.escape(query));
		TopDocs topDocs = s.search(q, 1000);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for(int k=0;k<scoreDocs.length;k++)
		{
			Document d=s.doc(scoreDocs[k].doc);
			System.out.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp1.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp1.flush();
			
		}
	}
	vsp1.close();
	
	
	
}

public void searchLMJMS() throws Exception
{
String indexpath="index";
	
	System.out.println("Processing for ShortQuery of TREC topics");
	HashMap<Integer,String>ShortQuery=new HashMap<Integer,String>();
	searchTRECtopics tr=new searchTRECtopics();
	ShortQuery=tr.parseShortQuery(ShortQuery);
	File vs=new File("LMJMS_output_shortquery.txt");
	PrintWriter vsp=new PrintWriter(vs);
	vsp.println("Topic"+"  "+"Query0"+"  "+"DocNo"+"  "+"Rank"+"  "+"Score"+"  "+"Run");
	vsp.flush();
	for(int i=1;i<=50;i++)
	{
		
		Analyzer a=new StandardAnalyzer();
		IndexReader r= DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
		IndexSearcher s=new IndexSearcher(r);
		s.setSimilarity(new LMJelinekMercerSimilarity((float) 0.7));
		QueryParser p=new QueryParser("TEXT",a);
		String query=ShortQuery.get(i);
		Query q=p.parse(QueryParser.escape(query));
		TopDocs topDocs = s.search(q, 1000);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for(int k=0;k<scoreDocs.length;k++)
		{
			Document d=s.doc(scoreDocs[k].doc);
			System.out.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp.flush();
			
		}
	}
	vsp.close();
	
	System.out.println("Processing LongQuery TREC topics");
	HashMap<Integer,String>LongQuery=new HashMap<Integer,String>();
	searchTRECtopics tr1=new searchTRECtopics();
	LongQuery=tr1.parseLongQuery(LongQuery);
	File vs1=new File("LMJMS_output_Longquery.txt");
	PrintWriter vsp1=new PrintWriter(vs1);
	vsp1.println("Topic"+"  "+"Query0"+"  "+"DocNo"+"  "+"Rank"+"  "+"Score"+"  "+"Run");
	vsp1.flush();
	for(int i=1;i<=50;i++)
	{
		
		Analyzer a=new StandardAnalyzer();
		IndexReader r= DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
		IndexSearcher s=new IndexSearcher(r);
		s.setSimilarity(new LMJelinekMercerSimilarity((float) 0.7));
		QueryParser p=new QueryParser("TEXT",a);
		String query=LongQuery.get(i);
		Query q=p.parse(QueryParser.escape(query));
		TopDocs topDocs = s.search(q, 1000);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for(int k=0;k<scoreDocs.length;k++)
		{
			Document d=s.doc(scoreDocs[k].doc);
			System.out.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp1.println(50+i+"\t"+"0"+"\t"+d.get("DOCNO")+"\t"+(k+1)+"\t"+scoreDocs[k].score+"\t"+"run-0");
			vsp1.flush();
			
		}
	}
	vsp1.close();
	

	
	
}


}
