package com.aimir.model.user;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;
/**
 * FOR Test
 * @author 허윤(unimath)
 *
 */
@SuppressWarnings("unused")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE) 
@Table(name="APP_USER")
public class User extends BaseObject implements IAuditable {
	private static final long serialVersionUID = 3257568390917667126L;
	
	@Id 
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="APP_USER_SEQ")
	@SequenceGenerator(name="APP_USER_SEQ", sequenceName="APP_USER_SEQ", allocationSize=1)
	@Column(name="id")
	private Long id;
	
	@Column(name="passwd")
	private String passwd;
	
	@Column(name="firstName", nullable=false)
	private String firstName;
	
	@Column(name="lastName", nullable=false)
	private String lastName;
	
	@Column(name="birthday")
	private Date birthday;
	
	@Column(name="birthday2")
	private Date birthday2;

	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public Date getBirthday() {
		return birthday;
	}
	
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	public Date getBirthday2() {
		return birthday2;
	}
	
	public void setBirthday2(Date birthday2) {
		this.birthday2 = birthday2;
	}
	
	/**
	 * @return Returns firstName and lastName
	 */
	public String getFullName() {
		return firstName + ' ' + lastName;
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

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getInstanceName() {
        return this.getFullName();
    }
}