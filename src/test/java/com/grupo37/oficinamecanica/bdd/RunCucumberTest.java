package com.grupo37.oficinamecanica.bdd;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Ponto de entrada JUnit 5 para os cenários de BDD (Cucumber) da Saga.
 * Roda junto com "mvn test" (Surefire descobre suites JUnit 5 Platform
 * normalmente) — não precisa de nenhuma configuração adicional no CI.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.grupo37.oficinamecanica.bdd")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty, summary")
public class RunCucumberTest {
}
