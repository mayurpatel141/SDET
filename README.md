# SDET
# Automation in this particular scenario has been achieved through Data Driven Framework
# For Data Part I have used .xls file to store/update data required for the execution of the test scenario

# src\com\stta\ExcelFiles has two file TestSuiteList.xls and SuiteOne.xls 
# We can list number of suite that we want to run/skip during the time of execution in TestSuiteList.xls file
# SuiteOne will have list of test cases with their corresponding test data

# Test Script for the above data is written at "src\com\stta\SuiteOne" path
# \SuiteOneCaseOne.java is a test case used to check Cheapest return tickect is shown to the customer on the web page
# \SuiteOneCaseTwo.java is a test case used to validate the Rest API GET Response.

# To run the Test Scenario form command line 
# Navigate to the project folder and pass "mvn install"

# Test Result output are updated in the excel file and it can also be viewed under TestNG (\test-output) folder