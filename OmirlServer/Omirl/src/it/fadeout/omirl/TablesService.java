package it.fadeout.omirl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.fadeout.omirl.business.DataChart;
import it.fadeout.omirl.business.OmirlUser;
import it.fadeout.omirl.business.StationAnag;
import it.fadeout.omirl.business.config.HydroLinkConfig;
import it.fadeout.omirl.business.config.HydroModelLinkConfig;
import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.business.config.SensorLinkConfig;
import it.fadeout.omirl.business.config.TableLinkConfig;
import it.fadeout.omirl.data.StationAnagRepository;
import it.fadeout.omirl.viewmodels.HydroModelViewModel;
import it.fadeout.omirl.viewmodels.MaxTableRowViewModel;
import it.fadeout.omirl.viewmodels.MaxTableViewModel;
import it.fadeout.omirl.viewmodels.MobileStation;
import it.fadeout.omirl.viewmodels.PrimitiveResult;
import it.fadeout.omirl.viewmodels.SectionBasinsCodesViewModel;
import it.fadeout.omirl.viewmodels.SectionBasinsViewModel;
import it.fadeout.omirl.viewmodels.SectionViewModel;
import it.fadeout.omirl.viewmodels.SensorListTableRowViewModel;
import it.fadeout.omirl.viewmodels.SensorListTableViewModel;
import it.fadeout.omirl.viewmodels.SensorValueRowViewModel;
import it.fadeout.omirl.viewmodels.SensorValueTableViewModel;
import it.fadeout.omirl.viewmodels.SensorViewModel;
import it.fadeout.omirl.viewmodels.SummaryInfo;
import it.fadeout.omirl.viewmodels.TableLink;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

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
	public SummaryInfo GetSummaryTable(@HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {
		System.out.println("TablesService.GetSummaryTable");

		// Create return array List
		SummaryInfo oSummaryInfo = null;
		// Date: will be received from client...
		Date oDate = new Date();

		if (sRefDate!=null)
		{
			if (sRefDate.equals("") == false) 
			{
				// Try e catch per fare il parsing 
				// se � valido sostituire oDate.
				SimpleDateFormat dtFormat = new SimpleDateFormat(Omirl.s_sDateHeaderFormat);
				try {

					oDate = dtFormat.parse(sRefDate);

				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

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
				File oLastFile = Omirl.lastFileModified(sPath, oDate);

				// Found?
				if (oLastFile != null) {

					System.out.println("TablesService.GetSummaryTable: Opening File " + oLastFile.getAbsolutePath());

					try {
						// Ok read sensors 
						oSummaryInfo = (SummaryInfo) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
						if (oSummaryInfo != null)
						{
							Date oLastDate = new Date(oLastFile.lastModified()); 
							SimpleDateFormat oFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
							oSummaryInfo.setUpdateDateTime(oFormat.format(oLastDate));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}							
				}
			}
		}

		// Return the list of sensors
		return oSummaryInfo;
	}




	@GET
	@Path("/max")
	@Produces({"application/xml", "application/json", "text/xml"})
	public MaxTableViewModel GetMaxTable(@HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {
		System.out.println("TablesService.GetMaxTable");

		// Create return array List
		MaxTableViewModel oMaxTable = null;
		// Date: will be received from client...
		Date oDate = new Date();

		if (sRefDate!=null)
		{
			if (sRefDate.equals("") == false) 
			{
				// Try e catch per fare il parsing 
				// se � valido sostituire oDate.
				SimpleDateFormat dtFormat = new SimpleDateFormat(Omirl.s_sDateHeaderFormat);
				try {

					oDate = dtFormat.parse(sRefDate);

				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		// Get Config
		Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");

		if (oConfObj != null)  {

			// Cast Config
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;			

			String sBasePath = oConfig.getFilesBasePath();

			sBasePath += "/tables/max";

			System.out.println("TablesService.GetMaxTable = " + sBasePath);

			// Get The path of the right date
			String sPath = Omirl.getSubPath(sBasePath, oDate);

			if (sPath != null) {

				System.out.println("TablesService.GetSummaryTable: searching path " + sPath);

				// Get The Last File:
				File oLastFile = Omirl.lastFileModified(sPath, oDate);

				// Found?
				if (oLastFile != null) {

					System.out.println("TablesService.GetSummaryTable: Opening File " + oLastFile.getAbsolutePath());

					try {
						// Ok read sensors 
						oMaxTable = (MaxTableViewModel) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}							
				}
			}
		}

		// Return the list of sensors
		return oMaxTable;
	}


	/**
	 * Get a list of stations near the device position
	 * @param dLat
	 * @param dLat
	 * @return A list with all the station around the mobile device position
	 */
	@GET
	@Path("/anag/{sCode}")
	@Produces({"application/xml", "application/json", "text/xml"})
	@Consumes({"application/xml", "application/json", "text/xml"})	
	public SensorViewModel getStationsAnag(@HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate, @PathParam("sCode") String sCode)
	{
		SensorViewModel oViewModel = new SensorViewModel();
		StationAnagRepository oRepo = new StationAnagRepository();
		StationAnag oAnag = oRepo.selectByStationCode(sCode);
		if (oAnag != null)
		{
			oViewModel.setMunicipality(oAnag.getMunicipality());
			oViewModel.setName(oAnag.getName());
			oViewModel.setShortCode(oAnag.getStation_code());
		}

		return oViewModel;


	}

	@GET
	@Path("/hydromodellist/")
	@Produces({"application/xml", "application/json", "text/xml"})
	@Consumes({"application/xml", "application/json", "text/xml"})	
	public List<HydroModelViewModel> getHydroModelList(@HeaderParam("x-session-token") String sSessionId)
	{
		List<HydroModelViewModel> oReturnList = new ArrayList<HydroModelViewModel>();
		try {			

			if (Omirl.getUserFromSession(sSessionId)!=null) {
				// Get Config
				Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");
				if (oConfObj != null)  {

					// Cast Config
					OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;

					ArrayList<HydroModelLinkConfig> oHydroModelList = oConfig.getHydroModelLinks();

					if (oHydroModelList != null)
					{
						for (HydroModelLinkConfig hydroModelLinkConfig : oHydroModelList) {
							HydroModelViewModel oViewModel = new HydroModelViewModel();
							oViewModel.setCode(hydroModelLinkConfig.getLinkCode());
							oViewModel.setDescription(hydroModelLinkConfig.getDescription());
							oViewModel.setIconLink(hydroModelLinkConfig.getIconLink());
							oViewModel.setIsDefault(hydroModelLinkConfig.getIsDefault());
							oReturnList.add(oViewModel);
						}
					}

				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return oReturnList;


	}

	@GET
	@Path("/sectionbasinlist/{sModelCode}")
	@Produces({"application/xml", "application/json", "text/xml"})
	public List<SectionBasinsViewModel> GetSectionBasinsList(@HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate, @PathParam("sModelCode") String sModelCode) {
		System.out.println("TablesService.GetSectionBasinsList: Code = " + sModelCode);

		// Create return array List
		List<SectionBasinsViewModel> aoSections = new ArrayList<SectionBasinsViewModel>();
		try {			

			if (Omirl.getUserFromSession(sSessionId)!=null) {
				// Date: will be received from client...
				Date oDate = new Date();

				if (sRefDate!=null)
				{
					if (sRefDate.equals("") == false) 
					{
						// Try e catch per fare il parsing 
						// se � valido sostituire oDate.
						SimpleDateFormat dtFormat = new SimpleDateFormat(Omirl.s_sDateHeaderFormat);
						try {

							oDate = dtFormat.parse(sRefDate);

						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}

				// Get Config
				Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");

				if (oConfObj != null)  {

					System.out.println("TablesService.GetSectionBasinsList: Config Found");

					// Cast Config
					OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;

					// Find the right Sensor Link Configuration
					for (HydroModelLinkConfig oLinkConfig : oConfig.getHydroModelLinks()) {

						if (oLinkConfig.getLinkCode().equals(sModelCode)) {

							System.out.println("SectionsService.GetSection: Section Code Config Found");

							// Get The path of the right date
							String sPath = Omirl.getSubPath(oLinkConfig.getFilePath(), oDate);

							if (sPath != null) {

								//sPath = sPath + "/features";

								System.out.println("TablesService.GetSectionBasinsList: searching path " + sPath);

								// Get The Last File
								File oLastFile = Omirl.lastFileModified(sPath, oDate);

								// Found?
								if (oLastFile != null) {

									System.out.println("SectionsService.GetSection: Opening File " + oLastFile.getAbsolutePath());

									try {
										// Ok read sections 
										aoSections = (List<SectionBasinsViewModel>) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
									} catch (Exception e) {
										e.printStackTrace();
									}							
								}
							}
							else {
								System.out.println("SectionsService.GetSection: WARNING path is null");
							}

							// We are done
							break;
						}

					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Return the list of sensors
		return aoSections;
	}


	@GET
	@Path("/anagByName/{sName}")
	@Produces({"application/xml", "application/json", "text/xml"})
	@Consumes({"application/xml", "application/json", "text/xml"})	
	public SensorViewModel getStationsAnagByName(@HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate, @PathParam("sName") String sName)
	{
		SensorViewModel oViewModel = new SensorViewModel();
		StationAnagRepository oRepo = new StationAnagRepository();
		StationAnag oAnag = oRepo.selectByName(sName);
		if (oAnag != null)
		{
			oViewModel.setMunicipality(oAnag.getMunicipality());
			oViewModel.setName(oAnag.getName());
			oViewModel.setShortCode(oAnag.getStation_code());
		}

		return oViewModel;


	}



	/**
	 * Gets sensors data
	 * @return
	 */
	@GET
	@Path("/exportmaxvalues")
	@Produces({"application/octet-stream"})
	public Response ExportSensorValuesTable(@QueryParam("sRefDate") String sRefDate) {

		return ExportValue(sRefDate);

	}


	private Response ExportValue(String sRefDate)
	{
		// Create return array List
		MaxTableViewModel oMaxTable = null;
		// Date: will be received from client...
		Date oDate = new Date();

		if (sRefDate!=null)
		{
			if (sRefDate.equals("") == false) 
			{
				// Try e catch per fare il parsing 
				// se � valido sostituire oDate.
				SimpleDateFormat dtFormat = new SimpleDateFormat(Omirl.s_sDateQueryParam);
				try {

					oDate = dtFormat.parse(sRefDate);

				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		// Get Config
		Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");

		if (oConfObj != null)  {

			// Cast Config
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;			

			String sBasePath = oConfig.getFilesBasePath();

			sBasePath += "/tables/max";

			System.out.println("TablesService.GetMaxTable = " + sBasePath);

			// Get The path of the right date
			String sPath = Omirl.getSubPath(sBasePath, oDate);

			if (sPath != null) {

				System.out.println("TablesService.GetSummaryTable: searching path " + sPath);

				// Get The Last File:
				File oLastFile = Omirl.lastFileModified(sPath, oDate);

				// Found?
				if (oLastFile != null) {

					System.out.println("TablesService.GetSummaryTable: Opening File " + oLastFile.getAbsolutePath());

					try {
						// Ok read sensors 
						oMaxTable = (MaxTableViewModel) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}							
				}
			}
		}


		final MaxTableViewModel oFinalTable = oMaxTable;
		StreamingOutput stream = null;
		if (oFinalTable != null)
		{

			stream = new StreamingOutput() {
				@Override
				public void write(OutputStream os) throws IOException, WebApplicationException {
					Writer writer = new BufferedWriter(new OutputStreamWriter(os));

					if (oFinalTable.getAlertZones()!=null)
					{
						if (oFinalTable.getAlertZones().size()>0) 
						{
							writer.write("Zona;5m;15m;30m;1h;3h;6h;12h;24h;\n");

							for (int iTableRows=0; iTableRows<oFinalTable.getAlertZones().size(); iTableRows++) {

								MaxTableRowViewModel oRow = oFinalTable.getAlertZones().get(iTableRows);

								String sZona = oRow.getName();
								String s5m = oRow.getM5val() + " " + oRow.getM5();
								String s15m = oRow.getM15val() + " " + oRow.getM15();
								String s30m = oRow.getM30val() + " " + oRow.getM30();
								String s1h = oRow.getH1val() + " " + oRow.getH1();
								String s3h = oRow.getH3val() + " " + oRow.getH3();
								String s6h = oRow.getH6val() + " " + oRow.getH6();
								String s12h = oRow.getH12val() + " " + oRow.getH12();
								String s24h = oRow.getH24val() + " " + oRow.getH24();

								if (sZona == null) sZona ="";
								if (s5m == null) s5m ="";
								if (s15m == null) s15m ="";
								if (s30m == null) s30m ="";
								if (s1h == null) s1h ="";
								if (s3h == null) s3h ="";
								if (s6h == null) s6h ="";
								if (s12h == null) s12h ="";
								if (s24h == null) s24h ="";

								writer.write(sZona+";");
								writer.write(s5m+";");
								writer.write(s15m+";");
								writer.write(s30m+";");
								writer.write(s1h+";");
								writer.write(s3h+";");
								writer.write(s6h+";");
								writer.write(s12h+";");
								writer.write(s24h+";");
								writer.write("\n");

							}
						}
					}

					if (oFinalTable.getDistricts().size()>0) 
					{
						writer.write("Provincia;5m;15m;30m;1h;3h;6h;12h;24h;\n");

						for (int iTableRows=0; iTableRows<oFinalTable.getDistricts().size(); iTableRows++) {

							MaxTableRowViewModel oRow = oFinalTable.getDistricts().get(iTableRows);

							String sZona = oRow.getName();
							String s5m = oRow.getM5val() + " " + oRow.getM5();
							String s15m = oRow.getM15val() + " " + oRow.getM15();
							String s30m = oRow.getM30val() + " " + oRow.getM30();
							String s1h = oRow.getH1val() + " " + oRow.getH1();
							String s3h = oRow.getH3val() + " " + oRow.getH3();
							String s6h = oRow.getH6val() + " " + oRow.getH6();
							String s12h = oRow.getH12val() + " " + oRow.getH12();
							String s24h = oRow.getH24val() + " " + oRow.getH24();

							if (sZona == null) sZona ="";
							if (s5m == null) s5m ="";
							if (s15m == null) s15m ="";
							if (s30m == null) s30m ="";
							if (s1h == null) s1h ="";
							if (s3h == null) s3h ="";
							if (s6h == null) s6h ="";
							if (s12h == null) s12h ="";
							if (s24h == null) s24h ="";

							writer.write(sZona+";");
							writer.write(s5m+";");
							writer.write(s15m+";");
							writer.write(s30m+";");
							writer.write(s1h+";");
							writer.write(s3h+";");
							writer.write(s6h+";");
							writer.write(s12h+";");
							writer.write(s24h+";");
							writer.write("\n");

						}
					}

					//writer.write("test");
					writer.flush();

				}
			};

		}


		return Response.ok(stream).header("Content-Disposition", "attachment;filename=Max_Values.csv").build();
	}


	/**
	 * Gets sensors data
	 * @return
	 */
	@GET
	@Path("/exportmodel/{sCode}")
	@Produces({"application/octet-stream"})
	public Response ExportModelHydro(@PathParam("sCode") String sCode, @QueryParam("sRefDate") String sRefDate) {

		System.out.println("StaionsService.ExportStationsListTable: Code = " + sCode);

		//Load model list
		List<SectionBasinsViewModel> aoSections = new ArrayList<SectionBasinsViewModel>();
		StreamingOutput stream = null;
		try {			

			// Get Config
			Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");
			if (oConfObj != null)  {

				// Cast Config
				OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;

				ArrayList<HydroModelLinkConfig> oHydroModelList = oConfig.getHydroModelLinks();

				for (HydroModelLinkConfig hydroModelLinkConfig : oHydroModelList) {
					if (hydroModelLinkConfig.getLinkCode().equals(sCode))
					{
						// Date: will be received from client...
						Date oDate = new Date();

						if (sRefDate!=null)
						{
							if (sRefDate.equals("") == false) 
							{
								// Try e catch per fare il parsing 
								// se � valido sostituire oDate.
								SimpleDateFormat dtFormat = new SimpleDateFormat(Omirl.s_sDateHeaderFormat);
								try {

									oDate = dtFormat.parse(sRefDate);

								} catch (ParseException e) {
									e.printStackTrace();
								}
							}
						}

						// Find the right Sensor Link Configuration
						for (HydroModelLinkConfig oLinkConfig : oConfig.getHydroModelLinks()) {

							if (oLinkConfig.getLinkCode().equals(sCode)) {

								System.out.println("SectionsService.GetSection: Section Code Config Found");

								// Get The path of the right date
								String sPath = Omirl.getSubPath(oLinkConfig.getFilePath(), oDate);

								if (sPath != null) {

									//sPath = sPath + "/features";

									System.out.println("TablesService.GetSectionBasinsList: searching path " + sPath);

									// Get The Last File
									File oLastFile = Omirl.lastFileModified(sPath, oDate);

									// Found?
									if (oLastFile != null) {

										System.out.println("SectionsService.GetSection: Opening File " + oLastFile.getAbsolutePath());

										try {
											// Ok read sections 
											aoSections = (List<SectionBasinsViewModel>) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());

											final List<SectionBasinsViewModel> oEndList =aoSections; 
											if (oEndList != null)
											{
												stream = new StreamingOutput() {
													@Override
													public void write(OutputStream os) throws IOException, WebApplicationException {
														Writer writer = new BufferedWriter(new OutputStreamWriter(os));

														writer.write("Basin;Section;\n");


														for (int iTableRows=0; iTableRows<oEndList.size(); iTableRows++) {

															if (oEndList.size() > 0)
																writer.write(oEndList.get(iTableRows).getBasinName()+";");

															List<SectionBasinsCodesViewModel> oColumns = oEndList.get(iTableRows).getSectionBasinsCodes();
															for (int iColumn=0; iColumn<oColumns.size(); iColumn++) {

																writer.write(oColumns.get(iColumn).getSectionName()+";");
															}

															writer.write("\n");
															//writer.write("test");
														}

														writer.flush();
													};

												};

											}

										} catch (Exception e) {
											e.printStackTrace();
										}							
									}
								}
								else {
									System.out.println("SectionsService.GetSection: WARNING path is null");
								}

								// We are done
								break;
							}
						}
					}

				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return Response.ok(stream).header("Content-Disposition", "attachment;filename="+sCode+"_List.csv").build();
	}	



}
