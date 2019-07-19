package com.aimir.dao.system.impl;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.OnDemandReadingOrderDao;
import com.aimir.model.device.Meter;
import com.aimir.model.system.OnDemandReadingOrder;
import com.aimir.util.SQLWrapper;

@Repository(value = "onDemandReadingOrderDao")
public class OnDemandReadingOrderDaoImpl extends
		AbstractHibernateGenericDao<OnDemandReadingOrder, Integer> implements
		OnDemandReadingOrderDao {
    private static Log log = LogFactory.getLog(OnDemandReadingOrderDaoImpl.class);

	@Autowired
	protected OnDemandReadingOrderDaoImpl(SessionFactory sessionFactory) {
		super(OnDemandReadingOrder.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<OnDemandReadingOrder> getOnDemandReadingOrder(Long referenceId) {
		Query query = getSession().createQuery("from OnDemandReadingOrder where referenceId = " + referenceId);
		return query.list();
				
		// return (List<OnDemandReadingOrder>)getHibernateTemplate().find("from OnDemandReadingOrder where referenceId = ?", referenceId);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<OnDemandReadingOrder> listOnDemandReadingOrder(String condition) {
		if(condition!=null && condition.length()>0) {
			Query query = getSession().createQuery("from OnDemandReadingOrder where " + condition);
			
			return query.list();
			
			/*return (List<OnDemandReadingOrder>)getHibernateTemplate().find(
					"from OnDemandReadingOrder where " + condition);*/
		} else {
			Query query = getSession().createQuery("from OnDemandReadingOrder");
			return query.list();
			
			/*return (List<OnDemandReadingOrder>)getHibernateTemplate().find("from OnDemandReadingOrder");*/
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<OnDemandReadingOrder> searchOnDemandReadingOrder(
			OnDemandReadingOrder param, String additionalCondition) {
		String sql = "from OnDemandReadingOrder \n";
		boolean isFirst = true;
		if (param != null) {
			if (param.getUserName() != null) {
				if (isFirst) {
					sql += "where ";
				} else {
					sql += "\n and ";
				}
				sql += "userName='" + param.getUserName() + "'";
				isFirst = false;
			}
			if (param.getReferenceId() != null) {
				if (isFirst) {
					sql += "where ";
				} else {
					sql += "\n and ";
				}
				sql += "referenceId=" + param.getReferenceId();
				isFirst = false;
			}
			if (param.getMeterSerialNumber() != null) {
				if (isFirst) {
					sql += "where ";
				} else {
					sql += "\n and ";
				}
				sql += "meterSerialNumber='" + param.getMeterSerialNumber()
						+ "'";
				isFirst = false;
			}
			if (param.getMeterValueDate() != null) {
				if (isFirst) {
					sql += "where ";
				}
				if (!isFirst) {
					sql += "\n and ";
				}
				sql += "meterValueDate='" + param.getMeterValueDate() + "'";
				isFirst = false;
			}
			if (param.getMeterValueDateFrom() != null) {
				if (isFirst) {
					sql += "where ";
				} else {
					sql += "\n and ";
				}
				sql += "meterValueDate>='" + param.getMeterValueDateFrom()
						+ "'";
				isFirst = false;
			}
			if (param.getMeterValueDateTo() != null) {
				if (isFirst) {
					sql += "where ";
				} else {
					sql += "\n and ";
				}
				sql += "meterValueDate<='" + param.getMeterValueDateTo() + "'";
				isFirst = false;
			}
			if (param.getOrderStatus() != null) {
				if (isFirst) {
					sql += "where ";
				} else {
					sql += "\n and ";
				}
				sql += "orderStatus=" + param.getOrderStatus() + "";
				isFirst = false;
			}
			if (param.getApplicationFault() != null) {
				if (isFirst) {
					sql += "where ";
				} else {
					sql += "\n and ";
				}
				sql += "applicationFault=" + param.getApplicationFault() + "";
				isFirst = false;
			}
			if (additionalCondition != null) {
				if (isFirst) {
					sql += "where ";
				} else {
					sql += "\n and ";
				}
				sql += additionalCondition;
				isFirst = false;
			}
		}
		log.debug("sql["+ sql+ "]");
		

		Query query = getSession().createQuery(sql);
		return query.list();
		
		// return (List<OnDemandReadingOrder>)getHibernateTemplate().find(sql);
	}

	@Override
	public int deleteOnDemandReadingOrder(OnDemandReadingOrder req) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("update OnDemandReadingOrder ");
		hqlBuf.append("set orderStatus=103 where \n");
		hqlBuf.append(" orderStatus in (101,102,201,202) ");
		hqlBuf.append(" and referenceId=? ");

		int count = 0;
		try {
		    Query query = getSession().createQuery(hqlBuf.toString());
		    query.setParameter(1, req.getReferenceId());
		    count = query.executeUpdate();
			/*count = this.getHibernateTemplate().bulkUpdate(hqlBuf.toString(),
					new Object[] { req.getReferenceId() });*/

    		if (count == 0) {
    			OnDemandReadingOrder searchParamClone = null;
    			try {
    				searchParamClone = (OnDemandReadingOrder) req.clone();
    				searchParamClone.setOrderStatus(null);
    			} catch (CloneNotSupportedException e) {}
    
    			List<OnDemandReadingOrder> list = searchOnDemandReadingOrder(searchParamClone,
    					"orderStatus in (202, 203, 298, 299)");
    			if (list != null & list.size() > 0) {
    				return 3;
    			}
    		}
    		if (count > 0) {
    			return 1;
    		} else {
    			return 2;
    		}
		} catch (Exception e) {
		    log.error(e, e);
        }
		
		return 0;
	}

	@Override
	public int updateOnDemandReadingOrder(OnDemandReadingOrder data,
			String[] fieldNames, Object[] fieldValues) {

		if (fieldNames == null || fieldNames.length == 0
				|| fieldNames.length != fieldValues.length) {
			return 0;
		}

		StringBuffer sql = new StringBuffer();
		sql.append("update OnDemandReadingOrder set ");
		for (int i = 0; i < fieldNames.length; i++) {
			if (i != 0) {
				sql.append(", ");
			}
			if (fieldValues[i] instanceof String) {
				sql.append(fieldNames[i]).append("='").append(fieldValues[i])
						.append("'");
			} else {
				sql.append(fieldNames[i]).append("=").append(fieldValues[i]);				
			}
		}
		if (data.getId() != null) {
			sql.append(" where id=?");
		} else {
			sql.append(" where referenceId=? and meterSerialNumber=? ");
		}

		Query query = getSession().createQuery(sql.toString());
		if(data.getId() != null) {
		    query.setParameter(1, data.getId());
		    return query.executeUpdate();
			/*return this.getHibernateTemplate().bulkUpdate(sql.toString(),
					new Object[] { data.getId() });*/
		} else {
		    query.setParameter(1, data.getId());
		    query.setParameter(2, data.getMeterSerialNumber());
		    return query.executeUpdate();
			// for callback test
			/*return this.getHibernateTemplate().bulkUpdate(sql.toString(),
					new Object[] { data.getReferenceId(), data.getMeterSerialNumber()});*/
		}
	}

	public Map<String, Double> getHistoricalMeteringData(Meter meter, String meterValueDate) {
	    
        MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());
	    String lpTable = MeterType.valueOf(meter.getMeterType().getName()).getLpTableName();
	    
        StringBuilder sb = new StringBuilder();
        sb.append("\nSELECT a.*, c.name");
        sb.append("\nFROM ").append(lpTable).append(" a, channel_config b, display_channel c, meterconfig d, meter e");
        sb.append("\nWHERE a.yyyymmddhh = :yyyymmddhh and a.mdev_id = :mdev_id");
        sb.append("\n  AND a.channel = b.channel_index");
        sb.append("\n  AND b.channel_id = c.id");
        sb.append("\n  AND b.meterconfig_id = d.id");
        sb.append("\n  AND d.id = e.devicemodel_id");
        sb.append("\n  AND e.mds_id= :mds_id");
        if (meterType == MeterType.EnergyMeter) {
            sb.append("\n  AND c.name in ('Active Energy Imp.', 'Active Energy')");
            sb.append("\n  AND c.service_type='Electricity'");
        } else if (meterType == MeterType.GasMeter) {
            sb.append("\n  AND c.name in ('Usage')");
            sb.append("\n  AND c.service_type='Gas'");
        } else if (meterType == MeterType.WaterMeter) {
            sb.append("\n  AND c.name in ('Usage')");
            sb.append("\n  AND c.service_type='Water'");
        } else if (meterType == MeterType.HeatMeter) {
            /* TODO 가스, 수도, 열량계에 대해서 검토 필요하다. 임시로 입력함 */
            sb.append("\n  AND c.name in ('Energy Usage', 'Volume Usage')");
            sb.append("\n  AND c.service_type='Heat'");
        }
        sb.append("\n  AND a.value_").append(meterValueDate.substring(10,12)).append(" IS NOT NULL");

        SQLQuery query = this.getSessionFactory().getCurrentSession()
                .createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("yyyymmddhh", meterValueDate.substring(0,10));
        query.setString("mdev_id", meter.getMdsId());
        query.setString("mds_id", meter.getMdsId());

        List<Map<String, Double>> result;
        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        Map<String, Double> retMap = new LinkedHashMap<String, Double>();
        DecimalFormat df = new DecimalFormat("00");
        double meterValue = 0;
        int minute = Integer.parseInt(meterValueDate.substring(10,12));

        if (result != null && result.size() > 0) {
            for (Map<String, Double> res : result) {
                meterValue = Double.parseDouble(res.get("VALUE").toString());
                for (int j = 0; j < minute; j++) {
                    if (res.get("VALUE_" + df.format(j)) != null) {
                        meterValue += Double.parseDouble(res.get(
                                "VALUE_" + df.format(j)).toString());
                    }
                }

                if (meterType == MeterType.EnergyMeter) {
                    retMap.put("meterValueEnergy", meterValue);
                } else if (meterType == MeterType.GasMeter) {
                    retMap.put("meterValueVolume", meterValue);
                } else if (meterType == MeterType.WaterMeter) {
                    retMap.put("meterValueVolume", meterValue);
                } else if (meterType == MeterType.HeatMeter) {
                    /** TODO 아래 내용은 검토가 필요하다. 임시로 입력함. */
                    if (res.get("NAME").toString().equals("Energy")) {
                        retMap.put("meterValueEnergy", meterValue);
                    } else if (res.get("NAME").toString().equals("Volume")) {
                        retMap.put("meterValueVolume", meterValue);
                    }
                }
            }
        }
        return retMap;
	}
}
/**
SELECT a.*, c.name
FROM lp_em a, channel_config b, display_channel c, meterconfig d, meter e
WHERE a.yyyymmddhh = '2015072000' and a.mdev_id = '17707964'
  AND a.channel = b.channel_index
  AND b.channel_id = c.id
  AND b.meterconfig_id= d.id
  AND d.id = e.devicemodel_id AND e.mds_id='17707964'
  AND c.name in ('Active Energy Imp.', 'Active Energy')
  AND c.service_type='Electricity'
  AND a.value_00 is not null
;
*/