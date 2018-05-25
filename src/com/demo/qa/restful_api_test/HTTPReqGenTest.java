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
  //TestNG的DataProvider, 首先用DataReader构造函数，读取Excel中Input的数据，
  //放入HashMap<String, RecordHandler>, Map的key值就是test case的ID，
  //value是RecordHandler对象，此对象中一个重要的成员属性就是input sheet里面 column和value 的键值对，
  //遍历Map将test case ID 与 test case的value 即input sheet前两列的值放入List<Object[]> ，
  //最后返回List的迭代器iterator (为了循环调用@Test方法)
  /*
   *  数据提供者在方法签名中声明了一个 ITestContext 类型的参数
   *  testng 会将当前的测试上下文设置给它
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
  //读取Excel (WorkBook) 的 ‘Input’ 和 ‘Baseline’ sheet
  //并且新建‘Output’, ‘Comparison’, ‘Result’ 三个空sheet
  //读取http_request_template.txt 内容转成string
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
