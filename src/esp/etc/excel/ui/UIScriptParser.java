package esp.etc.excel.ui;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import esp.etc.excel.process.RunningOperation;
import esp.etc.excel.utils.ExcelUtils;
import esp.etc.excel.utils.ParserUtils;
import esp.etc.excel.utils.StringUtils;

public class UIScriptParser extends Shell {
    
    private Text textFileOpen;
    private Table table;
    private TableItem item;
    private Text text_01;
    private Text text_02;
    private Text text_03;
    private Text text_04;
    private Text text_05;
    private Text text_06;
    private Text text_07;
    private Text text_08;
    private Text text_09;
    private Text text_10;
    private Text text_11;
    private Text text_12;
    private Text text_13;
    private Text text_14;
    private Text text_15;
    private Text text_16;
    private Text text_17;
    private Text text_18;
    private Text text_19;
    private Text text_20;
    private Text text_21;
    private Text text_22;
    private Text text_23;
    private Button btnExcelSave;
    
    protected void resetView() {
        textFileOpen.setText("");
        
        text_02.setText("");
        text_04.setText("");
        text_08.setText("");
        text_10.setText("");
        text_12.setText("");
        text_14.setText("");
        text_16.setText("");
        text_20.setText("");
        text_22.setText("");
        
        table.dispose();
        table = new Table(this, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
        table.setLocation(10, 460);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
    }
    
    /**
     * Launch the application.
     * @param args
     */
    public static void main(String args[]) {
        try {
            Display display = Display.getDefault();
            UIScriptParser shell = new UIScriptParser(display);
            shell.open();
            shell.layout();
            
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the shell.
     * @param display
     */
    public UIScriptParser(Display display) {
        super(display, SWT.SHELL_TRIM | SWT.BORDER);
        
        Button btnFileOpen = new Button(this, SWT.NONE);
        btnFileOpen.setFont(SWTResourceManager.getFont("맑은 고딕", 9, SWT.BOLD));
        btnFileOpen.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("unused")
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fd = new FileDialog(display.getActiveShell(), SWT.OPEN);
                fd.setText("Open");
                fd.setFilterPath("C:/HNEXT/workspace/esp-online-web/src/main/resources/nxuiSrc/Biz");
                String[] filterExt = { "*.xfdl", "*.*" };
                fd.setFilterExtensions(filterExt);
                
                String openFile = fd.open();
                
                if ( StringUtils.isNotEmpty(openFile) ) {
                    
                    // View 초기화
                    resetView();
                    
                    textFileOpen.setText(openFile);
                    
                    String programId = "";
                    String programName = "";
                    
                    String oneDepthWorkGroupEng = "";
                    String oneDepthWorkGroupKor = "";
                    String twoDepthWorkGroupEng = "";
                    String twoDepthWorkGroupKor = "";
                    
                    try {
                        
                        // Step 01. UI Dataset Parsing
                        String datasetList = ParserUtils.getDatasetIdList(openFile);
                        
                        // Step 02. UI Script Parsing
                        String scriptContents = ParserUtils.getScriptContents(openFile);
                        
                        int startPos = scriptContents.indexOf("/*");
                        int   endPos = scriptContents.indexOf("*/");
                        
                        String   commonScriptInfo = scriptContents.substring(startPos, endPos);
                        String functionScriptInfo = scriptContents.substring(endPos, scriptContents.length());
                        
                        // Step 03. Script Header Parsing
                        Map<String, String> headerMap = ParserUtils.getScriptHeaderInfo(commonScriptInfo);
                        
                        // Step 04. Script Function Parsing
                        List<Map<String, String>> scriptFunctionInfoList = ParserUtils.getScriptFunctionInfo(functionScriptInfo);
                        
                        if ( null != headerMap) {
                            programId   = StringUtils.nvl(headerMap.get("program_id"));
                            programName = StringUtils.nvl(headerMap.get("program_name"));
                            
                            if ( StringUtils.isNotEmpty(programId) &&  programId.length() > 3) {
                                oneDepthWorkGroupEng = programId.trim().substring(0, 2);
                                twoDepthWorkGroupEng = programId.trim().substring(0, 3);
                                
                                if ( StringUtils.isNotEmpty(oneDepthWorkGroupEng) && oneDepthWorkGroupEng.length() == 2 ) {
                                    oneDepthWorkGroupKor = ExcelUtils.getModuleName(oneDepthWorkGroupEng);
                                }
                                if ( StringUtils.isNotEmpty(twoDepthWorkGroupEng) && twoDepthWorkGroupEng.length() == 3 ) {
                                    twoDepthWorkGroupKor = ExcelUtils.getModuleName(twoDepthWorkGroupEng);
                                }
                            }
                            
                            text_02.setText(oneDepthWorkGroupKor);              // 대분류
                            text_04.setText(twoDepthWorkGroupKor);              // 중분류
                            text_08.setText(StringUtils.nvl(headerMap.get("program_id")));       // 화면ID
                            text_10.setText(StringUtils.nvl(headerMap.get("program_name")));     // 화면명
                            text_12.setText("화면");                              // 프로그램유형
                            text_14.setText(StringUtils.nvl(headerMap.get("description")));      // 화면설명
                            text_16.setText("");                                // 서비스 클래스ID
                            text_20.setText(StringUtils.nvl(headerMap.get("onload_setup")));     // 서비스 클래스ID
                            text_22.setText(datasetList);   // 데이터 구성항목(Optional)
                        }
                        
                        if ( null != scriptFunctionInfoList ) {
                            int cnt = 1;
                            for ( Map<String, String> tmpFuncMap : scriptFunctionInfoList ) {
                                   
                                String[] titles = { "No", "이벤트 명", "입력값/파라미터", "처리내용", "호출 화면ID/보고서ID", "", "서비스호출(서비스ID)",  "출력값 또는 처리결과", "비고" };
                                for (int i = 0; i < titles.length; i++) {
                                    TableColumn column = new TableColumn(table, SWT.VIRTUAL);
                                    column.setText(titles[i]);
                                }
                                
                                item = new TableItem(table, SWT.VIRTUAL);
                                // +--------+
                                //     No
                                // +--------+
                                item.setText(0, Integer.toString(cnt));
                                // +------------+
                                //     이벤트 명
                                // +------------+
                                item.setText(1, StringUtils.nvl(tmpFuncMap.get("function_name")));
                                // +----------------+
                                //     입력값/파라미터
                                // +----------------+
                                String inds = "";
                                if ( null != tmpFuncMap.get("objArgument") ) {
                                    // Popup objArgument
                                    inds = StringUtils.nvl(tmpFuncMap.get("objArgument"));
                                }
                                else if ( StringUtils.isEmpty(inds) && null != tmpFuncMap.get("inds") ) {
                                    // objDatas inds 
                                    inds = StringUtils.nvl(tmpFuncMap.get("inds"));
                                }
                                else if ( StringUtils.isEmpty(inds) && null != tmpFuncMap.get("param") ) {
                                    // function header @param
                                    inds = StringUtils.nvl(tmpFuncMap.get("param"));
                                }
                                item.setText(2, inds);
                                // +-----------+
                                //     처리내용
                                // +-----------+
                                item.setText(3, tmpFuncMap.get("description"));
                                // +--------------------+
                                //     호출 화면ID/보고서ID
                                // +--------------------+
                                item.setText(4, StringUtils.nvl(tmpFuncMap.get("formurl")));
                                // +-----------+
                                //     공란
                                // +-----------+
                                item.setText(5, "");
                                // +--------------------+
                                //     서비스호출(서비스ID)
                                // +--------------------+
                                item.setText(6, StringUtils.nvl(tmpFuncMap.get("scontroller")));
                                // +------------------+
                                //     출력값 또는 처리결과
                                // +------------------+
                                String outds = "";
                                if ( null != tmpFuncMap.get("outds") ) {
                                    // objDatas outds
                                    outds = StringUtils.nvl(tmpFuncMap.get("outds"));
                                }
                                else if ( StringUtils.isEmpty(outds) && null != tmpFuncMap.get("return") ) {
                                    // function header @return
                                    outds = StringUtils.nvl(tmpFuncMap.get("return"));
                                }
                                item.setText(7, outds);
                                // +--------------------+
                                //     비고
                                // +--------------------+
                                item.setText(8, "");
                                
                                for (int i = 0; i < titles.length; i++) {
                                    table.getColumn(i).pack();
                                }
                                cnt++;
                            }
                            table.setSize(new Point(895, 242));
                        }
                        
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });
        btnFileOpen.setBounds(10, 10, 76, 25);
        btnFileOpen.setText("File Open");
        
        textFileOpen = new Text(this, SWT.BORDER);
        textFileOpen.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        textFileOpen.setEditable(false);
        textFileOpen.setBounds(92, 12, 813, 21);
        
        Label lblHSep = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
        lblHSep.setBounds(10, 41, 895, 21);
        
        table = new Table(this, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
        table.setLocation(10, 460);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        
        text_01 = new Text(this, SWT.BORDER | SWT.CENTER);
        text_01.setText("대분류");
        text_01.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.NORMAL));
        text_01.setEditable(false);
        text_01.setBounds(10, 68, 120, 25);
        
        text_02 = new Text(this, SWT.BORDER);
        text_02.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        text_02.setEditable(false);
        text_02.setBounds(129, 68, 180, 25);
        
        text_03 = new Text(this, SWT.BORDER | SWT.CENTER);
        text_03.setText("중분류");
        text_03.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.NORMAL));
        text_03.setEditable(false);
        text_03.setBounds(308, 68, 120, 25);
        
        text_04 = new Text(this, SWT.BORDER);
        text_04.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        text_04.setEditable(false);
        text_04.setBounds(427, 68, 180, 25);
        
        text_05 = new Text(this, SWT.BORDER | SWT.CENTER);
        text_05.setText("중분류");
        text_05.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.NORMAL));
        text_05.setEditable(false);
        text_05.setBounds(606, 68, 120, 25);
        
        text_06 = new Text(this, SWT.BORDER);
        text_06.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        text_06.setEditable(false);
        text_06.setBounds(725, 68, 180, 25);
        
        text_07 = new Text(this, SWT.BORDER | SWT.CENTER);
        text_07.setText("화면ID");
        text_07.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.NORMAL));
        text_07.setEditable(false);
        text_07.setBounds(10, 92, 120, 25);
        
        text_08 = new Text(this, SWT.BORDER);
        text_08.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        text_08.setEditable(false);
        text_08.setBounds(129, 92, 180, 25);
        
        text_09 = new Text(this, SWT.BORDER | SWT.CENTER);
        text_09.setText("화면명");
        text_09.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.NORMAL));
        text_09.setEditable(false);
        text_09.setBounds(308, 92, 120, 25);
        
        text_10 = new Text(this, SWT.BORDER);
        text_10.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        text_10.setEditable(false);
        text_10.setBounds(427, 92, 180, 25);
        
        text_11 = new Text(this, SWT.BORDER | SWT.CENTER);
        text_11.setText("프로그램유형");
        text_11.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.NORMAL));
        text_11.setEditable(false);
        text_11.setBounds(606, 92, 120, 25);
        
        text_12 = new Text(this, SWT.BORDER);
        text_12.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        text_12.setEditable(false);
        text_12.setBounds(725, 92, 180, 25);
        
        text_13 = new Text(this, SWT.BORDER | SWT.CENTER);
        text_13.setText("화면설명");
        text_13.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.NORMAL));
        text_13.setEditable(false);
        text_13.setBounds(10, 116, 120, 25);
        
        text_14 = new Text(this, SWT.BORDER);
        text_14.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        text_14.setEditable(false);
        text_14.setBounds(129, 116, 478, 25);
        
        text_15 = new Text(this, SWT.BORDER | SWT.CENTER);
        text_15.setText("서비스 클래스ID");
        text_15.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.NORMAL));
        text_15.setEditable(false);
        text_15.setBounds(606, 116, 120, 25);
        
        text_16 = new Text(this, SWT.BORDER);
        text_16.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        text_16.setEditable(false);
        text_16.setBounds(725, 116, 180, 25);
        
        text_17 = new Text(this, SWT.BORDER);
        text_17.setText("1. 화면 레이아웃");
        text_17.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.NORMAL));
        text_17.setEditable(false);
        text_17.setBounds(10, 140, 597, 25);
        
        text_18 = new Text(this, SWT.BORDER);
        text_18.setText("2. 초기 화면 및 Default 값");
        text_18.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.NORMAL));
        text_18.setEditable(false);
        text_18.setBounds(606, 140, 299, 25);
        
        text_19 = new Text(this, SWT.BORDER);
        text_19.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        text_19.setEditable(false);
        text_19.setBounds(10, 164, 597, 150);
        
        text_20 = new Text(this, SWT.BORDER);
        text_20.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        text_20.setEditable(false);
        text_20.setBounds(606, 164, 299, 150);
        
        text_21 = new Text(this, SWT.BORDER);
        text_21.setText("3. 데이터 구성항목(Optional)");
        text_21.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.NORMAL));
        text_21.setEditable(false);
        text_21.setBounds(10, 313, 895, 25);
        
        text_22 = new Text(this, SWT.BORDER);
        text_22.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        text_22.setEditable(false);
        text_22.setBounds(10, 337, 895, 100);
        
        text_23 = new Text(this, SWT.BORDER);
        text_23.setText("4. 처리로직");
        text_23.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.NORMAL));
        text_23.setEditable(false);
        text_23.setBounds(10, 436, 895, 25);
        
        btnExcelSave = new Button(this, SWT.NONE);
        btnExcelSave.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if ( StringUtils.isEmpty(textFileOpen.getText()) ) {
                    MessageBox messageBox = new MessageBox(display.getActiveShell(), SWT.ICON_WARNING | SWT.OK);
                    messageBox.setText("Warning");
                    messageBox.setMessage("대상 파일을 선택 하세요.");
                    int buttonID = messageBox.open();
                    switch(buttonID) {
                        case SWT.OK:
                            // ok
                    }
                } else {
                    try {
                        new ProgressMonitorDialog(display.getActiveShell()).run(false, true, new RunningOperation(textFileOpen.getText(), btnExcelSave.getSelection()));
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });
        btnExcelSave.setFont(SWTResourceManager.getFont("맑은 고딕", 15, SWT.BOLD));
        btnExcelSave.setBounds(757, 708, 148, 49);
        btnExcelSave.setText("Excel Save");
                
        createContents();
    }

    /**
     * Create contents of the shell.
     */
    protected void createContents() {
        setText("ESP - UI to Excel");
        setSize(935, 806);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
