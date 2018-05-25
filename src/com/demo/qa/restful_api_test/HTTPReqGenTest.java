package com.demo.qa.restful_api_test;

import org.testng.annotations.Test;

import com.demo.qa.utils.DataReader;
import com.demo.qa.utils.RecordHandler;
import com.demo.qa.utils.SheetUtils;
import com.demo.qa.utils.Utils;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.BeforeTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class HTTPReqGenTest {
  String filePath="";
private XSSFWorkbook  wb = null;
private XSSFSheet inputSheet=null;
private XSSFSheet baselineSheet=null;
private XSSFSheet outputSheet=null;
private XSSFSheet comparsionSheet=null;
private XSSFSheet resultSheet=null;
private String template="";
private String startTime="";
private DataReader myInputData;
//http://www.cnblogs.com/wade-xu/p/4229805.html
@Test(dataProvider = "WorkBookData", description = "ReqGenTest")
  public void f(Integer n, String s) {
  }

  @DataProvider(name="WorkBookData")
  //TestNG��DataProvider, ������DataReader���캯������ȡExcel��Input�����ݣ�
  //����HashMap<String, RecordHandler>, Map��keyֵ����test case��ID��
  //value��RecordHandler���󣬴˶�����һ����Ҫ�ĳ�Ա���Ծ���input sheet���� column��value �ļ�ֵ�ԣ�
  //����Map��test case ID �� test case��value ��input sheetǰ���е�ֵ����List<Object[]> ��
  //��󷵻�List�ĵ�����iterator (Ϊ��ѭ������@Test����)
  /*
   *  �����ṩ���ڷ���ǩ����������һ�� ITestContext ���͵Ĳ���
   *  testng �Ὣ��ǰ�Ĳ������������ø���
   *  
   */
  protected Iterator<Object[]> testProvider(ITestContext context) {

	  List<Object[]> test_IDs = new ArrayList<Object[]>();

      myInputData = new DataReader(inputSheet, true, true, 0);
      Map<String, RecordHandler> myInput = myInputData.get_map();

      // sort map in order so that test cases ran in a fixed order
      Map<String, RecordHandler> sortmap = Utils.sortmap(myInput);
      
      for (Map.Entry<String, RecordHandler> entry : sortmap.entrySet()) {
          String test_ID = entry.getKey();
          String test_case = entry.getValue().get("TestCase");
          if (!test_ID.equals("") && !test_case.equals("")) {
              test_IDs.add(new Object[] { test_ID, test_case });
          }
          totalcase++;
      }
      
      myBaselineData = new DataReader(baselineSheet, true, true, 0);

  return test_IDs.iterator();
    };
  }
  @BeforeTest
  @Parameters("workBook")
  //��ȡExcel (WorkBook) �� ��Input�� �� ��Baseline�� sheet
  //�����½���Output��, ��Comparison��, ��Result�� ������sheet
  //��ȡhttp_request_template.txt ����ת��string
  public void setup(String path) {
	  filePath = path;
	  try {
		wb=new XSSFWorkbook(new FileInputStream(filePath));
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  inputSheet = wb.getSheet("Input");
	  baselineSheet = wb.getSheet("Baseline");
	  SheetUtils.removeSheetByName(wb,"Output");
	  SheetUtils.removeSheetByName(wb,"Comparison");
	  SheetUtils.removeSheetByName(wb, "Result");
	  outputSheet =  wb.createSheet("Output");
	  comparsionSheet = wb.createSheet("Comparison");
      resultSheet = wb.createSheet("Result");
      
      InputStream is= HTTPReqGenTest.class.getClassLoader().getResourceAsStream("http_request_template.txt");
      try {
		template = IOUtils.toString(is, Charset.defaultCharset());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		Assert.fail("Problem fetching data from input file:" + e.getMessage());
	}
      SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      startTime = sf.format(new Date());
  }

  @AfterTest
  public void afterTest() {
  }

}
