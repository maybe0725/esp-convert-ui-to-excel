package esp.etc.excel.process;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import esp.etc.excel.utils.ExcelUtils;
import esp.etc.excel.utils.HttpUtils;
import esp.etc.excel.utils.ParserUtils;


public class RunningOperation implements IRunnableWithProgress {

    private static final int TOTAL_TIME = 100;

    private String filePath = "";

    private boolean indeterminate;

    public RunningOperation(String filePath, boolean indeterminate) {
        this.filePath = filePath;
        this.indeterminate = indeterminate;
    }
    
    /**
     * LongRunningOperation constructor
     *
     * @param indeterminate whether the animation is unknown
     */
    public RunningOperation(boolean indeterminate) {
        this.indeterminate = indeterminate;
    }

    /**
     * Runs the long running operation
     *
     * @param monitor the progress monitor
     */
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        
        try {
            
            monitor.beginTask("엑셀파일 생성 중 입니다.", indeterminate? IProgressMonitor.UNKNOWN : TOTAL_TIME);
            
            monitor.subTask("Step 01. UI Dataset Parsing...");
            String datasetIdList = ParserUtils.getDatasetIdList(this.filePath);
            monitor.worked(20);
            Thread.sleep(500);
            
            monitor.subTask("Step 02. UI Script Parsing...");
            String scriptContents = ParserUtils.getScriptContents(this.filePath);
            monitor.worked(20);
            Thread.sleep(500);
            
            int startPos = scriptContents.indexOf("/*");
            int   endPos = scriptContents.indexOf("*/");
            
            String   commonScriptInfo = scriptContents.substring(startPos, endPos);
            String functionScriptInfo = scriptContents.substring(endPos, scriptContents.length());
            
            monitor.subTask("Step 03. Script Header Parsing...");
            Map<String, String> scriptHeaderInfo = ParserUtils.getScriptHeaderInfo(commonScriptInfo);
            monitor.worked(20);
            Thread.sleep(500);
            
            monitor.subTask("Step 04. Script Function Parsing...");
            List<Map<String, String>> scriptFunctionInfo = ParserUtils.getScriptFunctionInfo(functionScriptInfo);
            monitor.worked(20);
            Thread.sleep(500);
            
            monitor.subTask("Step 05. DB 처리중...");
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            Map<String, Object> jsonMap = new HashMap<String, Object>();
            jsonMap.put("header",   scriptHeaderInfo);
            jsonMap.put("function", scriptFunctionInfo.get(0));
            jsonMap.put("object",   scriptFunctionInfo.get(1));
            final String requestUrl    = "http://172.16.51.70:8088/iomanage/excel/uiScriptParser";
            final String requestMethod = "POST";
            final String requestBody   = gson.toJson(jsonMap);
            HttpUtils.post(requestUrl, requestMethod, requestBody);
            monitor.worked(20);
            Thread.sleep(500);
            
            monitor.subTask("Step 06. Excel 파일 생성중...");
            String convertResult = ExcelUtils.createExcel(datasetIdList, scriptHeaderInfo, scriptFunctionInfo);
            
            monitor.worked(20);
            monitor.subTask("");
            monitor.setTaskName("엑셀파일 생성이 완료되었습니다.");
            
            MessageDialog.openInformation(null, "Convert UI To Excel Result", convertResult);
            
            monitor.done();
            
            if (monitor.isCanceled())
                throw new InterruptedException("The long running operation was cancelled");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
