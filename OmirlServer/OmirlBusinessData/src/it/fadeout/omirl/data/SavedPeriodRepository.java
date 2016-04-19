package it.fadeout.omirl.data;

import it.fadeout.omirl.business.OmirlUser;
import it.fadeout.omirl.business.SavedPeriod;

import java.util.ArrayList;
import java.util.Date;

import org.hibernate.Query;
import org.hibernate.Session;

public class SavedPeriodRepository extends Repository<SavedPeriod> {
	
	
   public SavedPeriod selectByPeriodId(Integer idSavedPeriod) {
		
		Session oSession = null;
		SavedPeriod oSavedPeriod = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			Query oQuery = oSession.createQuery("from SavedPeriod where SavedPeriod.idUser = '" + idSavedPeriod+ "'");
			if (oQuery.list().size() > 0)
				oSavedPeriod =  (SavedPeriod) oQuery.list().get(0);

		}
		catch(Throwable oEx) {
			System.err.println(oEx.toString());
			oEx.printStackTrace();
		}
		finally {
			if (oSession!=null) {
				oSession.flush();
				oSession.clear();
				oSession.close();
			}

		}
		return oSavedPeriod;		
	}
	
	
	
	public boolean IsSavedPeriod(Date oDate) {
		if (oDate==null) return false;
		
		Session oSession = null;
		SavedPeriod oSavedPeriod = null;
		boolean bRet = false;
		
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			long lTime = oDate.getTime();
			Query oQuery = oSession.createQuery("from SavedPeriod where timestampStart <= " + lTime + " and timestampEnd>= " + lTime);
			if (oQuery.list().size() > 0)
			{
				oSavedPeriod =  (SavedPeriod) oQuery.list().get(0);
				bRet = true;
			}

		}
		catch(Throwable oEx) {
			System.err.println(oEx.toString());
			oEx.printStackTrace();
		}
		finally {
			if (oSession!=null) {
				oSession.flush();
				oSession.clear();
				oSession.close();
			}

		}
		
		return bRet;		
	}
}
