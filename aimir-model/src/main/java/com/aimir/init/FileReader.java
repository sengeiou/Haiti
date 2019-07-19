package com.aimir.init;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

/**
 * Read File Class
 * @author YeonKyoung Park(goodjob)
 *
 */
public class FileReader {

    private static Log log = LogFactory.getLog(FileReader.class);
	// protected String dataDir = "src/initdata/initrun";
	protected Set<FileContents> filecontents = new TreeSet<FileContents>();
	protected List<ITableIterator> tables = new ArrayList<ITableIterator>();
	
	public FileReader(String dataDir){
	    File fr=new File(dataDir);
	    File[] filelist = null;
	    
	    if (fr.isDirectory())
	        filelist = fr.listFiles();
	    else
	        filelist = new File[]{fr};
	        
	    try{
		    for (int i = 0; i < filelist.length; i++) {
		        if(
		           (filelist[i].getName().toLowerCase().indexOf(".xml") > 0 ||
		           filelist[i].getName().toLowerCase().indexOf(".xls") > 0) ){
		        	//&& filelist[i].getName().indexOf("_"+lang) > 0
		        	
		        	int idx = 0;
		        	String[] temp = filelist[i].getName().split("-");
		        	if(temp != null && temp.length > 0){
		        		try {
		        			idx = Integer.parseInt(temp[0]);		        		
		        		}catch(Exception e){}
		        	}
		        	filecontents.add(new FileContents(idx,filelist[i]));
		        }
		    }	
		    
		    if(filecontents != null && filecontents.size() > 0){
			    Iterator<FileContents> it = filecontents.iterator();
			    while(it.hasNext()){
			    	FileContents fc = (FileContents)it.next();
			    	parse(fc.fileContent);
			    }
		    }else{
		    	log.error("Empty Files !! ");
		    }
	    }catch(Exception e){
	    	log.error(e,e);
	    }
	
	}
	
	public List<ITableIterator> getTables(){
		return this.tables;
	}
	
	public void parse(File file) throws DataSetException, IOException {
		if(file.getName().toLowerCase().indexOf(".xml") > 0){
			parseXML(file);
		}
		if(file.getName().toLowerCase().indexOf(".xls") > 0){
			parseXLS(file);
		}
	}
	
	public void parseXLS(File file) throws DataSetException, IOException{
		XlsDataSet builder = new XlsDataSet(file);
		this.tables.add(builder.iterator());
	}
	
	public void parseXML(File file) throws DataSetException, MalformedURLException{
    	URI uri = file.toURI();
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        IDataSet dataSet = builder.build(uri.toURL());

        if(dataSet != null){
        	this.tables.add(dataSet.iterator());
        }
	}
	
	class FileContents implements Comparable<Object> {
		Integer number = new Integer(-1);
		File fileContent;

		public FileContents(int number, File fileContent) {
		   this.number = number;
		   this.fileContent = fileContent;
		}

		public String toString() {
		     return number + "(" + number + ") " + fileContent.getName(); 
		}
		
		public int compareTo(Object o) { 
		     return number.compareTo(((FileContents)o).number); 
		}
	}

}
