package com.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Paths;

import java.util.Arrays;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
public class Main 
{

	public static void main(String[] args) throws Exception 
	{
		int choice=0;
		
	
		System.out.println("\n1.Task1\n2.Task2\n3.Algorithm Comparisons");
		System.out.println("Enter the choice:");
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String l=br.readLine();
		choice=Integer.parseInt(l);
		
		//sc.close();
		Main m=new Main();
	
		switch(choice)
		{
		case 1: m.task1();
				break;
				
		case 2:m.task2();
			   	break;
		case 3:	compareAlgorithms c=new compareAlgorithms();
				c.compare();
				break;
			
		}
		
		
		
	
	

	}
	
	public void task1() throws Exception
	{
		
		System.out.println("Enter the query:");
		Scanner sc=new Scanner(System.in);
		String querystring=sc.nextLine();
		sc.close();
		String indexpath="index";
		IndexReader r= DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
		IndexSearcher s=new IndexSearcher(r);
		Analyzer a=new StandardAnalyzer();
		QueryParser p=new QueryParser("TEXT",a);
		Query q=p.parse(querystring);
		Set<Term>qterm=new HashSet<Term>();
		s.createNormalizedWeight(q,false).extractTerms(qterm);
		
		for(Term t:qterm)
			{
				System.out.println(t.text());
			}
			
			
			easySearch tfidf=new easySearch();
	
			HashMap<String,Double>arr=new HashMap<String,Double>();
			arr=tfidf.caltfidf(qterm);
			System.out.println("Relevance score of query");
			File fp=new File("Task1_output");
			PrintWriter wr=new PrintWriter(fp);
			System.out.println("DocNo"+"\t"+"RelevanceScore");
			for(Map.Entry<String,Double>h:arr.entrySet())
			{
				
				System.out.println(h.getKey()+"\t"+h.getValue());
				wr.println(h.getKey()+"\t"+h.getValue());
			}
			wr.close();

		
	}
	
	
	public void task2() throws Exception
	{
		System.out.println("Performing Search Algorithm on Short Query");
		performShortQuery();
		System.out.println("Performing Search Algorithm on Long Query");
		performlongQuery();
	}
	
	
	@SuppressWarnings("unchecked")
	public void performShortQuery() throws Exception
	{
		HashMap<Integer,String>ShortQuery=new HashMap<Integer,String>();
		searchTRECtopics tr=new searchTRECtopics();
		ShortQuery=tr.parseShortQuery(ShortQuery);
		for(Map.Entry<Integer, String>lq:ShortQuery.entrySet())
		{
			System.out.println(lq.getKey()+" "+lq.getValue());
		}
		HashMap<String,Double>arr1=new HashMap<String,Double>();
		File f1=new File("task2_output_shortquery.txt");
		PrintWriter task2op=new PrintWriter(f1);
		task2op.println("Topic"+"\t"+"Query0"+"\t"+"DocNo"+"\t"+"Rank"+"\t"+"Score"+"\t"+"Run");
		System.out.println("Topic"+"\t"+"Query0"+"\t"+"DocNo"+"\t"+"Rank"+"\t"+"Score"+"\t"+"Rank");
		for(int i=1;i<=50;i++)
		{
			arr1=tr.performTFIFQuery(ShortQuery.get(i));
			Set<Map.Entry<String, Double>>a1=arr1.entrySet();
			Object []p1=a1.toArray();
			Arrays.sort(p1,new Comparator()
					{
				public int compare(Object o1, Object o2) {
			        return ((Map.Entry<String, Double>) o2).getValue()
			                   .compareTo(((Map.Entry<String, Double>) o1).getValue());}
					});
			int cr=1;
			task2op.flush();
			for (Object e : p1) 
			{
				if(cr<=1000)
				{
			    System.out.println(50+i +"\t"+"0"+"\t"+((Map.Entry<String, Double>) e).getKey() + "\t"+cr+"\t"
			            + ((Map.Entry<String,Double>) e).getValue()+"\t"+"run-0");
			    task2op.println(50+i +"\t"+"0"+"\t"+((Map.Entry<String, Double>) e).getKey() + "\t"+cr+"\t"
			            + ((Map.Entry<String,Double>) e).getValue()+"\t"+"run-0");
			    task2op.flush();
				}
				cr++;
			}
			
		}
		task2op.close();
		
		

	}
	
	@SuppressWarnings("unchecked")
	public void performlongQuery() throws Exception
	{
		HashMap<Integer,String>LongQuery=new HashMap<Integer,String>();
		searchTRECtopics tr1=new searchTRECtopics();
		LongQuery=tr1.parseLongQuery(LongQuery);
//		for(Map.Entry<Integer, String>lq:LongQuery.entrySet())
//		{
//			System.out.println(lq.getKey()+" "+lq.getValue());
//		}
		HashMap<String,Double>arr2=new HashMap<String,Double>();
		File f2=new File("task2_output_Longquery.txt");
		PrintWriter task2op1=new PrintWriter(f2);
		task2op1.println("Topic"+"  "+"Query0"+"  "+"DocNo"+"  "+"Rank"+"  "+"Score"+"  "+"Run");
		System.out.println("Topic"+"  "+"Query0"+"  "+"DocNo"+"  "+"Rank"+"  "+"Score"+"  "+"Rank");
		for(int i=1;i<=50;i++)
		{
			arr2=tr1.performTFIFLQuery(LongQuery.get(i));
			Set<Map.Entry<String, Double>>a1=arr2.entrySet();
			Object []p1=a1.toArray();
			Arrays.sort(p1,new Comparator()
					{
				public int compare(Object o1, Object o2) {
			        return ((Map.Entry<String, Double>) o2).getValue()
			                   .compareTo(((Map.Entry<String, Double>) o1).getValue());}
					});
			int cr=1;
			task2op1.flush();
			for (Object e : p1) 
			{
				if(cr<=1000)
				{
			    System.out.println(50+i +"  "+"0"+"  "+((Map.Entry<String, Double>) e).getKey() + "  "+cr+"  "
			            + ((Map.Entry<String,Double>) e).getValue()+"  "+"run-0");
			    task2op1.println(50+i +"  "+"0"+"  "+((Map.Entry<String, Double>) e).getKey() + "  "+cr+"  "
			            + ((Map.Entry<String,Double>) e).getValue()+"  "+"run-0");
			    task2op1.flush();
				}
				cr++;
			}
			
		}
		task2op1.close();
		

		

	}

}
