package com.aimir.dao.mvm.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.DataGaps;
import com.aimir.model.mvm.LpEM;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.SearchCondition;
import com.aimir.util.StringUtil;

@Repository(value="lpemDao")
public class LpEMDaoImpl extends AbstractHibernateGenericDao<LpEM, Integer> implements LpEMDao{
    private static Log log = LogFactory.getLog(LpEMDaoImpl.class);
    private static String EnergyMeter = "1.3.1.1";

    @Autowired
    protected LpEMDaoImpl(SessionFactory sessionFactory) {
        super(LpEM.class);
        super.setSessionFactory(sessionFactory);
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<LpEM> getLpEMsByListCondition(Set<Condition> set) {

        return findByConditions(set);
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getLpEMsCountByListCondition(Set<Condition> set) {

        return findTotalCountByConditions(set);
    }


    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getLpEMsMaxMinSumAvg(Set<Condition> conditions, String div) {
        Criteria criteria = getSession().createCriteria(LpEM.class);

         if(conditions != null) {
                Iterator it = conditions.iterator();
                while(it.hasNext()){
                    Condition condition = (Condition)it.next();
                    Criterion addCriterion = SearchCondition.getCriterion(condition);

                    if(addCriterion != null){
                        criteria.add(addCriterion);
                    }
                }
            }

            ProjectionList pjl = Projections.projectionList();
            ProjectionList pj2 = Projections.projectionList();
            ProjectionList pj3 = Projections.projectionList();
            ProjectionList pj4 = Projections.projectionList();

            if("max".equals(div)) {
                pjl.add(Projections.max("value_00"));
            }
            else if("min".equals(div)) {
                pjl.add(Projections.min("value_00"));
            }
            else if("avg".equals(div)) {
                pjl.add(Projections.avg("value_00"));
            }
            else if("sum".equals(div)) {
                pjl.add(Projections.sum("value_00"));
            }

            criteria.setProjection(pjl);    

            
            return criteria.list();
    }

    public void updateLpEM(LpEM lpem){
        StringBuffer hqlBuf = new StringBuffer();
        hqlBuf.append("UPDATE LpEM ");
        hqlBuf.append("SET value_00 = ? ");
        hqlBuf.append("   ,value_01 = ? ");
        hqlBuf.append("   ,value_02 = ? ");
        hqlBuf.append("   ,value_03 = ? ");
        hqlBuf.append("   ,value_04 = ? ");
        hqlBuf.append("   ,value_05 = ? ");
        hqlBuf.append("   ,value_06 = ? ");
        hqlBuf.append("   ,value_07 = ? ");
        hqlBuf.append("   ,value_08 = ? ");
        hqlBuf.append("   ,value_09 = ? ");
        hqlBuf.append("   ,value_10 = ? ");
        hqlBuf.append("   ,value_11 = ? ");
        hqlBuf.append("   ,value_12 = ? ");
        hqlBuf.append("   ,value_13 = ? ");
        hqlBuf.append("   ,value_14 = ? ");
        hqlBuf.append("   ,value_15 = ? ");
        hqlBuf.append("   ,value_16 = ? ");
        hqlBuf.append("   ,value_17 = ? ");
        hqlBuf.append("   ,value_18 = ? ");
        hqlBuf.append("   ,value_19 = ? ");
        hqlBuf.append("   ,value_20 = ? ");
        hqlBuf.append("   ,value_21 = ? ");
        hqlBuf.append("   ,value_22 = ? ");
        hqlBuf.append("   ,value_23 = ? ");
        hqlBuf.append("   ,value_24 = ? ");
        hqlBuf.append("   ,value_25 = ? ");
        hqlBuf.append("   ,value_26 = ? ");
        hqlBuf.append("   ,value_27 = ? ");
        hqlBuf.append("   ,value_28 = ? ");
        hqlBuf.append("   ,value_29 = ? ");
        hqlBuf.append("   ,value_30 = ? ");
        hqlBuf.append("   ,value_31 = ? ");
        hqlBuf.append("   ,value_32 = ? ");
        hqlBuf.append("   ,value_33 = ? ");
        hqlBuf.append("   ,value_34 = ? ");
        hqlBuf.append("   ,value_35 = ? ");
        hqlBuf.append("   ,value_36 = ? ");
        hqlBuf.append("   ,value_37 = ? ");
        hqlBuf.append("   ,value_38 = ? ");
        hqlBuf.append("   ,value_39 = ? ");
        hqlBuf.append("   ,value_40 = ? ");
        hqlBuf.append("   ,value_41 = ? ");
        hqlBuf.append("   ,value_42 = ? ");
        hqlBuf.append("   ,value_43 = ? ");
        hqlBuf.append("   ,value_44 = ? ");
        hqlBuf.append("   ,value_45 = ? ");
        hqlBuf.append("   ,value_46 = ? ");
        hqlBuf.append("   ,value_47 = ? ");
        hqlBuf.append("   ,value_48 = ? ");
        hqlBuf.append("   ,value_49 = ? ");
        hqlBuf.append("   ,value_50 = ? ");
        hqlBuf.append("   ,value_51 = ? ");
        hqlBuf.append("   ,value_52 = ? ");
        hqlBuf.append("   ,value_53 = ? ");
        hqlBuf.append("   ,value_54 = ? ");
        hqlBuf.append("   ,value_55 = ? ");
        hqlBuf.append("   ,value_56 = ? ");
        hqlBuf.append("   ,value_57 = ? ");
        hqlBuf.append("   ,value_58 = ? ");
        hqlBuf.append("   ,value_59 = ? ");
        hqlBuf.append(" WHERE id.yyyymmddhh = ? ");
        hqlBuf.append(" AND id.channel = ? ");
        hqlBuf.append(" AND id.mdevType = ? ");
        hqlBuf.append(" AND id.mdevId = ? ");
        hqlBuf.append(" AND id.dst = ? ");

        //HQL문을 이용한 CUD를 할 경우에는 getHibernateTemplate().bulkUpdate() 메소드를 사용한다.
        Query query = getSession().createQuery(hqlBuf.toString());
        query.setParameter(1, lpem.getValue_00());
        query.setParameter(2, lpem.getValue_01());
        query.setParameter(3, lpem.getValue_02());
        query.setParameter(4, lpem.getValue_03());
        query.setParameter(5, lpem.getValue_04());
        query.setParameter(6, lpem.getValue_05());
        query.setParameter(7, lpem.getValue_06());
        query.setParameter(8, lpem.getValue_07());
        query.setParameter(9, lpem.getValue_08());
        query.setParameter(10, lpem.getValue_09());
        query.setParameter(11, lpem.getValue_10());
        query.setParameter(12, lpem.getValue_11());
        query.setParameter(13, lpem.getValue_12());
        query.setParameter(14, lpem.getValue_13());
        query.setParameter(15, lpem.getValue_14());
        query.setParameter(16, lpem.getValue_15());
        query.setParameter(17, lpem.getValue_16());
        query.setParameter(18, lpem.getValue_17());
        query.setParameter(19, lpem.getValue_18());
        query.setParameter(20, lpem.getValue_19());
        query.setParameter(21, lpem.getValue_20());
        query.setParameter(22, lpem.getValue_21());
        query.setParameter(23, lpem.getValue_22());
        query.setParameter(24, lpem.getValue_23());
        query.setParameter(25, lpem.getValue_24());
        query.setParameter(26, lpem.getValue_25());
        query.setParameter(27, lpem.getValue_26());
        query.setParameter(28, lpem.getValue_27());
        query.setParameter(29, lpem.getValue_28());
        query.setParameter(30, lpem.getValue_29());
        query.setParameter(31, lpem.getValue_30());
        query.setParameter(32, lpem.getValue_31());
        query.setParameter(33, lpem.getValue_32());
        query.setParameter(34, lpem.getValue_33());
        query.setParameter(35, lpem.getValue_34());
        query.setParameter(36, lpem.getValue_35());
        query.setParameter(37, lpem.getValue_36());
        query.setParameter(38, lpem.getValue_37());
        query.setParameter(39, lpem.getValue_38());
        query.setParameter(40, lpem.getValue_39());
        query.setParameter(41, lpem.getValue_40());
        query.setParameter(42, lpem.getValue_41());
        query.setParameter(43, lpem.getValue_42());
        query.setParameter(44, lpem.getValue_43());
        query.setParameter(45, lpem.getValue_44());
        query.setParameter(46, lpem.getValue_45());
        query.setParameter(47, lpem.getValue_46());
        query.setParameter(48, lpem.getValue_47());
        query.setParameter(49, lpem.getValue_48());
        query.setParameter(50, lpem.getValue_49());
        query.setParameter(51, lpem.getValue_50());
        query.setParameter(52, lpem.getValue_51());
        query.setParameter(53, lpem.getValue_52());
        query.setParameter(54, lpem.getValue_53());
        query.setParameter(55, lpem.getValue_54());
        query.setParameter(56, lpem.getValue_55());
        query.setParameter(57, lpem.getValue_56());
        query.setParameter(58, lpem.getValue_57());
        query.setParameter(59, lpem.getValue_58());
        query.setParameter(60, lpem.getValue_59());
        query.setParameter(61, lpem.getId().getYyyymmddhh());
        query.setParameter(62, lpem.getId().getChannel());
        query.setParameter(63, lpem.getId().getMDevType());
        query.setParameter(64, lpem.getId().getMDevId());
        query.setParameter(65, lpem.getId().getDst());
        query.executeUpdate();
        
        // bulkUpdate 때문에 주석처리
        /*this.getSession().bulkUpdate(hqlBuf.toString(),
            new Object[] { lpem.getValue_00(), lpem.getValue_01(), lpem.getValue_02(), lpem.getValue_03(), lpem.getValue_04(), lpem.getValue_05(), lpem.getValue_06(), lpem.getValue_07(), lpem.getValue_08(), lpem.getValue_09(),
            lpem.getValue_10(), lpem.getValue_11(), lpem.getValue_12(), lpem.getValue_13(), lpem.getValue_14(), lpem.getValue_15(), lpem.getValue_16(), lpem.getValue_17(), lpem.getValue_18(), lpem.getValue_19(),
            lpem.getValue_20(), lpem.getValue_21(), lpem.getValue_22(), lpem.getValue_23(), lpem.getValue_24(), lpem.getValue_25(), lpem.getValue_26(), lpem.getValue_27(), lpem.getValue_28(), lpem.getValue_29(),
            lpem.getValue_30(), lpem.getValue_31(), lpem.getValue_32(), lpem.getValue_33(), lpem.getValue_34(), lpem.getValue_35(), lpem.getValue_36(), lpem.getValue_37(), lpem.getValue_38(), lpem.getValue_39(),
            lpem.getValue_40(), lpem.getValue_41(), lpem.getValue_42(), lpem.getValue_43(), lpem.getValue_44(), lpem.getValue_45(), lpem.getValue_46(), lpem.getValue_47(), lpem.getValue_48(), lpem.getValue_49(),
            lpem.getValue_50(), lpem.getValue_51(), lpem.getValue_52(), lpem.getValue_53(), lpem.getValue_54(), lpem.getValue_55(), lpem.getValue_56(), lpem.getValue_57(), lpem.getValue_58(), lpem.getValue_59(),
            lpem.getId().getYyyymmddhh(), lpem.getId().getChannel(), lpem.getId().getMDevType(), lpem.getId().getMDevId(), lpem.getId().getDst()} );*/

     }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<DataGaps> search( int supplier, String tabType, String startDate, String endDate, int totalCount,
            String meter, String deviceCodeType, String deviceId ) {

        //10보다 작은 수 앞에 0 붙이기
        DecimalFormat df = new DecimalFormat("00");

        //주기 ( lpInterval ) 구해오기
        StringBuffer buffer = new StringBuffer();
        buffer.append(" select m.id, m.lpInterval \n");
        buffer.append(" From LpEM lp LEFT OUTER JOIN lp.id.meter m \n");
        buffer.append(" WHERE lp.id.channel = 1 \n");

        //yyyymmddhh
        if ( tabType.equals("0") )
            buffer.append(" AND lp.id.yyyymmddhh between " + "'" + startDate + "'" + "AND"  + "'" + endDate + "'" + " \n");
        else
            buffer.append(" AND lp.yyyymmdd between " + "'" + startDate + "'" + "AND"  + "'" + endDate + "'" + " \n");

        buffer.append(" GROUP BY m.id, m.lpInterval ");

        Query query = getSession().createQuery(buffer.toString());

        List<?> result = query.list();


        List<Meter> meterList = new ArrayList<Meter>();
        List<DataGaps> dataGaps = new ArrayList<DataGaps>();

        String lpInterval = null;
        String name = null;
        String mdsId = null;
        String lastReadDate = null;
        String meterId = null;
        String value = null;
        int valueTotalCount;

        for(int i = 0; i < result.size() ; i++) {
            valueTotalCount = 0;

            Meter meterData = new Meter();

            // 주기
            int interval = meterData.getLpInterval();

            Object[] resultData = (Object[])result.get(i);

            meterId =resultData[0].toString();
            lpInterval = resultData[1].toString();

            meterData.setLpInterval( Integer.parseInt(lpInterval) );
            meterList.add(meterData);
            StringBuffer sb = new StringBuffer();
            sb.append(" select \n " );
            sb.append(" m.customer.name \n");
            sb.append(",m.mdsId \n");
            sb.append(",m.lastReadDate \n");
            sb.append(",(( count(value)) )as value \n ");

            for ( int l=0; l<60; l=l+interval) {
                sb.append(" ,(( count(value_"+df.format(l)+")) )as value_"+df.format(l)+" \n "); //value_00 ~ value_59
            }

//          //주기 1분
//          if ( meterData.getLpInterval() == 1 ) {
//              for ( int l=0; l<60; l++ ) {
//                  sb.append(" ,(( count(value_"+df.format(l)+")) )as value_"+df.format(l)+" \n "); //value_00 ~ value_59
//              }
//          }
//          //주기 5분
//          if ( meterData.getLpInterval() == 5 ) {
//              for ( int l=5; l<56; l=l+5 ) {
//                  sb.append(" ,(( count(value_"+df.format(l)+")) )as value_"+df.format(l)+" \n "); //value_05 ~ value_55
//              }
//          }
//          //주기 10분
//          if ( meterData.getLpInterval() == 10 ) {
//              for ( int l=10; l<51; l=l+10 ) {
//                  sb.append(" ,(( count(value_"+l+")) )as value_"+l+" \n "); //value_10 ~ value_50
//              }
//          }
//          //주기 15분
//          if ( meterData.getLpInterval() == 15 ) {
//              for ( int l=15; l<46; l=l+15 ) {
//                  sb.append(" ,(( count(value_"+l+")) )as value_"+l+" \n "); //value_15 ~ value_45
//              }
//          }
//          //주기 30분
//          if ( meterData.getLpInterval() == 30 ) {
//              sb.append(" ,(( count(value_30)) )as value_30 \n "); //value_30
//          }

            sb.append(" From LpEM lp LEFT OUTER JOIN lp.id.meter m \n");
            sb.append(" WHERE lp.id.channel = 1 \n");
            //yyyymmddhh
            if ( tabType.equals("0") )
                sb.append(" AND lp.id.yyyymmddhh between " + "'" + startDate + "'" + "AND"  + "'" + endDate + "'" + " \n");
            else
                sb.append(" AND lp.yyyymmdd between " + "'" + startDate + "'" + "AND"  + "'" + endDate + "'" + " \n");

            sb.append(" AND m.id = " + meterId );
            //meterType : 전기
            sb.append(" AND m.meterType = (select c.id from Code c where code = )" + "'" + EnergyMeter + "'" + "   \n ");
            sb.append(" AND m.supplierId = " + supplier);

            //미터아이디 검색
            if (  meter != null && meter.length() > 0 ) {
                sb.append(" AND m.mdsId like " + "'%" + meter + "%'");
            }
            //장비타입 검색
            if ( deviceCodeType != null && deviceCodeType.length() > 0 ) {
                sb.append(" AND lp.deviceTypeCode = ( select c.id from Code c where code = " + "'" + deviceCodeType + "'" + ")"  );
            }
            //장비아이디 검색
            if ( deviceId != null && deviceId.length() > 0) {
                sb.append(" AND lp.deviceId like " + "'%" + deviceId + "%'");
            }
            sb.append(" GROUP BY m.customer.name, m.mdsId , m.lastReadDate ");

            Query query1 = getSession().createQuery(sb.toString());
            List<?> result1 = query1.list();

            for(int j = 0; j < result1.size() ; j++) {
                DataGaps dataGapsData = new DataGaps();

                //쿼리결과가 select 절 순서대로 array 리턴
                Object[] resultData1 = (Object[])result1.get(j);

                //필드인덱스
                int idx=0;

                //기본필드
                name            = resultData1[idx++].toString();
                mdsId           = resultData1[idx++].toString();
                lastReadDate    = resultData1[idx++].toString();

                //2010.04.05 13:00
                if ( lastReadDate != null ) {
                    String year = lastReadDate.substring( 0,4 );
                    String month = lastReadDate.substring( 4,6 );
                    String day = lastReadDate.substring( 6,8 );
                    String hour = lastReadDate.substring( 8,10 );
                    String minute = lastReadDate.substring( 10,12 );
                    lastReadDate =  year + "." + month + "." + day + " " + hour + ":" + minute ;
                }
                valueTotalCount = 0;


                // 60분 내에서 주기별 데이터수, 0으로 떨어지지않을경우 올림
                int intervalCnt = (int)Math.ceil(60 / interval);

                int fromIdx = idx;  // select 결과에서 기본필드 다음 인덱스
                int toIdx = idx + intervalCnt; // 시작인덱스 + 주기별데이터수

                for ( int l=fromIdx; l<=toIdx; l++ ) {
                    value = resultData1[l].toString();
                    valueTotalCount = valueTotalCount + Integer.parseInt(value);
                }
                dataGapsData.setCnt( Integer.toString(totalCount * intervalCnt) );
                dataGapsData.setFailCnt( String.valueOf(( totalCount * intervalCnt ) - valueTotalCount ) );


//              //주기 1분
//              if ( meterData.getLpInterval() == 1 ) {
//                  // 주기 1분 value ~ value59 의 total 개수 value , value_00 ~ value_59
//                  for ( int l=3; l<64; l++ ) {
//                      value = resultData1[l].toString();
//                      valueTotalCount = valueTotalCount + Integer.parseInt(value);
//                  }
//                  dataGapsData.setCnt( Integer.toString(totalCount * 60) );
//                  dataGapsData.setFailCnt( String.valueOf(( totalCount * 60 ) - valueTotalCount ) );
//              }
//
//              //주기 5분
//              if ( meterData.getLpInterval() == 5 ) {
//                  // 주기 5분 value ~ value_55 의 total 개수  value , value_05, 10 ,15 ,20 ,25 ,30 ,35 ,40 ,45 ,50 ,55 : 필드 14개
//                  for ( int l=3; l<16; l++ ) {
//                      value = resultData1[l].toString();
//                      valueTotalCount = valueTotalCount + Integer.parseInt(value);
//                  }
//                  dataGapsData.setCnt( Integer.toString(totalCount * 12) );
//                  dataGapsData.setFailCnt( String.valueOf(( totalCount * 12 ) - valueTotalCount ) );
//              }
//              //주기 10분
//              if ( meterData.getLpInterval() == 10 ) {
//                  // 주기 10분 value ~ value50 의 total 개수 value , value_10, value_20, value_30, value_40 , value_50 : 필드 6개
//                  for ( int l=3; l<9; l++ ) {
//                      value = resultData1[l].toString();
//                      valueTotalCount = valueTotalCount + Integer.parseInt(value);
//                  }
//                  dataGapsData.setCnt( Integer.toString(totalCount * 6) );
//                  dataGapsData.setFailCnt( String.valueOf(( totalCount * 6 ) - valueTotalCount ) );
//              }
//              //주기15분
//              if ( meterData.getLpInterval() == 15 ) {
//                  // 주기 15분 value15 ~ value45 의 total 개수  value, value_15 , value_30, value_45 : 필드 4개
//                  for ( int l=3; l<7; l++ ) {
//                      value = resultData1[l].toString();
//                      valueTotalCount = valueTotalCount + Integer.parseInt(value);
//                  }
//                  dataGapsData.setCnt( Integer.toString(totalCount * 4) );
//                  dataGapsData.setFailCnt( String.valueOf(( totalCount * 4 ) - valueTotalCount ) );
//              }
//              //주기 30분
//              if ( meterData.getLpInterval() == 30 ) {
//                  // 주기 30분   value , value_30 : 필드 2개
//                  for ( int l=3; l<5; l++ ) {
//                      value = resultData1[l].toString();
//                      valueTotalCount = valueTotalCount + Integer.parseInt(value);
//                  }
//                  dataGapsData.setCnt( Integer.toString(totalCount * 2) );
//                  dataGapsData.setFailCnt( String.valueOf(( totalCount * 2 ) - valueTotalCount ) );
//              }
//
//              //주기 60분
//              if ( meterData.getLpInterval() == 60 ) {
//                  // 주기 분 value60 개수   value
//                  value = resultData1[3].toString();
//                  valueTotalCount = valueTotalCount + Integer.parseInt(value);
//
//                  dataGapsData.setCnt(Integer.toString(totalCount));
//                  dataGapsData.setFailCnt( String.valueOf(( totalCount ) - valueTotalCount ) );
//              }

                dataGapsData.setName(name);
                dataGapsData.setMdsId(mdsId);
                dataGapsData.setLastReadDate(lastReadDate);
                dataGapsData.setSuccessCnt( String.valueOf(valueTotalCount) );
                dataGaps.add(dataGapsData);
            }
        }
        return dataGaps;
    }

    /*
     * 한전 BEMS 연동을 위해 추가됨.
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getLpEMs(String mdevType, String mdevId, int dst, String startYYYYMMDDHH, String endYYYYMMDDHH) {
        SQLQuery query = null;

        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT * ")
            .append("FROM LP_EM ")
            .append("WHERE DST = ? ")
            .append("AND MDEV_TYPE = ? ")
            .append("AND MDEV_ID = ? ")
            .append("AND CHANNEL = ? ")
            .append("AND YYYYMMDDHH BETWEEN ? AND ?");

        query = getSession().createSQLQuery(sbQuery.toString());
        query.setInteger(0, dst);
        query.setString(1, mdevType);
        query.setString(2, mdevId);
        query.setInteger(3, 1);
        query.setString(4, startYYYYMMDDHH);
        query.setString(5, endYYYYMMDDHH);
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    /*
     * 온전한 형태의 미전송 데이터를 셀렉트해 옴(이가 빠지거나 누적값이 비정상적인 데이타 제외)
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getLpEMsByNoSended() {
        SQLQuery query = null;

        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT A.CHANNEL, A.YYYYMMDDHH, A.DST, A.MDEV_ID, A.MDEV_TYPE, A.VALUE, A.VALUE_00, A.VALUE_15, A.VALUE_30, A.VALUE_45 ")
            .append(", B.VALUE_00 AS VALUE_00_RESULT , B.VALUE_15 AS VALUE_15_RESULT, B.VALUE_30 AS VALUE_30_RESULT, B.VALUE_45 AS VALUE_45_RESULT ")
            .append("FROM LP_EM A ")
            .append("JOIN (SELECT * FROM LP_EM ")
            .append("WHERE CHANNEL = 98 ")
            //플래그가 미전송이거나, 일정 횟수 이하의 에러인 경우를 셀렉트 해 옴
            .append("AND ((VALUE_00 is null OR VALUE_15 is null OR VALUE_30 is null OR VALUE_45 is null ) or (VALUE_00 = 0 OR VALUE_15 = 0 OR VALUE_30 = 0 OR VALUE_45 = 0 ) or ((VALUE_00 > 2 and VALUE_00<= 4) OR (VALUE_15 > 2 and VALUE_15<= 4)  OR (VALUE_30 > 2 and VALUE_30<= 4)  OR (VALUE_45 > 2 and VALUE_45<= 4)))) B ")
            .append("ON A.YYYYMMDDHH = B.YYYYMMDDHH ")
            //이가 빠진 데이터는 제외 -> 15분 업로드로 변경해 이가빠진 데이터도 전송하도록 수정 -> 이가 빠진 데이터에 대한 처리는 MeterReadingJob.java에서 처리하도록 함
            //.append("AND (a.value_00 is not null or a.value_15 is not null or a.value_30 is not null or a.value_45 is not null)")

            //누적값이 비정상적인 데이터는 제외 -> lp값은 전송하기 위해 MeterReadingJob.java에서 처리하도록 함
            //.append("AND a.value>0")
            .append("AND A.DST = B.DST ")
            .append("AND A.MDEV_TYPE = B.MDEV_TYPE ")
            .append("AND A.MDEV_TYPE = 'Meter' ")
            .append("AND B.MDEV_TYPE = 'Meter' ")
            .append("AND A.MDEV_ID = B.MDEV_ID ")
            .append("AND A.CHANNEL = 1 order by A.YYYYMMDDHH desc ")
            .append("offset 0 rows fetch next 200 rows only");

        query = getSession().createSQLQuery(sbQuery.toString());
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /*
     * 온전한 형태의 미전송 데이터를 셀렉트해 옴(이가 빠지거나 누적값이 비정상적인 데이타 제외)
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getLpEMsByNoSendedACD() {
        SQLQuery query = null;

        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT A.CHANNEL, A.YYYYMMDDHH, A.DST, A.MDEV_ID, A.MDEV_TYPE, A.VALUE, A.VALUE_00, A.VALUE_15, A.VALUE_30, A.VALUE_45 ")
            .append(", B.VALUE_00 AS VALUE_00_RESULT , B.VALUE_15 AS VALUE_15_RESULT, B.VALUE_30 AS VALUE_30_RESULT, B.VALUE_45 AS VALUE_45_RESULT ")
            .append("FROM LP_EM A ")
            .append("JOIN (SELECT * FROM LP_EM ")
            .append("WHERE CHANNEL = 98 ")
            //플래그가 미전송이거나, 일정 횟수 이하의 에러인 경우를 셀렉트 해 옴
            .append("AND ((VALUE_00 is null OR VALUE_15 is null OR VALUE_30 is null OR VALUE_45 is null ) or (VALUE_00 = 0 OR VALUE_15 = 0 OR VALUE_30 = 0 OR VALUE_45 = 0 ) or ((VALUE_00 > 2 and VALUE_00<= 4) OR (VALUE_15 > 2 and VALUE_15<= 4)  OR (VALUE_30 > 2 and VALUE_30<= 4)  OR (VALUE_45 > 2 and VALUE_45<= 4)))) B ")
            .append("ON A.YYYYMMDDHH = B.YYYYMMDDHH ")
            //이가 빠진 데이터는 제외 -> 15분 업로드로 변경해 이가빠진 데이터도 전송하도록 수정 -> 이가 빠진 데이터에 대한 처리는 MeterReadingJob.java에서 처리하도록 함
            //.append("AND (a.value_00 is not null or a.value_15 is not null or a.value_30 is not null or a.value_45 is not null)")

            //누적값이 비정상적인 데이터는 제외 -> lp값은 전송하기 위해 MeterReadingJob.java에서 처리하도록 함
            //.append("AND a.value>0")
            .append("AND A.DST = B.DST ")
            .append("AND A.MDEV_TYPE = B.MDEV_TYPE ")
            .append("AND A.MDEV_TYPE = 'Modem' ")
            .append("AND B.MDEV_TYPE = 'Modem' ")
            .append("AND A.MDEV_ID = B.MDEV_ID ")
            .append("AND A.CHANNEL = 1 order by A.YYYYMMDDHH desc ")
            .append("offset 0 rows fetch next 200 rows only");

        query = getSession().createSQLQuery(sbQuery.toString());
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getLpEMsByNoSendedDummy(String selectDate) {
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT A.CHANNEL, " +
                "substr(CAST(CURRENT_TIMESTAMP AS VARCHAR(30)),1,4)||substr(CAST(CURRENT_TIMESTAMP AS VARCHAR(30)),6,2)||substr(CAST(CURRENT_TIMESTAMP AS VARCHAR(30)),9,2)||substr(CAST(CURRENT_TIMESTAMP AS VARCHAR(30)),12,2) yyyymmddhh, " +
                "A.DST, A.MDEV_ID, A.MDEV_TYPE, A.VALUE, A.VALUE_00, A.VALUE_15, A.VALUE_30, A.VALUE_45 ")
            .append(", B.VALUE_00 AS VALUE_00_RESULT , B.VALUE_15 AS VALUE_15_RESULT, B.VALUE_30 AS VALUE_30_RESULT, B.VALUE_45 AS VALUE_45_RESULT ")
            .append("FROM LP_EM A ")
            .append("JOIN (SELECT * FROM LP_EM ")
            .append("WHERE CHANNEL = 98 ")
            //플래그가 미전송이거나, 일정 횟수 이하의 에러인 경우를 셀렉트 해 옴
            .append(") B ")
            .append("ON A.YYYYMMDDHH = B.YYYYMMDDHH ")
            //이가 빠진 데이터는 제외
            .append("AND (a.value_00 is not null and a.value_15 is not null and a.value_30 is not null and a.value_45 is not null)")
            //누적값이 비정상적인 데이터는 제외
            .append("AND a.value>0")
            .append("AND A.DST = B.DST ")
            .append("AND A.MDEV_TYPE = B.MDEV_TYPE ")
            .append("AND A.MDEV_ID = B.MDEV_ID ")
            //검침 데이터가 많은 날짜
            .append("AND a.yyyymmdd='"+selectDate+"' ")
            //현재 시간의 데이터 전송
            .append("AND a.hh = substr(CAST(CURRENT_TIMESTAMP AS VARCHAR(30)),12,2) ")
            .append("AND A.CHANNEL = 1 order by A.YYYYMMDDHH desc ")
            .append("offset 0 rows fetch next 1000 rows only");
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getLpEMsByNoSended(String mdevType) {
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT A.CHANNEL, A.YYYYMMDDHH, A.DST, A.MDEV_ID, A.MDEV_TYPE, A.VALUE, A.VALUE_00, A.VALUE_15, A.VALUE_30, A.VALUE_45 ")
            .append(", B.VALUE_00 AS VALUE_00_RESULT , B.VALUE_15 AS VALUE_15_RESULT, B.VALUE_30 AS VALUE_30_RESULT, B.VALUE_45 AS VALUE_45_RESULT ")
            .append("FROM LP_EM A ")
            .append("JOIN (SELECT * FROM LP_EM ")
            .append("WHERE CHANNEL = 98 ")
            .append("AND (VALUE_00 = 0 OR VALUE_15 = 0 OR VALUE_30 = 0 OR VALUE_45 = 0 )) B ")
            .append("ON A.YYYYMMDDHH = B.YYYYMMDDHH ")
            .append("AND A.DST = B.DST ")
            .append("AND A.MDEV_TYPE = B.MDEV_TYPE ")
            .append("AND A.MDEV_ID = B.MDEV_ID ")
            .append("AND A.CHANNEL = 1 ")
            .append("AND A.MDEV_TYPE = ?");

        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString(0, mdevType);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getLpEMsByNoSended(String mdevType,String lpTableName) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        //cal.add(Calendar.DATE, -1);

        SimpleDateFormat curSdf = new SimpleDateFormat("yyyyMMdd");
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT A.CHANNEL, A.YYYYMMDDHH, A.DST, A.MDEV_ID, A.MDEV_TYPE, A.VALUE, A.VALUE_00, A.VALUE_15, A.VALUE_30, A.VALUE_45,A.MDEV_ID,A.LOCATION_ID,C.FLOOR,C.BLOCK,C.SUPPLIER_ID ")
            .append(", B.VALUE_00 AS VALUE_00_RESULT , B.VALUE_15 AS VALUE_15_RESULT, B.VALUE_30 AS VALUE_30_RESULT, B.VALUE_45 AS VALUE_45_RESULT ")
            .append("FROM "+lpTableName+" A ")
            .append("JOIN (SELECT * FROM "+lpTableName+" ")
            .append("WHERE CHANNEL = 98 ")
            .append("AND (VALUE_00 = 0 OR VALUE_15 = 0 OR VALUE_30 = 0 OR VALUE_45 = 0 )) B ")
            .append("ON A.YYYYMMDDHH = B.YYYYMMDDHH ")
            .append("JOIN (select l1.supplier_id,l1.id,l1.name AS Floor,l2.name AS block from location l1 join (select * from location) l2 on l1.parent_ID=l2.id) C on A.LOCATION_ID = C.ID ")
            .append("AND A.DST = B.DST ")
            .append("AND A.MDEV_TYPE = B.MDEV_TYPE ")
            .append("AND A.MDEV_ID = B.MDEV_ID ")
            .append("AND A.CHANNEL = 1 ")
            .append("AND A.YYYYMMDD = ? ")
            .append("AND A.MDEV_TYPE = ? ");

        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString(0, curSdf.format(cal.getTime()));
        query.setString(1, mdevType);
//      log.debug("date:"+curSdf.format(cal.getTime()));
//      log.debug("mdevType:"+mdevType);
//      log.debug(sbQuery.toString());
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getTotalCummulValue(String mdevId, String yyyymmdd,int hh, int mm, int interval) {
        StringBuffer dayCummulQuery = new StringBuffer();
        StringBuffer hourCummulQuery = new StringBuffer();
        StringBuffer minCummulQuery = new StringBuffer();
        //lp의 처음 시작 시간이 0시인 경우는 이전 시간까지의 누적값을 계산하지 않아도 된다.
        if(hh!=0) {
            hourCummulQuery.append("(select sum(value_00)+sum(value_15)+sum(value_30)+sum(value_45) " + 
        "from lp_em where channel=1 and mdev_type='Meter' and mdev_id=? and yyyymmddhh between ? and ? group by mdev_id)");
        }

        //lp의 처음 시작 타임이 00분인 경우는 해당 시간 이전 인터벌 까지의  누적값을 계산하지 않아도 된다.
        if(mm!=0) {
            int[] intervals=new int[60/interval];
            String intervalStr="";
            for(int i=0;i<intervals.length;i++) {
                intervals[i]=i*60/intervals.length;
                if(intervals[i]<mm) {
                    intervalStr+=(i!=0 ? "+ ":"")+"value_"+(intervals[i]<10 ? "0"+intervals[i]:intervals[i]) ;
                }
            }
            minCummulQuery.append("(select "+intervalStr+" from lp_em where channel=1 and mdev_id=? and yyyymmddhh =? and mdev_type='Meter')");
        }

        dayCummulQuery.append("select (activeenergyratetot "+(hh!=0 ? " + " + hourCummulQuery:"")+(mm!=0 ? " + "+minCummulQuery:"")+
                ") as totalCummulValue from BILLING_DAY_EM where yyyymmdd=? and mdev_id=? and mdev_type=2");

        log.debug("mdevId["+mdevId+"] yyyymmdd["+yyyymmdd+"] hh["+hh+"] mm["+mm+"]");
        log.debug("Query: "+dayCummulQuery.toString());
        SQLQuery query = getSession().createSQLQuery(dayCummulQuery.toString());
        int conCnt=0;
        if(hh!=0) {
            query.setString(conCnt++, mdevId);
            query.setString(conCnt++, yyyymmdd+"00" );
            query.setString(conCnt++, yyyymmdd+((hh-1)<10 ? "0"+(hh-1):(hh-1)) );
        }
        if(mm!=0) {
            query.setString(conCnt++, mdevId);
            query.setString(conCnt++, yyyymmdd+(hh<10 ? "0"+hh:hh));
        }
        query.setString(conCnt++, yyyymmdd );
        query.setString(conCnt++, mdevId);
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * @param mdevId
     * @param yyyymmdd
     * @param hh - 0이 들어와서는 안된다
     * @param mm
     * @param interval
     * @return
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getHourCummulValueNoSelf(String mdevId, String yyyymmdd,int hh, int mm, int interval) {
        StringBuffer hourCummulQuery = new StringBuffer();
        StringBuffer minCummulQuery = new StringBuffer();

        //lp의 처음 시작 타임이 00분인 경우는 해당 시간 이전 인터벌 까지의  누적값을 계산하지 않아도 된다.
        if(mm!=0) {
            int[] intervals=new int[60/interval];
            String intervalStr="";
            for(int i=0;i<intervals.length;i++) {
                intervals[i]=i*60/intervals.length;
                if(intervals[i]<mm) {
                    intervalStr+=(i!=0 ? "+ ":"")+"value_"+(intervals[i]<10 ? "0"+intervals[i]:intervals[i]) ;
                }
            }
            minCummulQuery.append("(select "+intervalStr+" from lp_em where channel=1 and mdev_id=? and yyyymmddhh =? and mdev_type='Meter')");
        }

        hourCummulQuery.append("select (sum(value_00)+sum(value_15)+sum(value_30)+sum(value_45) "+
        (mm!=0 ? " + "+minCummulQuery:"")+") as hourCummulValue from lp_em where channel=1 and mdev_type='Meter' and mdev_id=? and yyyymmddhh between ? and ? group by mdev_id");

        log.debug("mdevId["+mdevId+"] yyyymmdd["+yyyymmdd+"] hh["+hh+"] mm["+mm+"]");
        log.debug("Query: "+hourCummulQuery.toString());
        SQLQuery query = getSession().createSQLQuery(hourCummulQuery.toString());
        int conCnt=0;
        if(mm!=0) {
            query.setString(conCnt++, mdevId);
            query.setString(conCnt++, yyyymmdd+(hh<10 ? "0"+hh:hh));
        }
        query.setString(conCnt++, mdevId);
        query.setString(conCnt++, yyyymmdd+"00" );
        query.setString(conCnt++, yyyymmdd+((hh-1)<10 ? "0"+(hh-1):(hh-1)) );
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * @param mdevId
     * @param yyyymmdd
     * @param hh
     * @param mm - 0이 들어와서는 안된다.
     * @param interval
     * @return
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMinCummulValueNoSelf(String mdevId, String yyyymmdd,int hh, int mm, int interval) {
        StringBuffer minCummulQuery = new StringBuffer();
        //lp의 처음 시작 타임이 00분인 경우는 해당 시간 이전 인터벌 까지의  누적값을 계산하지 않아도 된다.
        if(mm!=0) {
            int[] intervals=new int[60/interval];
            String intervalStr="";
            for(int i=0;i<intervals.length;i++) {
                intervals[i]=i*60/intervals.length;
                if(intervals[i]<mm) {
                    intervalStr+=(i!=0 ? "+ ":"")+"value_"+(intervals[i]<10 ? "0"+intervals[i]:intervals[i]) ;
                }
            }
            minCummulQuery.append("select "+intervalStr+" as minCummulValue from lp_em where channel=1 and mdev_id=? and yyyymmddhh =? and mdev_type='Meter'");
        }

        log.debug("mdevId["+mdevId+"] yyyymmdd["+yyyymmdd+"] hh["+hh+"] mm["+mm+"]");
        log.debug("Query: "+minCummulQuery.toString());
        SQLQuery query = getSession().createSQLQuery(minCummulQuery.toString());
        int conCnt=0;
        if(mm!=0) {
            query.setString(conCnt++, mdevId);
            query.setString(conCnt++, yyyymmdd+(hh<10 ? "0"+hh:hh));
        }
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }



    public void updateSendedResult(LpEM lpem) {
        try {
            boolean comma = false;
            String strValue = null;
            StringBuffer sbQuery = new StringBuffer();
            sbQuery.append("UPDATE LpEM SET ");
            for (int mm = 0; mm < 60; mm++) {
                strValue = BeanUtils.getProperty(lpem, "value_" + (mm<10? "0":"")+mm);

                if (comma && strValue != null) {
                    sbQuery.append(",");
                    comma = false;
                }

                if (strValue != null) {
                    sbQuery.append(" value_" + (mm<10? "0":"")+mm + "=" + strValue + "");
                    comma = true;
                }
            }

            sbQuery.append(" WHERE id.yyyymmddhh = ? ")
                .append(" AND id.channel = ? ")
                .append(" AND id.mdevType = ? ")
                .append(" AND id.mdevId = ? ")
                .append(" AND id.dst = ? ");

            //log.debug(sbQuery.toString());

            //HQL문을 이용한 CUD를 할 경우에는 getHibernateTemplate().bulkUpdate() 메소드를 사용한다.
            
            // bulkUpdate 때문에 주석처리
            /*this.getSession().bulkUpdate(sbQuery.toString(),
                new Object[] {lpem.getId().getYyyymmddhh()
                        , lpem.getId().getChannel()
                        , lpem.getId().getMDevType()
                        , lpem.getId().getMDevId()
                        , lpem.getId().getDst()} );*/
        }
        catch (Exception e) {
            log.error(e);
        }

    }
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public void updateSendedResultByCondition(Map<String, Object> conditionMap) {
        
        String yyyymmddhh = (String) conditionMap.get("yyyymmddhh");
        String mdevId = (String) conditionMap.get("mdevId");
        String mdevType = (String) conditionMap.get("mdevType");
        String sendResult = (String ) conditionMap.get("sendResult");
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n update LP_EM");
        sb.append("\n set send_Result = :sendResult");
        sb.append("\n where yyyymmddhh = :yyyymmddhh and MDEV_ID = :mdevId and MDEV_TYPE = :mdevType");
        
        SQLQuery query = getSession().createSQLQuery(sb.toString());
        query.setString("yyyymmddhh",yyyymmddhh);
        query.setString("mdevId",mdevId);
        query.setString("mdevType",mdevType);
        query.setString("sendResult",sendResult);
        
        query.executeUpdate();
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionEmCo2LpValuesParentId(Map<String, Object> condition) {

        log.info("최상위 위치별 총합\n==== conditions ====\n" + condition);

        @SuppressWarnings("unused")
        Integer locationId = (Integer) condition.get("locationId");
        Integer channel = (Integer) condition.get("channel");
        // 탄소일 경우만 0 ,
        // 수도/온도/습도의
        // 사용량일때는 1

        String startDate = (String) condition.get("startDate");
        //String hh0 = (String) condition.get("hh0");

        StringBuffer sb = new StringBuffer();

        sb.append("\n   SELECT SUM(VALUE_00)+SUM(VALUE_15)+SUM(VALUE_30)+SUM(VALUE_45) AS SUMVALUE");
        sb.append("\n   FROM LP_EM LP INNER JOIN (SELECT ID FROM LOCATION WHERE PARENT_ID=:parentId) L ");
        sb.append("\n   ON LP.LOCATION_ID = L.ID ");
        sb.append("\n   WHERE LP.CHANNEL=:channel AND YYYYMMDDHH =:startDate ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setInteger("parentId", locationId);
        query.setInteger("channel", channel);

        query.setString("startDate", startDate);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getConsumptionEmLpValuesParentId(Map<String, Object> condition) {

        log.info("최상위 위치별 총합\n==== conditions ====\n" + condition);

        @SuppressWarnings("unused")
        Integer locationId = (Integer) condition.get("locationId");
        Integer channel = (Integer) condition.get("channel");
        // 탄소일 경우만 0 ,
        // 수도/온도/습도의
        // 사용량일때는 1

        String startDate = (String) condition.get("startDate");
        //String hh0 = (String) condition.get("hh0");

        StringBuffer sb = new StringBuffer();

        sb.append("\n   SELECT SUM(VALUE_00) AS VALUE_00,SUM(VALUE_15) AS VALUE_15,SUM(VALUE_30) AS VALUE_30,SUM(VALUE_45) AS VALUE_45 ");
        sb.append("\n   FROM LP_EM LP INNER JOIN (SELECT ID FROM LOCATION WHERE PARENT_ID=:parentId) L ");
        sb.append("\n   ON LP.LOCATION_ID = L.ID ");
        sb.append("\n   WHERE LP.CHANNEL=:channel AND YYYYMMDDHH =:startDate ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setInteger("parentId", locationId);
        query.setInteger("channel", channel);

        query.setString("startDate", startDate);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public int getLpInterval( String mdevId ) {

        //주기 ( lpInterval ) 구해오기
        StringBuffer buffer = new StringBuffer();
        buffer.append("\n SELECT LP_INTERVAL FROM METER ");
        buffer.append("\n WHERE MDS_ID =:mdevId ");


        SQLQuery query = getSession().createSQLQuery(buffer.toString());
        query.setString("mdevId", mdevId);

        List<Object> result = query.list();

        return Integer.parseInt(String.valueOf(result.get(0)));
    }

    /**
     * method name : getMeterDetailInfoLpData<b/>
     * method Desc : MDIS - Meter Management 맥스가젯의 Detail Information 탭에서 LpEM 데이터를 조회한다.
     *
     * @param conditionMap
     * @param channel
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public Double getMeterDetailInfoLpData(Map<String, Object> conditionMap, Integer channel) {

//        Integer meterId = (Integer) conditionMap.get("meterId");
        String mdsId = StringUtil.nullToBlank(conditionMap.get("mdsId"));
        String deviceType = StringUtil.nullToBlank(conditionMap.get("deviceType"));
        Integer dst = (Integer) conditionMap.get("dst");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT value ");
        sb.append("\nFROM LpEM l ");
        sb.append("\nWHERE l.id.mdevType = :deviceType ");
//        sb.append("\nAND   l.id.mdevId = :meterId ");
        sb.append("\nAND   l.id.mdevId = :mdsId ");
        sb.append("\nAND   l.id.dst = :dst ");
        sb.append("\nAND   l.id.channel = :channel ");
        sb.append("\nAND   l.id.yyyymmddhh = (SELECT MAX(l2.id.yyyymmddhh) ");
        sb.append("\n                         FROM LpEM l2 ");
        sb.append("\n                         WHERE l2.id.mdevType = :deviceType ");
//        sb.append("\n                         AND   l2.id.mdevId = :meterId ");
        sb.append("\n                         AND   l2.id.mdevId = :mdsId ");
        sb.append("\n                         AND   l2.id.dst = :dst ");
        sb.append("\n                         AND   l2.id.channel = :channel ");
        sb.append("\n                        ) ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("deviceType", deviceType);
        query.setString("mdsId", mdsId);
        query.setInteger("dst", dst);
        query.setInteger("channel", channel);

        return ((Number)query.uniqueResult()).doubleValue();
    }


    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<LpEM> getLastData(Integer meterId) {
        
        
        //시스템에서 사용하는 채널은 제외한다. CO2,ValidationStatus,Integrated,PowerFactor(시스템에서 계산하여 생성하는 채널들)
        //현재시간 기준으로 10분 내외 수정한 데이터 목록을 조회한다.
        final String qstr = "FROM LpEM em " +
                        "WHERE em.meterId=:METER_ID AND " +
//                      "NOT em.id.channel IN (:CHANNELS) AND " +
                        "(em.writeDate >= :START_DATE AND em.writeDate <= :END_DATE) " +
                        "ORDER BY em.id.yyyymmddhh DESC";
        
        final Integer[] sysChannels = new Integer[] {
                ElectricityChannel.Co2.getChannel(),
                ElectricityChannel.Integrated.getChannel(),
                ElectricityChannel.PowerFactor.getChannel(),
                ElectricityChannel.ValidationStatus.getChannel() };
        
        
        Query query = getSession().createQuery(qstr);
        query.setInteger("METER_ID", meterId);
//      query.setParameterList("CHANNELS", sysChannels);
        

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar cal = Calendar.getInstance();
        
        String endDate = sf.format(cal.getTime());
        
        //10분 전 시간
        cal.add(Calendar.MINUTE, -10);
        String startDate = sf.format(cal.getTime());
        
        
        query.setString("START_DATE", startDate);
        query.setString("END_DATE", endDate);
    
        List<LpEM> list = query.list();

        if (list == null || list.size() == 0) {
            log.info("no billing data");
            return null;
        }
        return list;
    }
    
    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<LpEM> getLastData(String mdsId) {
        
        
        //시스템에서 사용하는 채널은 제외한다. CO2,ValidationStatus,Integrated,PowerFactor(시스템에서 계산하여 생성하는 채널들)
        //현재시간 기준으로 10분 내외 수정한 데이터 목록을 조회한다.
        String qstr = "select max(em.id.yyyymmddhh) FROM LpEM em " +
                        "WHERE em.id.mdevId=:mdsId";
        
        Query query = getSession().createQuery(qstr);
        query.setString("mdsId", mdsId);
//      query.setParameterList("CHANNELS", sysChannels);
    
        Object obj = query.uniqueResult();
        
        qstr = "from LpEM em WHERE em.id.mdevId=:mdsId AND em.id.yyyymmddhh=:yyyymmddhh";
        query = getSession().createQuery(qstr);
        query.setString("mdsId", mdsId);
        query.setString("yyyymmddhh", (String)obj);

        List list = query.list();
        if (list == null || list.size() == 0) {
            log.info("no billing data");
            return null;
        }
        return list;
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<LpEM> getLpEMByMeter(Meter meter, String yyyymmddhh, Integer... channels) {
        log.debug("\n meter: " + meter.getMdsId() +
                "\n yyyymmddhh: " + yyyymmddhh +
                "\n channels: " + Arrays.toString(channels));
        
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("\n FROM LpEM em");
        queryStr.append("\n WHERE em.id.yyyymmddhh = :yyyymmddhh ");
        queryStr.append("\n AND em.id.mdevId = :meter ");
        queryStr.append("\n AND em.id.mdevType = :mdevType ");
        queryStr.append("\n AND em.id.channel in (:channels)");
        
        Query query = getSession().createQuery(queryStr.toString());
        query.setString("yyyymmddhh", yyyymmddhh);
        query.setString("meter", meter.getMdsId());
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());
        query.setParameterList("channels", channels);   
        List<LpEM> list = query.list();
        return list;        
    }
    
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<LpEM> getProperLpEMByMeter(Meter meter, String yyyymmddhh, Integer... channels) {
		String yyyymmdd = yyyymmddhh.substring(0, 8);
		String lastYyyymmddhh = CalendarUtil.getDate(yyyymmdd, Calendar.DATE, -7) + "00";
		log.debug("\n meter: " + meter.getMdsId() +
				"\n lastYyyymmddhh: " + lastYyyymmddhh +
				"\n yyyymmddhh: " + yyyymmddhh +				
				"\n channels: " + Arrays.toString(channels));
		
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("\n FROM LpEM em");
		queryStr.append("\n WHERE em.id.yyyymmddhh between :lastYyyymmddhh and :yyyymmddhh ");
		queryStr.append("\n AND em.value_00 is not null ");
		queryStr.append("\n AND em.id.mdevId = :meter ");
		queryStr.append("\n AND em.id.mdevType = :mdevType ");
		queryStr.append("\n AND em.id.channel in (:channels)");
		queryStr.append("\n ORDER BY em.id.yyyymmddhh DESC ");
		
		Query query = getSession().createQuery(queryStr.toString());
		query.setString("lastYyyymmddhh", lastYyyymmddhh);
		query.setString("yyyymmddhh", yyyymmddhh);
		query.setString("meter", meter.getMdsId());
		query.setString("mdevType", CommonConstants.DeviceType.Meter.name());
		query.setParameterList("channels", channels);	
		List<LpEM> list = query.list();
		if (list.size() < 1) {
			log.info("proper size is null");
		}
		return list;		
	}    
    
    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void delete(String meterId, String yyyymmdd) {
        String qstr = "DELETE from LpEM em WHERE em.id.mdevId = :meterId AND em.yyyymmdd = :yyyymmdd";
        Query query = getSession().createQuery(qstr);
        query.setString("meterId", meterId);
        query.setString("yyyymmdd", yyyymmdd);
        query.executeUpdate();
    }
    
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void oldLPDelete(String mdsId, String bDate) {
        StringBuilder hqlBuf = new StringBuilder();
        hqlBuf.append("DELETE FROM LpEM");
        hqlBuf.append(" WHERE id.yyyymmddhh <= ? ");
        hqlBuf.append(" AND id.mdevId = ? ");

     // bulkUpdate 때문에 주석처리
        /*this.getSession().bulkUpdate(hqlBuf.toString(),
            new Object[] {bDate, mdsId} );*/

	}
	
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<Object> getLpReportByDcuSys() 
	{
		//String yyyymmdd = yyyymmddhh.substring(0, 8);
		//String lastYyyymmddhh = CalendarUtil.getDate(yyyymmdd, Calendar.DATE, -7) + "00";
		log.debug("# # # Metering Rate by LP! "  );
		/*
		select aa.sys_id, aa.devicemodel_id, aa.fw_ver, aa.fw_revision, aa.modem_cnt, bb.lp_cnt from
		(
		  select mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_ver, modem.fw_revision, count(modem.id) as modem_cnt
		  from
		    modem left outer join mcu on modem.mcu_id = mcu.id
		  group by mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_ver, modem.fw_revision
		  order by mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_Ver, modem.fw_revision
		) aa left outer join
		(
		  select dd.sys_id, dd.devicemodel_id, dd.fw_ver, dd.fw_revision, count(*) as lp_cnt
		  from 
		    ( select * from lp_em where channel=1 and yyyymmddhh between '2017010201' and '2017010300') cc,
		    ( select mcu.sys_id, modem.mcu_id, modem.devicemodel_id,modem.id as modem_id, modem.fw_ver, modem.fw_revision
		      from 
		         MODEM left outer join mcu on modem.mcu_id=mcu.id
		      group by mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.id, modem.fw_ver, modem.fw_revision
		      order by mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_ver, modem.fw_revision) dd
		   where cc.modem_id = dd.modem_id 
		   group by dd.sys_id, dd.devicemodel_id, dd.fw_ver, dd.fw_revision
		   order by dd.sys_id, dd.devicemodel_id, dd.fw_ver, dd.fw_revision
		) bb
		 on aa.sys_id = bb.sys_id and aa.devicemodel_id = bb.devicemodel_id and aa.fw_ver = bb.fw_ver and aa.fw_revision = bb.fw_revision;*/
		
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("\n SELECT aa.sys_id, aa.devicemodel_id, aa.fw_ver, aa.fw_revision, aa.modem_cnt, bb.lp_cnt ");
		queryStr.append("\n FROM   ");
		queryStr.append("\n   	( SELECT mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_ver, modem.fw_revision, COUNT(modem.id) AS modem_cnt ");
		queryStr.append("\n  	FROM modem LEFT OUTER JOIN mcu ON modem.mcu_id = mcu.id 	");
		queryStr.append("\n  	GROUP BY mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_ver, modem.fw_revision 	");
		queryStr.append("\n  	ORDER BY mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_ver, modem.fw_revision 	");
		queryStr.append("\n  	) aa 	LEFT OUTER JOIN  	");
		queryStr.append("\n  	( SELECT dd.sys_id, dd.devicemodel_id, dd.fw_ver, dd.fw_revision, COUNT(*) AS lp_cnt ");
		queryStr.append("\n 	  FROM  ");
		queryStr.append("\n 	 	( SELECT * FROM lp_em WHERE channel=1 AND yyyymmddhh BETWEEN '2017010201' AND '2017010300') cc, 	");
		queryStr.append("\n 	 	( SELECT mcu.sys_id, modem.mcu_id, modem.devicemodel_id,modem.id as modem_id, modem.fw_ver, modem.fw_revision	");
		queryStr.append("\n 		   FROM modem LEFT OUTER JOIN mcu ON modem.mcu_id=mcu.id 	");
		queryStr.append("\n 		   GROUP BY mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.id, modem.fw_ver, modem.fw_revision		 ");
		queryStr.append("\n	  		   ORDER BY mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_ver, modem.fw_revision	) dd 		");
		queryStr.append("\n       WHERE cc.modem_id = dd.modem_id    	 ");
		queryStr.append("\n 	  GROUP BY dd.sys_id, dd.devicemodel_id, dd.fw_ver, dd.fw_revision	 	");
		queryStr.append("\n 	  ORDER BY dd.sys_id, dd.devicemodel_id, dd.fw_ver, dd.fw_revision		 ");
		queryStr.append("\n 	) bb		 ");
		queryStr.append("\n ON  aa.sys_id = bb.sys_id  ");
		queryStr.append("\n AND aa.devicemodel_id = bb.devicemodel_id			 ");
		queryStr.append("\n AND aa.fw_ver = bb.fw_ver			 ");
		queryStr.append("\n AND aa.fw_revision = bb.fw_revision	 ");
		queryStr.append("\n ORDER BY aa.sys_id, aa.fw_ver, aa.fw_revision	 ");
		
		Query query = getSession().createSQLQuery(queryStr.toString());
		//query.setString("lastYyyymmddhh", lastYyyymmddhh);
		//query.setString("yyyymmddhh", yyyymmddhh);
		//query.setString("meter", meter.getMdsId());
		//query.setString("mdevType", CommonConstants.DeviceType.Meter.name());
		//query.setParameterList("channels", channels);	
		//List<LpEM> list = query.list();
		List<Object> list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		if (list.size() < 1) {
			log.info("Size of Query Result is null");
		}
		return list;
	}
	
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<Object> getMeterReportByDcuSys() 
	{
		log.debug("# # # Metering Rate by Meter(Daily)! "  );
		/*select aa.sys_id, aa.devicemodel_id, aa.fw_ver, aa.fw_revision, aa.modem_cnt, bb.metering_cnt from
		(select mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_ver, modem.fw_revision, count(modem.id) modem_cnt
		     from 
		        modem left outer join mcu
		        on modem.mcu_id = mcu.id
		     group by mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_ver, modem.fw_revision 
		     order by mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_ver, modem.fw_revision) aa
		left outer join
		(select d.sys_id, d.devicemodel_id, d.fw_ver, d.fw_revision, count(*) metering_cnt
		from 
		  (select modem_id from lp_em where channel = 1 and yyyymmddhh between '2017010301' and '2017010400' group by modem_id) c,
		  (select mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.id modem_id, modem.fw_ver, modem.fw_revision
		     from 
		        modem left outer join mcu
		        on modem.mcu_id = mcu.id
		     group by mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.id, modem.fw_ver, modem.fw_revision 
		     order by mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_ver, modem.fw_revision) d
		  where c.modem_id = d.modem_id
		  group by d.sys_id, d.devicemodel_id, d.fw_ver, d.fw_revision
		  order by d.sys_id, d.devicemodel_id, d.fw_ver, d.fw_revision) bb
		  on aa.sys_id = bb.sys_id and aa.devicemodel_id = bb.devicemodel_id and aa.fw_ver = bb.fw_ver and aa.fw_revision = bb.fw_revision;
		  */
		
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("\n SELECT aa.sys_id, aa.devicemodel_id, aa.fw_ver, aa.fw_revision, aa.modem_cnt, bb.metering_cnt ");
		queryStr.append("\n FROM   ");
		queryStr.append("\n   	( SELECT mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_ver, modem.fw_revision, COUNT(modem.id) AS modem_cnt ");
		queryStr.append("\n  	FROM modem LEFT OUTER JOIN mcu ON modem.mcu_id = mcu.id 	");
		queryStr.append("\n  	GROUP BY mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_ver, modem.fw_revision 	");
		queryStr.append("\n  	ORDER BY mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_ver, modem.fw_revision 	");
		queryStr.append("\n  	) aa 	LEFT OUTER JOIN  	");
		queryStr.append("\n  	( SELECT dd.sys_id, dd.devicemodel_id, dd.fw_ver, dd.fw_revision, COUNT(*) AS metering_cnt ");
		queryStr.append("\n 	  FROM  ");
		queryStr.append("\n 	 	( SELECT modem_id FROM lp_em WHERE channel=1 AND yyyymmddhh BETWEEN '2017010201' AND '2017010300' GROUP BY modem_id) cc, ");
		queryStr.append("\n 	 	( SELECT mcu.sys_id, modem.mcu_id, modem.devicemodel_id,modem.id as modem_id, modem.fw_ver, modem.fw_revision	");
		queryStr.append("\n 		   FROM modem LEFT OUTER JOIN mcu ON modem.mcu_id=mcu.id 	");
		queryStr.append("\n 		   GROUP BY mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.id, modem.fw_ver, modem.fw_revision		 ");
		queryStr.append("\n	  		   ORDER BY mcu.sys_id, modem.mcu_id, modem.devicemodel_id, modem.fw_ver, modem.fw_revision	) dd 		");
		queryStr.append("\n       WHERE cc.modem_id = dd.modem_id    	 ");
		queryStr.append("\n 	  GROUP BY dd.sys_id, dd.devicemodel_id, dd.fw_ver, dd.fw_revision	 	");
		queryStr.append("\n 	  ORDER BY dd.sys_id, dd.devicemodel_id, dd.fw_ver, dd.fw_revision		 ");
		queryStr.append("\n 	) bb		 ");
		queryStr.append("\n ON  aa.sys_id = bb.sys_id  ");
		queryStr.append("\n AND aa.devicemodel_id = bb.devicemodel_id			 ");
		queryStr.append("\n AND aa.fw_ver = bb.fw_ver			 ");
		queryStr.append("\n AND aa.fw_revision = bb.fw_revision	 ");
		queryStr.append("\n ORDER BY aa.sys_id, aa.fw_ver, aa.fw_revision	 ");
		
		Query query = getSession().createSQLQuery(queryStr.toString());		
		List<Object> list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		if (list.size() < 1) {
			log.info("Size of Query Result is null");
		}
		return list;
	}
	
	
    
    
    
}