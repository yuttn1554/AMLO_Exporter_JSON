import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MainClass { //AMLO_Exporter_JSON
	public static void main(String args[]) {
		try{  
			//String sqlwhere=" and rownum<20 "; 
			String sqlwhere=" "; 
			//config.ini----------------------------------------------------
			//InputStream ips = new FileInputStream("config.ini");
			InputStream ips = new FileInputStream("config.ini");
			Properties pfile = new Properties();			
			pfile.load(ips);   
			String searchby=pfile.getProperty("searchby");
			System.out.println("searchby :"+searchby);
			//----------------------------------------------------
			Calendar cal = GregorianCalendar.getInstance(); 
			cal.add(Calendar.HOUR, 11);
			cal.add(Calendar.DATE, -1);
			SimpleDateFormat spdate =new SimpleDateFormat("yyyyMMdd");
			String logdate =spdate.format(cal.getTime()); 
			SimpleDateFormat spdatetime =new SimpleDateFormat("yyyyMMddHHmm");
			String logdatetime =spdatetime.format(cal.getTime()); 
			//-----------------------------------------------------------------------------
			String FileName = ""; 
			String FileNamePDF = ""; 
			String savelocation = "/home/amlo/OUT/"+"JSON"+logdate+"";
			File f = new File(savelocation);
			if(!f.exists()) { f.mkdirs(); } 
			//-----------------------------------------------------------------------------
			//String writerFileName =savelocation+"AMLO-"+logdatetime+".json";
			String writerFileName ="";
			if(searchby.contains("0")||searchby=="0") { 
				  sqlwhere= sqlwhere+" AND (cc.createddate between to_date('"+logdate+"'||'0000','YYYYMMDDHH24MI')-(7/24) AND to_date('"+logdate+"'||'2359','YYYYMMDDHH24MI')-(7/24))  " ; 
				} else
			if(searchby.contains("1")||searchby=="1") {
				String caseno=pfile.getProperty("caseno");
				String caseyear=pfile.getProperty("caseyear");
				String orgcode=pfile.getProperty("orgcode");
				  sqlwhere= sqlwhere+" AND cc.crimecaseno='"+caseno+"' "
							+ " AND cc.crimecaseyear='"+caseyear+"' "
							+ " AND ps.orgcode='"+orgcode+"' "; 
				} else
			if(searchby.contains("2")||searchby=="2") {
				String startdate=pfile.getProperty("startdate");
				String enddate=pfile.getProperty("enddate");
				String orgcode=pfile.getProperty("orgcode");
				  sqlwhere= sqlwhere+" AND (cc.createddate between to_date('"+startdate+"'||'0000','YYYYMMDDHH24MI')-(7/24) AND to_date('"+enddate+"'||'2359','YYYYMMDDHH24MI')-(7/24)) "
				  		+ " AND ps.orgcode='"+orgcode+"' " ; 
				} else {
				  sqlwhere= sqlwhere+" AND (cc.createddate between to_date('"+logdate+"'||'0000','YYYYMMDDHH24MI')-(7/24) AND to_date('"+logdate+"'||'2359','YYYYMMDDHH24MI')-(7/24))  " ; 
				}
			//=========================== 
			Connection c = ConnectCrime(); 
			String sqlgetccid = "  " ;
			//-----------------------------------------------------------------------------
			Statement stmtGet = c.createStatement();
			Statement stmtCase = c.createStatement();
			Statement stmtCharge = c.createStatement();
			Statement stmtEvidance = c.createStatement();
			Statement stmtExhibit = c.createStatement();
			Statement stmtSuspect = c.createStatement();
			Statement stmtupd = c.createStatement();  
			//AMLO============================================================================  
			JSONArray arrCase1 = new JSONArray(); 
			sqlgetccid=SQLQUERY( sqlwhere, " AND bdlc.groupid in ('7')" );
			ResultSet rsGetID1= stmtGet.executeQuery(sqlgetccid);
			int countrow1=0;
			String CCID1="";  FileNamePDF="/home/amlo/OUT/"+logdate+""; 
			//----------------------------------------------------
			while(rsGetID1.next()) { countrow1++;
			 	CCID1=rsGetID1.getString("ccid");  
				FileName = "AMLO-"+rsGetID1.getString("orgcode")+"-"+rsGetID1.getString("initialname")+"-"+rsGetID1.getString("crimecaseyear")+"-"+rsGetID1.getString("crimecaseno");
				FileNamePDF=FileNamePDF+"/"+FileName+".pdf";
			 //------------------------------------------------------------- 
				JSONObject objCase=new JSONObject();  
				objCase=getCrimeByID(stmtCase, stmtCharge, stmtExhibit, stmtSuspect, CCID1, FileNamePDF); arrCase1.add(objCase);  
			 //-------------------------------------------------------------       
			}
			rsGetID1.close(); writerFileName =savelocation+"/AMLO-"+logdatetime+".json"; 
			Calendar cal_End1 = GregorianCalendar.getInstance();   String logdate_End1 =spdatetime.format(cal_End1.getTime());  
			System.out.println("logdate_End >"+logdate_End1);   System.out.println("countrow>"+countrow1);
			writerJson(  writerFileName,  arrCase1) ;  System.out.println("writerFileName>"+writerFileName);  
			//AMLO============================================================================END 
			//AMLO BASIC============================================================================AMLO BASIC	
			JSONArray arrCase2 = new JSONArray(); 
			sqlgetccid=SQLQUERY( sqlwhere, " AND bdlc.groupid in ('9')" );
			ResultSet rsGetID2= stmtGet.executeQuery(sqlgetccid);
			int countrow2=0;
			String CCID2="";  FileNamePDF="/home/amlo/OUT+/"+"AMLOBASIS"+logdate+"";
			//----------------------------------------------------
			while(rsGetID2.next()) { countrow2++;
				CCID2=rsGetID2.getString("ccid");  
				FileName = "AMLO-"+rsGetID2.getString("orgcode")+"-"+rsGetID2.getString("initialname")+"-"+rsGetID2.getString("crimecaseyear")+"-"+rsGetID2.getString("crimecaseno");
				FileNamePDF=FileNamePDF+"/"+FileName+".pdf";
			 //------------------------------------------------------------- 
				JSONObject objCase=new JSONObject();  
				objCase=getCrimeByID(stmtCase, stmtCharge, stmtExhibit, stmtSuspect, CCID2, FileNamePDF); arrCase2.add(objCase);  
			 //-------------------------------------------------------------       
			}
			rsGetID2.close(); writerFileName =savelocation+"/AMLOBASIS-"+logdatetime+".json"; 
			Calendar cal_End2 = GregorianCalendar.getInstance();   String logdate_End2 =spdatetime.format(cal_End2.getTime());  
			System.out.println("logdate_End >"+logdate_End2);   System.out.println("countrow>"+countrow2);
			writerJson(  writerFileName,  arrCase2) ;  System.out.println("writerFileName>"+writerFileName);  
			//AMLO BASIC============================================================================
         	System.out.println("JSON file SUCCESS:"+writerFileName+"|End Time:"+logdate_End2);  
			stmtGet.close();
			stmtCase.close(); 
			stmtCharge.close();
			stmtEvidance.close();
			stmtExhibit.close();
			stmtSuspect.close();
			stmtupd.close();
			c.close(); 
			//-----------------------------------------------------------------------------
			
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	} 

	public static Boolean UpdCaseStatus(Statement stmtCase,String id) { 
	      try {
				Calendar cal = GregorianCalendar.getInstance(); 
				cal.add(Calendar.HOUR, 4);
				SimpleDateFormat spdate =new SimpleDateFormat("yyyyMMddHHmm",Locale.US);
				String logdatetime =spdate.format(cal.getTime()); 
	    	  String UPDSTR="Update CRIMECASE$EXTERNALSUBMIT set statusucceed='completed', datesucceed=to_date('"+logdatetime+"','YYYYMMDDHH24MI') where id='"+id+"' ";
	    	  stmtCase.executeQuery(UPDSTR);
	    	  System.out.println("UPDSTR:"+UPDSTR);
	 			return true;
	      } catch (SQLException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
		 	 return false;
	      }  
	}
	public static Boolean writerJson(String FileName,JSONArray arr) { 
	      try {
	    	  FileWriter file = new FileWriter(FileName);
	    	  	file.write(arr.toJSONString());
	         	file.close(); 
	         	System.out.println("JSON file created: "+FileName);
	 			return true;
	      } catch (IOException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
		 	 return false;
	      }  
	}
	public static Connection ConnectCrime( ) throws Exception { 
		Class.forName("oracle.jdbc.OracleDriver");				
		//String IP ="172.17.3.99";		
		String IP ="172.17.3.102"; 
		String Port ="1521";
		String ServiceName ="crimes";
		String USER ="crimesentry";
		String PASS ="crimesentry123";		    		   
		String jdbc = "jdbc:oracle:thin:@"+IP+":"+Port+"/"+ServiceName+""; //connect to land transport
		Properties p = new Properties();
		p.put("user",USER );
		p.put("password",PASS);
		Connection c = DriverManager.getConnection(jdbc,p); 
	    return c; 
	} 
	public static Connection ConnectC2P( ) throws Exception { 
		Class.forName("oracle.jdbc.OracleDriver");				
		String IP ="172.17.3.102";
		String Port ="1521";
		String ServiceName ="crimes";
		String USER ="crimes2polis";
		String PASS ="crimes2polis123";		    		   
		String jdbc = "jdbc:oracle:thin:@"+IP+":"+Port+"/"+ServiceName+""; //connect to land transport
		Properties p = new Properties();
		p.put("user",USER );
		p.put("password",PASS);
		Connection c = DriverManager.getConnection(jdbc,p); 
	    return c; 
	} 

	public static String SQLQUERY(String sqlwhere,String Wheregroupid ) throws Exception { 


		String sqlgetccid = " SELECT  cc.id ccid,cc.crimecaseno,cc.crimecaseyear,ps.orgcode,ps.initialname   " + 
				" FROM  Crimecase$crimecase Cc         " + 
				" LEFT JOIN Crimecas$crimecase_Policestati Ccps ON Cc.Id=Ccps.Crimecase$crimecaseid   " + 
				" LEFT JOIN  Administration$policestation Ps ON Ps.Id=Ccps.Administration$policestationid     " + 
				" LEFT JOIN People$person_Crimecase Percc ON Cc.Id=Percc.Crimecase$crimecaseid     " + 
				" LEFT JOIN  People$person Per ON Percc.People$personid=Per.Id    " + 
				" LEFT JOIN people$chargeprefix_person Perpf ON Per.Id=Perpf.People$personid    " + 
				" LEFT JOIN  People$chargeprefix Cpf ON Perpf.People$chargeprefixid=Cpf.Id  " + 
				" LEFT JOIN People$chargeprefix_Charge Cpfcr ON Cpf.Id=Cpfcr.People$chargeprefixid   " + 
				" LEFT JOIN  Basedata$charge Bc ON Cpfcr.Basedata$chargeid=Bc.Id   " + 
				" LEFT JOIN BASEDAT$CHARGE_REPORTGROUPCHAR bls ON bc.id=bls.basedata$chargeid  " + 
				" LEFT JOIN BASEDATA$REPORTGROUPCHARGE blsc ON bls.BASEDATA$REPORTGROUPCHARGEID=blsc.id  " + 
				" LEFT JOIN BASED$REPORTGROUP_REPORTGROUPC blscl ON blsc.id=blscl.BASEDATA$REPORTGROUPCHARGEID  " + 
				" LEFT JOIN BASEDATA$GROUPCHARGE bdlc ON blscl.BASEDATA$GROUPCHARGEID=bdlc.id    " + 
				" WHERE ps.istakethecase='1' AND cc.crimecaseno is not null AND cc.crimecaseyear is not null     " +  
				sqlwhere+Wheregroupid+ // AND bdlc.groupid in ('7','9')    
				" Group by cc.id,cc.crimecaseno,cc.crimecaseyear,ps.orgcode,ps.initialname " ;  

		System.out.println("sqlgetcase:"+sqlgetccid);
//		String sqlgetccid = " SELECT  cc.id ccid,cc.crimecaseno,cc.crimecaseyear,ps.orgcode,ps.initialname,max(ex.id) exid   "
//				+ " FROM  Crimecase$crimecase Cc       "
//				+ " LEFT JOIN Crimecas$crimecase_Policestati Ccps ON Cc.Id=Ccps.Crimecase$crimecaseid    LEFT JOIN  Administration$policestation Ps ON Ps.Id=Ccps.Administration$policestationid  "
//				+ " LEFT JOIN CRIMECASE$CRIMECASE_GROUPCHARG CcGc ON Cc.Id=CcGc.Crimecase$crimecaseid    LEFT JOIN BASEDATA$GROUPCHARGE bdlc ON CcGc.BASEDATA$GROUPCHARGEID=bdlc.id   "
//				+ " LEFT JOIN CRIMECAS$EXTERNALSUBM_CRIMECAS Ccex ON Cc.Id=Ccex.Crimecase$crimecaseid    LEFT JOIN CRIMECASE$EXTERNALSUBMIT ex ON Ccex.CRIMECASE$EXTERNALSUBMITID=ex.id     "
//				+ " WHERE ps.istakethecase='1' AND cc.crimecaseno is not null "
//				+ " AND cc.crimecaseyear is not null AND bdlc.groupid in ('7','9')  "
//				+ " and ex.statusucceed='approve' AND ex.datemasterconfirm between ((sysdate)-(31/24)) AND ((sysdate)-(7/24))  "
//				+ " Group by cc.id,cc.crimecaseno,cc.crimecaseyear,ps.orgcode,ps.initialname " ; 
	    return sqlgetccid; 
	} 
	public static JSONObject getCrimeByID( Statement stmtCase, Statement stmtCharge, Statement stmtExhibit, Statement stmtSuspect,String CCID,String FileName ) {
		  String result = "";
		  JSONObject objCase=new JSONObject();    
		  try {
				objCase.put("filename",FileName);  
				String sqlgetCase = " Select cc.id ccid,cc.caseno,cc.crimecaseno,cc.crimecaseyear,substr(cc.behaviorofcrimecase,0,2000)  behavior "
						+ " ,substr(cc.display_chargemain,0,2000) display_chargemain ,case when cc.statusmagenta='Magenta_inActive' then 'จำหน่ายคดี' else 'อยู่ระหว่างดำเนินการ' end statusmagenta,cc.statusmagenta_iv "
						+ " ,ps.orgcode,ps.name,ps.telephonenumber psphone ,po.fullname pofullname , "
						+ " hpo.headposition||' '||ps.initialname|| ' '||ps.bk||' '||ps.bh HEADPOS_ORG "
						+ " ,to_char(cc.caseacceptdate+(7/24),'YYYYMMDDHH24MI','nls_calendar=''Thai Buddha'' nls_date_language = Thai') AcDate"
						+ " ,to_char(cc.OccuredDateTimeFrom+(7/24),'YYYYMMDDHH24MI','nls_calendar=''Thai Buddha'' nls_date_language = Thai') OcDate"
						+ " ,to_char(cc.caseacceptdate+(7/24),'dd','nls_calendar=''Thai Buddha'' nls_date_language = Thai') AcDateD "
						+ " ,to_char(cc.caseacceptdate+(7/24),'Month','nls_calendar=''Thai Buddha'' nls_date_language = Thai') AcDateM "
						+ " ,to_char(cc.caseacceptdate+(7/24),'YYYY','nls_calendar=''Thai Buddha'' nls_date_language = Thai') AcDateY "
						+ " ,to_char(cc.caseacceptdate+(7/24),'HH24:MI','nls_calendar=''Thai Buddha'' nls_date_language = Thai') AcDateH  "
						+ " ,susarr.StatusSuspect,po.firstname,po.surname ,po.MobilePhoneOTP POMobilePhone"
						+ " ,case when po.FlagHelp ='1' then po.PositionNameTh_POLIS||'('|| Duty_POLIS||')' else po.pos_org end pos_org  "
						+ " ,rk.rankabbreviationth,pos.positionnameth "
						+ " ,hpo.headpo,hpo.headfirstname,hpo.headsurname,hpo.headrank,hpo.headposition "
						+ " ,to_char(sysdate,'DD','nls_calendar=''Thai Buddha'' nls_date_language = Thai') ||' '|| "
						+ "    trim(to_char(sysdate,'MONTH','nls_calendar=''Thai Buddha'' nls_date_language = Thai') )||' '|| "
						+ "    to_char(sysdate,'YYYY','nls_calendar=''Thai Buddha'' nls_date_language = Thai') NowDMY "
						+ " ,( "
						+ " (case when CC.crimelocation is not null then 'เหตุเกิดที่'||to_char(cc.crimelocation) else '' end ) "
						+ " ||(case when CC.buildingnamelocation is not null then ' ประเภท'||to_char(cc.buildingnamelocation) else '' end ) "
						+ " ||(case when CC.crimelocationnumber is not null then ' ชั้น-เลขที่ '||to_char(cc.crimelocationnumber) else '' end ) "
						+ " ||(case when CC.CrimeMooLocation is not null then ' หมู่ '||to_char(CC.CrimeMooLocation) else '' end ) "
						+ " ||(case when cc.crimelocationseparate is not null then ' แยก '||to_char(cc.crimelocationseparate) else '' end ) "
						+ " ||(case when CC.CrimeLocationLane is not null then ' ซอย '||to_char(CC.CrimeLocationLane) else '' end ) "
						+ " ||(case when CC.CrimeLocationRoad is not null then ' ถนน '||to_char(CC.CrimeLocationRoad) else '' end ) "
						+ " ||(case when cc.crimelocationkilometers is not null then ' กม. '||to_char(cc.crimelocationkilometers) else '' end ) "
						+ " ||(case when tam.name is not null then ' ตำบล/แขวง '||to_char(tam.name) else '' end ) "
						+ " ||(case when amp.name is not null then ' อำเภอ/เขต '||to_char(amp.name) else '' end ) "
						+ " ||(case when pov.name is not null then ' จังหวัด '||to_char(pov.name) else '' end ) "
						+ " ) as crimelocation"
						+ " From  Crimecase$crimecase Cc      "
						+ " Left join (  "
						+ "    Select per_cc.crimecase$crimecaseid ccid , max(case when per.StatusSuspectArrest='KnowAndArrest'  then 2 else 1 end) StatusSuspect  "
						+ "    from  PEOPLE$PERSON_CRIMECASE per_cc     "
						+ "    Left join PEOPLE$PERSON per On per_cc.People$personid=per.Id "
						+ "    where per.statusvictimorsuspect='Suspect' and per.StatusSuspectArrest='KnowAndArrest' "
						+ "    group by per_cc.crimecase$crimecaseid "
						+ " ) susarr on cc.id=susarr.ccid "
						+ " Left Join crimecas$crimecase_policeoffic Ccpo On Cc.Id=Ccpo.Crimecase$crimecaseid   "
						+ " Left Join  administration$policeofficer Po On Ccpo.ADMINISTRATION$POLICEOFFICERID=Po.id    "
						+ " left outer join ADMINISTRATI$POLICEOFFICE_RANK polrk on polrk.administration$policeofficerid=Po.id "
						+ " left outer join administration$rank rk on polrk.administration$rankid=rk.id "
						+ " left outer join ADMINISTRAT$POLICEOFFI_POSITIO polpos on polpos.administration$policeofficerid=Po.id "
						+ " left outer join administration$position pos on polpos.administration$positionid=pos.id  " 
						+ " Left Join Crimecas$crimecase_Policestati Ccps On Cc.Id=Ccps.Crimecase$crimecaseid   "
						+ " Left Join  Administration$policestation Ps On Ps.Id=Ccps.Administration$policestationid     "
						+ "Left Join crimecase$crimecase_tambon Cctam On Cc.Id=Cctam.Crimecase$crimecaseid   Left Join  basedata$tambon tam On Cctam.basedata$tambonid=tam.Id "
						+ "Left Join crimecase$crimecase_amphur Ccamp On Cc.Id=Ccamp.Crimecase$crimecaseid   Left Join  basedata$amphur amp On ccamp.basedata$amphurid=amp.Id "
						+ "Left Join crimecase$crimecase_province  CCpov On Cc.Id=CCpov.Crimecase$crimecaseid   Left Join  basedata$province pov On CCpov.basedata$provinceid=pov.Id " 
						+ " left join ( "
						+ "    select pshpo.Administration$policestationid psid,hpo.fullname headpo, "
						+ "    rk.rankabbreviationth headrank, "
						+ "    pos.positionnameth headposition, "
						+ "    hpo.pos_org headpos_org, "
						+ "    hpo.firstname headfirstname, "
						+ "    hpo.surname headsurname "
						+ "    from ADMINISTR$POLICEOFF_POLICESTAT pshpo  "
						+ "    Left Join  administration$policeofficer hPo On pshpo.ADMINISTRATION$POLICEOFFICERID=hPo.id  "
						+ "    left outer join ADMINISTRATI$POLICEOFFICE_RANK polrk on polrk.administration$policeofficerid=hPo.id "
						+ "    left outer join administration$rank rk on polrk.administration$rankid=rk.id "
						+ "    left outer join ADMINISTRAT$POLICEOFFI_POSITIO polpos on polpos.administration$policeofficerid=hPo.id "
						+ "    left outer join administration$position pos on polpos.administration$positionid=pos.id "
						+ "    left join SYSTEM$USERROLES hporo On hpo.Id=hporo.SYSTEM$USERID "
						+ "    Left Join  SYSTEM$USERROLE ro On hporo.SYSTEM$USERROLEID=ro.id  "
						+ "    where ro.name='Superintendent'   and (pos.positionnameth='ผกก.' or hPo.Duty_POLIS='หัวหน้าสถานี') "
						+ "     group by pshpo.Administration$policestationid ,hpo.fullname , rk.rankabbreviationth , pos.positionnameth ,  hpo.pos_org ,hpo.firstname , hpo.surname "
						+ " ) hpo on ps.id=hpo.psid  " 
						+ " Where cc.id=  '"+CCID+"'  " ;
				//objCase.put("Query Action",sqlgetCase);  
					//----------------------------------------------------
					ResultSet rsCase= stmtCase.executeQuery(sqlgetCase);
					ResultSetMetaData rsmdCase = rsCase.getMetaData();  
					int numColumns = rsmdCase.getColumnCount();
					//----------------------------------------------------
					while(rsCase.next()) { 
						for (int i=1; i<=numColumns; i++) {
							String column_name = rsmdCase.getColumnName(i);
							objCase.put(column_name,rsCase.getString(column_name)); 
						}   
						//EvidenceList-------------------------------------------------------------
						String sqlgetEvidence = "SELECT ps.orgcode,cc.caseno,cc.crimecaseno,cc.crimecaseyear,cc.id as ccid,oew.evidencewitness    "
								+ " FROM asset$otherevidencewitness oew   "
								+ " left join asset$otherevildencewitn_perso oewps on oew.id = oewps.asset$otherevidencewitnessid  left join people$person pps on  oewps.people$personid = pps.id   "
								+ " left join asset$otherevildencewi_isexhib oewaix on oew.id = oewaix.asset$otherevidencewitnessid left join crimecase$isexhibit aix on oewaix.crimecase$isexhibitid=aix.id   "
								+ " left join crimecase$isexhibit_crimecase aixcc on aix.id = aixcc.crimecase$isexhibitid  left join crimecase$crimecase cc on aixcc.crimecase$crimecaseid=cc.id   "
								+ " left join crimecas$crimecase_policestati ccps on cc.id=ccps.crimecase$crimecaseid  left join administration$policestation ps on ccps.administration$policestationid=ps.id    "
								+ " Where cc.id=  '"+CCID+"'  " ;
						//objCase.put("Query Action sqlgetEvidence",sqlgetEvidence); 
						JSONArray arrEvidence = new JSONArray(); 
						ResultSet rsEvidence= stmtCharge.executeQuery(sqlgetEvidence); 
						while(rsEvidence.next()) {
							JSONObject objEvidence=new JSONObject();  
							objEvidence.put("orgcode",rsEvidence.getString("orgcode"));  
							objEvidence.put("crimecaseno",rsEvidence.getString("crimecaseno")); 
							objEvidence.put("crimecaseyear",rsEvidence.getString("crimecaseyear"));  
							objEvidence.put("evidencewitness",rsEvidence.getString("evidencewitness"));  
							arrEvidence.add(objEvidence); 
						}  
						rsEvidence.close();
						objCase.put("WitnessList",arrEvidence); 
						//ExhibitList-------------------------------------------------------------
						String sqlgetExhibit = " select ps.orgcode,cc.caseno,cc.crimecaseno,cc.crimecaseyear,cc.id as ccid "
								+ " ,DEFFNULL(ass.assettypestr) assettypestr ,DEFFNULL(ass.name) as assetname ,DEFFNULL(ass.brand) assetbrand,DEFFNULL(ass.model) as assetmodel,DEFFNULL(ass.amount) amount,DEFFNULL(cun.unitnameth) unitnameth  "
								+ " ,ass.value assetvalue   "
								+ " from asset$asset ass    "
								+ " left join asset$asset_assettype ass_ast on  ass.id=ass_ast.asset$assetid  left join basedata$assettype ast on  ass_ast.basedata$assettypeid=ast.id    "
								+ " left join basedata$assettype_countunit ast_cun on  ast.id=ast_cun.basedata$assettypeid   left join basedata$countunit cun on  ast_cun.basedata$countunitid=cun.id    "
								+ " inner join asset$asset_isexhibit ass_iex on  ass.id=ass_iex.asset$assetid  inner join crimecase$isexhibit iex on  ass_iex.crimecase$isexhibitid=iex.id   "
								+ " inner join crimecase$isexhibit_crimecase iex_cc on  iex.id=iex_cc.crimecase$isexhibitid  inner join crimecase$crimecase cc on  iex_cc.crimecase$crimecaseid=cc.id   "
								+ " inner join crimecas$crimecase_policestati ccps on cc.id=ccps.crimecase$crimecaseid   inner join administration$policestation ps on ccps.administration$policestationid=ps.id   	  "
								+ " where    (ps.orgcode is not null )   and cc.casestatus<>'migrate'  and ( ass.name is not null or ast.assettypeid is not null )  and cc.id = '"+CCID+"'" ;
						JSONArray arrExhibit = new JSONArray(); 
						//objCase.put("Query Action sqlgetExhibit",sqlgetExhibit);  
						ResultSet rsExhibit= stmtExhibit.executeQuery(sqlgetExhibit); 
						while(rsExhibit.next()) {
							JSONObject objExhibit=new JSONObject();  
							objExhibit.put("orgcode",rsExhibit.getString("orgcode"));  
							objExhibit.put("crimecaseno",rsExhibit.getString("crimecaseno")); 
							objExhibit.put("crimecaseyear",rsExhibit.getString("crimecaseyear"));   
							objExhibit.put("assettypestr",rsExhibit.getString("assettypestr")); 
							objExhibit.put("assetname",rsExhibit.getString("assetname")); 
							objExhibit.put("assetbrand",rsExhibit.getString("assetbrand")); 
							objExhibit.put("assetmodel",rsExhibit.getString("assetmodel")); 
							objExhibit.put("amount",rsExhibit.getString("amount")); 
							objExhibit.put("unitnameth",rsExhibit.getString("unitnameth"));  
							objExhibit.put("assetvalue",rsExhibit.getString("assetvalue"));  
							arrExhibit.add(objExhibit); 
						}  
						rsExhibit.close();
						objCase.put("AssetList",arrExhibit); 
						//SuspectList-------------------------------------------------------------
						String sqlgetSuspect =  " Select ps.orgcode,cc.caseno,cc.crimecaseno,cc.crimecaseyear,cc.id as ccid ,DEFFNULL(per.fullnameperson) fullnameperson ,DEFFNULL(per.peopleregistrationid) peopleregistrationid,DEFFNULL(per.mobilenumber) mobilenumber, "
								+ " DEFFNULL(per.addresspresent)  address,nvl(per.HousePresent,'-') as HousePresent,nvl(per.TrokPresent,'-') as TrokPresent , "
								+ " nvl(per.RoadPresent,'-') as RoadPresent ,nvl(per.LanePresent,'-') as LanePresent ,nvl(per.MooPresent,'-') as  MooPresent, "
								+ " nvl(tam.name,'-') as tam,nvl(amp.name,'-') as amp,nvl(prov.name,'-') as prov,nvl(per.passportnumber,'-') as passportnumber,per.age "
								+ " From    People$person_Crimecase Percc "
								+ " inner Join  People$person Per On Percc.People$personid=Per.Id    "
								+ " left outer join PEOPLE$PERSON_AMPHUR_PRESENT peramp on peramp.people$personid = per.id "
								+ " left outer join basedata$amphur amp on peramp.basedata$amphurid=amp.id "
								+ " left outer join PEOPLE$PERSON_TAMBON_PRESENT pertam on pertam.people$personid = per.id "
								+ " left outer join basedata$tambon tam on pertam.basedata$tambonid=tam.id "
								+ " left outer join PEOPLE$PERSON_PROVINCE_PRESENT perprov on perprov.people$personid=per.id "
								+ " left outer join basedata$province prov on perprov.basedata$provinceid=prov.id "
								+ " left outer join PEOPLE$PERSON_RESULTCRIMESCASE resultper on resultper.people$personid=per.id "
								+ " inner join crimecase$crimecase cc on  Percc.crimecase$crimecaseid=cc.id   "
								+ " inner join crimecas$crimecase_policestati ccps on cc.id=ccps.crimecase$crimecaseid  "
								+ " inner join administration$policestation ps on ccps.administration$policestationid=ps.id   	  "
								+ " Where Percc.Crimecase$crimecaseid=  '"+CCID+"' and per.statusvictimorsuspect='Suspect' and (per.firstnameth is not null or per.firstnameen is not null)  " ;
						JSONArray arrSuspect = new JSONArray(); 
						//objCase.put("Query Action sqlgetSuspect",sqlgetSuspect);  
						ResultSet rsSuspect= stmtSuspect.executeQuery(sqlgetSuspect); 
						while(rsSuspect.next()) {
							JSONObject objSuspect=new JSONObject();  
							objSuspect.put("orgcode",rsSuspect.getString("orgcode"));  
							objSuspect.put("crimecaseno",rsSuspect.getString("crimecaseno")); 
							objSuspect.put("crimecaseyear",rsSuspect.getString("crimecaseyear"));  
							objSuspect.put("fullnameperson",rsSuspect.getString("fullnameperson")); 
							objSuspect.put("peopleregistrationid",rsSuspect.getString("peopleregistrationid")); 
							objSuspect.put("mobilenumber",rsSuspect.getString("mobilenumber")); 
							objSuspect.put("address",rsSuspect.getString("address")); 
							objSuspect.put("HousePresent",rsSuspect.getString("HousePresent")); 
							objSuspect.put("TrokPresent",rsSuspect.getString("TrokPresent")); 
							objSuspect.put("RoadPresent",rsSuspect.getString("RoadPresent")); 
							objSuspect.put("LanePresent",rsSuspect.getString("LanePresent")); 
							objSuspect.put("MooPresent",rsSuspect.getString("MooPresent")); 
							objSuspect.put("tam",rsSuspect.getString("tam")); 
							objSuspect.put("amp",rsSuspect.getString("amp")); 
							objSuspect.put("prov",rsSuspect.getString("prov")); 
							objSuspect.put("passportnumber",rsSuspect.getString("passportnumber")); 
							objSuspect.put("age",rsSuspect.getString("age"));  
							arrSuspect.add(objSuspect); 
						}  
						rsSuspect.close();
						objCase.put("SuspectList",arrSuspect); 
						//ChargeList-------------------------------------------------------------
						String sqlgetCharge = " select ps.orgcode,cc.caseno,cc.crimecaseno,cc.crimecaseyear,cc.id as ccid ,substr(ch.chargenameth,0,2000) chargename  "
								+ " from People$person_Crimecase Percc  inner Join People$person per  On Percc.People$personid=per.Id         "
								+ " inner join crimecase$crimecase cc on  Percc.crimecase$crimecaseid=cc.id   "
								+ " inner join crimecas$crimecase_policestati ccps on cc.id=ccps.crimecase$crimecaseid  "
								+ " inner join administration$policestation ps on ccps.administration$policestationid=ps.id   	  "
								+ " inner Join people$chargeprefix_person perpf On per.Id = perpf.People$personid  inner Join people$chargeprefix pf  On perpf.people$chargeprefixid=pf.Id   "
								+ " inner Join people$chargeprefix_charge cfch On pf.Id = cfch.people$chargeprefixid  inner join  basedata$charge ch  on cfch.basedata$chargeid=ch.id     "
								+ " inner join basedata$charge_lawsubcategory bls on ch.id=bls.basedata$chargeid  inner join basedata$lawsubcategory blsc on bls.basedata$lawsubcategoryid=blsc.id   "
								+ " inner join basedat$lawsubcateg_lawcategor blscl on blsc.id=blscl.basedata$lawsubcategoryid  inner join basedata$lawcategory bdlc on blscl.basedata$lawcategoryid=bdlc.id  "
								+ " where bdlc.id ='233342755693135329' and percc.crimecase$crimecaseid= '"+CCID+"' "
								+ " group by  ps.orgcode,cc.caseno,cc.crimecaseno,cc.crimecaseyear,cc.id   , substr(ch.chargenameth,0,2000)    " ;
						JSONArray arrCharge = new JSONArray(); 
						//objCase.put("Query Action sqlgetCharge",sqlgetCharge);  
						ResultSet rsCharge= stmtCharge.executeQuery(sqlgetCharge); 
						while(rsCharge.next()) {
							JSONObject objCharge=new JSONObject();  
							objCharge.put("orgcode",rsCharge.getString("orgcode"));  
							objCharge.put("crimecaseno",rsCharge.getString("crimecaseno")); 
							objCharge.put("crimecaseyear",rsCharge.getString("crimecaseyear"));  
							objCharge.put("chargename",rsCharge.getString("chargename")); 
							arrCharge.add(objCharge); 
						}  
						rsCharge.close();
						objCase.put("ChargeList",arrCharge); 
					}
					rsCase.close();
				//------------------------------------------------------------- 
	   return objCase;
	  } catch (Exception e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	   return objCase;
	  }

	 }

}
