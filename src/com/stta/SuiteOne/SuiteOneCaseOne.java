package com.stta.SuiteOne;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.stta.utility.Read_XLS;
import com.stta.utility.SuiteUtility;

//SuiteOneCaseOne Class Inherits From SuiteOneBase Class.
//So, SuiteOneCaseOne Class Is Child Class Of SuiteOneBase Class And SuiteBase Class.
public class SuiteOneCaseOne extends SuiteOneBase{
	Read_XLS FilePath = null;
	String SheetName = null;
	String TestCaseName = null;	
	String ToRunColumnNameTestCase = null;
	String ToRunColumnNameTestData = null;
	String TestDataToRun[]=null;
	static boolean TestCasePass=true;
	static int DataSet=-1;	
	static boolean Testskip=false;
	static boolean Testfail=false;
	SoftAssert s_assert =null;	
	
	
	
	
	@BeforeTest
	public void checkCaseToRun() throws IOException{
		//Called init() function from SuiteBase class to Initialize .xls Files
		init();			
		//To set SuiteOne.xls file's path In FilePath Variable.
		FilePath = TestCaseListExcelOne;		
		TestCaseName = this.getClass().getSimpleName();	
		//SheetName to check CaseToRun flag against test case.
		SheetName = "TestCasesList";
		//Name of column In TestCasesList Excel sheet.
		ToRunColumnNameTestCase = "CaseToRun";
		//Name of column In Test Case Data sheets.
		ToRunColumnNameTestData = "DataToRun";
		//Bellow given syntax will Insert log In applog.log file.
		Add_Log.info(TestCaseName+" : Execution started.");
		
		//To check test case's CaseToRun = Y or N In related excel sheet.
		//If CaseToRun = N or blank, Test case will skip execution. Else It will be executed.
		if(!SuiteUtility.checkToRunUtility(FilePath, SheetName,ToRunColumnNameTestCase,TestCaseName)){
			Add_Log.info(TestCaseName+" : CaseToRun = N for So Skipping Execution.");
			//To report result as skip for test cases In TestCasesList sheet.
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "SKIP");
			//To throw skip exception for this test case.
			throw new SkipException(TestCaseName+"'s CaseToRun Flag Is 'N' Or Blank. So Skipping Execution Of "+TestCaseName);
		}	
		//To retrieve DataToRun flags of all data set lines from related test data sheet.
		TestDataToRun = SuiteUtility.checkToRunUtilityOfData(FilePath, TestCaseName, ToRunColumnNameTestData);
	}
	
	//Accepts 2 column's String data In every Iteration.
	@Test(dataProvider="SuiteOneCaseOneData")
	public void SuiteOneCaseOneTest(String Departure_airport,String Arrival_airport){
		
		DataSet++;
		
		//Created object of testng SoftAssert class.
		s_assert = new SoftAssert();
		
		//If found DataToRun = "N" for data set then execution will be skipped for that data set.
		if(!TestDataToRun[DataSet].equalsIgnoreCase("Y")){	
			Add_Log.info(TestCaseName+" : DataToRun = N for data set line "+(DataSet+1)+" So skipping Its execution.");
			//If DataToRun = "N", Set Testskip=true.
			Testskip=true;
			throw new SkipException("DataToRun for row number "+DataSet+" Is No Or Blank. So Skipping Its Execution.");
		}
		
		//If found DataToRun = "Y" for data set then bellow given lines will be executed.
		//To Convert data from String to Integer
		String ValueOne = Departure_airport;
		String ValueTwo = Arrival_airport;
		Calendar cal = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("dd MMM YY");
		//Adding 7 days from the current date
		cal.add(Calendar.DATE,+7);
		String departure_date = formatter.format(cal.getTime());
		cal.add(Calendar.DATE,+7);
		String return_date = formatter.format(cal.getTime());
		
		//To Initialize Chrome browser.
		loadWebBrowser();
		//To navigate to URL.
		driver.navigate().to("https://www.emirates.com/ae/english/");
		String browserTile ="Emirates flights � Book a flight, browse our flight offers and explore the Emirates Experience";
		//If the Page is not opened will refresh the page
		if(!driver.getTitle().contains(browserTile)){
			driver.navigate().refresh();
		}
		//listing down element on which action has to be performed
		WebElement depatureAirport= driver.findElement(By.xpath("//input[@name='Departure airport']"));
		WebElement arrivalAirport= driver.findElement(By.xpath("//input[@name='Arrival airport']"));
		
		depatureAirport.clear();
		depatureAirport.sendKeys(ValueOne);
		autoSelectLoction(ValueOne);

		arrivalAirport.clear();
		arrivalAirport.sendKeys(ValueTwo);
		autoSelectLoction(ValueTwo);
		
		WebElement fromDateBox= driver.findElement(By.id("search-flight-date-picker--depart"));
		WebElement toDateBox= driver.findElement(By.id("search-flight-date-picker--return"));
		
		((JavascriptExecutor)driver).executeScript ("document.getElementById('search-flight-date-picker--depart').removeAttribute('readonly',0);"); // Enables the from date box
		fromDateBox.clear();
		fromDateBox.sendKeys(departure_date);
		
		((JavascriptExecutor)driver).executeScript ("document.getElementById('search-flight-date-picker--return').removeAttribute('readonly',0);"); // Enables the from date box
		toDateBox.clear();
		toDateBox.sendKeys(return_date);
		toDateBox.sendKeys(Keys.ESCAPE);
		
		WebElement searchFlights= driver.findElement(By.xpath("//button[@type='submit']"));
		searchFlights.click();
		
		Boolean lowestPrice = driver.findElement(By.id("ctl00_c_ctlLowPrice_dvLowestPriceDisplay")).isDisplayed();
		WebElement summaryAmount = driver.findElement(By.className("summary-curr-only"));

		s_assert.assertEquals(lowestPrice, 1, "Cheapest return tickect is shown to the customer an the price for the same is : " +summaryAmount);
		
		if(s_assert != null){
			//At last, test data assertion failure will be reported In testNG reports and It will mark your test data, test case and test suite as fail.
			Testfail=true;
			s_assert.assertAll();
		}
	}
	
	//@AfterMethod method will be executed after execution of @Test method every time.
	@AfterMethod
	public void reporterDataResults(){		
		if(Testskip){
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as SKIP In excel.");
			//If found Testskip = true, Result will be reported as SKIP against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "SKIP");
		}
		else if(Testfail){
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as FAIL In excel.");
			//To make object reference null after reporting In report.
			s_assert = null;
			//Set TestCasePass = false to report test case as fail In excel sheet.
			TestCasePass=false;	
			//If found Testfail = true, Result will be reported as FAIL against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "FAIL");			
		}else{
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as PASS In excel.");
			//If found Testskip = false and Testfail = false, Result will be reported as PASS against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "PASS");
		}
		//At last make both flags as false for next data set.
		Testskip=false;
		Testfail=false;
	}
	
	//This data provider method will return 4 column's data one by one In every Iteration.
	@DataProvider
	public Object[][] SuiteOneCaseOneData(){
		//To retrieve data from Data 1 Column,Data 2 Column,Data 3 Column and Expected Result column of SuiteOneCaseOne data Sheet.
		//Last two columns (DataToRun and Pass/Fail/Skip) are Ignored programatically when reading test data.
		return SuiteUtility.GetTestDataUtility(FilePath, TestCaseName);
	}	
	
	//To report result as pass or fail for test cases In TestCasesList sheet.
	@AfterTest
	public void closeBrowser(){
		//To Close the web browser at the end of test.
		closeWebBrowser();
		if(TestCasePass){
			Add_Log.info(TestCaseName+" : Reporting test case as PASS In excel.");
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "PASS");
		}
		else{
			Add_Log.info(TestCaseName+" : Reporting test case as FAIL In excel.");
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "FAIL");			
		}
	}
	
	public void autoSelectLoction(String location){
		List<WebElement> list = driver.findElements(By.xpath("//ol[@class='location__list']//li/descendant::div/p[@class='location__airport__acronym to-highlight']"));
//		System.out.println(list.size());
		for(int i=0; i<list.size(); i++){
//			System.out.println(list.get(i).getText());
			if(list.get(i).getText().contains(location)){
				list.get(i).click();
				break;
			}
		}
	}
}