/*
 *
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.connector.integration.test.docusign;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DocusignConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        String connectorName = System.getProperty("connector_name") + "-connector-" +
                System.getProperty("connector_version") + ".zip";
        init(connectorName);
        esbRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.put("Content-Type", "application/json");
        String accessToken = connectorProperties.getProperty("accessToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);
    }

    /**
     * Positive test case for getLoginInformation method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"},
            description = "docusign{getLoginInformation} integration test with mandatory parameters.")
    public void testGetLoginInformationWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getLoginInformation");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "getLoginInformationMandatory.json");
        String accountId = esbRestResponse.getBody().getJSONArray("loginAccounts").getJSONObject(0).getString("accountId");
        connectorProperties.setProperty("accountId", accountId);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/login_information";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("loginAccounts").getJSONObject(0).getString("accountId"),
                apiRestResponse.getBody().getJSONArray("loginAccounts").getJSONObject(0).getString("accountId"));
    }

    /**
     * Positive test case for getLoginInformation method with optional parameters.
     */
    @Test(groups = {"wso2.esb"},
            description = "docusign{getLoginInformation} integration test with optional parameters.")
    public void testGetLoginInformationWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getLoginInformation");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "getLoginInformationOptional.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/login_information?api_password=" +
                connectorProperties.getProperty("includeApiPassword") + "&include_account_id_guid=" +
                connectorProperties.getProperty("includeAccountIDGuid") + "&login_settings=" +
                connectorProperties.getProperty("loginSetting");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("loginAccounts").getJSONObject(0).getString("accountId"),
                apiRestResponse.getBody().getJSONArray("loginAccounts").getJSONObject(0).getString("accountId"));
    }

    /**
     * Positive test case for ping method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"},
            description = "docusign{ping} integration test with mandatory parameters.")
    public void testPingWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:ping");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "pingMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /*
    *  Positive test case for createEnvelopeFromDocument method with mandatory parameters.
    */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testGetLoginInformationWithMandatoryParameters"},
            description = "docusign{createEnvelopeFromDocument} integration test with mandatory parameters.")
    public void testCreateEnvelopeFromDocumentWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createEnvelopeFromDocument");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "createEnvelopeFromDocumentMandatory.json");
        String envelopeIdDraft = esbRestResponse.getBody().getString("envelopeId");
        connectorProperties.setProperty("envelopeIdDraft", envelopeIdDraft);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/envelopes/" + connectorProperties.getProperty("envelopeIdDraft");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("envelopeId"),
                apiRestResponse.getBody().getString("envelopeId"));
    }

    /*
    *  Positive test case for createEnvelopeFromDocument method with optional parameters.
    */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testGetLoginInformationWithMandatoryParameters"},
            description = "docusign{createEnvelopeFromDocument} integration test with optional parameters.")
    public void testCreateEnvelopeFromDocumentWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createEnvelopeFromDocument");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "createEnvelopeFromDocumentOptional.json");
        String envelopeId = esbRestResponse.getBody().getString("envelopeId");
        connectorProperties.setProperty("envelopeId", envelopeId);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/envelopes/" + connectorProperties.getProperty("envelopeId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("envelopeId"),
                apiRestResponse.getBody().getString("envelopeId"));
    }

    /*
*  Positive test case for listRecipientsInEnvelope method with mandatory parameters.
*/
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateEnvelopeFromDocumentWithOptionalParameters"},
            description = "docusign{listRecipientsInEnvelope} integration test with mandatory parameters.")
    public void testListRecipientsInEnvelopeWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listRecipientsInEnvelope");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "listRecipientsInEnvelopeMandatory.json");
        String recipientId = esbRestResponse.getBody().getJSONArray("signers").getJSONObject(0).getString("recipientId");
        connectorProperties.setProperty("recipientId", recipientId);
        String userId = esbRestResponse.getBody().getJSONArray("signers").getJSONObject(0).getString("userId");
        connectorProperties.setProperty("userId", userId);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/envelopes/" + connectorProperties.getProperty("envelopeId") + "/recipients";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("signers").getJSONObject(0).getString("recipientId"),
                apiRestResponse.getBody().getJSONArray("signers").getJSONObject(0).getString("recipientId"));
    }

    /**
     * Negative test case for listRecipientsInEnvelope method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateEnvelopeFromDocumentWithOptionalParameters"},
            description = "docusign{listRecipientsInEnvelope} integration test with negative case.")
    public void testListRecipientsInEnvelopeWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listRecipientsInEnvelope");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "listRecipientsInEnvelopeNegative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/envelopes/" + connectorProperties.getProperty("negEnvelopeId") + "/recipients";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getString("errorCode"),
                apiRestResponse.getBody().getString("errorCode"));
    }

    /*
    *  Positive test case for createRecipient method with mandatory parameters.
    */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateEnvelopeFromDocumentWithOptionalParameters"},
            description = "docusign{createRecipient} integration test with mandatory parameters.")
    public void testCreateRecipientWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createRecipient");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "createRecipientMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
    }

    /*
    *  Positive test case for createRecipient method with optional parameters.
    */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListRecipientsInEnvelopeWithMandatoryParameters"},
            description = "docusign{createRecipient} integration test with mandatory parameters.")
    public void testCreateRecipientWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createRecipient");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "createRecipientOptional.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
    }

    /**
     * Negative test case for createRecipient method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateEnvelopeFromDocumentWithOptionalParameters"},
            description = "docusign{createRecipient} integration test with negative case.")
    public void testCreateRecipientWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createRecipient");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "createRecipientNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /*
    *  Positive test case for updateRecipients method with optional parameters.
    */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateEnvelopeFromDocumentWithOptionalParameters"},
            description = "docusign{updateRecipients} integration test with optional parameters.")
    public void testUpdateRecipientsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateRecipients");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "updateRecipientsOptional.json");
        String updatedRecipientId = esbRestResponse.getBody().getJSONArray("recipientUpdateResults").
                getJSONObject(0).getString("recipientId");
        connectorProperties.setProperty("updatedRecipientId", updatedRecipientId);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/envelopes/" + connectorProperties.getProperty("envelopeId") + "/recipients/" +
                connectorProperties.getProperty("updatedRecipientId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("recipientUpdateResults").getJSONObject(0).
                getString("recipientId"), apiRestResponse.getBody().getJSONArray("signers").getJSONObject(0).
                getString("recipientId"));
    }

    /**
     * Negative test case for updateRecipients method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateEnvelopeFromDocumentWithOptionalParameters"},
            description = "docusign{updateRecipients} integration test with negative case.")
    public void testUpdateRecipientsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateRecipients");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "updateRecipientsNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for getEnvelope method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateEnvelopeFromDocumentWithOptionalParameters"},
            description = "docusign{getEnvelope} integration test with mandatory parameters.")
    public void testGetEnvelopeWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getEnvelope");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "getEnvelopeMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/envelopes/" + connectorProperties.getProperty("envelopeId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("envelopeId"),
                apiRestResponse.getBody().getString("envelopeId"));
    }

    /**
     * Positive test case for getEnvelope method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateEnvelopeFromDocumentWithOptionalParameters"},
            description = "docusign{getEnvelope} integration test with mandatory parameters.")
    public void testGetEnvelopeWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getEnvelope");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "getEnvelopeOptional.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/envelopes/" + connectorProperties.getProperty("envelopeId") + "?include=" +
                connectorProperties.getProperty("include");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("envelopeId"),
                apiRestResponse.getBody().getString("envelopeId"));
    }

    /**
     * Negative test case for getEnvelope method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateEnvelopeFromDocumentWithOptionalParameters"},
            description = "docusign{getEnvelope} integration test with negative case.")
    public void testGetEnvelopeWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getEnvelope");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "getEnvelopeNegative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/envelopes/" + connectorProperties.getProperty("negEnvelopeId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getString("errorCode"),
                apiRestResponse.getBody().getString("errorCode"));
    }

    /**
     * Positive test case for listEnvelopeDocuments method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateEnvelopeFromDocumentWithOptionalParameters"},
            description = "docusign{listEnvelopeDocuments} integration test with mandatory parameters.")
    public void testListEnvelopeDocumentsMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listEnvelopeDocuments");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "listEnvelopeDocumentsMandatory.json");
        String documentId = esbRestResponse.getBody().getJSONArray("envelopeDocuments").getJSONObject(0).getString("documentId");
        connectorProperties.setProperty("documentId", documentId);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId") +
                "/envelopes/" + connectorProperties.getProperty("envelopeId") + "/documents";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for listEnvelopeDocuments method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateEnvelopeFromDocumentWithOptionalParameters"},
            description = "docusign{listEnvelopeDocuments} integration test with negative case.")
    public void testListEnvelopeDocumentsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listEnvelopeDocuments");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "listEnvelopeDocumentsNegative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/envelopes/" + connectorProperties.getProperty("negEnvelopeId") + "/documents";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getString("errorCode"),
                apiRestResponse.getBody().getString("errorCode"));
    }

    /*
    *  Positive test case for createEnvelopFromTemplate method with optional parameters.
    */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testGetLoginInformationWithMandatoryParameters"},
            description = "docusign{createEnvelopFromTemplate} integration test with optional parameters.")
    public void testCreateEnvelopFromTemplateWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createEnvelopeFromTemplate");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "createEnvelopeFromTemplateOptional.json");
        String envelopeIdOfTemplate = esbRestResponse.getBody().getString("envelopeId");
        connectorProperties.setProperty("envelopeIdOfTemplate", envelopeIdOfTemplate);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/envelopes/" + connectorProperties.getProperty("envelopeIdOfTemplate");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("envelopeId"),
                apiRestResponse.getBody().getString("envelopeId"));
    }

    /*
    *  Positive test case for getTemplate method with mandatory parameters.
    */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testGetLoginInformationWithMandatoryParameters"},
            description = "docusign{getTemplate} integration test with mandatory parameters.")
    public void testGetTemplateWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getTemplate");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "getTemplateMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/templates/" + connectorProperties.getProperty("templateId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("envelopeTemplateDefinition").get("templateId").toString(),
                apiRestResponse.getBody().getJSONObject("envelopeTemplateDefinition").get("templateId").toString());
    }

    /**
     * Negative test case for getTemplate method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testGetLoginInformationWithMandatoryParameters"},
            description = "docusign{getTemplate} integration test with negative case.")
    public void testGetEnvelopeDocumentsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getTemplate");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "getTemplateNegative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/templates/" + connectorProperties.getProperty("negTemplateId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getString("errorCode"),
                apiRestResponse.getBody().getString("errorCode"));
    }

    /*
    *  Positive test case for listTemplates method with mandatory parameters.
    */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testGetLoginInformationWithMandatoryParameters"},
            description = "docusign{listTemplates} integration test with mandatory parameters.")
    public void testListTemplatesWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listTemplates");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "listTemplatesMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/templates";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("envelopeTemplates").getJSONObject(0).getString("templateId"),
                apiRestResponse.getBody().getJSONArray("envelopeTemplates").getJSONObject(0).getString("templateId"));
    }

    /**
     * Negative test case for listTemplates method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testGetLoginInformationWithMandatoryParameters"},
            description = "docusign{listTemplates} integration test with negative case.")
    public void testListTemplatesWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listTemplates");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "listTemplatesNegative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("negAccountId")
                + "/templates";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getString("errorCode"),
                apiRestResponse.getBody().getString("errorCode"));
    }

    /**
     * Positive test case for getDocumentFromAnEnvelope method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"},
            dependsOnMethods = {"testCreateEnvelopeFromDocumentWithOptionalParameters", "testListEnvelopeDocumentsMandatoryParameters"},
            description = "docusign{getDocumentFromAnEnvelope} integration test with mandatory parameters.")
    public void testGetDocumentFromAnEnvelopeMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getDocumentFromAnEnvelope");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "getDocumentFromAnEnvelopeMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/envelopes/" + connectorProperties.getProperty("envelopeId") + "/documents/" +
                connectorProperties.getProperty("documentId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for getDocumentFromAnEnvelope method.
     */
    @Test(groups = {"wso2.esb"},
            dependsOnMethods = {"testCreateEnvelopeFromDocumentWithOptionalParameters", "testListEnvelopeDocumentsMandatoryParameters"},
            description = "docusign{getDocumentFromAnEnvelope} integration test with negative case.")
    public void testGetDocumentFromAnEnvelopeWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getDocumentFromAnEnvelope");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "getDocumentFromAnEnvelopeNegative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/envelopes/" + connectorProperties.getProperty("negEnvelopeId") + "/documents/" +
                connectorProperties.getProperty("documentId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getString("errorCode"),
                apiRestResponse.getBody().getString("errorCode"));
    }

    /**
     * Positive test case for sendDraftEnvelope method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testGetLoginInformationWithMandatoryParameters"},
            description = "docusign{sendDraftEnvelope} integration test with mandatory parameters.")
    public void testSendDraftEnvelopeMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:sendDraftEnvelope");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "sendDraftEnvelopeMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/envelopes/" + connectorProperties.getProperty("draftEnvelopeId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for sendDraftEnvelope method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testGetLoginInformationWithMandatoryParameters"},
            description = "docusign{sendDraftEnvelope} integration test with negative case.")
    public void testSendDraftEnvelopeWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:sendDraftEnvelope");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "sendDraftEnvelopeNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /*
    *  Positive test case for createEnvelope method with optional parameters.
    */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testGetLoginInformationWithMandatoryParameters"},
            description = "docusign{createEnvelope} integration test with optional parameters.")
    public void testCreateEnvelopeWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createEnvelope");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST",
                esbRequestHeadersMap, "createEnvelopeOptional.json");
        String sentEnvelopeId = esbRestResponse.getBody().getString("envelopeId");
        connectorProperties.setProperty("sentEnvelopeId", sentEnvelopeId);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/restapi/" +
                connectorProperties.getProperty("apiVersion") + "/accounts/" + connectorProperties.getProperty("accountId")
                + "/envelopes/" + connectorProperties.getProperty("sentEnvelopeId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("envelopeId"),
                apiRestResponse.getBody().getString("envelopeId"));
    }
}