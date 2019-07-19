package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * AIMIR System에서 Locale 적용을 위한 국가 코드 정보를 나타내는 모델 클래스
 * @author 최은정(ej8486)
 * @version 1.0
 */
@Entity
@Table(name="COUNTRY")
// @Cache(type=CacheType.SOFT)
public class Country extends BaseObject{

	private static final long serialVersionUID = -4348862749074118175L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="COUNTRY_SEQ")
	@SequenceGenerator(name="COUNTRY_SEQ", sequenceName="COUNTRY_SEQ", allocationSize=1) 
    private Integer id;
    
    @Column(nullable=false)
    private String name;                // 국가이름
    @Column(name="code_2", unique=true, nullable=false)              
    private String code_2letter;        // 2글자 코드
    @Column(name="code_3")              
    private String code_3letter;        // 3글자 코드
    private String code_number;         // 국가 숫자 코드
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCode_2letter() {
        return code_2letter;
    }
    public void setCode_2letter(String code_2letter) {
        this.code_2letter = code_2letter;
    }
    public String getCode_3letter() {
        return code_3letter;
    }
    public void setCode_3letter(String code_3letter) {
        this.code_3letter = code_3letter;
    }
    public String getCode_number() {
        return code_number;
    }
    public void setCode_number(String code_number) {
        this.code_number = code_number;
    }
    
    @Override
    public String toString()
    {
        return "Country " + toJSONString();
    }
    
    public String toJSONString() {

        String str = "";
        
        str = "{"
            + "id:'" + this.id
            + "', name:'" + this.name
            + "', code_2letter:'" + ((this.code_2letter == null)? "null":this.code_2letter)
            + "'}";
        
        return str;
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
