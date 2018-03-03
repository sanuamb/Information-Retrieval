package com.search;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;


public class IReader 
{
	String indexpath="index";
	int maxdocs=0;
	
	public int getMaxDocs()
	{
		try 
		{
			IndexReader reader=DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
			maxdocs=reader.maxDoc();
		} 
		catch (IOException e) 
		{
			
			e.printStackTrace();
		}
		
		return maxdocs;
	}
	
	public Set<Term>getQueryTerms(Set<Term>qterm,Query p) throws IOException
	{
		//HashSet<Term>temp=new HashSet<Term>();
		IndexReader r= DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
		IndexSearcher s=new IndexSearcher(r);
		
		s.createNormalizedWeight(p,false).extractTerms(qterm);
		return qterm;
		
	}
	
}
