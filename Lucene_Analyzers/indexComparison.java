import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

public class indexComparison {
	
	void parseDoc(File file,IndexWriter writer)
	{
		
		
		ArrayList<String>Document=new ArrayList<String>();
		
		StringBuilder b=new StringBuilder();
		
		try {
			Scanner scan=new Scanner(new FileInputStream(file));
			
			while(scan.hasNextLine())
			{
				String line=scan.nextLine();
				
				Pattern p=Pattern.compile("<DOC>");
				Matcher m=p.matcher(line);
				Pattern p1=Pattern.compile("</DOC>");
				Matcher m1=p1.matcher(line);
				
				if(m.matches())
				{
					
				
					while(m1.matches()==false)
					{
						if(scan.hasNextLine())
						{
						b.append(line);
						line=scan.nextLine();
						m1=p1.matcher(line);
						}
						else
						{
							break;
						}
						
							}
					
					b.append(line);
					
				}
				Document.add(b.toString());
				
				b.setLength(0);
			}
			
			getText(Document,writer);
			scan.close();
			
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
			
		
		
		
	}

	void getText(ArrayList<String>Document,IndexWriter writer)
	{
		//System.out.println(Document);
		String sdocid=null;
		String shead=null;
		String sbyline=null;
		String sdateline=null;
		String stext=null;
		
		
		
		
		Pattern docno=Pattern.compile("<DOCNO>(.*?)</DOCNO>");
		Pattern head=Pattern.compile("<HEAD>(.*?)</HEAD>");
		Pattern byline=Pattern.compile("<BYLINE>(.*?)</BYLINE>");
		Pattern dateline=Pattern.compile("<DATELINE>(.*?)</DATELINE>");
		Pattern text=Pattern.compile("<TEXT>(.*?)</TEXT>");
		
		ArrayList<HashMap<String, String>>Documents=new ArrayList<HashMap<String, String>>();
		
		HashMap<String, String>doc=new HashMap<String,String>();
		
		String s1head="";
		String s1byline="";
		String s1dateline="";
		String s1text="";
				
		//Extracting DocNo
		for(int i=0; i<Document.size();i++)
		{
			
			doc=new HashMap<String,String>();
			Matcher mdocno=docno.matcher(Document.get(i));
			
			
			if(mdocno.find())
			{
				//int k=mdocno.groupCount();
				sdocid=mdocno.group(1);
				
				
			}
			
			Matcher mhead=head.matcher(Document.get(i));
			s1head="";
			while(mhead.find())
			{
				
				shead=mhead.group(1);
				
				s1head=s1head+" "+shead;
				
				
			}
			
			
			//Extracting Byline
			Matcher mbyline=byline.matcher(Document.get(i));
			s1byline="";
			while(mbyline.find())
			{
				sbyline=mbyline.group(1);
				s1byline=s1byline+" "+sbyline;
				
			} 
			
			
			
			//Extracting Dateline
			Matcher mdateline=dateline.matcher(Document.get(i));
			s1dateline="";
			while(mdateline.find())
			{
				sdateline=mdateline.group(1);
				s1dateline=s1dateline+" "+sdateline;
			}
			
			
			//Extracting Text
			Matcher mtext=text.matcher(Document.get(i));
			s1text="";
			while(mtext.find())
			{
				stext=mtext.group(1);
				s1text=s1text+" "+stext;
			}
			
			
			doc.put("DOCNO", sdocid);
			doc.put("HEAD", s1head);
			doc.put("BYLINE", s1byline);
			doc.put("DATELINE", s1dateline);
			doc.put("TEXT", stext);
			
			Documents.add(doc);
			
			
		}
		
		try {
			indexDocuments(Documents,writer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}

public void indexDocuments(ArrayList<HashMap<String,String>>Documents,IndexWriter writer) throws IOException
	{
		for(HashMap<String, String>Doc:Documents)
		{
			indexing(writer,Doc);
		}
		
		
	}
	
public void indexing(IndexWriter writer,HashMap<String, String>Doc)
	{
		Document ldoc=new Document();
		ldoc.add(new StringField("DOCNO",Doc.get("DOCNO"), Field.Store.YES));
		ldoc.add(new TextField("HEAD", Doc.get("HEAD"), Field.Store.YES));
		ldoc.add(new TextField("BYLINE", Doc.get("BYLINE"), Field.Store.YES));
		ldoc.add(new TextField("DATELINE", Doc.get("DATELINE"), Field.Store.YES));
		ldoc.add(new TextField("TEXT", Doc.get("TEXT"), Field.Store.YES));
		try {
			
			writer.addDocument(ldoc);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

public void getdocinfo(String indexPath)
{
	try 
	{
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get( (indexPath))));
		File f =new File("Ananlyzer_Vocabulary");
		
		
		//Print the total number of documents in the corpus
		System.out.println("Total number of documents in the corpus: "+reader.maxDoc());                            

	 //Print the number of documents containing the term "new" in <field>TEXT</field>.

		System.out.println("Number of documents containing the term \"new\" for field \"TEXT\": "+reader.docFreq(new Term("TEXT","new")));

    //Print the total number of occurrences of the term "new" across all documents for <field>TEXT</field>.

		System.out.println("Number of occurrences of \"new\" in the field \"TEXT\": "+reader.totalTermFreq(new Term("TEXT","new")));                                                       

		                                                               

		   Terms vocabulary = MultiFields.getTerms(reader, "TEXT");

  //Print the size of the vocabulary for <field>TEXT</field>, applicable when the index has only one segment.

	System.out.println("Size of the vocabulary for this field: "+vocabulary.size());

//Print the total number of documents that have at least one term for <field>TEXT</field>

 System.out.println("Number of documents that have at least one term for this field: "+vocabulary.getDocCount());

 //Print the total number of tokens for <field>TEXT</field>

 System.out.println("Number of tokens for this field: "+vocabulary.getSumTotalTermFreq());

		                               

		                //Print the total number of postings for <field>TEXT</field>

System.out.println("Number of postings for this field: "+vocabulary.getSumDocFreq());      

		                               

		                //Print the vocabulary for <field>TEXT</field>

		  TermsEnum iterator = vocabulary.iterator();

		       BytesRef byteRef = null;

		       //System.out.println("\n*******Vocabulary-Start**********");
		       
		       FileOutputStream fos=new FileOutputStream(f);
		       PrintWriter pw=new PrintWriter(fos);

		       while((byteRef = iterator.next()) != null) {

		           String term = byteRef.utf8ToString();

		           //System.out.print(term+"\t");
		           pw.write(term+"\t");
		           

		       }

		       //System.out.println("\n*******Vocabulary-End**********");   */     

		       pw.flush(); 
		       fos.close();
		       pw.close();
		                reader.close();

		 
	} 
	catch (IOException e) 
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}



	public static void main(String[] args) 
	{
		String path,indexpath;
		indexpath="IR_1/Standard_Analyzer_Index";
		// TODO Auto-generated method stub
		path="corpus/";
		Directory dir;
		try 
		{
			dir = FSDirectory.open(Paths.get(indexpath));
			Analyzer analyzer=new StandardAnalyzer();
			IndexWriterConfig iwc=new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(dir, iwc);
			File folder=new File(path);
			File [] listoffiles=folder.listFiles();
			
			indexComparison in=new indexComparison();
			System.out.println("---Start Parsing & Indexing Files---");
			for(int i=1;i<listoffiles.length;i++)
			{
				//File file =new File(path);
				File file=listoffiles[i];
				System.out.println(file.getName());
				//IndexWriter writer = null;
				in.parseDoc(file, writer);
				//break;
			}
			
			writer.forceMerge(1);
			writer.commit();
			writer.close();
			System.out.println("----Finished Parsing & Indexing Files----");
			//System.out.println("-------Analysis Information-------");
			in.getdocinfo(indexpath);

			
			
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	


	}




