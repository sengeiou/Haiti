package com.aimir.model.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;

import net.sf.json.JSONString;

/**
 * 공급사 정보
 * 하나의 사이트에서 여러 공급사 관리를 할 수 있으므로 예전처럼 지역으로 구분하는 것은 한계가 있음.  
 * 
 * 시스템을 구축하는 대상이 무엇인가에 따라서 빌딩/공장이 될 수 있다. 
 * 지금까지는 공급사 중심으로 시스템을 구축했지만 빌딩/공장을 중심으로 구축할 수 있다. 
 * 용어의 혼동이 있을 수 있지만 개념을 잘 파악한다면 상관없다. 
 * 신재생 에너지 개념이 포함되면 좀 더 포괄적인 의미로 사용할 수 있다. 
 *  
 * 에너지 공급자 
 * - 전기 
 * - 수도 
 * - 가스 
 * - 기타 
 *  
 * 처음엔 공급자로써 설계가 됐으나 소비 주체가 될 수 있음. 가령 BEMS 구축을 위해서 공급자는 없는데 빌딩 자체를 공급자로 보고 메시지를 빌딩에 맞도록 수정해야 한다. 
 * 
 *
 */
@Entity
// @Cache(type=CacheType.SOFT)
public class Supplier extends BaseObject implements JSONString, IAuditable {

	private static final long serialVersionUID = 4835340675412939368L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SUPPLIER_SEQ")
    @SequenceGenerator(name = "SUPPLIER_SEQ", sequenceName = "SUPPLIER_SEQ", allocationSize = 1)
    @ColumnInfo(name = "PK", descr = "PK")
    private Integer id;

    @Column(unique = true, nullable = false, length = 100)
    @ColumnInfo(name = "공급사명")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    @ColumnInfo(name = "국가코드", descr = "")
    @ReferencedBy(name = "name")
    private Country country;

    @Column(name = "country_id", nullable = true, updatable = false, insertable = false)
    private Integer countryId;

    @Column(name = "sys_date_pattern", nullable = true, updatable = true, insertable = true)
    private String sysDatePattern;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lang_id")
    @ReferencedBy(name = "name")
    private Language lang;

    @Column(name = "lang_id", nullable = true, updatable = false, insertable = false)
    private Integer langId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timezone_id")
    @ColumnInfo(name = "표준시간대코드", descr = "")
    @ReferencedBy(name = "name")
    private TimeZone timezone;

    @Column(name = "timezone_id", nullable = true, updatable = false, insertable = false)
    private Integer timezoneId;

    @Column(length = 150)
    @ColumnInfo(name = "공급사 주소")
    private String address;

    /**
     * Metering Data Decimal Pattern and ('f' truncate(절삭) or 'r' round(반올림),
     * 'c' ceil(무조건 올림)) 검침 사용량 포맷에 적용(kW, kWh, W,Wh, gcal, m3, mcal 등) 온도 압력
     * 퍼센트 제외
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "pattern", column = @Column(name = "md_Pattern")),
            @AttributeOverride(name = "round", column = @Column(name = "md_Round")),
            @AttributeOverride(name = "groupingSeperator", column = @Column(name = "md_GroupingSeperator")),
            @AttributeOverride(name = "decimalSeperator", column = @Column(name = "md_DecimalSeperator")) })
    private DecimalPattern md;

    /**
     * Currency Decimal Pattern and ('f' truncate(절삭) or 'r' round(반올림), 'c'
     * ceil(무조건 올림)) 통화(사용요금 등) 포맷에 적용
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "pattern", column = @Column(name = "cd_Pattern")),
            @AttributeOverride(name = "round", column = @Column(name = "cd_Round")),
            @AttributeOverride(name = "groupingSeperator", column = @Column(name = "cd_GroupingSeperator")),
            @AttributeOverride(name = "decimalSeperator", column = @Column(name = "cd_DecimalSeperator")) })
    private DecimalPattern cd;

    @Column(length = 400)
    @ColumnInfo(name = "계약")
    private String descr;

    @Column(length = 20)
    @ColumnInfo(name = "전화번호")
    private String telno;

    @Column(length = 100)
    @ColumnInfo(name = "관리자")
    private String administrator;

    @ColumnInfo(name = "빌딩면적")
    private Double area;

    @Column(length = 100)
    @ColumnInfo(name = "속성")
    private String attribute;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    @ColumnInfo(name = "서비스 타입")
    private Set<SupplyType> supplyTypes = new HashSet<SupplyType>(0); // 공급사가  다루는 서비스타입

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    @ColumnInfo(name = "관리구역")
    private Set<Location> locations = new HashSet<Location>(0); // 공급사가 관리하는 지역

    @Column(length = 200)
    @ColumnInfo(name = "이미지", descr = "공급사 로고 이미지")
    private String image;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    @ColumnInfo(name = "에너지 절감 목표")
    private Set<EnergySavingGoal> energySavingGoal = new HashSet<EnergySavingGoal>(
            0);

    @Column(name = "TAX_RATE", columnDefinition = "float default 0")
    @ColumnInfo(name = "세율", descr = "가나 ECG Vendor 세율")
    private Float taxRate;

    @Column(name = "COMMISSION_RATE", columnDefinition = "float default 0")
    @ColumnInfo(name = "commission 비율", descr = "가나 ECG Vendor commission 비율")
    private Float commissionRate;
    
    @Column(name="LICENCE_USE", nullable=true, updatable=true, insertable=true)
	@ColumnInfo(name="디바이스 등록  라이센스 사용여부", descr="디바이스 등록  라이센스 사용여부")
	private Integer licenceUse;
	
	@Column(name="LICENCE_METER_COUNT", nullable=true, updatable=true, insertable=true)
	@ColumnInfo(name="미터 등록 라이센스 제한 개수", descr="미터 등록 라이센스 제한 개수")
	private Integer licenceMeterCount;

    public String getSysDatePattern() {
        return sysDatePattern;
    }

    public void setSysDatePattern(String sysDatePattern) {
        this.sysDatePattern = sysDatePattern;
    }

    // 2010.02.05 추가:김민수
    public Supplier() {
    }

    public Supplier(int id) {
        this.id = id;
    }

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

    @XmlTransient
    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @XmlTransient
    public Language getLang() {
        return lang;
    }

    public void setLang(Language lang) {
        this.lang = lang;
    }

    @XmlTransient
    public TimeZone getTimezone() {
        return timezone;
    }

    public void setTimezone(TimeZone timezone) {
        this.timezone = timezone;
    }

    public String getAdministrator() {
        return administrator;
    }

    public void setAdministrator(String administrator) {
        this.administrator = administrator;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public DecimalPattern getMd() {
        return md;
    }

    public void setMd(DecimalPattern md) {
        this.md = md;
    }

    public DecimalPattern getCd() {
        return cd;
    }

    public void setCd(DecimalPattern cd) {
        this.cd = cd;
    }

    @XmlTransient
    public Set<SupplyType> getSupplyTypes() {
        return supplyTypes;
    }

    public void setSupplyTypes(Set<SupplyType> supplyTypes) {
        this.supplyTypes = supplyTypes;
    }

    public void addSupplyType(SupplyType supplyType) {
        if (supplyType == null)
            throw new IllegalArgumentException("Null SupplyType");

        supplyTypes.add(supplyType);
        // supplyType.setSupplier(this);
    }

    @XmlTransient
    public Set<Location> getLocations() {
        return locations;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }

    public void addLocation(Location location) {
        if (location == null)
            throw new IllegalArgumentException("Null Location");

        // location.setSupplier(this);
        locations.add(location);
    }

    /*
    @Transient
    public List<Location> getParentLocations() {
        List<Location> parents = new ArrayList<Location>(0);

        for (Location location : getLocations()) {
            if (location.getParent() == null)
                parents.add(location);
        }
        return parents;
    }
    */

    /*
    public Set<Contract> getContracts() {
        return contracts;
    }
    public void setContracts(Set<Contract> contracts) {
        this.contracts = contracts;
    }
    */
    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getTelno() {
        return telno;
    }

    public void setTelno(String telno) {
        this.telno = telno;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @XmlTransient
    public Set<EnergySavingGoal> getEnergySavingGoal() {
        return energySavingGoal;
    }

    public void setEnergySavingGoal(Set<EnergySavingGoal> energySavingGoal) {
        this.energySavingGoal = energySavingGoal;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Integer getLangId() {
        return langId;
    }

    public void setLangId(Integer langId) {
        this.langId = langId;
    }

    public Integer getTimezoneId() {
        return timezoneId;
    }

    public void setTimezoneId(Integer timezoneId) {
        this.timezoneId = timezoneId;
    }

    public Float getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Float taxRate) {
        this.taxRate = taxRate;
    }

    public Float getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(Float commissionRate) {
        this.commissionRate = commissionRate;
    }

    public void setCdPattern(String pattern) {
        if(this.cd != null) {
            cd.setPattern(pattern);
        } else {
            cd = new DecimalPattern();
            cd.setPattern(pattern);
        }
    }

    public void setCdRound(String round) {
        if(this.cd != null) {
            cd.setRound(round);
        } else {
            cd = new DecimalPattern();
            cd.setRound(round);
        }
    }

    public void setCdGroupingSeperator(String groupingSeperator) {
        if(this.cd != null) {
            cd.setGroupingSeperator(groupingSeperator);
        } else {
            cd = new DecimalPattern();
            cd.setGroupingSeperator(groupingSeperator);
        }
    }

    public void setCdDecimalSeperator(String decimalSeperator) {
        if(this.cd != null) {
            cd.setDecimalSeperator(decimalSeperator);
        } else {
            cd = new DecimalPattern();
            cd.setDecimalSeperator(decimalSeperator);
        }
    }

    public void setMdPattern(String pattern) {
        if(this.md != null) {
            md.setPattern(pattern);
        } else {
            md = new DecimalPattern();
            md.setPattern(pattern);
        }
    }

    public void setMdRound(String round) {
        if(this.md != null) {
            md.setRound(round);
        } else {
            md = new DecimalPattern();
            md.setRound(round);
        }
    }

    public void setMdGroupingSeperator(String groupingSeperator) {
        if(this.md != null) {
            md.setGroupingSeperator(groupingSeperator);
        } else {
            md = new DecimalPattern();
            md.setGroupingSeperator(groupingSeperator);
        }
    }

    public void setMdDecimalSeperator(String decimalSeperator) {
        if(this.md != null) {
            md.setDecimalSeperator(decimalSeperator);
        } else {
            md = new DecimalPattern();
            md.setDecimalSeperator(decimalSeperator);
        }
    }

    @Override
    public String toString() {
        return "Supplier " + toJSONString();
    }
    
    public Integer getLicenceUse() {
		return licenceUse;
	}
	public void setLicenceUse(Integer licenceUse) {
		this.licenceUse = licenceUse;
	}
	
	public Integer getLicenceMeterCount() {
		return licenceMeterCount;
	}
	public void setLicenceMeterCount(Integer licenceMeterCount) {
		this.licenceMeterCount = licenceMeterCount;
	}

    public String toJSONString() {

        String str = "";	
        
        str = "{"
            + "id:'" + this.id
            + "', name:'" + this.name
            + "', country:'" + ((this.country == null)? "":this.country.getName())
            + "', language:'" + ((this.lang == null)? "":this.lang.getName())
 //           + "', timezone:'" + ((this.timezone == null)? "":this.timezone.getName())
            + "', address:'" + ((this.address == null)? "":this.address)
            + "', administrator:'" + ((this.administrator == null)? "":this.administrator)
            + "', area:'" + ((this.area == null)? "":this.area)
            + "', mdPattern:'" + ((this.md == null)? "":md.getPattern())
            + "', cdPattern:'" + ((this.cd == null)? "":cd.getPattern())
            + "', mdRound:'" + ((this.md == null)? "":md.getRound())
            + "', cdRound:'" + ((this.cd == null)? "":cd.getRound())
            + "', mdGroupingSeperator:'" + ((this.md == null)? "":md.getGroupingSeperator())
            + "', cdGroupingSeperator:'" + ((this.cd == null)? "":cd.getGroupingSeperator())
            + "', mdDecimalSeperator:'" + ((this.md == null)? "":md.getDecimalSeperator())
            + "', cdDecimalSeperator:'" + ((this.cd == null)? "":cd.getDecimalSeperator())
            + "', descr:'" + ((this.descr == null)? "":this.descr)
            + "', telno:'" + ((this.telno == null)? "":this.telno)
            + "', sysDatePattern:'" + ((this.sysDatePattern == null)? "":this.sysDatePattern)
            + "', attribute:'" + ((this.attribute == null)? "":this.attribute)
            + "', timezone:'" + ((this.timezone == null)? "":this.timezone.getName())
            + "', licenceUse:'" + ((this.licenceUse == null)? "":this.licenceUse)
            + "', licenceMeterCount:'" + ((this.licenceMeterCount == null)? "":this.licenceMeterCount)
            + "'}";
        
        return str;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Supplier other = (Supplier) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    @Override
    public String getInstanceName() {
        return this.getName();
    }
}
