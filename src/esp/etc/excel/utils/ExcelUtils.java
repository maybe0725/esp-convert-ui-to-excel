package esp.etc.excel.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

    final static boolean logFlag = false;
    
    // +------------------------------------------------------------+
    //     [참고]
    //     X:\system_development\99. 공유폴더\20190502_업무모듈명_(공통)
    // +------------------------------------------------------------+

    /**
     * Get Module Name
     * @param moduleKey
     * @return
     */
    public static String getModuleName(String moduleKey) {
        Map<String, String> moduleMap = new HashMap<>();
        moduleMap.put("SC",  "공통");
        moduleMap.put("SCS", "공통설정관리");
        moduleMap.put("SCM", "공통기준정보");
        moduleMap.put("SCP", "프로그램관리");
        moduleMap.put("SCT", "메타관리");
        moduleMap.put("SCU", "사용자관리");
        moduleMap.put("SCA", "권한관리");
        moduleMap.put("SCC", "공통관리");
        moduleMap.put("SCB", "배치관리");

        moduleMap.put("OD",  "영업");
        moduleMap.put("ODS", "영업설정관리");
        moduleMap.put("ODM", "영업기준정보");
        moduleMap.put("ODP", "판매계획");
        moduleMap.put("ODO", "주문관리");
        moduleMap.put("ODD", "출하관리");
        moduleMap.put("ODB", "매출관리");
        
        moduleMap.put("PI",  "구매");
        moduleMap.put("PIS", "구매설정관리");
        moduleMap.put("PIM", "구매기준정보");
        moduleMap.put("PIO", "구매관리");
        moduleMap.put("PIP", "송장관리");
        moduleMap.put("PIR", "입고관리");
        moduleMap.put("PIT", "출고관리");
        moduleMap.put("PII", "재고관리");
        moduleMap.put("PIC", "수입관리");
        
        moduleMap.put("FI",  "회계");
        moduleMap.put("FIS", "회계설정관리");
        moduleMap.put("FIM", "회계기준정보");
        moduleMap.put("FIG", "일반회계");
        moduleMap.put("FIR", "매출채권");
        moduleMap.put("FIP", "매입채무");
        moduleMap.put("FIA", "고정자산");
        moduleMap.put("FIT", "세무관리");
        
        moduleMap.put("TR",  "자금");
        moduleMap.put("TRS", "자금설정관리");
        moduleMap.put("TRM", "자금기준정보");
        moduleMap.put("TRC", "자금수지");
        moduleMap.put("TRI", "입출금관리");
        moduleMap.put("TRP", "금융상품");
        moduleMap.put("TRF", "외환관리");
        moduleMap.put("TRL", "결산관리");
        
        moduleMap.put("PD",  "생산");
        moduleMap.put("PDS", "생산설정관리");
        moduleMap.put("PDM", "생산기준정보");
        moduleMap.put("PDP", "생산계획");
        moduleMap.put("PDE", "생산실행");
        moduleMap.put("PDC", "생산마감");

        moduleMap.put("CO",  "원가");
        moduleMap.put("COS", "원가설정관리");
        moduleMap.put("COM", "원가기준정보");
        moduleMap.put("COB", "예산관리");
        moduleMap.put("COC", "실적관리");
        moduleMap.put("COP", "계획관리");
        moduleMap.put("COI", "투자관리");
        moduleMap.put("COA", "경영정보");
        
        return moduleMap.get(StringUtils.nvl(moduleKey).toUpperCase());
    }
    
    public static String createExcel (String datasetIdList, Map<String, String> scriptHeaderInfo, List<Map<String, String>> scriptFunctionInfoList) {

        String result = "";
        
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File("C:\\HNEXT\\esp-convert-ui-to-excel\\output\\template.xlsx")));
            
            XSSFRow  row = null;
            XSSFCell cell = null;
            
            // +---------------------+
            //     '표지' Sheet
            // +---------------------+
            XSSFSheet coverSheet = workbook.getSheetAt(0);
            
            row  = coverSheet.getRow(2);
            cell = row.getCell(3);
            cell.setCellValue("H-Next");
            
            row  = coverSheet.getRow(10);
            cell = row.getCell(3);
            cell.setCellValue(DateUtils.getDateyyyyMMdd("."));
            
            // +---------------------+
            //     '재개정이력' Sheet
            // +---------------------+
            //XSSFSheet historySheet = workbook.getSheetAt(1);
            
            String programId          = ""; 
            String programName        = ""; 
            String programType        = "";
            String programDesc        = "";
            String programOnloadSetup = "";
            
            String oneDepthWorkGroupEng = "";
            String oneDepthWorkGroupKor = "";
            String twoDepthWorkGroupEng = "";
            String twoDepthWorkGroupKor = "";
            
            if ( null != scriptHeaderInfo ) {
                
                // +---------------------+
                //     'UI' Sheet
                // +---------------------+
                XSSFSheet uiSheet = workbook.getSheetAt(2);
                
                programId          = StringUtils.nvl(scriptHeaderInfo.get("program_id"));
                programName        = StringUtils.nvl(scriptHeaderInfo.get("program_name"));
                programType        = "화면";
                programDesc        = StringUtils.nvl(scriptHeaderInfo.get("description"));
                programOnloadSetup = StringUtils.nvl(scriptHeaderInfo.get("onload_setup"));
                
                if ( StringUtils.isNotEmpty(programId) &&  programId.length() > 3) {
                    
                    // Sheet 명 변경 ---> Program Id
                    workbook.setSheetName(2, programId);
                    
                    oneDepthWorkGroupEng = programId.trim().substring(0, 2);
                    twoDepthWorkGroupEng = programId.trim().substring(0, 3);
                    
                    if ( StringUtils.isNotEmpty(oneDepthWorkGroupEng) && oneDepthWorkGroupEng.length() == 2 ) {
                        oneDepthWorkGroupKor = getModuleName(oneDepthWorkGroupEng);
                    }
                    if ( StringUtils.isNotEmpty(twoDepthWorkGroupEng) && twoDepthWorkGroupEng.length() == 3 ) {
                        twoDepthWorkGroupKor = getModuleName(twoDepthWorkGroupEng);
                    }
                }
                
                // +-----------------------+
                //     대분류
                // +-----------------------+
                row  = uiSheet.getRow(2);
                row.getCell(1).setCellValue(oneDepthWorkGroupKor);
                
                // +-----------------------+
                //     중분류
                // +-----------------------+
                row  = uiSheet.getRow(2);
                row.getCell(5).setCellValue(twoDepthWorkGroupKor);
                
                // +-----------------------+
                //     화면ID
                // +-----------------------+
                uiSheet.getRow(3).getCell(1).setCellValue(programId);
                
                // +-----------------------+
                //     화면명
                // +-----------------------+
                uiSheet.getRow(3).getCell(5).setCellValue(programName);
                
                // +-----------------------+
                //     프로그램유형
                // +-----------------------+
                uiSheet.getRow(3).getCell(9).setCellValue(programType);
                
                // +-----------------------+
                //     화면설명
                // +-----------------------+
                uiSheet.getRow(4).getCell(1).setCellValue(programDesc);
                
                // +-----------------------+
                //     초기 화면 및 Defalut 값
                // +-----------------------+
                uiSheet.getRow(6).getCell(8).setCellValue(programOnloadSetup);
                
                // +-------------------------+
                //     데이터 구성항목 (Optional)
                // +-------------------------+
                uiSheet.getRow(8).getCell(0).setCellValue(datasetIdList);
                
                int currentRow = 11;
                if ( null != scriptFunctionInfoList && scriptFunctionInfoList.size() > 0) {
                    for ( Map<String, String> scriptFunctionInfo : scriptFunctionInfoList ) {
                        
                        // +--------------+
                        //   입력값/파라미터
                        // +--------------+
                        String inds = "";
                        if ( null != scriptFunctionInfo.get("objArgument") ) {
                            // Popup objArgument
                            inds = StringUtils.nvl(scriptFunctionInfo.get("objArgument"));
                        }
                        else if ( StringUtils.isEmpty(inds) && null != scriptFunctionInfo.get("inds") ) {
                            // objDatas inds 
                            inds = StringUtils.nvl(scriptFunctionInfo.get("inds"));
                        }
                        else if ( StringUtils.isEmpty(inds) && null != scriptFunctionInfo.get("param") ) {
                            // function header @param
                            inds = StringUtils.nvl(scriptFunctionInfo.get("param"));
                        }
                        
                        // +--------------+
                        //   출력값 또는 처리결과
                        // +--------------+
                        String outds = "";
                        if ( null != scriptFunctionInfo.get("outds") ) {
                            // objDatas outds
                            outds = StringUtils.nvl(scriptFunctionInfo.get("outds"));
                        }
                        else if ( StringUtils.isEmpty(outds) && null != scriptFunctionInfo.get("return") ) {
                            // function header @return
                            outds = StringUtils.nvl(scriptFunctionInfo.get("return"));
                        }
                        
                        // +-----------+
                        //   엑셀 컬럼 값 셋팅
                        // +-----------+
                        
                        // 이벤트명
                        uiSheet.getRow(currentRow).getCell(0).setCellValue(StringUtils.nvl(scriptFunctionInfo.get("function_name")));
                        // 입력값/파라미터
                        uiSheet.getRow(currentRow).getCell(1).setCellValue(inds);       
                        // 처리내용
                        uiSheet.getRow(currentRow).getCell(2).setCellValue(StringUtils.nvl(scriptFunctionInfo.get("description")));    
                        // 호출 화면ID/보고서ID 
                        uiSheet.getRow(currentRow).getCell(5).setCellValue(StringUtils.nvl(scriptFunctionInfo.get("formurl")));
                        // Blank
                        uiSheet.getRow(currentRow).getCell(6).setCellValue("");
                        // 서비스호출 (서비스ID)
                        uiSheet.getRow(currentRow).getCell(7).setCellValue(StringUtils.nvl(scriptFunctionInfo.get("sController")));
                        // 출력값 또는 처리결과
                        uiSheet.getRow(currentRow).getCell(8).setCellValue(outds);
                        // 비고
                        uiSheet.getRow(currentRow).getCell(9).setCellValue(""); 
                        
                        currentRow++;
                    }
                }
                
                String yyyyMMdd = DateUtils.getDateyyyyMMdd(null);
                String fileName = "ESP_DS06_UI설계서_(" + oneDepthWorkGroupKor + ")_" + programId + "_" + programName.replaceAll(" ", "") + "_V1.0_" + yyyyMMdd + ".xlsx";
                File file = new File("C:\\HNEXT\\esp-convert-ui-to-excel\\output\\" + fileName);
                FileOutputStream fos = null;
                
                try {
                    fos = new FileOutputStream(file);
                    workbook.write(fos);
                    result = "엑셀파일 생성이 완료되었습니다.";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    result = e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = e.getMessage();
                } finally {
                    try {
                        if(workbook!=null) workbook.close();
                        if(fos!=null) fos.close();
                        
                    } catch (IOException e) {
                        e.printStackTrace();
                        result = e.getMessage();
                    }
                }
            } else {
                result = "추출된 정보가 존재 하지 않습니다.";
            }
        } catch (Exception e) {
           e.printStackTrace();
           result = e.getMessage();
        }
        
        return result;
    }
    
    
    public static void main(String[] args) throws Exception {
        
        String filePath = "C:\\HNEXT\\workspace\\esp-online-web\\src\\main\\resources\\nxuiSrc\\Biz\\Sc\\Scm\\SCM0005M00.xfdl";
        //String filePath = "C:\\HNEXT\\workspace\\esp-online-web\\src\\main\\resources\\nxuiSrc\\Biz\\Sc\\Scm\\SCM0003P02.xfdl";
        //String filePath = "C:\\HNEXT\\workspace\\esp-online-web\\src\\main\\resources\\nxuiSrc\\Biz\\Sc\\Scm\\SCM0001M00.xfdl";
        
        // Step 01. UI Dataset Parsing
        String datasetList = ParserUtils.getDatasetIdList(filePath);
        
        // Step 02. UI Script Parsing
        String scriptContents = ParserUtils.getScriptContents(filePath);
        
        int startPos = scriptContents.indexOf("/*");
        int   endPos = scriptContents.indexOf("*/");
        
        String   commonScriptInfo = scriptContents.substring(startPos, endPos);
        String functionScriptInfo = scriptContents.substring(endPos, scriptContents.length());
        
        // Step 03. Script Header Parsing
        Map<String, String> headerMap = ParserUtils.getScriptHeaderInfo(commonScriptInfo);
        
        // Step 04. Script Function Parsing
        List<Map<String, String>> scriptFunctionInfo = ParserUtils.getScriptFunctionInfo(functionScriptInfo);
        
        if (logFlag) {
            System.out.println("\n=====================================================");
            System.out.println(":: ExcelUtils Class > main method ::");
            System.out.println("=====================================================");
            System.out.println(">>> datasetList <<< \n" + datasetList);
            System.out.println("+---------------------------------------------------+");
            System.out.println(">>> headerMap <<< \n" + headerMap);
            System.out.println("+---------------------------------------------------+");
            System.out.println(">>> scriptFunctionInfo <<< \n" + scriptFunctionInfo);
            System.out.println("=====================================================\n");
        }
        
        ExcelUtils.createExcel(datasetList, headerMap, scriptFunctionInfo);
    }
    
}
