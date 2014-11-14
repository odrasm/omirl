package it.fadeout.omirl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import it.fadeout.omirl.business.DataChart;
import it.fadeout.omirl.business.OmirlUser;
import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.business.config.SensorLinkConfig;
import it.fadeout.omirl.business.config.TableLinkConfig;
import it.fadeout.omirl.viewmodels.PrimitiveResult;
import it.fadeout.omirl.viewmodels.SummaryInfo;
import it.fadeout.omirl.viewmodels.TableLink;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path("/tables")
public class TablesService {
	
	@Context
	ServletConfig m_oServletConfig;

	
	@GET
	@Path("/test")
	@Produces({"application/xml", "application/json", "text/xml"})
	public PrimitiveResult TestOmirl() {
		// Just a keep alive message
		PrimitiveResult oTest = new PrimitiveResult();
		oTest.StringValue = "Table Service is Working";
		return oTest;
	}
	
	@GET
	@Path("/tablelinks")
	@Produces({"application/xml", "application/json", "text/xml"})	
	public ArrayList<TableLink> getTableLinks(@HeaderParam("x-session-token") String sSessionId) {
		
		ArrayList<TableLink> aoRet = new ArrayList<>();
		
		try {
			boolean bShowPrivate = false;
			if (Omirl.getUserFromSession(sSessionId) != null) {
				bShowPrivate = true;
			}
			
			// Get Config
			Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");
			
			if (oConfObj != null)  {
				// Cast Config
				OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;			
				
				for (int iConfigured =0 ; iConfigured< oConfig.getTableLinks().size(); iConfigured++ )
				{
					TableLinkConfig oTableLink = oConfig.getTableLinks().get(iConfigured);
					
					boolean bAdd = false;
					
					if (bShowPrivate==true) bAdd = true;
					else {
						if (oTableLink.isPrivate() == false) bAdd = true;
					}
					
					if (bAdd){
						aoRet.add(oTableLink.getTableLink());
					}
				}
			}
			
			
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}
		
		return aoRet;
	}
	
	
	@GET
	@Path("/datatablelinks")
	@Produces({"application/xml", "application/json", "text/xml"})	
	public ArrayList<TableLink> getDataTableLinks(@HeaderParam("x-session-token") String sSessionId) {
		
		ArrayList<TableLink> aoRet = new ArrayList<>();
		
		try {
			boolean bShowPrivate = false;
			if (Omirl.getUserFromSession(sSessionId) != null) {
				bShowPrivate = true;
			}
			
			// Get Config
			Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");
			
			if (oConfObj != null)  {
				// Cast Config
				OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;			
				
				for (int iConfigured =0 ; iConfigured< oConfig.getDataTableLinks().size(); iConfigured++ )
				{
					TableLinkConfig oTableLink = oConfig.getDataTableLinks().get(iConfigured);
					
					boolean bAdd = false;
					
					if (bShowPrivate==true) bAdd = true;
					else {
						if (oTableLink.isPrivate() == false) bAdd = true;
					}
					
					if (bAdd){
						aoRet.add(oTableLink.getTableLink());
					}
				}
			}			
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}
		
		return aoRet;
	}	
	@GET
	@Path("/summary")
	@Produces({"application/xml", "application/json", "text/xml"})
	public SummaryInfo GetSummaryTable(@HeaderParam("x-session-token") String sSessionId) {
		System.out.println("TablesService.GetSummaryTable");
		
		// Create return array List
		SummaryInfo oSummaryInfo = null;
		// Date: will be received from client...
		Date oDate = new Date();
		
		// Get Config
		Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");
		
		if (oConfObj != null)  {
			
			// Cast Config
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;			
			
			String sBasePath = oConfig.getFilesBasePath();
			
			sBasePath += "/tables/summary";
			
			System.out.println("TablesService.GetSummaryTable = " + sBasePath);
			
			// Get The path of the right date
			String sPath = Omirl.getSubPath(sBasePath, oDate);
			
			if (sPath != null) {
				
				System.out.println("TablesService.GetSummaryTable: searching path " + sPath);
				
				// Get The Last File: TODO: here use also the date and get the last before the date!!
				File oLastFile = Omirl.lastFileModified(sPath);
				
				// Found?
				if (oLastFile != null) {
					
					System.out.println("TablesService.GetSummaryTable: Opening File " + oLastFile.getAbsolutePath());
					
					try {
						// Ok read sensors 
						oSummaryInfo = (SummaryInfo) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}							
				}
			}
		}
		
		// Return the list of sensors
		return oSummaryInfo;
	}
	
	
	
	
}
