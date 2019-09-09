package esp.etc.excel.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class ParserUtils {
    
    final static boolean logFlag = false;
    
    final static String STR_EMPTY             = "";
    final static String STR_BLANK             = " ";
    final static String STR_TAB               = "\t";
    final static String STR_DQ                = "\"";
    final static String STR_SQ                = "'";
    final static String STR_COMMA             = ",";
    final static String STR_COLON             = ":";
    final static String STR_SEMICOLON         = ";";
    final static String STR_ROUND_BRACKETS_L  = "(";
    final static String STR_ROUND_BRACKETS_R  = ")";
    final static String STR_CURLY_BRACKETS_L  = "{";
    final static String STR_CURLY_BRACKETS_R  = "}";
    final static String STR_SQUARE_BRACKETS_L = "[";
    final static String STR_SQUARE_BRACKETS_R = "]";
    
    // +------------------------------+
    // script header info
    // +------------------------------+
    final static String SH_PROGRAM_ID   = "@programid";
    final static String SH_PROGRAM_NAME = "@programname";
    final static String SH_CREATOR      = "@creator";
    final static String SH_CREATED_DATE = "@createddate";
    final static String SH_DESCRIPTION  = "@description";
    final static String SH_ONLOAD_SETUP = "@onloadsetup";
    
    // +------------------------------+
    // function header info
    // +------------------------------+
    final static String FH_FUNCTION_NAME = "@functionname";
    final static String FH_DESCRIPTION   = "@description";
    final static String FH_PARAM         = "@param";
    final static String FH_RETURN        = "@return";
           
    // +------------------------------+
    // function detail objDatas info
    // +------------------------------+
    final static String FD_OBJ_DATAS   = "varobjdatas";
    final static String FD_SVCID       = "svcid";       
    final static String FD_SCONTROLLER = "sController"; 
    final static String FD_INDS        = "inds";
    final static String FD_OUTDS       = "outds";
      
    // +------------------------------+
    // function detail popup info
    // +------------------------------+
    final static String FD_OBJ_ARGUMENT  = "varobjargument";
    final static String FD_OBJ_POPUPDATA = "varobjpopupdata";
    final static String FD_POPUPID       = "popupid"; 
    final static String FD_FORMURL       = "formurl"; 
    final static String FD_OARGS         = "oArgs";   
    
    /**
     * File Contents Parsing
     * @param filePath
     * @return String File Contents
     * @throws Exception
     */
    @SuppressWarnings({"unused" })
    public static String getFileContents(String filePath) throws Exception {
        File file = null;
        BufferedReader br = null;
        String readLine = "";
        StringBuffer sb = new StringBuffer();
        try {
            file = new File(filePath);
            br = new BufferedReader(new FileReader(file));
            while( (readLine = br.readLine()) != null ) {
                sb.append(br.readLine());
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                br.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    /**
     * Get Dataset id List String
     * @param filePath
     * @return String Dataset List
     * @throws Exception
     */
    public static String getDatasetIdList(String filePath) throws Exception {
        
        // XML Document 객체 생성
        /*
        InputSource is = new InputSource(new StringReader(uiXML));
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        */
        
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath));
        
        // xpath 생성
        XPath xpath = XPathFactory.newInstance().newXPath();
         
        StringBuffer datasetListSB = new StringBuffer();
        
        // NodeList 가져오기 : row 아래에 있는 모든 col1 을 선택
        NodeList cols = (NodeList)xpath.evaluate("//Objects/Dataset", document, XPathConstants.NODESET);
        for( int idx=0; idx<cols.getLength(); idx++ ){
            
            if (logFlag) {
                System.out.println(cols.item(idx).getNodeName());   // Dataset
                System.out.println(cols.item(idx).getAttributes().getNamedItem("id").getNodeName());    // id
                System.out.println(cols.item(idx).getAttributes().getNamedItem("id").getNodeValue());   // ds_list
                System.out.println(cols.item(idx).getAttributes().getNamedItem("id").getTextContent()); // ds_list
            }
            
            datasetListSB.append(cols.item(idx).getAttributes().getNamedItem("id").getNodeValue());
            if (idx<cols.getLength()-1) {
                datasetListSB.append(", ");
//                datasetListSB.append("\n");
            }
        }
        
        return datasetListSB.toString();
    }
    
    /**
     * Script Contents Parsing
     * @param filePath
     * @return String Script Contents
     * @throws Exception
     */
    public static String getScriptContents(String filePath) throws Exception {
        File file = null;
        BufferedReader br = null;
        String readLine = "";
        StringBuffer sb = new StringBuffer();
        boolean scriptFlag = false;
        try {
            file = new File(filePath);
            br = new BufferedReader(new FileReader(file));
            while( (readLine = br.readLine()) != null ) {
                if ( readLine.indexOf("<Script") > -1 ) {
                    scriptFlag = true;
                } 
                if ( scriptFlag ) {
                    sb.append(readLine);
                    sb.append("\n");
                }
                if ( readLine.indexOf("</Script>") > -1 ) {
                    scriptFlag = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                br.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    /**
     * Script Header Parsing
     * @param scriptHeaderString
     * @return
     * @throws Exception
     */
    public static Map<String, String> getScriptHeaderInfo(String scriptHeaderString) throws Exception {
        
        InputStream is = new ByteArrayInputStream(scriptHeaderString.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String    readLine = "";
        String tmpReadLine = "";
        String currentKey  = "";
        
        Map<String, String> scriptHeaderMap = new HashMap<String, String>();
        
        while( (readLine = br.readLine()) != null ) {
            tmpReadLine = readLine.replaceAll(STR_BLANK, STR_EMPTY).replaceAll(STR_TAB, STR_EMPTY).toLowerCase();
            if ( tmpReadLine.indexOf(SH_PROGRAM_ID) > -1 ) {
                if ( tmpReadLine.indexOf(SH_PROGRAM_ID+":") > -1 ) {
                    currentKey = "program_id";
                    scriptHeaderMap.put(currentKey, readLine.substring(readLine.indexOf(":")+1, readLine.length()).trim());
                } else {
                    currentKey = "program_id";
                    scriptHeaderMap.put(currentKey, readLine.substring(readLine.indexOf("Program ID")+11, readLine.length()).trim());
                }
            }
            else if ( tmpReadLine.indexOf(SH_PROGRAM_NAME) > -1 ) {
                if ( tmpReadLine.indexOf(SH_PROGRAM_NAME+":") > -1 ) {
                    currentKey = "program_name";
                    scriptHeaderMap.put(currentKey, readLine.substring(readLine.indexOf(":")+1, readLine.length()).trim());
                } else {
                    currentKey = "program_name";
                    scriptHeaderMap.put(currentKey, readLine.substring(readLine.indexOf("Program Name")+13, readLine.length()).trim());
                }
            }
            else if ( tmpReadLine.indexOf(SH_CREATOR) > -1 ) {
                if ( tmpReadLine.indexOf(SH_CREATOR+":") > -1 ) {
                    currentKey = "creator";
                    scriptHeaderMap.put(currentKey, readLine.substring(readLine.indexOf(":")+1, readLine.length()).trim());
                } else {
                    currentKey = "program_name";
                    scriptHeaderMap.put(currentKey, readLine.substring(readLine.indexOf("Creator")+8, readLine.length()).trim());
                }
            }
            else if ( tmpReadLine.indexOf(SH_CREATED_DATE) > -1 ) {
                if ( tmpReadLine.indexOf(SH_CREATED_DATE+":") > -1 ) {
                    currentKey = "created_date";
                    scriptHeaderMap.put(currentKey, readLine.substring(readLine.indexOf(":")+1, readLine.length()).trim());
                } else {
                    currentKey = "created_date";
                    scriptHeaderMap.put(currentKey, readLine.substring(readLine.indexOf("Created Date")+13, readLine.length()).trim());
                }
            }
            else if ( tmpReadLine.indexOf(SH_DESCRIPTION) > -1 ) {
                if ( tmpReadLine.indexOf(SH_DESCRIPTION+":") > -1 ) {
                    currentKey = "description";
                    scriptHeaderMap.put(currentKey, readLine.substring(readLine.indexOf(":")+1, readLine.length()).trim());
                } else {
                    currentKey = "description";
                    scriptHeaderMap.put(currentKey, readLine.substring(readLine.indexOf("Description")+12, readLine.length()).trim());
                }
            }
            else if ( tmpReadLine.indexOf(SH_ONLOAD_SETUP) > -1 ) {
                if ( tmpReadLine.indexOf(SH_ONLOAD_SETUP+":") > -1 ) {
                    currentKey = "onload_setup";
                    scriptHeaderMap.put(currentKey, readLine.substring(readLine.indexOf(":")+1, readLine.length()).trim());
                } else {
                    currentKey = "onload_setup";
                    scriptHeaderMap.put(currentKey, readLine.substring(readLine.indexOf("OnLoad Setup")+13, readLine.length()).trim());
                }
            } 
            else if ( tmpReadLine.indexOf("==========") > -1 ) {
                break;
            }
            else {
                if ( StringUtils.isNotEmpty(currentKey) ) {
                    scriptHeaderMap.put(currentKey, scriptHeaderMap.get(currentKey)+"\n"+readLine.replace("*", STR_EMPTY).trim());
                }
            }
        }
        
        if (logFlag) {
            System.out.println("\n=======================================================");
            System.out.println("::: ParserUtils Class > getScriptHeaderInfo method :::");
            System.out.println("=======================================================");
            System.out.println(">>> scriptHeaderMap <<< \n" +scriptHeaderMap);
            System.out.println("=======================================================\n");
        }

        return scriptHeaderMap;
    }
    
    /**
     * Script Function Parsing
     * @param scriptFunctionString
     * @return
     * @throws Exception
     */
    public static List<Map<String, String>> getScriptFunctionInfo(String scriptFunctionString) throws Exception {
     
        InputStream is = new ByteArrayInputStream(scriptFunctionString.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        
        String readLine = "";
        String tmpReadLine = "";
        String currentFunctionName = "";
        String currentKey = "";
        
        StringBuffer     tmpSB      = new StringBuffer();
        
        Map<String, String>       scriptFunctionInfoMap     = new HashMap<String, String>();
        List<Map<String, String>> scriptFunctionInfoMapList = new ArrayList<Map<String, String>>();
        
        boolean        startFlag = false;
        boolean     objDatasFlag = false;
        boolean  objArgumentFlag = false;
        boolean objPopupDataFlag = false;
        
        while( (readLine = br.readLine()) != null ) {
            
            tmpReadLine = readLine.replaceAll(STR_BLANK, STR_EMPTY).replaceAll(STR_TAB, STR_EMPTY).toLowerCase();
            
            // +--------------------------+
            //   Function Header Parsing
            // +--------------------------+
            
            // +------------------+
            //   @Function Name
            // +------------------+
            if ( tmpReadLine.indexOf(FH_FUNCTION_NAME) > -1 ) {
                startFlag = true;
                if ( tmpReadLine.indexOf(FH_FUNCTION_NAME+":") > -1 ) {
                    currentKey = "function_name";
                    scriptFunctionInfoMap.put(currentKey, readLine.substring(readLine.indexOf(":")+1, readLine.length()).trim());
                } else {
                    currentKey = "function_name";
                    scriptFunctionInfoMap.put(currentKey, readLine.substring(readLine.indexOf("Name")+5, readLine.length()).trim());
                }
                currentFunctionName = scriptFunctionInfoMap.get(currentKey);
            }
            // +---------------+
            //   @Description
            // +---------------+
            else if ( tmpReadLine.indexOf(SH_DESCRIPTION) > -1 ) {
                startFlag = true;
                if ( tmpReadLine.indexOf(SH_DESCRIPTION+":") > -1 ) {
                    currentKey = "description";
                    scriptFunctionInfoMap.put(currentKey, readLine.substring(readLine.indexOf(":")+1, readLine.length()).trim());
                } else {
                    currentKey = "description";
                    scriptFunctionInfoMap.put(currentKey, readLine.substring(readLine.indexOf("description")+12, readLine.length()).trim());
                }
            }
            // +---------+
            //   @param
            // +---------+
            else if ( tmpReadLine.indexOf(FH_PARAM) > -1 ) {
                startFlag = true;
                if ( tmpReadLine.indexOf(FH_PARAM+":") > -1 ) {
                    currentKey = "param";
                    scriptFunctionInfoMap.put(currentKey, readLine.substring(readLine.indexOf(":")+1, readLine.length()).trim());
                } else {
                    currentKey = "param";
                    scriptFunctionInfoMap.put(currentKey, readLine.substring(readLine.indexOf("param")+6, readLine.length()).trim());
                }
            }
            // +----------+
            //   @return
            // +----------+
            else if ( tmpReadLine.indexOf(FH_RETURN) > -1 ) {
                startFlag = true;
                if ( tmpReadLine.indexOf(FH_RETURN+":") > -1 ) {
                    currentKey = "return";
                    scriptFunctionInfoMap.put(currentKey, readLine.substring(readLine.indexOf(":")+1, readLine.length()).trim());
                } else {
                    currentKey = "return";
                    scriptFunctionInfoMap.put(currentKey, readLine.substring(readLine.indexOf("return")+6, readLine.length()).trim());
                }
            }
            else if ( startFlag && tmpReadLine.indexOf("*/") > -1 ) {
                startFlag = false;
                if ( null != scriptFunctionInfoMap.get("function_name") && StringUtils.isNotEmpty(scriptFunctionInfoMap.get("function_name"))) {
                    scriptFunctionInfoMapList.add(scriptFunctionInfoMap);
                    scriptFunctionInfoMap = new HashMap<String, String>();
                }
            }
            
            if ( StringUtils.isNotEmpty(currentFunctionName) && !"form_onload".equals(currentFunctionName)) {
                
                // +-------------------+
                //   objDatas Parsing
                // +-------------------+
                if ( tmpReadLine.indexOf(FD_OBJ_DATAS) > -1 && tmpReadLine.indexOf("};") > -1) {
                    tmpSB.append(readLine);
                    addObjDatas(scriptFunctionInfoMapList, currentFunctionName, tmpSB.toString().replaceAll(STR_TAB, STR_EMPTY).replaceAll(STR_BLANK, STR_EMPTY));                    
                    tmpSB = new StringBuffer();
                }
                else if ( tmpReadLine.indexOf(FD_OBJ_DATAS) > -1 ) {
                    objDatasFlag = true;
                }
                
                if ( objDatasFlag ) {
                    if ( readLine.indexOf("//") > -1) {
                        // 주석제거
                        readLine = readLine.substring(0, readLine.indexOf("//"));
                    }
                    if ( readLine.indexOf("/*") > -1 ) {
                        // 주석제거
                        readLine = readLine.substring(0, readLine.indexOf("/*"));
                    }
                    tmpSB.append(readLine);
                    if ( tmpReadLine.indexOf("};") > -1 ) {
                        addObjDatas(scriptFunctionInfoMapList, currentFunctionName, tmpSB.toString().replaceAll(STR_TAB, STR_EMPTY).replaceAll(STR_BLANK, STR_EMPTY));
                        tmpSB = new StringBuffer();
                        objDatasFlag = false;
                    }
                }
                
                // +----------------------+
                //   objArgument Parsing
                // +----------------------+
                if ( tmpReadLine.indexOf(FD_OBJ_ARGUMENT) > -1 && tmpReadLine.indexOf("};") > -1) {
                    tmpSB.append(readLine);
                    addObjArgument(scriptFunctionInfoMapList, currentFunctionName, tmpSB.toString().replaceAll(STR_TAB, STR_EMPTY).replaceAll(STR_BLANK, STR_EMPTY));                    
                    tmpSB = new StringBuffer();
                }
                else if ( tmpReadLine.indexOf(FD_OBJ_ARGUMENT) > -1 ) {
                    objArgumentFlag = true;
                }
                
                if ( objArgumentFlag ) {
                    if ( readLine.indexOf("//") > -1) {
                        // 주석제거
                        readLine = readLine.substring(0, readLine.indexOf("//"));
                    }
                    if ( readLine.indexOf("/*") > -1 ) {
                        // 주석제거
                        readLine = readLine.substring(0, readLine.indexOf("/*"));
                    }
                    tmpSB.append(readLine);
                    if ( tmpReadLine.indexOf("};") > -1 ) {
                        addObjArgument(scriptFunctionInfoMapList, currentFunctionName, tmpSB.toString().replaceAll(STR_TAB, STR_EMPTY).replaceAll(STR_BLANK, STR_EMPTY));
                        tmpSB = new StringBuffer();
                        objArgumentFlag = false;
                    }
                }
                
                // +-----------------------+
                //   objPopupData Parsing
                // +-----------------------+
                if ( tmpReadLine.indexOf(FD_OBJ_POPUPDATA) > -1 && tmpReadLine.indexOf("};") > -1) {
                    tmpSB.append(readLine);
                    addObjPopupData(scriptFunctionInfoMapList, currentFunctionName, tmpSB.toString().replaceAll(STR_TAB, STR_EMPTY).replaceAll(STR_BLANK, STR_EMPTY));                    
                    tmpSB = new StringBuffer();
                }
                else if ( tmpReadLine.indexOf(FD_OBJ_POPUPDATA) > -1 ) {
                    objPopupDataFlag = true;
                }
                
                if ( objPopupDataFlag ) {
                    if ( readLine.indexOf("//") > -1 ) {
                        // 주석제거
                        readLine = readLine.substring(0, readLine.indexOf("//"));
                    }
                    if ( readLine.indexOf("/*") > -1 ) {
                        // 주석제거
                        readLine = readLine.substring(0, readLine.indexOf("/*"));
                    }
                    tmpSB.append(readLine);
                    if ( tmpReadLine.indexOf("};") > -1 ) {
                        addObjPopupData(scriptFunctionInfoMapList, currentFunctionName, tmpSB.toString().replaceAll(STR_TAB, STR_EMPTY).replaceAll(STR_BLANK, STR_EMPTY));
                        tmpSB = new StringBuffer();
                        objPopupDataFlag = false;
                    }
                }
            }
        }
        
        if (logFlag) {
            System.out.println("\n=======================================================");
            System.out.println("::: ParserUtils Class > getScriptFunctionInfo method :::");
            System.out.println("=======================================================");
            System.out.println(">>> scriptFunctionInfoMapList <<< \n" + scriptFunctionInfoMapList);
            System.out.println("=======================================================\n");
        }
        
        /*
        
        */
        
        return scriptFunctionInfoMapList;
    }
    
    /**
     * objDatas append
     * @param scriptFunctionInfoList
     * @param findFunctionName
     * @param objDatas
     * @throws Exception
     */
    public static void addObjDatas(List<Map<String, String>> scriptFunctionInfoList, String findFunctionName, String objDatas) throws Exception {
        String svcid       = "";        
        String sController = "";
        String inds        = "";
        String outds       = "";
        if ( StringUtils.isNotEmpty(objDatas) ) {
            // +------------------------------------------------------------------------------------------------------------------------+
            // varobjDatas={svcid:"svcSave",sController:"SCM050102",inds:["compObjMstDTO=ds_list:U"],outds:["ds_list=compObjMstDTO"]};
            // +------------------------------------------------------------------------------------------------------------------------+
            objDatas = objDatas.replace("varobjDatas={", STR_EMPTY).replace("};", STR_EMPTY);
            String[] arrayObjDatas = objDatas.split(STR_COMMA);
            for ( String tmp : arrayObjDatas ) {
                if ( tmp.indexOf(FD_SVCID) > -1 ) {
                    svcid = tmp.replace("svcid", STR_EMPTY).replace(STR_COLON, STR_EMPTY).replaceAll(STR_DQ, STR_EMPTY);
                }
                else if ( tmp.indexOf(FD_SCONTROLLER) > -1 ) {
                    sController = tmp.replace("sController", STR_EMPTY).replace(STR_COLON, STR_EMPTY).replaceAll(STR_DQ, STR_EMPTY);
                }
                else if ( tmp.indexOf(FD_INDS) > -1 ) {
                    inds = tmp.replace("inds", STR_EMPTY).replace(STR_COLON, STR_EMPTY).replace(STR_SQUARE_BRACKETS_L, STR_EMPTY).replace(STR_SQUARE_BRACKETS_R, STR_EMPTY).replaceAll(STR_DQ, STR_EMPTY);
                }
                else if ( tmp.indexOf(FD_OUTDS) > -1 ) {
                    outds = tmp.replace("outds", STR_EMPTY).replace(STR_COLON, STR_EMPTY).replace(STR_SQUARE_BRACKETS_L, STR_EMPTY).replace(STR_SQUARE_BRACKETS_R, STR_EMPTY).replaceAll(STR_DQ, STR_EMPTY);
                }
            }
            
            for ( Map<String, String> tmpMap : scriptFunctionInfoList ) {
                String tmpFunctionName = tmpMap.get("function_name");
                if ( tmpFunctionName.equals(findFunctionName) ) {
                    tmpMap.put(FD_SVCID, svcid);
                    tmpMap.put(FD_SCONTROLLER, sController);
                    tmpMap.put(FD_INDS, inds);
                    tmpMap.put(FD_OUTDS, outds);
                }
            }
        }
    }
    
    /**
     * objArgument append
     * @param scriptFunctionInfoList
     * @param findFunctionName
     * @param objArgument
     * @throws Exception
     */
    public static void addObjArgument(List<Map<String, String>> scriptFunctionInfoList, String findFunctionName, String objArgument) throws Exception {
        if ( StringUtils.isNotEmpty(objArgument) ) {
            // +---------------------------------------------------+
            // varobjArgument={paramSysId:sysId,paramPrgmId:prgmId};
            // +---------------------------------------------------+
            objArgument = objArgument.replace("varobjArgument={", STR_EMPTY).replace("};", STR_EMPTY);
            for ( Map<String, String> tmpMap : scriptFunctionInfoList ) {
                String tmpFunctionName = tmpMap.get("function_name");
                if ( tmpFunctionName.equals(findFunctionName) ) {
                    tmpMap.put("objArgument", objArgument);
                }
            }
        }
    }
    
    /**
     * objPopupData append
     * @param scriptFunctionInfoList
     * @param findFunctionName
     * @param objPopupData
     * @throws Exception
     */
    public static void addObjPopupData(List<Map<String, String>> scriptFunctionInfoList, String findFunctionName, String objPopupData) throws Exception {
        String popupid = "";        
        String formurl = "";
        String oArgs   = "";
        if ( StringUtils.isNotEmpty(objPopupData) ) {
            // +--------------------------------------------------------------------------------------------------------------------------+
            // varobjPopupData={popupid:"popup_prgm",formurl:"Biz.Scm::SCM0003P02.xfdl",oArgs:objArgument};
            // varobjPopupData={popupid:"popup_prgm",//callbackidformurl:"Biz.Scm::SCM0003P02.xfdl",//formurloArgs:objArgument,//argument};
            // +--------------------------------------------------------------------------------------------------------------------------+
            objPopupData = objPopupData.replace("varobjPopupData={", STR_EMPTY).replace("};", STR_EMPTY);
            String[] arrayObjDatas = objPopupData.split(STR_COMMA);
            for ( String tmp : arrayObjDatas ) {
                if ( tmp.indexOf(FD_POPUPID) > -1 ) {
                    popupid = tmp.replace("popupid", STR_EMPTY).replace(STR_COLON, STR_EMPTY).replaceAll(STR_DQ, STR_EMPTY);
                }
                else if ( tmp.indexOf(FD_FORMURL) > -1 ) {
                    formurl = tmp.replace("formurl", STR_EMPTY).replace(STR_COLON, STR_EMPTY).replaceAll(STR_DQ, STR_EMPTY);
                }
                else if ( tmp.indexOf(FD_OARGS) > -1 ) {
                    oArgs = tmp.replace("oArgs", STR_EMPTY).replace(STR_COLON, STR_EMPTY).replaceAll(STR_DQ, STR_EMPTY);
                }
            }
            
            for ( Map<String, String> tmpMap : scriptFunctionInfoList ) {
                String tmpFunctionName = tmpMap.get("function_name");
                if ( tmpFunctionName.equals(findFunctionName) ) {
                    tmpMap.put(FD_POPUPID, popupid);
                    tmpMap.put(FD_FORMURL, formurl);
                    tmpMap.put(FD_OARGS, oArgs);
                }
            }
        }
    }
    
}
