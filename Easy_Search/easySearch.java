package com.search;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.lucene.codecs.TermVectorsFormat;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queries.function.valuesource.TermFreqValueSource;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.*;

public class easySearch 
{
	public HashMap<String,Double>caltfidf(Set<Term>qterm) throws Exception
{
		String indexpath="index";
		IndexReader r1=DirectoryReader.open(FSDirectory.open(Paths.get( (indexpath))));
		double normlen,term_occ_docs,max_docs=0;
		int doc;
		
		HashMap<Integer,Double>len=new HashMap<Integer,Double>();
		HashMap<String,Double>array=new HashMap<String,Double>();
		
		
		List<LeafReaderContext>leafC1=r1.getContext().reader().leaves();
		ClassicSimilarity sim=new ClassicSimilarity();
			for(int i=0;i<leafC1.size();i++)
			{
				LeafReaderContext lc1=leafC1.get(i);
				int numofdoc=lc1.reader().maxDoc();
				int docbase=lc1.docBase;
				
				//Calculate Length
				for(int k=0;k<numofdoc;k++)
				{
					normlen=sim.decodeNormValue(lc1.reader().getNormValues("TEXT").get(k));
					Document d=lc1.reader().document(k);
					int h=k+docbase;
					double lenofdoc=(double)(1/(normlen*normlen));
					len.put(h, lenofdoc);
				}
				
			for(Term t1:qterm)
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
}
