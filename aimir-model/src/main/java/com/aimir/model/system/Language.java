package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.aimir.model.BaseObject;

/**
 * @fileName Language.java
 * Locale 적용을 위한 언어코드
 * @author 최은정(ej8486)
 * @version 1.0
 */
@Entity
public class Language extends BaseObject{
    private static final long serialVersionUID = -4348862749074118175L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="LANGUAGE_SEQ")
    @SequenceGenerator(name="LANGUAGE_SEQ", sequenceName="LANGUAGE_SEQ", allocationSize=1)
    private Integer id;
    
    @Column(unique=true, nullable=false)
    private String name;                // 언어 풀네임
    @Column(name="code_2")              
    private String code_2letter;        // 2글자 코드
    @Column(name="code_3")              
    private String code_3letter;        // 3글자 코드
    
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
    
    @Override
    public String toString()
    {
        return "Language " + toJSONString();
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