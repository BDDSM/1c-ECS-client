/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.test.responses;

import org.junit.jupiter.api.Test;

import com.e1c.g5.appliedobjects.ecs.client.response.responses.ErrorResponse;
import com.e1c.g5.appliedobjects.ecs.client.response.responses.ErrorResponseFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorResponseTest
{
    // arrange
    public static final String error_generic = "[\"error\",{\"correlationId\":\"A\"},{\"code\":\"serverError\",\"source\":\"auth\",\"errors\":[\"Application 40e84f9b-c4b9-4fa4-8def-91b943fc3e88 is not enabled. Unable to sign in\"]}]";
    public static final String error_authenticate__AccessDenied = "[\"error\",{\"correlationId\":\"A\"},{\"code\":\"accessDenied\",\"recoveryHint\":\"AMCe302hFjpYpuKJfumKinE/wTIu2DVwiKtc71GVt90c6K3Wgb+E2bxDcYZgyRHGMWwVXXSLOy36FhmnL04OKG03nyjrdCViPEA2GGGFOUb6SI0QHTBCoQ7SnLOY+ED6nBcHydvVV3BXPP6sqQM59lM6gbTxiHMm4JOkpOiX9aXCCnjPscCCXKlNV+Kw3cV/EcqGjMUMiZHGtKwNnlpGA3v9jlahgCA7sUxz3lBT5u2UritpVUHvz5piTN1T3qwETqsAQFsdcKT64g0hcqwcfJroOirA1VZ27OlYAgu6yQ3MhVYVZ6SGAMPRv1RejZ5Rm6VhiytIQRR9EtmTG77ysKy425IAQBS/6xOgD8AK7uMNoLJb9MY3THMtzO7QwqBANObpB8N6i00esW6em3GQJ4+plUvgfCWpywkbsCcVhASurWHTtpY=\",\"errors\":[]}]";
    public static final String error_authenticate__AlreadyAuthenticated = "[\"error\",{\"correlationId\":\"B\"},{\"code\":\"exists\",\"source\":\"auth\",\"errors\":[\"You have already authenticated\"]}]";

    // Создание объекта
    @Test
    void test_CreateInstance_success()
    {
        ErrorResponseFactory factory = new ErrorResponseFactory();

        // act
        // assert
        ErrorResponse err_Generic = factory.createResponse(error_generic);
        ErrorResponse errAuth_AccessDenied = factory.createResponse(error_authenticate__AccessDenied);
        ErrorResponse errAuth_AlreadyAuthenticated = factory.createResponse(error_authenticate__AlreadyAuthenticated);
    }

    @Test
    void GIVEN__error__WHEN__authenticate_accessDenied__THEN__isAccessDenied()
    {
        ErrorResponseFactory factory = new ErrorResponseFactory();

        // act
        ErrorResponse errAuth_AccessDenied = factory.createResponse(error_authenticate__AccessDenied);
        ErrorResponse.Information errorInfo = errAuth_AccessDenied.getInformation();

        // assert
        assertTrue(errorInfo.isAccessDenied());
    }

    @Test
    void GIVEN__error__WHEN__authenticate_alreadyAuthenticated__THEN__isCode_exists_And_Source_auth()
    {
        ErrorResponseFactory factory = new ErrorResponseFactory();

        // act
        ErrorResponse errAuth_AlreadyAuthenticated = factory.createResponse(error_authenticate__AlreadyAuthenticated);
        ErrorResponse.Information errorInfo = errAuth_AlreadyAuthenticated.getInformation();

        // assert
        assertTrue(errorInfo.isCode_exists_And_Source_auth());
    }
}
