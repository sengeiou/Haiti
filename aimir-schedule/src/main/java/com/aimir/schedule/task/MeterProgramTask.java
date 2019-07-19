package com.aimir.schedule.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.constants.CommonConstants.DefaultCmdResult;
import com.aimir.constants.CommonConstants.MeterProgramKind;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.MeterDao;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.GroupMember;
import com.aimir.schedule.command.CmdOperationUtil;

/**
 * Meter Program Task<br>
 * 모델별로 등록된 미터 프로그램을 적용시키는 작업.
 * @author kskim
 *
 */
public class MeterProgramTask extends GroupTask {
	protected static Log log = LogFactory.getLog(MeterProgramTask.class);

	@Autowired
	private MeterDao meterDao;
	
	@Autowired
	private CmdOperationUtil cmdOperationUtil;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void execute(JobExecutionContext context){
		log.debug("MeterProgramTask");
		
		Session session = null;
		
		try {
			
			List<Integer> meters = new ArrayList<Integer>();
			// 그룹에 포함된 미터 목록
			for (int i = 0; i < this.groupMembers.length; i++){
				GroupMember groupMember = this.groupMembers[i];
				Meter meter = meterDao.get(groupMember.getMember());

				if (meter != null) {
					meters.add(meter.getId());
				}
			}

			session = sessionFactory.openSession();
			Query query = null;

			/**
			 * 미터 프로그램 적용 목록중 실패하거나 시도하지 않은 항목들을 조회하는 쿼리.
			 */
			final String q = "SELECT m.id, mp.kind, m.mdsId FROM "
					+ "Meter m, "
					+ "MeterConfig mc, "
					+ "MeterProgram mp "
					+ "WHERE "
					+ "mc.deviceModel=m.model "
					+ "AND mp.meterConfigId=mc.id "
					+ "AND mp.id IN (SELECT MAX(id) FROM MeterProgram GROUP BY kind,meterConfigId) "
					+ "AND NOT m.id IN (SELECT mpl.meter.id FROM MeterProgramLog mpl WHERE mpl.result=:RESULT AND m.id=mpl.meter.id AND mpl.meterProgram.id=mp.id) "
					+ "AND m.id IN (:METERS) AND mp.kind!=:KIND " + "ORDER BY m.id";

			query = session.createQuery(q);
			query.setParameter("RESULT", DefaultCmdResult.SUCCESS);
			query.setParameter("KIND", MeterProgramKind.SAPTable);
			query.setParameterList("METERS", meters);
			
			
			List<?> list = query.list();

			for (Object object : list) {
				String mdsid = null;
				try {
					if (object instanceof Object[]) {
						Object[] arrObject = (Object[]) object;

						Integer meterId = (Integer) arrObject[0];
						MeterProgramKind kind = (MeterProgramKind) arrObject[1];
						mdsid = (String)arrObject[2];

						// 미터의 모뎀 타입을 조회한다.
						Meter meter = meterDao.get(meterId);
						Modem modem = meter.getModem();

						if (modem != null) {

							// 조회 목록중 모템 타입별로 다른 방법으로 명령을 전송한다.
							if (modem.getModemType() == ModemType.Converter_Ethernet) {
								cmdOperationUtil.cmdMeterProgram(meter
										.getMdsId(), kind);
							} else if (modem.getModemType() == ModemType.MMIU) {
								cmdOperationUtil.cmdBypassMeterProgram(meter
										.getMdsId(), kind);
							}
						}
					}

				} catch (Exception e) {
					log.error(e, e);
					if(!this.failMembers.contains(mdsid)){
						this.addFailMember(mdsid);
					}
				} 
			}
			
			if(isSuccess()){
				setSuccessResult();
			}else{
				setFailResult();
			}
		}
		catch (Exception e) {
			log.error("MeterProgramTask Error");
			log.error(e, e);
			setFailResult(e.getMessage());
		}
		finally {
		    if (session != null) session.close();
		}
		
	}
}
