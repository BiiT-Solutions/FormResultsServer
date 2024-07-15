package com.biit.forms.persistence;

import com.biit.forms.persistence.logger.TestLogging;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        TestLogging.info("### Test started '" + result.getMethod().getMethodName() + "' from '" + result.getTestClass().getName() + "'.");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        TestLogging.info("### Test finished '" + result.getMethod().getMethodName() + "' from '" + result.getTestClass().getName() + "'.");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        TestLogging.errorMessage(this.getClass().getName(), "### Test failed '" + result.getMethod().getMethodName() + "' from '" + result.getTestClass().getName() + "'.");
    }

    @Override
    public void onTestSkipped(ITestResult result) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {
        TestLogging.info(this.getClass().getName(), "##### Starting tests from '" + context.getName() + "'.");
    }

    @Override
    public void onFinish(ITestContext context) {
        TestLogging.info(this.getClass().getName(), "##### Tests finished from '" + context.getName() + "'.");
    }
}
