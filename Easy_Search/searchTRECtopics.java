package com.search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;

public class searchTRECtopics 
{
	//regex for long query and short query
	public HashMap<Integer,String>parseShortQuery(HashMap<Integer,String>ShortQuery) throws FileNotFoundException
	{
		File f=new File("Topics.RTF");
		String l="";
		Scanner s=new Scanner(f);
		int count=1;
		Pattern p=Pattern.compile("<title>(.*?)[^<desc>]",Pattern.MULTILINE);
		while(s.hasNextLine())
		{
			String line=s.nextLine();
			
			Matcher m=p.matcher(line);
			//System.out.println(m.matches());
			//System.out.println(m.find());
			if(m.matches())
			{
				//int k=m.groupCount();
				//for(int z=0;z<k;z++)
				//{
					l=m.group(1);
					//System.out.println(l);
					l=l.replace("Topic:","");
				//}
				ShortQuery.put(count, l);
				count++;
			}
		}
		
		s.close();
		
		
		return ShortQuery;
	}
	
	public HashMap<Integer,String>parseLongQuery(HashMap<Integer,String>LongQuery) throws FileNotFoundException
	{
		File f1=new File("Topics.RTF");
		Scanner s=new Scanner(f1);
		int count=1;
		Pattern p1=Pattern.compile("<desc>");
		Pattern p2=Pattern.compile("<smry>");
		String data="";
		
		while(s.hasNextLine())
		{
			String line=s.nextLine();
			
			Matcher m1=p1.matcher(line);
			Matcher m2=p2.matcher(line);
			data="";
			//System.out.println(m1.find());
			if(m1.find())
			{
				line=line.replace("<desc>","");
				line=line.replace("Description:","");
				while(m2.find()==false)	
				{
					
					if(s.hasNextLine())
					{
						line=line.replace("\\","");
						data=data+" "+line;
						line=s.nextLine();
						m2=p2.matcher(line);
						
					}
					
					
				}
				LongQuery.put(count, data);
				count++;
			
				
			}
			
				

		}
		
		s.close();
		
		
		return LongQuery;
		
		
	}
	
	public HashMap<String,Double>performTFIFQuery(String query) throws ParseException, IOException
	{
		String indexpath="index";
		Analyzer a=new StandardAnalyzer();
		
			HashMap<String,Double>doc_score=new HashMap<String,Double>();
			QueryParser p=new QueryParser("TEXT",a);
			Query q=p.parse(QueryParser.escape(query));
			IndexReader r= DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
			IndexSearcher s=new IndexSearcher(r);
			Set<Term>sqterm=new HashSet<Term>();
			s.createNormalizedWeight(q,false).extractTerms(sqterm);
			Iterator<Term>it=sqterm.iterator();
			while(it.hasNext())
			{
				System.out.println(it.next().text());
			}
			doc_score=gettfidf(sqterm,indexpath);
			
			
			

		
		return doc_score;
		
	}
	
	public HashMap<String,Double> gettfidf(Set<Term>sqterm,String indexpath) throws IOException
	{
		IndexReader r1=DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
		double normlen,term_occ_docs,max_docs=0,sum_val=0;
		int doc,count=0;
		HashMap<Integer,Double>len=new HashMap<Integer,Double>();
		
		HashMap<String,Double>array=new HashMap<String,Double>();
		//HashMap<String,Double>DocScore=new HashMap<String,Double>();
		
		List<LeafReaderContext>leafC1=r1.getContext().reader().leaves();
		//for(Term t1:sqterm)
		//{
			
			
			ClassicSimilarity sim=new ClassicSimilarity();
			for(int i=0;i<leafC1.size();i++)
			{
				LeafReaderContext lc1=leafC1.get(i);
				int numofdoc=lc1.reader().maxDoc();
				int docbase=lc1.docBase;
				System.out.println(docbase);
				//Calculate Length
				for(int k=0;k<numofdoc;k++)
				{
					normlen=sim.decodeNormValue(lc1.reader().getNormValues("TEXT").get(k));
					Document d=lc1.reader().document(k);
					//String docno1=d.get("DOCNO");
					int h=k+docbase;
					double lenofdoc=(double)(1/(normlen*normlen));
					len.put(h, lenofdoc);
				}
				
			for(Term t1:sqterm)
			{
				System.out.println(t1.text());
				//Calculate IDF
				double df1=r1.docFreq(t1);
				max_docs=r1.maxDoc();
				double idf=Math.log(1+(double)(max_docs/df1));
				
				PostingsEnum e=MultiFields.getTermDocsEnum(lc1.reader(),"TEXT",t1.bytes());	
				if(e!=null)
				{
				while((doc=e.nextDoc())!=PostingsEnum.NO_MORE_DOCS)
				{
					term_occ_docs=e.freq();
					int docid=e.docID();
					
					Document d=lc1.reader().document(docid);
					String docno=d.get("DOCNO");
					
					double lendoc=len.get(docid+docbase);
					double tf=(double)(term_occ_docs/lendoc);
					double score=tf*idf;
					if(array.containsKey(docno))
					{
						array.put(docno, array.get(docno)+score);
						
					}
					else
					{
						array.put(docno,score);
					}
					
						
				}
				
				
			}
				
			}
		
		
			
		
	}
	
		return array;
		
		}
		
	public HashMap<String,Double>performTFIFLQuery(String query) throws ParseException, IOException
	{
		String indexpath="index";
		Analyzer a=new StandardAnalyzer();
		
		HashMap<String,Double>doc_score=new HashMap<String,Double>();
			QueryParser p=new QueryParser("TEXT",a);
			Query q=p.parse(QueryParser.escape(query));
			IndexReader r= DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
			IndexSearcher s=new IndexSearcher(r);
			Set<Term>sqterm=new HashSet<Term>();
			s.createNormalizedWeight(q,false).extractTerms(sqterm);
			Iterator<Term>it=sqterm.iterator();
			while(it.hasNext())
			{
				System.out.println(it.next().text());
			}
			doc_score=LQgettfidf(sqterm,indexpath);
					
		return doc_score;
		
	}
	public HashMap<String,Double> LQgettfidf(Set<Term>sqterm,String indexpath) throws IOException
	{
		IndexReader r1=DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
		double normlen,term_occ_docs,max_docs=0,sum_val=0;
		int doc,count=0;
		HashMap<String,Double>len=new HashMap<String,Double>();
		
		HashMap<String,Double>array=new HashMap<String,Double>();
		//HashMap<String,Double>DocScore=new HashMap<String,Double>();
		
		List<LeafReaderContext>leafC1=r1.getContext().reader().leaves();
		//for(Term t1:sqterm)
		//{
			
			//System.out.println(t1.text());
			//Calculate IDF
			
			ClassicSimilarity sim=new ClassicSimilarity();
			for(int i=0;i<leafC1.size();i++)
			{
				LeafReaderContext lc1=leafC1.get(i);
				int numofdoc=lc1.reader().maxDoc();
				//int docbase=lc1.docBase;
				//System.out.println(docbase);
				//Calculate Length
				for(int k=0;k<numofdoc;k++)
				{
					normlen=sim.decodeNormValue(lc1.reader().getNormValues("TEXT").get(k));
					Document d=lc1.reader().document(k);
					String docno1=d.get("DOCNO");
					double lenofdoc=(double)(1/(normlen*normlen));
					len.put(docno1, lenofdoc);
				}
				
				for(Term t1:sqterm)
				{
					double df1=r1.docFreq(t1);
					max_docs=r1.maxDoc();
					double idf=Math.log(1+(double)(max_docs/df1));
					
				PostingsEnum e=MultiFields.getTermDocsEnum(lc1.reader(),"TEXT",t1.bytes());	
				if(e!=null)
				{
				while((doc=e.nextDoc())!=PostingsEnum.NO_MORE_DOCS)
				{
					term_occ_docs=e.freq();
					int docid=e.docID();
					
					Document d=lc1.reader().document(docid);
					String docno=d.get("DOCNO");
					
					double lendoc=len.get(docno);
					double tf=(double)(term_occ_docs/lendoc);
					double score=tf*idf;
					
					
					if(array.containsKey(docno))
					{
						array.put(docno, array.get(docno)+score);
						
					}
					else
					{
						array.put(docno,score);
					}
			
				}
				}
				
			//}
		
		
		}
			
		
	}
	
		
		return array;
		
		}


}
