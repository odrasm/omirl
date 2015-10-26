package it.fadeout.omirl.data;

import java.util.Date;

import it.fadeout.omirl.business.OpenSession;

import org.hibernate.Query;
import org.hibernate.Session;

public class OpenSessionRepository extends Repository<OpenSession> {
	
	public OpenSession selectBySessionId(String sSessionId) {
		Session oSession = null;
		OpenSession oOpenSession = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			Query oQuery = oSession.createQuery("from OpenSession where sessionId = '" + sSessionId+ "'");
			if (oQuery.list().size() > 0)
				oOpenSession =  (OpenSession) oQuery.list().get(0);

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
		return oOpenSession;
	}
	
	public void updateBySessionId(String sSessionId) {
		Session oSession = null;
		OpenSession oOpenSession = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			Query oQuery = oSession.createQuery("from OpenSession where sessionId = '" + sSessionId+ "'");
			if (oQuery.list().size() > 0)
			{
				oOpenSession =  (OpenSession) oQuery.list().get(0);
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
		
		oOpenSession.setLastTouch(new Date().getTime());
		Save(oOpenSession);
		//return oOpenSession;
	}
	
	public int deleteOldSessionId(int iMinutes) {
		long lNow = new Date().getTime();
		long lMilliseconds = iMinutes * 60 * 1000;
		long lRefDate = lNow - lMilliseconds;
		
		Session oSession = null;
		int iDeletedRow = 0;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			oSession.beginTransaction();
			Query oQuery = oSession.createQuery("delete from OpenSession where lastTouch < " + lRefDate);
			iDeletedRow = oQuery.executeUpdate();
			oSession.getTransaction().commit();
		}
		catch(Throwable oEx) {
			System.err.println(oEx.toString());
			oEx.printStackTrace();
			
			if (oSession!=null)
			{
				try {
					oSession.getTransaction().rollback();
				}
				catch(Exception oEx2) {
					System.err.println(oEx2.toString());
					oEx2.printStackTrace();					
				}
			}
		}
		finally {
			if (oSession!=null) {
				oSession.flush();
				oSession.clear();
				oSession.close();
			}

		}
		return iDeletedRow;
	}

}
