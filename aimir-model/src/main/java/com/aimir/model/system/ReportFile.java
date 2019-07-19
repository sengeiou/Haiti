package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * 
 * @author 박종성(elevas)
 *
 */
@Entity
@Table(name = "REPORT_FILE")
public class ReportFile extends BaseObject implements JSONString{

    private static final long serialVersionUID = -4455650930271509994L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REPORT_FILE_SEQ")
    @SequenceGenerator(name="REPORT_FILE_SEQ", sequenceName="REPORT_FILE_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;

	@Column(name="filename")
	@ColumnInfo(name="첨부파일명")
    private String filename;
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
	public String toString()
	{
	    return "ReportFile "+toJSONString();
	}
	
	public String toJSONString() {
	    
	    String retValue = "";
		
	    retValue = "{"
	        + "id:'" + this.id
	        + ", filename:'" + this.filename
	        + "'}";
	    
	    return retValue;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}
}
